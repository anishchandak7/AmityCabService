 package com.example.anish.amitycabservice;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

//import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
//import com.google.firebase.perf.FirebasePerformance;
//import com.google.firebase.perf.metrics.Trace;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

//import io.fabric.sdk.android.Fabric;

 public class DriverListActivity extends AppCompatActivity {

    Integer imgid=R.drawable.profile_pic;
    String driver;
FirebaseAuth auth;
private static final String DRIVERNAME_KEY="drivername";
private static final String TAG="UPDATE";
//Trace mytrace;
private CollectionReference driversDetailscollectionReference=FirebaseFirestore.getInstance().collection("Drivers_Details");
private CollectionReference userDatacollectionReference=FirebaseFirestore.getInstance().collection("User_Data");
private final String OBJECT_KEY="document_keys";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
  //      mytrace= FirebasePerformance.getInstance().newTrace("test_trace");
  //      mytrace.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_list);

        auth=FirebaseAuth.getInstance();
        final ListView driverList= findViewById(R.id.drivers_listview);

    //    Fabric.with(this, new Crashlytics());

        String [] drivername = new String[6];

            Task<QuerySnapshot> query = driversDetailscollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot DocumentSnapshots) {
                    driversDetailscollectionReference.addSnapshotListener(DriverListActivity.this, new EventListener<QuerySnapshot>() {
                        @SuppressLint("LogNotTimber")
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if(e==null&&queryDocumentSnapshots!=null)
                            {
                                Long seats[]= new Long[6];
                                String [] driverexp=new String[6];
                                for(int i=0;i<queryDocumentSnapshots.getDocuments().size();i++) {
                                    Log.d("documents", queryDocumentSnapshots.getDocuments().toString());
                                    Log.d("id",queryDocumentSnapshots.getDocuments().get(i).getId());
                                    drivername[i]=queryDocumentSnapshots.getDocuments().get(i).getString("driver_name");
                                    seats[i]= (Long) queryDocumentSnapshots.getDocuments().get(i).get("Seats_avail");
                                    driverexp[i]=queryDocumentSnapshots.getDocuments().get(i).getString("experience");
                                }
                                CustomListView customListView = new CustomListView(DriverListActivity.this, imgid, drivername, driverexp, seats);
                                driverList.setAdapter(customListView);
                            }
                        }
                    });
                }
            });

        driverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {

                setName(drivername[position]);
          //      setSeats(position);
                //String user_data_id=getIntent().getStringExtra("User_data_id");
                //Log.d("User_data_id",user_data_id);
                Intent intent=getIntent();
                    Map<String,Object> dataToSave=new HashMap<>();
                    dataToSave= (Map<String, Object>) intent.getSerializableExtra("dataToSave");
                    String email=intent.getStringExtra("Email");
                    dataToSave.put(DRIVERNAME_KEY,drivername[position]);
                    userDatacollectionReference.document(email).set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.d("Success_message","user data updated successfully!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

                    Task<QuerySnapshot> documentReference=driversDetailscollectionReference.get().addOnSuccessListener(DriverListActivity.this,new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.isEmpty())
                            {
                                return;
                            }
                            else
                            {
                                Log.d("SIZE", String.valueOf(queryDocumentSnapshots.size()));
                                Log.d("DOC_ID", String.valueOf(queryDocumentSnapshots.getDocuments().get(position).get("Seats_avail")));
                                String doc_path=queryDocumentSnapshots.getDocuments().get(position).getId();
                                long left_seats= (long) queryDocumentSnapshots.getDocuments().get(position).get("Seats_avail");
                                left_seats--;
                                DocumentReference reference=FirebaseFirestore.getInstance().collection("Drivers_Details").document(doc_path);
                                reference.update("Seats_avail",left_seats).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("SUCCESS_MSG","seats updated successfully!");
                                        String username=getIntent().getStringExtra("username");
                                        String email= Objects.requireNonNull(auth.getCurrentUser().getEmail()).toString();
                                        Intent intent=new Intent(getApplicationContext(),MapsMainActivity.class);
                                        intent.putExtra("user_name",username);
                                        intent.putExtra("Email",email);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                }
        });
    }

     @Override
     protected void onStop() {
      //  mytrace.stop();
        super.onStop();
     }

     public void setName(String drivername)
    {
        driver=drivername;
    }
    public String getName()
    {
        return driver;
    }
}
