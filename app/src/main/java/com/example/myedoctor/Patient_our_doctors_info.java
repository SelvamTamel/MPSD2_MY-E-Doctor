package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Patient_our_doctors_info extends AppCompatActivity {
    //Variables to store data received from RecyclerAdapter_Our_Doctors
    private String name, bio,  specialise, id;

    //Initialize layout variables
    private CircleImageView profileImg;
    private TextView tvName, tvBio, tvSpecialise;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_our_doctors_info);

        progressBar = findViewById(R.id.progressBar);

        //Back Button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBack.requestFocusFromTouch();
                finish();
            }
        });

        //Visualize progress bar
        progressBar.setVisibility(View.VISIBLE);

        //Find view by id
        profileImg = findViewById(R.id.profile_our_doctors_info);
        tvName = findViewById(R.id.name_our_doctors_info);
        tvSpecialise = findViewById(R.id.specialise_our_doctors_info);
        tvBio = findViewById(R.id.bio_our_doctors_info);

        //Receive data
        Intent intent = getIntent();
        name = intent.getExtras().getString("Name");
        specialise = intent.getExtras().getString("Specialise");
        bio = intent.getExtras().getString("Bio");
        id = intent.getExtras().getString("Id");

        /*--Setting the values--*/
        //Set Doctor Name
        tvName.setText("Dr. " + name);

        //Set Doctor Specialise
        tvSpecialise.setText(specialise);

        //Set Doctor Bio
        bio = bio.replace("\\n", "\n");
        tvBio.setText(bio);

        //Locate user's file location at Firebase Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        //Retrieve health education logo
        StorageReference profileImgRef = storageReference.child("doctors/" + id + "/profile.jpg");

        //Set health education logo
        profileImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImg);
            }
        });

        //Dismiss progress bar
        progressBar.setVisibility(View.GONE);
    }
}
