package com.sanat.complainmgt.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sanat.complainmgt.R;
import com.sanat.complainmgt.model.ComplainModel;
import com.sanat.complainmgt.model.ResponseLogin;
import com.sanat.complainmgt.model.ResponseSaveComplain;
import com.sanat.complainmgt.model.UserModel;
import com.sanat.complainmgt.network.APIs;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";

    private ActionBar toolbar;
    private RecyclerView rvHistory;
    List<ComplainModel> complainModels;
    HistoryAdapter adapter;
    APIs apiURL = new APIs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupToolbar();
        buildRecycleViewHistory();
    }

    private void setupToolbar() {
        Log.i(TAG, "setupToolbar: ");
        toolbar = getSupportActionBar();
        toolbar.setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildRecycleViewHistory() {
        rvHistory = findViewById(R.id.rvHistory);
        complainModels = new ArrayList<>();
        adapter = new HistoryAdapter(HistoryActivity.this, complainModels);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(HistoryActivity.this, 1);
        rvHistory.setLayoutManager(mLayoutManager);
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        rvHistory.setAdapter(adapter);

        getComplainsByUserIDURL("1");

    }

    private void getComplainsByUserIDURL(String userID){
        String URL = apiURL.getComplainsByUserIDURL(userID);
        Log.i(TAG, "login: " + URL);
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Please wait...", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: " + response);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                ResponseSaveComplain res = gson.fromJson(response, ResponseSaveComplain.class);
                if (res.getSuccess()){
                    complainModels.clear();
                    complainModels.addAll(res.getData());
                }
                dialog.dismiss();
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.i(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }


    class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
        private Context context;
        private List<ComplainModel> models;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView  txtDate, txtTitle, txtSL;
            public LinearLayout linRow;
            public MyViewHolder(View view) {
                super(view);
                txtDate = view.findViewById(R.id.txtDate);
                txtTitle = view.findViewById(R.id.txtTitle);
                txtSL = view.findViewById(R.id.txtSL);
                linRow = view.findViewById(R.id.linRow);
            }
        }

        public HistoryAdapter(Context context,  List<ComplainModel> models) {
            this.context = context;
            this.models = models;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_all_history_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final ComplainModel model = models.get(position);
            int sl = (position+1);
            holder.txtSL.setText(String.valueOf(sl));
            holder.txtDate.setText(model.getEntryDate());
            holder.txtTitle.setText(model.getTitle());
            holder.linRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), model.getModel().getModelName(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return models.size();
        }

    }


}
