package io.pacheco.orders.ui.product.list;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.pacheco.orders.ProductActivity;
import io.pacheco.orders.R;
import io.pacheco.orders.adapters.ProductAdapter;
import io.pacheco.orders.api.ProductApi;
import io.pacheco.orders.api.RetrofitClientInstance;
import io.pacheco.orders.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListFragment extends Fragment implements ProductAdapter.ListItemClickListener {
    private ProductListViewModel productListViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> products = new ArrayList<>();
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product_list, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        recyclerView = root.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new ProductAdapter(products, this);
        recyclerView.setAdapter(mAdapter);

        ProductApi service = RetrofitClientInstance.getRetrofitInstance().create(ProductApi.class);
        Call<List<Product>> call = service.getAll();

        call.enqueue(new Callback<List<Product>>() {@Override
        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
            progressDialog.dismiss();
            Log.i("products found", response.message());
            generateDataList((ArrayList<Product>) response.body());
        }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("error", t.getMessage());
                Toast.makeText(getActivity(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(getActivity(), ProductActivity.class);
        intent.putExtra("product", products.get(clickedItemIndex));
        startActivity(intent);
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(ArrayList<Product> newProducts) {

        Log.i("products", "get products");
        if(newProducts != null) {
            Log.i("current products", newProducts.toString());
            Log.i("first product", newProducts.get(0).getName());
            products.addAll(newProducts);
        }
        mAdapter.notifyDataSetChanged();
    }
}
