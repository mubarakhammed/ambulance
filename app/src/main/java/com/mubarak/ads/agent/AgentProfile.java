package com.mubarak.ads.agent;

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
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.mubarak.ads.R;
import com.mubarak.ads.authentication.AgentLogin;
import com.mubarak.ads.user.UserMain;

import org.json.JSONException;
import org.json.JSONObject;

public class AgentProfile extends AppCompatActivity {

    ProgressDialog progressDialog;
    public static final String TAG = "Profile";
    private TextView lastseen, lastupdate;
    private EditText name, tag, mail, phone;
    private Button update;
    private ImageView back;
    String last ,up, nam, email,  ph;
    Snackbar snackbar;
    private String access = AgentLogin.accesskey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_profile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating");

        getData();

        back = (ImageView) findViewById(R.id.back_from_update);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        lastseen = (TextView) findViewById(R.id.dispatcher_lastseen);
        lastupdate = (TextView) findViewById(R.id.dispatcher_lastupdate);

        name = (EditText) findViewById(R.id.dispatcher_name);
        mail = (EditText) findViewById(R.id.dispatcher_email);
        phone = (EditText) findViewById(R.id.dispatcher_phone);

        update = (Button) findViewById(R.id.updateprofile);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View contextView = findViewById(android.R.id.content);
                snackbar = Snackbar.make(contextView, "Coming Soon", Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.yellow));
                snackbar.show();
            }
        });


    }

    private void getData(){
        showDialog();
        AndroidNetworking.get("https://ambulance.schoolofsalesmanship.com/api/user-profile")
                .setPriority(Priority.LOW)
                .addHeaders("Authorization","Bearer "+ access)
                .addHeaders("content-type", "application/json")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int status = jObj.getInt("status");
                            JSONObject data = jObj.getJSONObject("user_details");

                            switch (status) {
                                case 200:
                                    last = data.getString("created_at");
                                    nam = data.getString("dispatcher_name");
                                    up = data.getString("updated_at");
                                    email = data.getString("dispatcher_email");
                                    ph = data.getString("dispatcher_phone");

                                    lastseen.setText(last);
                                    lastupdate.setText(up);
                                    name.setText(nam);
                                    mail.setText(email);
                                    phone.setText(ph);

                                    break;
                                default:
                                    View contextView = findViewById(android.R.id.content);
                                    snackbar = Snackbar.make(contextView, "We encountered an error. Kindly retry", Snackbar.LENGTH_SHORT);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(getResources().getColor(R.color.red));
                                    snackbar.show();

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
                        View contextView = findViewById(android.R.id.content);
                        snackbar = Snackbar.make(contextView, "We encountered an error. Kindly retry", Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.red));
                        snackbar.show();
                        if (error.getErrorCode() != 0) {

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