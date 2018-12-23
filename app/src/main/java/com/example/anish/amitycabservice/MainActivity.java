package com.example.anish.amitycabservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button signin;

    private FirebaseAuth auth;
    private static final String TAG="UPDATE";
    private CollectionReference userDataCollectionReference= FirebaseFirestore.getInstance().collection("User_Data");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO GETTING THE SOURCE
        signin= findViewById(R.id.signin_btn);

        auth=FirebaseAuth.getInstance();

        //TODO SETTING ONCLICKLISTENER
        signin.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.signin_btn) {
            if(auth.getCurrentUser()!=null)
            {
                SharedPreferences preferences=getApplicationContext().getSharedPreferences("usernm",MODE_PRIVATE);
                //auth.getCurrentUser().getDisplayName().toString();
                String email= Objects.requireNonNull(auth.getCurrentUser().getEmail()).toString();
                Log.d(TAG,email);
                Task<QuerySnapshot> task=userDataCollectionReference.get().addOnSuccessListener(MainActivity.this, new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots!=null)
                        {
                            Log.d(TAG, String.valueOf(queryDocumentSnapshots.getDocuments().size()));
                            queryDocumentSnapshots.getQuery().whereEqualTo("Email",email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot userDataSnapshots) {
                                    if(userDataSnapshots!=null)
                                    {
                                        Log.d(TAG, String.valueOf(userDataSnapshots.getDocuments().get(0)));
                                        String username=userDataSnapshots.getDocuments().get(0).getString("username");
                                        Intent intent = new Intent(getApplicationContext(), MapsMainActivity.class);
                                        intent.putExtra("Email", email);
                                        intent.putExtra("user_name", username);
                                        startActivity(intent);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                });
            }
            else {
                Intent signin = new Intent(getApplicationContext(), SignIn_Activity.class);
                startActivity(signin);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
