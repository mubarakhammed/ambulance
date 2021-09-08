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

public class RequestReset extends AppCompatActivity {

    private EditText idnumber;
    private ImageView back;
    private Button request;
    Snackbar snackbar;
    ProgressDialog progressDialog;
    public static final String TAG = "Request";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_reset);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Requesting...");

        idnumber = (EditText) findViewById(R.id.agentrequest_id_number);
        request = (Button) findViewById(R.id.requestcode_btn);
        back = (ImageView) findViewById(R.id.back_from_reset);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (idnumber.getText().toString().isEmpty()){
                    View contextView = findViewById(android.R.id.content);
                    snackbar = Snackbar.make(contextView, "Empty fields are not allowed", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();

                }else{
                    requestCode();
                }

            }
        });
    }

    private void requestCode() {
        showDialog();
        AndroidNetworking.get("https://ambulance.schoolofsalesmanship.com/api/password-reset-link?dispatcher_tag="+idnumber.getText().toString())
               // .addBodyParameter("dispatcher_tag", idnumber.getText().toString())

                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        // do anything with response
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                           // int status = jObj.getInt("status");
                            String msg = jObj.getString("message");
                            View contextView = findViewById(android.R.id.content);
                            snackbar = Snackbar.make(contextView, msg, Snackbar.LENGTH_SHORT);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(getResources().getColor(R.color.blue));
                            snackbar.show();
                            Intent goforward = new Intent(RequestReset.this, PasswordReset.class);
                            startActivity(goforward);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        hideDialog();
                        if (error.getErrorCode() != 0) {
                            Toast.makeText(getApplicationContext(), "fatal error getting code.", Toast.LENGTH_LONG).show();

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