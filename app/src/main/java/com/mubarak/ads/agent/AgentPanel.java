package com.mubarak.ads.agent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mubarak.ads.R;

public class AgentPanel extends AppCompatActivity {

    private LinearLayout pending, active, profile, searh;
    private Button exit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_panel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }

        pending = (LinearLayout) findViewById(R.id.pending);
        active = (LinearLayout) findViewById(R.id.active);
        profile = (LinearLayout) findViewById(R.id.profile);
        searh = (LinearLayout) findViewById(R.id.search);
        exit = (Button) findViewById(R.id.exit);

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pending = new Intent(AgentPanel.this, GetEmergency.class);
                startActivity(pending);
            }
        });
        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pending = new Intent(AgentPanel.this, ActiveEmergency.class);
                startActivity(pending);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pending = new Intent(AgentPanel.this, AgentProfile.class);
                startActivity(pending);
            }
        });

        searh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pending = new Intent(AgentPanel.this, MapsActivity.class);
                startActivity(pending);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
                finishAffinity();
            }
        });

    }
}