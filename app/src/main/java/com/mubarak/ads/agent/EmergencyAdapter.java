package com.mubarak.ads.agent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.mubarak.ads.R;
import com.mubarak.ads.authentication.AgentLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.ViewHolder> {

    private List <EmergencyList> listitems;
    private Context context;
    public static final String TAG = "Emergency";
    private String access = AgentLogin.accesskey;

    public EmergencyAdapter(List<EmergencyList> listitems, int list_item, Context context) {
        this.listitems = listitems;
        this.context = context;

    }

    @NonNull
    @Override
    public EmergencyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.emergencylist, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyAdapter.ViewHolder holder, int position) {
        final EmergencyList listHomeItem = listitems.get(position);

        holder.name.setText(listHomeItem.getName());
        holder.location.setText(listHomeItem.getLocation());
        holder.type.setText(listHomeItem.getType());
        holder.number.setText(listHomeItem.getNumber());
        holder.time.setText(listHomeItem.getTime());

        holder.tag = listHomeItem.getTag();
        holder.phone =  listHomeItem.getName();

        holder.id = listHomeItem.getId();

    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, location ,type, number, time;
        private String tag, callername, phone, id;
        private Button activate;
        private ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.pendingName);
            location = itemView.findViewById(R.id.pendinglocation);
            type = itemView.findViewById(R.id.pendingType);
            time = itemView.findViewById(R.id.timeStamp);
            number = itemView.findViewById(R.id.pendingNumber);
            activate = itemView.findViewById(R.id.pendingmakeActive);

            progressBar = itemView.findViewById(R.id.activateProgress);

            activate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AndroidNetworking.post("https://ambulance.schoolofsalesmanship.com/api/status-update")
                            .setPriority(Priority.LOW)
                            .addHeaders("Authorization","Bearer "+ access)
                            .addHeaders("content-type", "application/json")
                            .addBodyParameter("emergency_id", id)
                            .addBodyParameter("em_status", "active")
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    Log.d(TAG, "onResponse: " + response);
                                    try {
                                        JSONObject jObj = new JSONObject(response);
                                        int status = jObj.getInt("status");
                                        switch (status) {
                                            case 200:
                                                progressBar.setVisibility(View.GONE);
                                                Intent n_act = new Intent(context, ActiveEmergency.class);
                                                n_act.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(n_act);
                                                break;
                                            default:
                                                Toast.makeText(context, "Error deleting emergency", Toast.LENGTH_LONG).show();
                                                Log.d(TAG, "onResponse: " + "an error occurred");
                                                break;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError error) {
                                    progressBar.setVisibility(View.GONE);
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
            });


        }

    }
}
