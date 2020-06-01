package io.pacheco.orders.api;

import java.util.List;

import io.pacheco.orders.models.Order;
import io.pacheco.orders.models.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserApi {
    @GET("user")
    Call<List<User>> getAll(@Query("role") String role);
}
