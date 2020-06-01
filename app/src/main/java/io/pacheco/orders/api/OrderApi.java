package io.pacheco.orders.api;

import java.util.List;

import io.pacheco.orders.models.Order;
import io.pacheco.orders.models.Product;
import retrofit2.Call;
import retrofit2.http.GET;

public interface OrderApi {
    @GET("order")
    Call<List<Order>> getAll();
}
