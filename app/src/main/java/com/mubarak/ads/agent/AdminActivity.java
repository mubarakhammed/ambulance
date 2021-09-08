package com.mubarak.ads.agent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mubarak.ads.R;
import com.mubarak.ads.authentication.AgentRegisteration;

public class AdminActivity extends AppCompatActivity {
    private Button dispatcher, hospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }


        dispatcher = (Button) findViewById(R.id.registerDispatcher);
        hospital = (Button) findViewById(R.id.registerHospital);

        dispatcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dis = new Intent(AdminActivity.this, AgentRegisteration.class);
                startActivity(dis);
            }
        });

        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hos = new Intent(AdminActivity.this, RegisterHospitalClinic.class);
                startActivity(hos);
            }
        });
    }
}