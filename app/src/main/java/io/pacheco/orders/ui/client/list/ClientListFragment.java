package io.pacheco.orders.ui.client.list;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.pacheco.orders.R;
import io.pacheco.orders.adapters.UserAdapter;
import io.pacheco.orders.api.UserApi;
import io.pacheco.orders.api.RetrofitClientInstance;
import io.pacheco.orders.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientListFragment extends Fragment implements UserAdapter.ListItemClickListener {
    private ClientListViewModel clientListViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> clients = new ArrayList<>();
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientListViewModel =
                ViewModelProviders.of(this).get(ClientListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_client_list, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        recyclerView = root.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new UserAdapter(clients, this);
        recyclerView.setAdapter(mAdapter);

        UserApi service = RetrofitClientInstance.getRetrofitInstance().create(UserApi.class);
        Call<List<User>> call = service.getAll("client");

        call.enqueue(new Callback<List<User>>() {@Override
        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
            progressDialog.dismiss();
            Log.i("clients found", response.message());
            generateDataList((ArrayList<User>) response.body());
        }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("error", t.getMessage());
                Toast.makeText(getActivity(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
//        Intent intent = new Intent(getActivity(), UserActivity.class);
//        intent.putExtra("product", orders.get(clickedItemIndex));
//        startActivity(intent);
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(ArrayList<User> newUsers) {

        Log.i("clients", "get clients");
        if(newUsers != null) {
            clients.addAll(newUsers);
        }
        mAdapter.notifyDataSetChanged();
    }
}
