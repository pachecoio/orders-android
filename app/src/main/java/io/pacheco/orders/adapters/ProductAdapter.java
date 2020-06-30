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
import io.pacheco.orders.models.Product;
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private ArrayList<Product> products;

    private final ListItemClickListener mOnClickListener;

    private final ListItemLongClickListener mOnLongClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public interface ListItemLongClickListener {
        void onListItemLongClick(int clickedItemIndex);
    }

    public ProductAdapter(ArrayList<Product> products, ListItemClickListener listener, ListItemLongClickListener longListener) {
        this.products = products;
        mOnClickListener = listener;
        mOnLongClickListener = longListener;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // each data item is just a string in this case
        TextView itemTitle;
        TextView itemPrice;
        ImageView itemImage;
        LinearLayout parentLayout;
        public ProductViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.list_item_name);
            itemPrice = itemView.findViewById(R.id.list_item_price);
            itemImage = itemView.findViewById(R.id.list_item_image);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(position);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            mOnLongClickListener.onListItemLongClick(position);
            return false;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_item, parent, false);
        return new ProductViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = products.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.itemTitle.setText(product.getName());
        holder.itemPrice.setText(Utils.getFormattedCurrency(product.getPrice()));
        Picasso.get()
                .load(product.getImage().replaceAll("localhost", "192.168.14.100"))
                .placeholder(R.drawable.product_bg)
                .fit()
                .error(R.drawable.product_bg)
                .into(holder.itemImage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return products.size();
    }

}