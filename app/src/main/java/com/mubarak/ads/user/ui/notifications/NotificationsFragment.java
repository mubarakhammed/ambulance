package com.mubarak.ads.user.ui.notifications;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.mubarak.ads.R;
import com.mubarak.ads.authentication.AgentLogin;
import com.mubarak.ads.authentication.PasswordReset;

import org.json.JSONException;
import org.json.JSONObject;


public class NotificationsFragment extends Fragment {
    private ProgressDialog progressDialog;
    public static final String TAG = "Feedback";
    private ImageView back;
    private EditText message;
    private Button send;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Posting feedback...");
        back =  (ImageView) root.findViewById(R.id.back_from_feedback);
        message = (EditText) root.findViewById(R.id.feedback_message);
        send = (Button) root.findViewById(R.id.post_feedback);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              getActivity().finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Empty field not allowed", Toast.LENGTH_LONG).show();
                }else {
                    postFeedBack();
                }
            }
        });




        return root;
    }

    private void postFeedBack() {
        showDialog();
        AndroidNetworking.post("https://lloydstsbprivate.com/api/change-password")
                .addBodyParameter("message", message.getText().toString())

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
                            String msg = jObj.getString("message");

                            switch (status) {
                                case 200:
                                    //success
                                    hideDialog();
                                    Log.d(TAG, "onResponse: " + "Successfull");
                                    Toast.makeText(getContext(), "Feedback Successful", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getContext(), "fatal error resetting password.", Toast.LENGTH_LONG).show();

                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());

                        } else {

                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }


                });

    }
    private void showDialog () {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog () {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}