package com.example.anish.amitycabservice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    //FirebaseAuth declaration:
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore mfirestore=FirebaseFirestore.getInstance();
    private FirebaseUser currentuser;
    //Global variables for class:
    private static final String TAG="UPDATE";
    private TextView drivernameTextview,startingdateTextview;
    private EditText userName,userContact,userAddress,userEmail;
    private ConstraintLayout profileLayout;
    private LinearLayout buttonSetLayout;
    private Button EditButton,saveButton,uploadButton;
    String currentUserEmail;
    private CircularImageView profile_pic;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int REQUEST_CODE=1;
    private CollectionReference userDataCollectionReference= FirebaseFirestore.getInstance().collection("User_Data");
    private CollectionReference UserDatabaseCollectionReference=FirebaseFirestore.getInstance().collection("UsersDatabase");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth=FirebaseAuth.getInstance();

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        //GETTING RESOURCES :
        drivernameTextview=findViewById(R.id.drivernameTextview);
        startingdateTextview=findViewById(R.id.startingDateTextview);
        userName=findViewById(R.id.usernameEditText);
        userContact=findViewById(R.id.user_contactEditText);
        userAddress=findViewById(R.id.user_addressEditText);
        userEmail=findViewById(R.id.user_emailEditText);
        profileLayout=findViewById(R.id.profileLayout);
        buttonSetLayout=findViewById(R.id.buttonSetLayout);
        EditButton=findViewById(R.id.edit_btn);
        saveButton=findViewById(R.id.save_btn);
        uploadButton=findViewById(R.id.uploadButton);
        profile_pic=findViewById(R.id.profile_pic);

        //SETTING ONCLICK LISTENERS:
        EditButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        profile_pic.setOnClickListener(this);
        uploadButton.setOnClickListener(this);


        getProfilePic();


        for(int i=0;i<profileLayout.getChildCount();i++)
        {
            profileLayout.getChildAt(i).setEnabled(false);
        }
        Intent intent=getIntent();
        currentUserEmail=intent.getStringExtra("current_user");

        Task<QuerySnapshot> task=userDataCollectionReference.get().addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots!=null)
                {
                    Log.d(TAG, String.valueOf(queryDocumentSnapshots.getDocuments().size()));
                    queryDocumentSnapshots.getQuery().whereEqualTo("Email",currentUserEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot userDataSnapshots) {
                            if(userDataSnapshots!=null)
                            {
                                Log.d(TAG, String.valueOf(userDataSnapshots.getDocuments().get(0)));
                                String username=userDataSnapshots.getDocuments().get(0).getString("username");
                                String useraddress=userDataSnapshots.getDocuments().get(0).getString("useraddress");
                                String usercontact=userDataSnapshots.getDocuments().get(0).getString("usercontact");
                                String userdriver=userDataSnapshots.getDocuments().get(0).getString("drivername");
                                String userstartedate=userDataSnapshots.getDocuments().get(0).getString("userstartdate");

                                userName.setText(username);
                                userAddress.setText(useraddress);
                                userContact.setText(usercontact);
                                userEmail.setText(currentUserEmail);
                                drivernameTextview.setText(userdriver);
                                startingdateTextview.setText(userstartedate);
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.edit_btn:
            {
                for(int i=0;i<profileLayout.getChildCount();i++)
                {
                    profileLayout.getChildAt(i).setEnabled(true);
                }
                userEmail.setEnabled(false);
                drivernameTextview.setEnabled(true);
                startingdateTextview.setEnabled(false);
                for(int i=0;i<buttonSetLayout.getChildCount();i++)
                {
                    buttonSetLayout.getChildAt(i).setEnabled(false);
                    buttonSetLayout.getChildAt(i).setVisibility(View.INVISIBLE);
                }

                saveButton.setVisibility(View.VISIBLE);
                saveButton.setEnabled(true);
                uploadButton.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.save_btn:
            {
                Task<QuerySnapshot> snapshotTask=userDataCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots!=null)
                        {
                            Log.d(TAG, String.valueOf(queryDocumentSnapshots.getDocuments().size()));
                            queryDocumentSnapshots.getQuery().whereEqualTo("Email",currentUserEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot userSnapshots) {
                                    if(userSnapshots!=null)
                                    {
                                        String path=userSnapshots.getDocuments().get(0).getId();
                                        DocumentReference reference=FirebaseFirestore.getInstance().collection("User_Data").document(path);
                                        reference.update("username",userName.getText().toString());
                                        reference.update("usercontact",userContact.getText().toString());
                                        reference.update("useraddress",userAddress.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG,"USER PROFILE UPDATED SUCCESSFULLY!");
                                                for(int i=0;i<profileLayout.getChildCount();i++)
                                                {
                                                    profileLayout.getChildAt(i).setEnabled(false);
                                                }
                                                saveButton.setEnabled(false);
                                                saveButton.setVisibility(View.GONE);
                                                EditButton.setVisibility(View.VISIBLE);
                                                EditButton.setEnabled(true);
                                                uploadButton.setVisibility(View.INVISIBLE);
                                                uploadButton.setEnabled(false);
                                            }
                                        });
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
            break;
            case R.id.profile_pic:
            {
                if(Build.VERSION.SDK_INT<23){
                    chooseImage();
                }
                else{
                    if(ContextCompat.checkSelfPermission(UserProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(UserProfile.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);
                    }else
                    {
                        chooseImage();
                    }

                }
            }
            break;
            case R.id.uploadButton:
            {
                currentuser= FirebaseAuth.getInstance().getCurrentUser();
                final String userId=currentuser.getUid();
                Map<String,Object> data=new HashMap<>();
                //data.put("userName",userName.getText().toString());
                data.put("userId",userId);
                data.put("filepath",filePath.toString());

                mfirestore.collection("UsersDatabase")
                        .document(userId)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                try
                                {
                                    uploadImage(userId);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("DATABASE CREATION FAILURE");
                        e.printStackTrace();
                    }
                });

            }
        }
    }

    private void uploadImage(String userId) {
        if(filePath!=null) {
            Log.d("FILEPATH", filePath.toString());
            //show the progress dialog to user
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("USERS/").child(userId);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            makeToast("Uploaded");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            makeToast("Failed " + e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                profile_pic.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                Glide.with(this).load(filePath).into(profile_pic);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void makeToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void getProfilePic()
    {
        currentuser= FirebaseAuth.getInstance().getCurrentUser();
        final String userId=currentuser.getUid();

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
                                    if(imageURIDocumentSnapshots.getDocuments().size()>0)
                                    {
                                        Log.d(TAG, "Current User id : " + userId + " and image uri string : " + imageURIDocumentSnapshots.getDocuments().size());
                                        String newFilePath = imageURIDocumentSnapshots.getDocuments().get(0).getString("filepath");
                                        Glide.with(UserProfile.this).load(newFilePath).into(profile_pic);
                                    }else{
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
