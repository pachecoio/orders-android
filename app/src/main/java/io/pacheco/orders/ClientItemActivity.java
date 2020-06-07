package io.pacheco.orders;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.pacheco.orders.api.RetrofitClientInstance;
import io.pacheco.orders.api.UserApi;
import io.pacheco.orders.models.Order;
import io.pacheco.orders.models.User;
import io.pacheco.orders.ui.client.list.ClientListFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientItemActivity extends AppCompatActivity {

    EditText clientName;
    EditText clientEmail;
    Button saveBtn;
    Button deleteBtn;
    ProgressDialog progressDialog;
    UserApi service = RetrofitClientInstance.getRetrofitInstance().create(UserApi.class);

    User client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_item);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Novo cliente");
        }

        clientName = findViewById(R.id.client_name);
        clientEmail = findViewById(R.id.client_email);
        saveBtn = findViewById(R.id.save_btn);
        deleteBtn = findViewById(R.id.delete_btn);

        Intent intent = getIntent();
        this.client = intent.getParcelableExtra("client");

        if(this.client != null) {
            getSupportActionBar().setTitle(client.getName());
            clientName.setText(this.client.getName());
            clientEmail.setText(this.client.getEmail());
        } else {
            deleteBtn.setVisibility(View.GONE);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClient();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClient(client);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void saveClient() {
        progressDialog.show();
        String name = clientName.getText().toString();
        String email = clientEmail.getText().toString();

        if(client == null) client = new User();

        if(!clientName.getText().toString().isEmpty()) client.setName(name);
        if(!clientEmail.getText().toString().isEmpty()) client.setEmail(email);

        Call<User> call;

        if(client.getId() == null) {
            Log.i("without id", "create client");
            client.setRole("client");
            call = service.create(client);
        } else {
            Log.i("client id found", String.valueOf(client.getId()));
            call = service.update(client.getId(), client);
        }

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                Log.i("client saved", response.message());
                goBack();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Error creating client", t.getMessage());
                goBack();
            }
        });

    }

    public void deleteClient(User client) {
        progressDialog.show();
        Call<String> call = service.delete(client.getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                Log.i("client deleted", response.message());
                goBack();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Error deleting client", t.getMessage());
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

}
