package com.sanat.complainmgt.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sanat.complainmgt.R;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";

    CardView btnNewComplain, btnHistory;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setupToolbar();
        widget();
    }

    private void widget() {
        btnNewComplain = findViewById(R.id.btnNewComplain);
        btnNewComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, NewComplainActivity.class);
                startActivity(intent);
            }
        });
        btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        Log.i(TAG, "setupToolbar: ");
        toolbar = getSupportActionBar();
        toolbar.hide();
    }

}