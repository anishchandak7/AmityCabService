package com.example.anish.amitycabservice;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.RecenterButton;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//import io.fabric.sdk.android.Fabric;

import static java.lang.String.valueOf;

public class MapsMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MenuItem.OnMenuItemClickListener, PermissionsListener,
        MapboxMap.OnMapClickListener,LocationEngineListener,
        OnMapReadyCallback{
private static final String TAG="UPDATE";
private static final String EMAIL_KEY="email";
private static final String DATE_TIME_KEY="date_time";
private static final String BOOKING_STATUS_KEY="booking_status";
private static final String DRIVER_KEY="drivername";
private static final String TIMING_KEY="timing";

    private com.mapbox.mapboxsdk.maps.MapView mMapView;
    private RecenterButton recenterButton;
    private DrawerLayout drawer;
    private FirebaseAuth auth;
    private NavigationView navigationView;
    private Menu menu;
    private Button bookseatbtn;
    private GridLayout gridLayout;
    private TextView avail_seats;
    private boolean booked=false;
    private Button cancelbtn;
    private TextView todayDriver;
    private int buttontapped;
    private String booking_status= "not booked";
    private long seat;
    private String timming;
    private String driver_name;
    private MapboxMap map;
    private CircularImageView userprofilepic;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location driverlocation;
    String user_name;
    String email;
    private int buttontappedchanged;
    SharedPreferences preferences;
    TextView username_header;
    String set_email;
    private FirebaseUser currentuser;
    private CollectionReference bookingsCollectionReference=FirebaseFirestore.getInstance().collection("Bookings");
    private CollectionReference eveningDriverCollectionReference= FirebaseFirestore.getInstance().collection("Evening_Drivers");
    private CollectionReference UserDatabaseCollectionReference=FirebaseFirestore.getInstance().collection("UsersDatabase");
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.NavigationViewLight);

        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(),"pk.eyJ1IjoiYW5pc2hjaGFuZGFrNyIsImEiOiJjamo4OTBrenAwd2V4M3Z0ZWxtdzFjdGV6In0.58c_eTQjMx3JL0D9eD7NRw");


        setContentView(R.layout.activity_maps_main);

        auth=FirebaseAuth.getInstance();

        LoadPreferences();
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mMapView.onResume();

        //Navigation bar (MENU BAR ) code
        navigationView= findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);


        android.support.v7.widget.Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        recenterButton= findViewById(R.id.recenterBtn);
        recenterButton.show();
        recenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location lastLocation=locationEngine.getLastLocation();
                if(lastLocation!=null) {
                    driverlocation = lastLocation;
                    setCameraPosition(driverlocation);
                }else {
                    locationEngine.addLocationEngineListener(MapsMainActivity.this);
                }
            }
        });
        drawer= findViewById(R.id.draw_layout);


        menu=navigationView.getMenu();

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(MapsMainActivity.this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Secondary menu code

        bookseatbtn= findViewById(R.id.bookseat_btn);
        cancelbtn= findViewById(R.id.cancelbooking_btn);
        gridLayout= findViewById(R.id.timmingsGridLayout);
        todayDriver= findViewById(R.id.driver_name);
        user_name=getIntent().getStringExtra("user_name");
        email= Objects.requireNonNull(auth.getCurrentUser().getEmail()).toString();

        username_header=navigationView.getHeaderView(0).findViewById(R.id.username_textview);
        set_email=getIntent().getStringExtra("Email");
        if(set_email!=null)
        {
            Log.d(TAG,set_email);
            username_header.setText(set_email);
        }
        else
        {
            set_email=auth.getCurrentUser().getEmail().toString();
            //username_header.setText(set_email.toString());
            Log.d(TAG,set_email);

            username_header.setText(set_email);
        }
        preferences=getApplicationContext().getSharedPreferences("email",MODE_PRIVATE);
        preferences.edit().putString("email",email);
        preferences.edit().commit();
        preferences.edit().apply();

        //SETTING PROFILE PIC IN NAVIGATION HEADER
        userprofilepic=navigationView.getHeaderView(0).findViewById(R.id.userprofilepic);
        setprofilepic();
        //SINGLE CLICK EVENT MECHANISM FOR CLICK ANY ITEM IN GRID LAYOUT
        setSingleClickEvent(gridLayout);

        avail_seats= findViewById(R.id.availseats_textview);
        String finalSet_email = set_email;
        if(booked==false&&buttontapped!=-1) {
            bookseatbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsMainActivity.this);
                    builder.setTitle("Confirm ?").setMessage("Do you want to confirm your timming?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO Evening driver is setting up here not morning driver.
                            if (seat != 0) {
                                booked = true;
                                booking_status = "booked";
                                SavePreferences(buttontapped, booked);
                                seat--;
                                updateEveningDriver(seat, timming);
                                updateBookings(finalSet_email, booking_status, driver_name);
                                String seats_String = String.valueOf(seat);
                                avail_seats.setText(seats_String);


                                gridLayout.getChildAt(buttontapped).setBackgroundResource(R.drawable.selectedtimming);
                                gridLayout.setEnabled(false);
                            }
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Booking cancelled", Toast.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"NO TIMMING FOUND!",Toast.LENGTH_SHORT).show();
        }
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seat<8) {
                    if(booked==true){
                        booked=false;
                        booking_status = "cancelled";
                        SavePreferences(buttontapped,booked);
                        driver_name=" ";
                        long s=seat+1;
                        avail_seats.setText(valueOf(s));
                        updateEveningDriver(s, timming);
                        updateBookings(finalSet_email,booking_status,driver_name);
                        gridLayout.getChildAt(buttontapped).setBackgroundResource(R.drawable.timinggroup);
                        gridLayout.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"booking cancelled",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Oops! You currently have no bookings",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Max seat limit is 8",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setSingleClickEvent(GridLayout gridLayout)
    {
        for(int i=0;i<gridLayout.getChildCount();i++)
        {
            Button button=(Button) gridLayout.getChildAt(i);

            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(booked==false) {
                        String timming=  button.getText().toString();
                        buttontapped= finalI;
                            checkForBooking(timming);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Cancel your previous booking",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void SavePreferences(int buttontapped,boolean booked){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tappedButton",buttontapped);
        editor.putBoolean("booked_status",booked);
        editor.commit();   // I missed to save the data to preference here,.
    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        buttontapped=sharedPreferences.getInt("tappedButton",0);
        booked=sharedPreferences.getBoolean("booked_status",false);
    }


public void checkForBooking(String timming)
    {
        Task<QuerySnapshot> task=eveningDriverCollectionReference.get().addOnSuccessListener(MapsMainActivity.this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty())
                {
                    return;
                }else
                {
                    Log.d(TAG, valueOf(queryDocumentSnapshots.getDocuments().size()));
                    queryDocumentSnapshots.getQuery().whereEqualTo("timing",timming).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d(TAG,queryDocumentSnapshots.getDocuments().get(0).getId());
                            String driver_name=queryDocumentSnapshots.getDocuments().get(0).getString("driver_name");
                            long seats=queryDocumentSnapshots.getDocuments().get(0).getLong("seats_avail");
                            String seats_inString= String.valueOf(seats);
                            todayDriver.setText("Driver : " +driver_name);
                            avail_seats.setText(seats_inString);
                            setEveningData(seats,timming,driver_name);
                        }
                    });
                }
            }
        });
    }


    private void updateBookings(String email,String booking_status,String driver_name)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        Log.i("date",currentDateandTime);

        Map<String,Object> setBookings=new HashMap<>();
        setBookings.put(EMAIL_KEY,email);
        setBookings.put(DATE_TIME_KEY,currentDateandTime);
        setBookings.put(BOOKING_STATUS_KEY,booking_status);
        setBookings.put(DRIVER_KEY,driver_name);
        setBookings.put(TIMING_KEY,timming);
        FirebaseFirestore bookingsCollection=FirebaseFirestore.getInstance();
        bookingsCollection.collection("Bookings").document(email).set(setBookings).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Bookings are updated Successfully!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setEveningData(long seat,String timming,String driver_name)
    {
        this.seat=seat;
        this.timming=timming;
        this.driver_name=driver_name;
    }

    private void setActiveUserData(String driver_name)
    {
        this.driver_name=driver_name;
    }
    private void updateEveningDriver(long seat, String timming)
    {
        long seats=seat;
        Task<QuerySnapshot> task=eveningDriverCollectionReference.get().addOnSuccessListener(MapsMainActivity.this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty())
                {
                    return;
                }else
                {
                    //Log.d(TAG, valueOf(queryDocumentSnapshots.getDocuments().size()));
                    queryDocumentSnapshots.getQuery().whereEqualTo("timing",timming).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            try
                            {
                                if(queryDocumentSnapshots.getDocuments().size()>0)
                                {
                                    Log.d(TAG,queryDocumentSnapshots.getDocuments().get(0).getId());
                                    String path=queryDocumentSnapshots.getDocuments().get(0).getId();
                                    DocumentReference eveningdriver_refernce=FirebaseFirestore.getInstance().collection("Evening_Drivers").document(path);
                                    eveningdriver_refernce.update("seats_avail",seats).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG,"EVENING DRIVERS SEATS UPDATED SUCCESSFULLY!");
                                        }
                                    });
                                }
                            }catch (Exception e)
                            {
                                avail_seats.setText("");
                                e.printStackTrace();
                                Toast.makeText(MapsMainActivity.this,"BOOKING CANCELLED!",Toast.LENGTH_SHORT).show();
                            }
                            }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            LoadPreferences();
            super.onBackPressed();

    }

    @Override
    protected void onResume() {
        LoadPreferences();
        super.onResume();
        mMapView.onResume();
    }
    @SuppressLint("MissingPermission")
    @Override
    protected void onStop() {
        SavePreferences(buttontapped,booked);
        //myTrace.stop();
        super.onStop();
        if(locationEngine!=null)
        {

            locationEngine.removeLocationUpdates();
        }

        if(locationLayerPlugin!=null)
        {
            locationLayerPlugin.onStop();
        }
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        SavePreferences(buttontapped,booked);
        super.onDestroy();
        if(locationEngine!=null)
        {

            locationEngine.deactivate();
        }
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        SavePreferences(buttontapped,booked);
        super.onPause();
        mMapView.onPause();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        LoadPreferences();
        super.onStart();
        if(locationEngine!=null)
        {
            locationEngine.activate();
            locationEngine.requestLocationUpdates();
        }
        if(locationLayerPlugin!=null)
        {
            locationLayerPlugin.onStart();
        }
        mMapView.onStart();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    //TODO: WHAT TO DO WHEN PERMISSION IS GRANTED

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout :

            {
                item=menu.findItem(R.id.logout);
                item.setOnMenuItemClickListener(this);
            }
            break;
            case R.id.profile :
            {
                item= menu.findItem(R.id.profile);
                item.setOnMenuItemClickListener(this);
            }
            break;

            case R.id.booking :
            {
                item=menu.findItem(R.id.booking);
                item.setOnMenuItemClickListener(this);
            }break;
            case R.id.feedback :
            {
                 item=menu.findItem(R.id.feedback);
                 item.setOnMenuItemClickListener(this);
            }
        }

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId()==R.id.logout)
        {
            Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_SHORT).show();
            auth.signOut();
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.profile) {
            Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getApplicationContext(),UserProfile.class);
            intent.putExtra("current_user",set_email);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.booking)
        {
            Toast.makeText(getApplicationContext(), "Bookings", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getApplicationContext(),BookingsActivity.class);
            intent.putExtra("current_user",set_email);
            startActivity(intent);
        }else if(item.getItemId()==R.id.feedback)
        {
            Intent intent=new Intent(getApplicationContext(),FeedbackActivity.class);
            intent.putExtra("current_user",set_email);
            startActivity(intent);
        }
        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("book",booked);
        outState.putString("driver",driver_name);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map=mapboxMap;
        enableLocation();
    }

    private void enableLocation()
    {
        if(PermissionsManager.areLocationPermissionsGranted(this))
        {
            //IF it returns true

            initializeLocationEngine();
            initializeLocationLayer();
        }
        else
        {
            //Ask user for persmission
            permissionsManager=new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    private void initializeLocationEngine()
    {
        locationEngine=new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        @SuppressLint("MissingPermission")

        Location lastLocation=locationEngine.getLastLocation();
        if(lastLocation!=null) {
            driverlocation = lastLocation;
            setCameraPosition(driverlocation);
        }else {
            locationEngine.addLocationEngineListener(this);
        }
    }
    @SuppressLint("MissingPermission")
    private void initializeLocationLayer()
    {
        locationLayerPlugin=new LocationLayerPlugin(mMapView,map,locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);

    }

    public void setCameraPosition(Location location)
    {
        map.animateCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom(new com.mapbox.mapboxsdk.geometry.LatLng(location.getLatitude(),location.getLongitude()),13));
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {

        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location!=null)
        {
            driverlocation=location;
            setCameraPosition(driverlocation);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
//Explain why user have to give the permission
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted)
        {
            enableLocation();
        }
    }

    @Override
    public void onMapClick(@NonNull com.mapbox.mapboxsdk.geometry.LatLng point) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    public void setprofilepic()
    {
        currentuser= FirebaseAuth.getInstance().getCurrentUser();
        final String userId=currentuser.getUid();

        Log.d(TAG,"Current user id :"+userId);
        Task<QuerySnapshot> querySnapshotTask=UserDatabaseCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try{
                    if(queryDocumentSnapshots!=null)
                    {
                        Log.d(TAG, String.valueOf(queryDocumentSnapshots.getDocuments().size()));
                        queryDocumentSnapshots.getQuery().whereEqualTo("userId",userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot imageURIDocumentSnapshots) {
                                if(imageURIDocumentSnapshots!=null)
                                {
                                    if(imageURIDocumentSnapshots.getDocuments().size()>0) {
                                        Log.d(TAG, "Current User id : " + userId + " and image uri string : " + imageURIDocumentSnapshots.getDocuments().size());
                                        String newFilePath = imageURIDocumentSnapshots.getDocuments().get(0).getString("filepath");
                                        Glide.with(MapsMainActivity.this).load(newFilePath).into(userprofilepic);
                                    }
                                    else
                                    {
                                        return;
                                    }
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
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
