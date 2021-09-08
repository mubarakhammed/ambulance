package com.mubarak.ads.user.ui.home;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.mubarak.ads.R;
import com.mubarak.ads.authentication.AgentLogin;
import com.mubarak.ads.authentication.AgentRegisteration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    String userSubAdmin, userAddress, userState, userSublocality, userLocality, userAdmin;
    Spinner casetype;
    String caseList[] = {"Road Accident", "Dead Victim", "Fire Accident", "Building Collapse","Cardiac Arrest", "others"};
    private EditText phone, victimNumber;
    ProgressDialog progressDialog;
    Dialog mdialog;
    String  combined_Address;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private TextView locationdisplay;
    public static final String TAG = "Emergency";
    public static  String globalLocation;
    private LinearLayout alert;
    private String access = AgentLogin.accesskey;

    String addressline1, addressline2, addressline3, addressline4, addressline5;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mdialog = new Dialog(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Posting emergency...");

        locationdisplay = (TextView) root.findViewById(R.id.location);
        //locationdisplay.setText(location);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditor = mPreferences.edit();

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
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
            if (addresses != null && addresses.size() > 0) {
                userSubAdmin= addresses.get(0).getSubAdminArea();
                userLocality= addresses.get(0).getLocality();
                userAdmin= addresses.get(0).getAdminArea();
                userSublocality = addresses.get(0).getSubLocality();


                addressline1 = addresses.get(0).getAddressLine(1);
                addressline2 = addresses.get(0).getFeatureName();
                addressline3 = addresses.get(0).getPremises();
                addressline4 = addresses.get(0).getThoroughfare();
                addressline5 = addresses.get(0).getUrl();


                globalLocation = userState;
                locationdisplay.setText(userSubAdmin+ ", "+ userAdmin);
            }
            else {
                userSubAdmin = "Unknown";
                locationdisplay.setText(userState);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        combined_Address = userAddress + ", " + userSubAdmin;
        String address =combined_Address;
        //mEditor.putString(getString(R.string.stored_address), address );
        // mEditor.commit();
        Log.d(TAG, "The address: "+ address);




        casetype = (Spinner) root.findViewById(R.id.caseSpinner);
        casetype.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, caseList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        casetype.setAdapter(aa);

        phone = (EditText) root.findViewById(R.id.phoneNumber);
        victimNumber = (EditText) root.findViewById(R.id.number_of_victms);
        alert = (LinearLayout) root.findViewById(R.id.alert);
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (victimNumber.getText().toString().isEmpty() || phone.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Fill empty fields", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onClick: " + addressline1+addressline2+addressline3+addressline4+addressline5);
                }else{
                    postAlert();
                }
            }
        });

        return root;
    }

    private void postAlert() {
        showDialog();
        AndroidNetworking.post("https://ambulance.schoolofsalesmanship.com/api/log-emergency")
                .addBodyParameter("caller_name", "General Name")
                .addBodyParameter("caller_phone", phone.getText().toString())
                .addBodyParameter("total_injured", victimNumber.getText().toString())
                .addBodyParameter("relation_to_injured", "null")
                .addBodyParameter("address", combined_Address)
                .addBodyParameter("em_long", "3768")
                .addBodyParameter("em_lat", "3768")
                .addBodyParameter("ambulance_required", "Ambulance")
                .addBodyParameter("emergency_type", casetype.getSelectedItem().toString())
                .addBodyParameter("notes", "null")
                .addHeaders("Authorization","Bearer "+ access)
                .addHeaders("content-type", "application/json")
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
                            switch (status) {
                                case 200:
                                    //success
                                    hideDialog();
                                    Showdialog();
                                    break;
                                 default:
                                    hideDialog();
                                    Toast.makeText(getContext(), "We encountered an error", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getContext(), "fatal error podting emergency.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());

                        } else {

                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }


                });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showDialog () {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog () {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void Showdialog() {
        Button ok;

        mdialog = new Dialog(getContext());
        mdialog.setContentView(R.layout.posted_response);
        mdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = mdialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        ok = mdialog.findViewById(R.id.okay);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.dismiss();
            }
        });
        mdialog.setCancelable(false);
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        mdialog.show();


    }




    }