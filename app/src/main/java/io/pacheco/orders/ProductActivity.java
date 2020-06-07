package io.pacheco.orders;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.pacheco.orders.api.ProductApi;
import io.pacheco.orders.api.RetrofitClientInstance;
import io.pacheco.orders.models.Product;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {

    Product product;
    ImageView productImage;
    EditText productName;
    EditText productPrice;
    EditText productDescription;
    Button saveBtn;
    Button deleteBtn;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private File image;

    ProgressDialog progressDialog;

    Integer productId = null;

    ProductApi service = RetrofitClientInstance.getRetrofitInstance().create(ProductApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");

        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);
        saveBtn = findViewById(R.id.save_btn);
        deleteBtn = findViewById(R.id.delete_btn);

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Novo produto");
        }

        Intent intent = getIntent();
        product = intent.getParcelableExtra("product");

        if(product != null) {
            deleteBtn.setVisibility(View.VISIBLE);
            saveBtn.setEnabled(true);
            this.productId = product.getId();
            getSupportActionBar().setTitle(product.getName());
            Picasso.get()
                    .load(product.getImage().replaceAll("localhost", "192.168.14.100"))
                    .placeholder(R.drawable.product_bg)
                    .fit()
                    .error(R.drawable.product_bg)
                    .into(productImage);
            productName.setText(product.getName());
            productPrice.setText(String.valueOf(product.getPrice()));
            productDescription.setText(product.getDescription());
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Date date = new Date();
            long ms = date.getTime();

            this.image = persistImage(imageBitmap, "product_image_" + String.valueOf(ms));
            productImage.setImageBitmap(imageBitmap);
            saveBtn.setEnabled(true);
        }
    }

    public File persistImage(Bitmap bitmap, String name) {
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("error persisting image", "Error writing bitmap", e);
        }

        return imageFile;
    }

    public void saveProduct() {

        progressDialog.show();

        Map<String, RequestBody> map = new HashMap<>();

        map.put("name", RequestBody.create(MediaType.parse("text/plain"), productName.getText().toString()));
        map.put("description", RequestBody.create(MediaType.parse("text/plain"), productDescription.getText().toString()));
        map.put("price", RequestBody.create(MediaType.parse("text/plain"), productPrice.getText().toString()));

        if (image != null) {
            RequestBody imageFile = RequestBody.create(MediaType.parse("image/jpg"), image);
            map.put("image\"; filename=\"" + image.getName(), imageFile);
        }

        Call<Product> call;

        if(product != null && product.getId() != null) {
            call = service.update(this.productId, map);
        } else {
            call = service.create(map);
        }

        call.enqueue(new Callback<Product>() {@Override
        public void onResponse(Call<Product> call, Response<Product> response) {
            progressDialog.dismiss();
            Log.i("Product saved", response.message());
            finish();
        }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("error", t.getMessage());
                Toast.makeText(ProductActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void deleteProduct() {

        Log.i("action", "delete product");

        progressDialog.show();

        Call<String> call = service.delete(product.getId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                Log.i("Product deleted", response.message());
                finish();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("error deleting product", t.getMessage());
                finish();
            }
        });
    }

}
