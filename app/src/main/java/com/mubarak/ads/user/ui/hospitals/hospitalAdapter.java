package com.mubarak.ads.user.ui.hospitals;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mubarak.ads.R;

import java.util.List;


public class hospitalAdapter  extends RecyclerView.Adapter<hospitalAdapter.ViewHolder>  {

    private List<hospitalList> listitems;
    private Context context;


    public hospitalAdapter(List<hospitalList> listitems, int list_item, Context context) {
        this.listitems = listitems;
        this.context = context;
    }

    @NonNull
    @Override
    public hospitalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.hospitallist, parent, false);

        return new hospitalAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull hospitalAdapter.ViewHolder holder, int position) {

        final hospitalList listhospitalItem = listitems.get(position);

      holder.name.setText(listhospitalItem.getHospitalname());
        holder.mail.setText(listhospitalItem.getHospitalmail());
        holder.type.setText(listhospitalItem.getHospitaltype());
        holder.address.setText(listhospitalItem.getHospitaladdress());
        holder.speciality.setText(listhospitalItem.getHospitalspecility());

        holder.phone = listhospitalItem.getHospitalphone();

    }



    @Override
    public int getItemCount() {
        return listitems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        private TextView mail;
        private TextView type;
        private TextView address;
        private TextView speciality;
        private Button call;
        private String phone;

        private LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.hospitalname);
            address = itemView.findViewById(R.id.hospitallocation);
            mail = itemView.findViewById(R.id.hospitalemail);
            speciality = itemView.findViewById(R.id.specific);
            type = itemView.findViewById(R.id.type);
            call = itemView.findViewById(R.id.callhospital);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                }
            });

            linearLayout = itemView.findViewById(R.id.hospital_layout);
        }
    }
}
