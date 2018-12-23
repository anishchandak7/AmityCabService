package com.example.anish.amitycabservice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.perf.FirebasePerformance;
//import com.google.firebase.perf.metrics.Trace;

import java.util.HashMap;
import java.util.Map;

//import io.fabric.sdk.android.Fabric;

public class FeedbackActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {

    private static final String CURRENTUSER_KEY="current user";
    private static final String DRIVER_KEY="driver";
    private static final String RATING_KEY="rating";
    private static final String FEEDBACK_KEY="feedback";
    private static final String TAG="UPDATE";
    private Spinner spinner;
    private RatingBar ratingBar;
    private TextView driver_name_feedback;
    String currentUser;
    private EditText feedback;
    private RelativeLayout feedbackLayout;
    private Button sendbutton,back;
    String driver_name;
    String feed_back;
//    Trace mytrace;
//ViewPager viewPager;
    private static final String[] paths={"Select driver","Sudesh","Ramvir","Rahul","Raju","Rajendra","Suresh"};
    ArrayAdapter<String> adapter;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        mytrace= FirebasePerformance.getInstance().newTrace("test_trace");
//        mytrace.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        spinner=findViewById(R.id.spinner);
        driver_name_feedback=findViewById(R.id.feedback_drivername);
        ratingBar=findViewById(R.id.ratingBar);
        feedback=findViewById(R.id.feedback_edittext);
        feedbackLayout=findViewById(R.id.feedback_layout);
        sendbutton=findViewById(R.id.send_btn);
        //back=findViewById(R.id.back);

//        Fabric.with(this, new Crashlytics());
        ratingBar.setMax(5);
        feed_back=feedback.getText().toString();
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(feed_back==null)
                {
                    Toast.makeText(FeedbackActivity.this,"Please give some feedback !",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    saveFeedback(driver_name);
                }
            }
        });
        currentUser=getIntent().getStringExtra("current_user");
  //      viewPager=findViewById(R.id.ViewPager);
        adapter=new ArrayAdapter<>(FeedbackActivity.this,android.R.layout.simple_dropdown_item_1line,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        switch (position)
        {
            case 0 :
            {
                feedbackLayout.setVisibility(View.INVISIBLE);
                feedbackLayout.setEnabled(false);
            }
            break;

            case 1 :
            {
                feedbackLayout.setVisibility(View.VISIBLE);
                feedbackLayout.setEnabled(true);
                driver_name=paths[position].toString();
                driver_name_feedback.setText(driver_name);
            }
break;

            case 2 :
            {
                feedbackLayout.setVisibility(View.VISIBLE);
                feedbackLayout.setEnabled(true);
                driver_name=paths[position].toString();
                //saveFeedback(driver_name);
                driver_name_feedback.setText(driver_name);
            }
break;

            case 3 :
            {
                feedbackLayout.setVisibility(View.VISIBLE);
                feedbackLayout.setEnabled(true);
                driver_name=paths[position].toString();
                //saveFeedback(driver_name);

                driver_name_feedback.setText(driver_name);
            }

break;
            case 4 :
            {
                feedbackLayout.setVisibility(View.VISIBLE);
                feedbackLayout.setEnabled(true);
                driver_name=paths[position].toString();
                //saveFeedback(driver_name);

                driver_name_feedback.setText(driver_name);
            }

break;
            case 5 :
            {
                feedbackLayout.setVisibility(View.VISIBLE);
                feedbackLayout.setEnabled(true);
                 driver_name=paths[position].toString();
                //saveFeedback(driver_name);

                driver_name_feedback.setText(driver_name);
            }

break;
            case 6 :
            {
                feedbackLayout.setVisibility(View.VISIBLE);
                feedbackLayout.setEnabled(true);
                 driver_name=paths[position].toString();

                driver_name_feedback.setText(driver_name);
                //saveFeedback(driver_name);
            }
            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        feedbackLayout.setVisibility(View.INVISIBLE);
        feedbackLayout.setEnabled(false);
    }

    public void saveFeedback(String driver_name)
    {
        int user_ratings=ratingBar.getNumStars();
        Map<String,Object> setData=new HashMap<>();
        setData.put(CURRENTUSER_KEY,currentUser);
        setData.put(DRIVER_KEY,driver_name);
        setData.put(RATING_KEY,user_ratings);
        setData.put(FEEDBACK_KEY,feedback.getText().toString());

        FirebaseFirestore feedbackCollection=FirebaseFirestore.getInstance();
        feedbackCollection.collection("Feedback_Data").document(currentUser).set(setData).addOnSuccessListener(FeedbackActivity.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"FEEDBACK SAVED SUCCESSFULLY!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onStop() {
  //      mytrace.stop();
        super.onStop();
    }
}
