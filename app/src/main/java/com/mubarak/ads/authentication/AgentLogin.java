package com.mubarak.ads.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.mubarak.ads.R;
import com.mubarak.ads.agent.AdminActivity;
import com.mubarak.ads.agent.AgentPanel;
import com.mubarak.ads.agent.AgentProfile;
import com.mubarak.ads.user.UserMain;

import org.json.JSONException;
import org.json.JSONObject;

public class AgentLogin extends AppCompatActivity {
    private EditText id, password;
    private ImageView close;
    public static final String TAG = "Login";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    ProgressButton progressButton;
    private TextView adminLink, resetLink;
    View view;
    Dialog dialog;
    Snackbar snackbar;
    public static String accesskey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_login);

        dialog = new Dialog(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
        close = (ImageView) findViewById(R.id.closepage);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adminLink = (TextView) findViewById(R.id.adminLink);
        adminLink.setPaintFlags(adminLink.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        resetLink = (TextView) findViewById(R.id.resetlink);
        resetLink.setPaintFlags(resetLink.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              requestAccess();
            }
        });
           resetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(AgentLogin.this, RequestReset.class);
                startActivity(register);
            }
        });
        id = (EditText) findViewById(R.id.agent_id_number);
        password = (EditText) findViewById(R.id.agent_id_password);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(AgentLogin.this);
        mEditor = mPreferences.edit();


        view = findViewById(R.id.loginbtn);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    View contextView = findViewById(android.R.id.content);
                    snackbar = Snackbar.make(contextView, "Empty fields are not allowed", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();

                } else {
                    progressButton = new ProgressButton(getApplicationContext(), view);
                    String phone = id.getText().toString();
                    mEditor.putString(getString(R.string.stored_email), phone);
                    String spassword = password.getText().toString();
                    mEditor.putString(getString(R.string.stored_password), spassword);
                    mEditor.commit();
                    loginUser();

                }
            }
        });

        Preference();

    }

    private void loginUser() {
        progressButton.buttonActivated();
        AndroidNetworking.post("https://ambulance.schoolofsalesmanship.com/api/login")
                .addBodyParameter("dispatcher_tag", id.getText().toString())
                .addBodyParameter("password", password.getText().toString())
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
                            String access = jObj.getString("access");

                            switch (status) {
                                case 200:
                                    accesskey = access;
                                    progressButton.buttonFinished();
                                    Intent intent = new Intent(getApplicationContext(), AgentPanel.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                default:
                                    progressButton.buttonError();
                                   bottomError();
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressButton.buttonError();
                        Toast.makeText(getApplicationContext(), "Fatal error occured. Check network and retry", Toast.LENGTH_LONG).show();
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

    private void Preference(){
        String usermail = mPreferences.getString(getString(R.string.stored_email), "");
        String userpassword = mPreferences.getString(getString(R.string.stored_password), "");
        id.setText(usermail);
    }
    private void bottomError() {
        View view = getLayoutInflater().inflate(R.layout.bottomloginerror, null);
        BottomSheetDialog dialog = new BottomSheetDialog(AgentLogin.this);
        dialog.setContentView(view);
        dialog.show();
    }
    private void requestAccess(){
        dialog.setContentView(R.layout.regpopup);
        Button retry = dialog.findViewById(R.id.retry);
        final EditText pin  = (EditText) dialog.findViewById(R.id.pininput);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("See me", "onClick: " + pin.getText().toString());

                if (pin.getText().toString().trim().equals("adamsabiola")){
                    Intent go = new Intent(AgentLogin.this, AdminActivity.class);
                    startActivity(go);
                    dialog.dismiss();
                }else {
                    Toast.makeText(getApplicationContext(), "Error, make sure you are an admin", Toast.LENGTH_LONG).show();
                }


            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
    }
}