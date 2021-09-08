package com.mubarak.ads.agent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.material.snackbar.Snackbar;
import com.mubarak.ads.R;
import com.mubarak.ads.authentication.AgentLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ActiveAdapter extends RecyclerView.Adapter<ActiveAdapter.ViewHolder> {

    private List<ActiveList> listitems;
    private Context context;
    public static final String TAG = "Active Emergency";
    private String access = AgentLogin.accesskey;

    public ActiveAdapter(List<ActiveList> listitems, int list_item, Context context) {
        this.listitems = listitems;
        this.context = context;

    }

    @NonNull
    @Override
    public ActiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.activelist, parent, false);


        return new ActiveAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveAdapter.ViewHolder holder, int position) {
        final ActiveList listHomeItem = listitems.get(position);

        holder.name.setText(listHomeItem.getName());
        holder.location.setText(listHomeItem.getLocation());
        holder.type.setText(listHomeItem.getType());
        holder.number.setText(listHomeItem.getNumber());
        holder.update.setText(listHomeItem.getTime());

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

        private TextView name, location ,type, number, update;
        private String tag, callername, phone, id;
        private Button call, delete;
        private ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.activeName);
            location = itemView.findViewById(R.id.activelocation);
            type = itemView.findViewById(R.id.activeType);
            update = itemView.findViewById(R.id.updatedtime);
            number = itemView.findViewById(R.id.activeNumber);
            call = itemView.findViewById(R.id.call);
            delete = itemView.findViewById(R.id.delete);
            progressBar = itemView.findViewById(R.id.deleteprogress);

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete
                    AndroidNetworking.get("https://ambulance.schoolofsalesmanship.com/api/delete-emergency/"+id)
                            .setPriority(Priority.LOW)
                            .addHeaders("Authorization","Bearer "+ access)
                            .addHeaders("content-type", "application/json")
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
                                                Intent n_act = new Intent(context, GetEmergency.class);
                                                n_act.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(n_act);
                                                progressBar.setVisibility(View.GONE);
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
