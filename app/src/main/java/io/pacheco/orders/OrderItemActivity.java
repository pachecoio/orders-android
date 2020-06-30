package io.pacheco.orders;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.pacheco.orders.adapters.ProductAdapter;
import io.pacheco.orders.api.OrderApi;
import io.pacheco.orders.api.ProductApi;
import io.pacheco.orders.api.RetrofitClientInstance;
import io.pacheco.orders.api.UserApi;
import io.pacheco.orders.helpers.Utils;
import io.pacheco.orders.models.Order;
import io.pacheco.orders.models.Product;
import io.pacheco.orders.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderItemActivity extends AppCompatActivity implements ProductAdapter.ListItemClickListener, ProductAdapter.ListItemLongClickListener, AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<User> clients = new ArrayList<>();
    private ArrayList<Product> productsToAdd = new ArrayList<>();
    ProgressDialog progressDialog;
    Spinner spinClient;
    Spinner spin;
    ArrayAdapter arrayAdapter;
    ArrayAdapter clientArrayAdapter;

    Order order;

    ArrayList<String> productsNames = new ArrayList<>();
    ArrayList<String> clientsNames = new ArrayList<>();

    ProductApi service = RetrofitClientInstance.getRetrofitInstance().create(ProductApi.class);

    UserApi clientService = RetrofitClientInstance.getRetrofitInstance().create(UserApi.class);

    OrderApi orderService = RetrofitClientInstance.getRetrofitInstance().create(OrderApi.class);

    Button saveBtn;
    Button deleteBtn;

    TextView itemPrice;

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
        order = intent.getParcelableExtra("order");

        recyclerView = findViewById(R.id.recycler_view);
        itemPrice = findViewById(R.id.order_total);

        saveBtn = findViewById(R.id.save_btn);
        deleteBtn = findViewById(R.id.delete_btn);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new ProductAdapter(products, this, this);
        recyclerView.setAdapter(mAdapter);

        if(order != null) {
            getSupportActionBar().setTitle("Pedido de " + order.getClient().getName());
            generateDataList(order.getProducts());
            productsToAdd.addAll(order.getProducts());
            setTotalPrice();
        } else {
            order = new Order();
            order.setProducts(products);
            deleteBtn.setVisibility(View.INVISIBLE);
        }

        spin = (Spinner) findViewById(R.id.products_spinner);
        spin.setOnItemSelectedListener(this);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, productsNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(arrayAdapter);

        spinClient = (Spinner) findViewById(R.id.clients_spinner);
        spinClient.setOnItemSelectedListener(this);
        clientArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, clientsNames);
        clientArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinClient.setAdapter(clientArrayAdapter);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrder();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOrder(order);
            }
        });

        getProducts();

        getClients();

    }

    public void deleteProductByIndex(int index) {
        products.remove(index);
        mAdapter.notifyDataSetChanged();
    }

    public void getClients() {
        progressDialog.show();
        Call<List<User>> call = clientService.getAll("client");

        call.enqueue(new Callback<List<User>>() {@Override
        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
            progressDialog.dismiss();
            Log.i("clients found", response.message());
            generateClientSpinnerDataList((ArrayList<User>) response.body());
        }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("error", t.getMessage());
                Toast.makeText(OrderItemActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveOrder() {

        Order newOrder = new Order(
                products,
                order.getClient()
        );

        Call<Order> call;

        if(order.getId() == null) {
            Log.i("without id", "create order");
            call = orderService.create(newOrder);
        } else {
            Log.i("order id found", String.valueOf(order.getId()));
            newOrder.setId(order.getId());
            call = orderService.update(order.getId(), newOrder);
        }

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                progressDialog.dismiss();
                Log.i("order saved", response.message());
                goBack();
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Error creating order", t.getMessage());
                goBack();
            }
        });

    }

    public void goBack() {
        Log.i("finish", "go back to previous activity");
        setResult(RESULT_OK);
        Log.i("go back", "Inserted intent data and go back");
        this.finish();
    }

    public void deleteOrder(Order order) {
        progressDialog.show();
        Call<String> call = orderService.delete(order.getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                Log.i("order deleted", response.message());
                goBack();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Error deleting order", t.getMessage());
                goBack();
            }
        });
    }

    public void setTotalPrice() {
        double total = 0;
        if(products != null && products.size() > 0) {
            for (Product product : products) {
                total += product.getPrice();
            }
        }
        itemPrice.setText("Total: " + Utils.getFormattedCurrency(total));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getProducts() {
        Call<List<Product>> call = service.getAll();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressDialog.dismiss();
                Log.i("products found", response.message());
                generateSpinnerDataList((ArrayList<Product>) response.body());
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("error", t.getMessage());
            }
        });
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(ArrayList<Product> newProducts) {

        Log.i("products", "get products");
        if(newProducts != null) {
            products.addAll(newProducts);
        }
        mAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }


    private void generateSpinnerDataList(ArrayList<Product> products) {

        productsNames.clear();
        productsToAdd.clear();

        productsNames.add("Adicionar produto");

        Log.i("spinner products", "get products");
        if(products != null) {
            productsToAdd.addAll(products);
            for(Product product : products) {
                productsNames.add("#" + product.getId() + " - " + product.getName());
            }
        }
        arrayAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    private void generateClientSpinnerDataList(ArrayList<User> newClients) {

        clientsNames.clear();
        clients.clear();

        clientsNames.add("Selecionar cliente");

        Log.i("spinner clients", "get clients");
        if(newClients != null) {
            clients.addAll(newClients);
            for(User client : clients) {
                clientsNames.add("#" + client.getId() + " - " + client.getName());
            }
        }
        clientArrayAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.i("Clicked item", products.get(clickedItemIndex).getName());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapterView.getId() == R.id.clients_spinner) {
            onSelectClient(i);
        } else if(adapterView.getId() == R.id.products_spinner) {
            onSelectProduct(i);
        }

    }

    public void onSelectProduct(int i) {
        String productId = productsNames.get(i).replaceAll("#", "");

        productId = productId.replaceAll(" - [a-z|A-Z|0-9]+", "");

        if(productId.equals("Adicionar produto")) return;

        int id = Integer.parseInt(productId);

        Product productToAdd = null;

        for(Product product : productsToAdd) {
            if(product.getId() == id) {
                productToAdd = product;
            }
        }

        if(productToAdd == null) return;

        products.add(productToAdd);

        spin.setSelection(0);

        setTotalPrice();

        arrayAdapter.notifyDataSetChanged();
    }

    public void onSelectClient(int i) {
        String clientId = clientsNames.get(i).replaceAll("#", "");

        clientId = clientId.replaceAll(" - [a-z|A-Z|0-9]+", "");

        if(clientId.equals("Selecionar cliente") || clientId.equals("Adicionar produto")) return;

        int id = Integer.parseInt(clientId);

        User clientToAdd = null;

        for(User client : clients) {
            if(client.getId() == id) {
                clientToAdd = client;
            }
        }

        if(clientToAdd == null) return;

        order.setClient(clientToAdd);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onListItemLongClick(int clickedItemIndex) {
        deleteProductByIndex(clickedItemIndex);
        Toast.makeText(OrderItemActivity.this, "Produto removido", Toast.LENGTH_SHORT).show();
    }
}
