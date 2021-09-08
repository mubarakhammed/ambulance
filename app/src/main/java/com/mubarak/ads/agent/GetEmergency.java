package com.mubarak.ads.agent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.mubarak.ads.R;
import com.mubarak.ads.authentication.AgentLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetEmergency extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LottieAnimationView lottieAnimationView;
    private static final String TAG = "Emergencies";
    RecyclerView.Adapter[] adapter = new RecyclerView.Adapter[1];
    List<EmergencyList> listitems;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private String access = AgentLogin.accesskey;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_emergency);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.pendingrefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfo();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        dialog = new Dialog(this);
        lottieAnimationView= (LottieAnimationView) findViewById(R.id.just_loading1);

        getInfo();

    }
    private void getInfo() {
        lottieAnimationView .setVisibility(View.VISIBLE);
        recyclerView= (RecyclerView) findViewById(R.id.emergency_recycler);
        recyclerView.setHasFixedSize(false);
        listitems = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter[0] = new EmergencyAdapter(listitems, R.layout.emergencylist, this);
        recyclerView.setAdapter(adapter[0]);


        AndroidNetworking.get("https://ambulance.schoolofsalesmanship.com/api/get-emergency/pending")
                .addHeaders("Authorization","Bearer "+ access)
                .addHeaders("content-type", "application/json")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        lottieAnimationView.setVisibility(View.GONE);
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            JSONArray array = jObj.getJSONArray("message");
                            Log.d(TAG, "onResponse: " + array);

                            if (array.length()<1){
                                Log.d(TAG, "onResponse: " + "I am empty");
                                //  empty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                EmergencyList item = new EmergencyList(
                                        o.getString("id"),
                                        o.getString("dispatcher_tag"),
                                        o.getString("em_caller"),
                                        o.getString("em_location"),
                                        o.getString("em_phone"),
                                        o.getString("em_type"),
                                        o.getString("em_status"),
                                        o.getString("em_no_victims"),
                                        o.getString("created_at")

                                );
                                listitems.add(item);
                            }

                            adapter[0] = new EmergencyAdapter(listitems, R.layout.emergencylist, getApplicationContext());
                            recyclerView.setAdapter(adapter[0]);


                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        lottieAnimationView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        Log.d(TAG, "onError:  " + error);
                        if (error.getErrorCode() != 0) {
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                            // get parsed error object (If ApiError is your class)

                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });

    }
}