package com.example.anish.amitycabservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.perf.FirebasePerformance;
//import com.google.firebase.perf.metrics.Trace;

import java.util.Objects;

import javax.annotation.Nullable;

//import io.fabric.sdk.android.Fabric;

public class BookingsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView driver_name_tv,address_tv,timmings_tv,driver_contact,bookingstatus;
    private Button backButton;
    private CollectionReference bookingsCollectionReference= FirebaseFirestore.getInstance().collection("Bookings");
    private CollectionReference driversCollectionReference= FirebaseFirestore.getInstance().collection("Drivers_Details");
    String currentUser;
  //  Trace mytrace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      //  mytrace= FirebasePerformance.getInstance().newTrace("test_trace");
       // mytrace.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        driver_name_tv=findViewById(R.id.driver_name_tv);
        address_tv=findViewById(R.id.location_tv);
        driver_contact=findViewById(R.id.driver_contact_tv);
        bookingstatus=findViewById(R.id.bookingStatus);
        timmings_tv=findViewById(R.id.timming_tv);
        //backButton=findViewById(R.id.back_button);
        //backButton.setOnClickListener(this);

       // Fabric.with(this, new Crashlytics());
        Intent intent=getIntent();
        currentUser=intent.getStringExtra("current_user");

        Task<QuerySnapshot> querySnapshotTask=bookingsCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots!=null)
                {
                    queryDocumentSnapshots.getQuery().whereEqualTo("email",currentUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if(e==null)
                            {
                                if(queryDocumentSnapshots.getDocuments().size()>0) {
                                    String drivername = Objects.requireNonNull(queryDocumentSnapshots).getDocuments().get(0).getString("drivername");
                                    String timings = queryDocumentSnapshots.getDocuments().get(0).getString("timing");
                                    String booking_status = queryDocumentSnapshots.getDocuments().get(0).getString("booking_status");
                                    Task<QuerySnapshot> drivertask = driversCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot driversqueryDocumentSnapshots) {
                                            if (driversqueryDocumentSnapshots != null) {
                                                driversqueryDocumentSnapshots.getQuery().whereEqualTo("driver_name", drivername).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        try {
                                                            if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                                                long contact = queryDocumentSnapshots.getDocuments().get(0).getLong("driver_contact");
                                                                String drivercontact = String.valueOf(contact);
                                                                driver_name_tv.setText(drivername);
                                                                driver_contact.setText(drivercontact);
                                                                timmings_tv.setText(timings);
                                                                bookingstatus.setText(booking_status);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else
                                {
                                    return;
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStop() {
       // mytrace.stop();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
    /*  if(v.getId()==R.id.back_button) {
          Intent intent = new Intent(getApplicationContext(), MapsMainActivity.class);
          startActivity(intent);
          finish();
      }
      */
    }
}
