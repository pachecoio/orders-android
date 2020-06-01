package io.pacheco.orders;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.pacheco.orders.adapters.ProductAdapter;
import io.pacheco.orders.api.ProductApi;
import io.pacheco.orders.api.RetrofitClientInstance;
import io.pacheco.orders.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ProductAdapter.ListItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> products = new ArrayList<>();
    private FloatingActionButton btn;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        getSupportActionBar().setTitle("Produtos");

        btn = findViewById(R.id.fab);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductActivity();
            }
        };

        recyclerView = findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new ProductAdapter(products, this);
        recyclerView.setAdapter(mAdapter);

        btn.setOnClickListener(onClickListener);

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
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == RESULT_OK) {

            Product product = (Product) data.getSerializableExtra("product");
            products.add(product);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void openProductActivity() {
        Intent intent = new Intent(this, ProductActivity.class);
        startActivityForResult(intent, 999);
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

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(MainActivity.this, ProductActivity.class);
        intent.putExtra("product", products.get(clickedItemIndex));
        startActivity(intent);
    }
}
