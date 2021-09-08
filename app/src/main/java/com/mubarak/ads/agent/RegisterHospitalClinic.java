package com.mubarak.ads.agent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mubarak.ads.AppSingleton;
import com.mubarak.ads.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterHospitalClinic extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ProgressDialog progressDialog;
    public  static  final String TAG = "Register hospital";
    Spinner type;
    String typeList [] = {"Hospital","Clinic"};
    private EditText email, name,  speciality, phone, location;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_hospital_clinic);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        email = (EditText) findViewById(R.id.hemail);
        name = (EditText) findViewById(R.id.hname);
        speciality = (EditText) findViewById(R.id.hspecialty);
        phone = (EditText) findViewById(R.id.hphone);
        location = (EditText) findViewById(R.id.hlocation);
        type = (Spinner) findViewById(R.id.htype);
        add = (Button) findViewById(R.id.registerh);

        type.setOnItemSelectedListener(RegisterHospitalClinic.this);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, typeList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(aa);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerHospital(email.getText().toString(), name.getText().toString(), type.getSelectedItem().toString(),speciality.getText().toString(),phone.getText().toString(), location.getText().toString());

            }
        });






    }
    private void registerHospital(final String email, final String name, final String type, final String speciality, final String phone, final String location) {
        String cancel_req_tag = "Hospital";
        progressDialog.setMessage("Please wait");
        showDialog();
        String url = "https://ehc9ja.herokuapp.com/hospitals/add";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                //   hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    switch(status)
                    {
                        case "ok":
                            hideDialog();
                            showSuccessAlert();
                            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();
                            break;
                        case "error":
                            hideDialog();
                            Toast.makeText(getApplicationContext(), "Incorrect Credentials", Toast.LENGTH_LONG).show();
                        default:
                            hideDialog();
                            Toast.makeText(getApplicationContext(), "Login not Successful check entries", Toast.LENGTH_LONG).show();
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Fatal error logging in, check internet", Toast.LENGTH_LONG).show();
                // hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("phone", phone);
                params.put("type", type);
                params.put("name", name);
                params.put("specialty", speciality);
                params.put("location", location);
                return params;
            }
        };

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);


    }





    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showDialog(){
        if (!progressDialog.isShowing())
            progressDialog.show();

    }

    private void hideDialog(){
        if (progressDialog.isShowing())
            progressDialog.dismiss();

    }
    public void showSuccessAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterHospitalClinic.this);
        builder.setTitle("Registration Status")
                .setMessage("Registration Successful!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        email.getText().clear();
                        name.getText().clear();
                        phone.getText().clear();
                        location.getText().clear();
                        speciality.getText().clear();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}



