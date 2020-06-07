package io.pacheco.orders.api;

import java.util.List;

import io.pacheco.orders.models.Order;
import io.pacheco.orders.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {
    @GET("user")
    Call<List<User>> getAll(@Query("role") String role);

    @POST("user")
    Call<User> create(@Body User client);

    @PUT("user/{id}")
    Call<User> update(@Path("id") Integer id, @Body User client);

    @DELETE("user/{id}")
    Call<String> delete(@Path("id") Integer id);
}
