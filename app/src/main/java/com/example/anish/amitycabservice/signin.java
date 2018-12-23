package com.example.anish.amitycabservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.Objects;

@SuppressLint("ValidFragment")
class signin extends Fragment implements View.OnClickListener{


    private EditText user_email,user_pass;
    private Button signin;
    private FirebaseAuth auth;
    private TextView forgotPass;
    String email;
    //ParseObject activeuserobj;
//    Trace myTrace;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
  //      myTrace = FirebasePerformance.getInstance().newTrace("test_trace");
  //      myTrace.start();

        View view= inflater.inflate(R.layout.signin,container,false);

        auth=FirebaseAuth.getInstance();
        user_email= view.findViewById(R.id.email_signin_edittext);
        user_pass= view.findViewById(R.id.pass_signin_edittext);
        signin= view.findViewById(R.id.signin_button);
        forgotPass= view.findViewById(R.id.forgotTextview);
        signin.setOnClickListener(this);
        forgotPass.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.signin_button) {
            email = user_email.getText().toString();
            final String password = user_pass.getText().toString();


            if (TextUtils.isEmpty(email)) {
                Toast.makeText(v.getRootView().getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(v.getRootView().getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) Objects.requireNonNull(getContext()), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        if (password.length() < 6) {
                            user_pass.setError(getString(R.string.minimum_password));
                        } else {
                            Toast.makeText(v.getRootView().getContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        setActiveUser(email);
                    }
                }
            });
        }
        else if(v.getId()==R.id.forgotTextview)
        {
            Intent intent=new Intent(getActivity(),ForgotPassActivity.class);
            startActivity(intent);
        }
    }


    private void setActiveUser(String email)
    {
        Intent intent=new Intent(getActivity(),MapsMainActivity.class);
        intent.putExtra("Email",email);
        startActivity(intent);
    }

    @Override
    public void onStop() {
    //    myTrace.stop();
        super.onStop();
    }
}
