package io.pacheco.orders.api;

import java.util.List;
import java.util.Map;

import io.pacheco.orders.models.Order;
import io.pacheco.orders.models.Product;
import io.pacheco.orders.models.User;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface OrderApi {
    @GET("order")
    Call<List<Order>> getAll();

    @POST("order")
    Call<Order> create(@Body Order order);

    @PUT("order/{id}")
    Call<Order> update(@Path("id") Integer id, @Body Order order);

    @DELETE("order/{id}")
    Call<String> delete(@Path("id") Integer id);
}
