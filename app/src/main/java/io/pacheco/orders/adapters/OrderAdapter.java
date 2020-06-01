package io.pacheco.orders.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.pacheco.orders.R;
import io.pacheco.orders.helpers.Utils;
import io.pacheco.orders.models.Order;
import io.pacheco.orders.models.Order;
import io.pacheco.orders.models.Product;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private ArrayList<Order> orders;

    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public OrderAdapter(ArrayList<Order> orders, ListItemClickListener listener) {
        this.orders = orders;
        mOnClickListener = listener;
    }

    class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView itemTitle;
        TextView itemPrice;
        ImageView itemImage;
        LinearLayout parentLayout;
        public OrderViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.list_item_name);
            itemPrice = itemView.findViewById(R.id.list_item_price);
            itemImage = itemView.findViewById(R.id.list_item_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(position);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_item, parent, false);
        return new OrderViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        double totalPrice = 0;

        ArrayList<Product> products = order.getProducts();

        if(products != null && products.size() > 0) {
            for (Product product : products) {
                totalPrice += product.getPrice();
            }
        }

        holder.itemTitle.setText(order.getClient().getName());
        holder.itemPrice.setText(Utils.getFormattedCurrency(totalPrice));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return orders.size();
    }

}