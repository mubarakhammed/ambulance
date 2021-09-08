package com.mubarak.ads.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.mubarak.ads.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AgentRegisteration extends AppCompatActivity {

    private EditText  name, email, phone, password, confirm_password;
    private Button register;
    public static final String TAG = "Register";
    ProgressDialog progressDialog;
    Snackbar snackbar;
    Dialog mdialog;
    String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_registeration);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }

        mdialog = new Dialog(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering...");


        name = (EditText) findViewById(R.id.register_full_name);
        email = (EditText) findViewById(R.id.register_emailAddress);
        phone = (EditText) findViewById(R.id.register_phonenumber);
        password = (EditText) findViewById(R.id.register_password);
        confirm_password = (EditText) findViewById(R.id.register_confirm_password);

        register = (Button) findViewById(R.id.registerbtn);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty() || email.getText().toString().isEmpty() || phone.getText().toString().isEmpty()
                        || password.getText().toString().isEmpty() || confirm_password.getText().toString().isEmpty()) {
                    View contextView = findViewById(android.R.id.content);
                    snackbar = Snackbar.make(contextView, "Empty field are not allowed", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                } else {
                    RegisterAgent();
                }

            }
        });

    }

    private void RegisterAgent() {
        showDialog();
        AndroidNetworking.post("https://ambulance.schoolofsalesmanship.com/api/register")
                .addBodyParameter("dispatcher_name", name.getText().toString())
                .addBodyParameter("dispatcher_email", email.getText().toString())
                .addBodyParameter("dispatcher_phone", phone.getText().toString())
                .addBodyParameter("dispatcher_password", confirm_password.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        // do anything with response
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int status = jObj.getInt("status");
                             tag = jObj.getString("dispatcher");
                            switch (status) {
                                case 200:
                                    //success
                                    hideDialog();
                                    Log.d(TAG, "onResponse: " + "You have successfully registered");
                                    Showdialog();
                                    break;
                                default:
                                    hideDialog();
                                    Toast.makeText(getApplicationContext(), "We encountered an error", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "onResponse: " + "an error occurred");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        hideDialog();
                        if (error.getErrorCode() != 0) {
                            Toast.makeText(getApplicationContext(), "fatal error registering agent.", Toast.LENGTH_LONG).show();

                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());

                        } else {

                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }


                });

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void Showdialog() {
        Button back;
        TextView tag1;

        mdialog = new Dialog(AgentRegisteration.this);
        mdialog.setContentView(R.layout.material_dialog);
        mdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = mdialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;


        back = mdialog.findViewById(R.id.back);
        tag1 = mdialog.findViewById(R.id.tag1);
        tag1.setText(tag);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone.getText().clear();
                name.getText().clear();
                phone.getText().clear();
                email.getText().clear();
                password.getText().clear();
                mdialog.dismiss();
            }
        });
        mdialog.setCancelable(false);
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        mdialog.show();


    }
}