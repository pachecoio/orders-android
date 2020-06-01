package io.pacheco.orders.api;

import java.util.List;

import io.pacheco.orders.models.Product;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProductApi {
    @GET("product")
    Call<List<Product>> getAll();

    @Multipart
    @PUT("product/{id}")
    Call<Product> update(
            @Path("id") Integer id,
            @Part("image\";") RequestBody image,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price);
}