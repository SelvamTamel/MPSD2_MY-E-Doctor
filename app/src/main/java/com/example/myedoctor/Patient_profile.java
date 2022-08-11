package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Patient_profile extends AppCompatActivity implements View.OnClickListener{

    private Uri imageUri;

    //Firebase
    private FirebaseUser user;
    private DatabaseReference userReference;
    private FirebaseAuth auth;
    private StorageReference storageReference, profileImgRef;
    private String userId;

    //Patient Profile variables
    private Button signOut, update;
    private TextView editProfileImg;
    private ImageView profileImage;
    private ImageButton btnBack;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_profile);

        profileImage = findViewById(R.id.patient_profileImg);

        editProfileImg = findViewById(R.id.patient_editProfileImg);
        editProfileImg.setOnClickListener(this);

        signOut = findViewById(R.id.btn_patient_signOut);
        signOut.setOnClickListener(this);

        update = findViewById(R.id.btn_patient_update);
        update.setOnClickListener(this);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        final TextView fullNameTextView = findViewById(R.id.patient_profileName);
        final TextView emailTextView = findViewById(R.id.tv_patientEmail);
        final TextView phoneNumberTextView = findViewById(R.id.tv_patientPhone);
        final TextView genderTextView = findViewById(R.id.tv_patientGender);
        final TextView dobTextView = findViewById(R.id.tv_patientDOB);


        //Locate user's credential location at Firebase Real-Time DB
        user = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("patients");
        userId = user.getUid();

        //Locate user's file location at Firebase Cloud Storage
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Retrieve user's profile image from Cloud Storage
        profileImgRef = storageReference.child("patients/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        profileImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        //Retrieve user's credentials from Real-Time DB
        userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Create an instance to store data retrieved from Firebase
                Class_Patient classPatientProfile = snapshot.getValue(Class_Patient.class);

                if(classPatientProfile != null){
                    String email = classPatientProfile.email;
                    String fullName = classPatientProfile.fullName;
                    String phoneNumber = classPatientProfile.phoneNumber;
                    String gender = classPatientProfile.gender;
                    String dob = classPatientProfile.dob;

                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);
                    phoneNumberTextView.setText(phoneNumber);
                    genderTextView.setText(gender);
                    dobTextView.setText(dob);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Patient_profile.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.patient_editProfileImg:
                editProfileImg.requestFocusFromTouch();
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startForResult.launch(openGalleryIntent);
                break;
            case R.id.btn_patient_update:
                uploadImageToFirebase();
                break;
            case R.id.btn_patient_signOut:
                auth.signOut();
                Intent signOutIntent = new Intent (Patient_profile.this, Main_SignIn.class);
                signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signOutIntent);
                break;
        }
    }

    //Newer way to use activity for result
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>(){
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() != null) {
                if (result.getResultCode() == RESULT_OK) {
                    imageUri = result.getData().getData();
                    profileImage.setImageURI(imageUri);
                }
            }else {
                Toast.makeText(Patient_profile.this, "No Changes Has Been Made", Toast.LENGTH_LONG).show();
            }
        }
    });

    //Here is commented because startActivityFor Result is deprecated on newer android
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                profileImage.setImageURI(imageUri);

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadImageToFirebase(imageUri);
                    }
                });
            }
        }else {
            Toast.makeText(Patient_profile.this, "No Changes Has Been Made", Toast.LENGTH_LONG).show();
        }
    }*/

    private void uploadImageToFirebase() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading profile image...");
        progressDialog.setProgress(0);
        progressDialog.show();

        //Upload image to firebase storage
        final StorageReference fileRef = storageReference.child("patients/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        Toast.makeText(Patient_profile.this,"Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Patient_profile.this, "Failed to Upload Image.", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int currentProgress = (int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }
}