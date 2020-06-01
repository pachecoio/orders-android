package io.pacheco.orders.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.pacheco.orders.R;
import io.pacheco.orders.helpers.Utils;
import io.pacheco.orders.models.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> clients;

    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public UserAdapter(ArrayList<User> clients, ListItemClickListener listener) {
        this.clients = clients;
        mOnClickListener = listener;
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView itemTitle;
        TextView itemPrice;
        ImageView itemImage;
        LinearLayout parentLayout;
        public UserViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.list_item_name);
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
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.client_list_item, parent, false);
        return new UserViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User client = clients.get(position);

        holder.itemTitle.setText(client.getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return clients.size();
    }

}