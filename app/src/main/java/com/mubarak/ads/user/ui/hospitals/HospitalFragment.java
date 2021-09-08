package com.mubarak.ads.user.ui.hospitals;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mubarak.ads.R;
import com.mubarak.ads.user.UserMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HospitalFragment extends Fragment {

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    String userSubAdmin, userAddress;
    ProgressDialog progressDialog;
    Dialog dialog;
    String  combined_Address;
    private TextView locationdisplay, emptyerror, healthtips;
    public static final String TAG = "Hospital";
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    private List<hospitalList> listitems;
    String  address;
    SwipeRefreshLayout swipeRefreshLayout;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_hospitals, container, false);

        locationdisplay = (TextView) root.findViewById(R.id.hlocation);
        dialog = new Dialog(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Getting nearest hospitals... Please wait");
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

        }
        try {

            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else {
            latitude = 0.0;
            longitude = 0.0;
        }
        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);


        try {

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                userAddress = addresses.get(0).getSubAdminArea();
                userSubAdmin = addresses.get(0).getLocality();
                locationdisplay.setText(userAddress + ", " + userSubAdmin);
            }
            else {
                userAddress = "Unknown";
                locationdisplay.setText(userAddress);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        combined_Address = userAddress + ", " + userSubAdmin;
        address =userAddress.toLowerCase();
        Log.d(TAG, "The address: "+ address);

        swipeRefreshLayout = (SwipeRefreshLayout)root.findViewById(R.id.hospitalRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadhospitals(address);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView = root.findViewById(R.id.hospital_recycler);
        recyclerView.setHasFixedSize(true);
        listitems = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new hospitalAdapter(listitems, R.layout.hospitallist, getContext());
        recyclerView.setAdapter(adapter);



        loadhospitals(address);





        return root;
    }

    private void loadhospitals(final String location) {

        showDialog();


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        try {
            String url = "https://ehc9ja.herokuapp.com/hospitals";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,  new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Log.d(TAG, "onResponse: " + s);
                    hideDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray array = jsonObject.getJSONArray("data");
                        if (array.length() == 0){
                            Log.d(TAG, "onResponse: " + "Empty data");
                            emptyerror.setVisibility(View.VISIBLE);
                            healthtips.setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i<array.length(); i++ ){

                            JSONObject o = array.getJSONObject(i);
                            hospitalList item = new hospitalList(
                                    o.getString("name"),
                                    o.getString("email"),
                                    o.getString("type"),
                                    o.getString("location"),
                                    o.getString("specialty"),
                                    o.getString("phone")


                            );
                            listitems.add(item);
                        }

                        adapter = new hospitalAdapter(listitems, R.layout.hospitallist, getContext());
                        recyclerView.setAdapter(adapter);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideDialog();
                    negative_reply();
                    Log.d(TAG, "onErrorResponse: " + error.toString());

                }
            }){


                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("location", location);
                    return params;
                }
            };
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private  void negative_reply(){

        dialog.setContentView(R.layout.error_popup);

        Button logout = dialog.findViewById(R.id.logout);
        Button retry = dialog.findViewById(R.id.retry);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent out = new Intent(getContext(), UserMain.class);
                startActivity(out);
                getActivity().finish();

            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadhospitals(address);
                dialog.dismiss();


            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }


    private void showDialog(){
        if (!progressDialog.isShowing())
            progressDialog.show();

    }

    private void hideDialog(){
        if (progressDialog.isShowing())
            progressDialog.dismiss();

    }


}