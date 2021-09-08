package com.mubarak.ads.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.mubarak.ads.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordReset extends AppCompatActivity {

    private EditText reset_key, dispatcherid, password, confirm_password;
    private Button reset;
    public static final String TAG = "Reset";
    private ImageView back;
    ProgressDialog progressDialog;
    Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Requesting...");

        back = (ImageView) findViewById(R.id.back_from_reset);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reset_key = (EditText) findViewById(R.id.resetcode);
        dispatcherid = (EditText) findViewById(R.id.resetid);
        password = (EditText) findViewById(R.id.resetpassword);
        confirm_password = (EditText) findViewById(R.id.reset_confirm_password);

        reset = (Button) findViewById(R.id.resetbtn);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reset_key.getText().toString().isEmpty() || dispatcherid.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
                confirm_password.getText().toString().isEmpty()){
                    View contextView = findViewById(android.R.id.content);
                    snackbar = Snackbar.make(contextView, "Empty fields are not allowed", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                }else {
                    resetPassword();
                }

            }
        });



    }

    private void resetPassword() {
        showDialog();
        AndroidNetworking.post("https://ambulance.schoolofsalesmanship.com/api/change-password")
                .addBodyParameter("dispatcher_tag", dispatcherid.getText().toString())
                .addBodyParameter("dispatcher_reset_key", reset_key.getText().toString())
                .addBodyParameter("new_password", confirm_password.getText().toString())
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
                            String msg = jObj.getString("message");

                            switch (status) {
                                case 200:
                                    //success
                                    hideDialog();
                                    Log.d(TAG, "onResponse: " + "Successfull");
                                    Toast.makeText(getApplicationContext(), "Password reset Successful", Toast.LENGTH_LONG).show();
                                    Intent reset = new Intent(PasswordReset.this, AgentLogin.class);
                                    startActivity(reset);
                                    finish();
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
                            Toast.makeText(getApplicationContext(), "fatal error resetting password.", Toast.LENGTH_LONG).show();

                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());

                        } else {

                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }


                });

    }

    private void showDialog () {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog () {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}

