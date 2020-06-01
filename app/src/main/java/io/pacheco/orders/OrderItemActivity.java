package io.pacheco.orders;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.pacheco.orders.adapters.ProductAdapter;
import io.pacheco.orders.api.ProductApi;
import io.pacheco.orders.api.RetrofitClientInstance;
import io.pacheco.orders.helpers.Utils;
import io.pacheco.orders.models.Order;
import io.pacheco.orders.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderItemActivity extends AppCompatActivity implements ProductAdapter.ListItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> products = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Novo pedido");
        }

        Intent intent = getIntent();
        Order order = intent.getParcelableExtra("order");

        if(order != null) {
            getSupportActionBar().setTitle("Pedido de " + order.getClient().getName());
        }

        recyclerView = findViewById(R.id.recycler_view);

        TextView itemName = findViewById(R.id.order_client_name);
        TextView itemPrice = findViewById(R.id.order_total);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new ProductAdapter(products, this);
        recyclerView.setAdapter(mAdapter);

        if(order != null) {
            generateDataList(order.getProducts());
            itemName.setText(order.getClient().getName());
            double total = 0;
            if(order.getProducts() != null && order.getProducts().size() > 0) {
                for (Product product : order.getProducts()) {
                    total += product.getPrice();
                }
            }
            itemPrice.setText("Total: " + Utils.getFormattedCurrency(total));
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(ArrayList<Product> newProducts) {

        Log.i("products", "get products");
        double total = 0;
        if(newProducts != null) {
            products.addAll(newProducts);
        }
        mAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.i("Clicked item", products.get(clickedItemIndex).getName());
    }
}
