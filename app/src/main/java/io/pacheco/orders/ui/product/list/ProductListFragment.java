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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.pacheco.orders.ClientItemActivity;
import io.pacheco.orders.ProductActivity;
import io.pacheco.orders.R;
import io.pacheco.orders.adapters.ProductAdapter;
import io.pacheco.orders.api.ProductApi;
import io.pacheco.orders.api.RetrofitClientInstance;
import io.pacheco.orders.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListFragment extends Fragment implements ProductAdapter.ListItemClickListener, ProductAdapter.ListItemLongClickListener {
    private ProductListViewModel productListViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> products = new ArrayList<>();
    ProgressDialog progressDialog;

    FloatingActionButton fab;

    public static final int OPEN_PRODUCT_REQUEST = 999;

    ProductApi service = RetrofitClientInstance.getRetrofitInstance().create(ProductApi.class);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product_list, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        fab = root.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = root.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new ProductAdapter(products, this, this);
        recyclerView.setAdapter(mAdapter);

        getProducts();
        return root;
    }

    private void getProducts() {
        Call<List<Product>> call = service.getAll();

        call.enqueue(new Callback<List<Product>>() {
            @Override
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
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(getActivity(), ProductActivity.class);
        intent.putExtra("product", products.get(clickedItemIndex));
        startActivityForResult(intent, OPEN_PRODUCT_REQUEST);
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(ArrayList<Product> newProducts) {

        products.clear();

        Log.i("products", "get products");
        if(newProducts != null && newProducts.size() > 0) {
            Log.i("current products", newProducts.toString());
            Log.i("first product", newProducts.get(0).getName());
            products.addAll(newProducts);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        getProducts();
    }

    @Override
    public void onListItemLongClick(int clickedItemIndex) {

    }
}
