package io.pacheco.orders.ui.order.list;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.pacheco.orders.OrderItemActivity;
import io.pacheco.orders.R;
import io.pacheco.orders.adapters.OrderAdapter;
import io.pacheco.orders.api.OrderApi;
import io.pacheco.orders.api.RetrofitClientInstance;
import io.pacheco.orders.models.Order;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListFragment extends Fragment implements OrderAdapter.ListItemClickListener {
    private OrderListViewModel orderListViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Order> orders = new ArrayList<>();
    ProgressDialog progressDialog;

    FloatingActionButton fab;

    OrderApi service = RetrofitClientInstance.getRetrofitInstance().create(OrderApi.class);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderListViewModel =
                ViewModelProviders.of(this).get(OrderListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_order_list, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        fab = root.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderItemActivity.class);
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
        mAdapter = new OrderAdapter(orders, this);
        recyclerView.setAdapter(mAdapter);

        getOrders();

//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    public void getOrders() {
        Call<List<Order>> call = service.getAll();

        call.enqueue(new Callback<List<Order>>() {@Override
        public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
            progressDialog.dismiss();
            Log.i("orders found", response.message());
            generateDataList((ArrayList<Order>) response.body());
        }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("error", t.getMessage());
                Toast.makeText(getActivity(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrders();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(getActivity(), OrderItemActivity.class);
        intent.putExtra("order", orders.get(clickedItemIndex));
        startActivity(intent);
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(ArrayList<Order> newOrders) {

        orders.clear();

        Log.i("orders", "get orders");
        if(newOrders != null) {
            orders.addAll(newOrders);
        }
        mAdapter.notifyDataSetChanged();
    }
}
