package com.sanat.complainmgt.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sanat.complainmgt.model.ResponseLogin;
import com.sanat.complainmgt.model.UserModel;
import com.sanat.complainmgt.network.APIs;
import com.sanat.complainmgt.others.HideKeyboard;
import com.sanat.complainmgt.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private ActionBar toolbar;
    private Button btn_login;
    private TextInputLayout txtUserEmpID, txtPassword;
    private TextView txt_create_acc, txt_forget_pass;
    private ScrollView loginContainer;
    HideKeyboard hideKeyboard;
    APIs apiURL = new APIs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupToolbar();
        widget();
    }

    private void widget() {
        Log.i(TAG, "widget: ");
        txtUserEmpID = findViewById(R.id.txtUserEmpID);
        txtPassword = findViewById(R.id.txtPassword);
        btn_login = findViewById(R.id.btn_login);
        txt_create_acc = findViewById(R.id.txt_create_acc);

        txt_forget_pass = findViewById(R.id.txt_forget_pass);
        txt_forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        loginContainer = findViewById(R.id.loginContainer);
        loginContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                View view1 = getCurrentFocus();
                if(view1 != null)
                    hideKeyboard.hideSoftKeyboard(LoginActivity.this);
                return false;
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtUserEmpID.getEditText().getText().toString().trim().equalsIgnoreCase("")){
                    Toast.makeText(LoginActivity.this, "Enter User ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (txtPassword.getEditText().getText().toString().trim().equalsIgnoreCase("")){
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                login(txtUserEmpID.getEditText().getText().toString().trim(), txtPassword.getEditText().getText().toString().trim());
            }
        });

        txt_create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        Log.i(TAG, "setupToolbar: ");
        toolbar = getSupportActionBar();
        toolbar.hide();
    }

    private void login(String EmpID, String Pass){

        String URL = apiURL.getLoginURL(EmpID, Pass);
        Log.i(TAG, "login: " + URL);
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Please wait...", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: ");
                Log.i(TAG, "onResponse: " + response);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                ResponseLogin res = gson.fromJson(response, ResponseLogin.class);
                dialog.dismiss();
                if (res.getSuccess())  {
                    UserModel user = res.getData().get(0);
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("UserModel", user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed. " + res.getMsg(), Toast.LENGTH_SHORT).show();
                }

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
}