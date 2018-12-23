package com.example.anish.amitycabservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

@SuppressLint("ValidFragment")
class signup extends Fragment implements View.OnClickListener {


    private static final String USERNAME_KEY="username";

    private static final String EMAIL_KEY="Email";

    private static final String USER_CONTACT_KEY="usercontact";

    private static final String USER_ADDRESS_KEY="useraddress";

    private static final String USER_STARTDATE_KEY="userstartdate";

    private EditText u_name, u_email, u_contact, u_pass, u_cpass, u_address,u_sdate;
    private Button signupbtn;
    private FirebaseAuth auth;
    public String currentDate;
    public  String uaddrss;
    Calendar myCalendar;
    //Trace mytrace;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    //    mytrace= FirebasePerformance.getInstance().newTrace("test_trace");
    //    mytrace.start();

        final View view = inflater.inflate(R.layout.signup, container, false);

        auth = FirebaseAuth.getInstance();

        myCalendar = Calendar.getInstance();
        signupbtn = view.findViewById(R.id.signup_btn);
        u_name = view.findViewById(R.id.name_signup_edittext);
        u_email = view.findViewById(R.id.email_signup_edittext);
        u_contact = view.findViewById(R.id.phone_signup_edittext);
        u_pass = view.findViewById(R.id.pass_signup_edittext);
        u_cpass = view.findViewById(R.id.cpass_signup_edittext);
        u_address = view.findViewById(R.id.address_signup_edittext);
        u_sdate= view.findViewById(R.id.sdate_signup_edittext);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        u_sdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(v.getRootView().getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        signupbtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(final View v) {

        if (v.getId() == R.id.signup_btn) {
            final String email = u_email.getText().toString().trim();
            String password = u_pass.getText().toString().trim();
            final String uname = u_name.getText().toString().trim();
            final String ucontact = u_contact.getText().toString().trim();
            final String uaddrss = u_address.getText().toString().trim();
            String ucpassword = u_cpass.getText().toString().trim();
            final String startingdate = u_sdate.getText().toString().trim();

            //Check if email is not empty
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(v.getRootView().getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }
            //Check if password is not empty
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(v.getRootView().getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }
            //check if password length is not less than 6
            if (password.length() < 6) {
                Toast.makeText(v.getRootView().getContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.equals(ucpassword)) {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener((Activity) Objects.requireNonNull(getContext()), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(v.getRootView().getContext(), "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(v.getRootView().getContext(), "Welcome to Amity Cab Services", Toast.LENGTH_SHORT).show();
                                    currentDate();

                                    Map<String,Object> dataToSave=new HashMap<>();
                                    dataToSave.put(USERNAME_KEY,uname);
                                    dataToSave.put(USER_ADDRESS_KEY,uaddrss);
                                    dataToSave.put(USER_CONTACT_KEY,ucontact);
                                    dataToSave.put(USER_STARTDATE_KEY,startingdate);
                                    dataToSave.put(EMAIL_KEY,email);

                                    Intent intent = new Intent(getActivity(), DriverListActivity.class);
                                    intent.putExtra("Email",email);
                                    intent.putExtra("username",uname);
                                    intent.putExtra("from","signup");
                                    intent.putExtra("dataToSave", (Serializable) dataToSave);
                                    //intent.putExtra("User_data_id",queryDocumentSnapshots.getDocuments().get(0).getId());
                                    startActivity(intent);
                                }
                            }
                        });
            } else {
                Toast.makeText(v.getRootView().getContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStop() {
    //    mytrace.stop();
        super.onStop();
    }

    public void currentDate() {
       /* DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // get current date time with Date()
        Date date = new Date();
        // System.out.println(dateFormat.format(date));
        // don't print it, but save it!
        currentDate=dateFormat.format(date);
       */
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        u_sdate.setText(sdf.format(myCalendar.getTime()));
    }

    public String getcurrentdate() {
        return currentDate;
    }

    public void setU_address(String uaddrss) {
        this.uaddrss = uaddrss;
    }
    public  String getUaddrss()
    {
        return uaddrss;
    }
}
//TODO ADD PARSE SERVER CONNECTIVITY
