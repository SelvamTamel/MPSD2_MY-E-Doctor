package com.example.myedoctor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Patient_health_edu_info extends AppCompatActivity {

    //Variables to store data received from RecyclerAdapter_Health_Edu
    private String title, content,  logoKey;

    //Initialize layout variables
    private ImageView logo;
    private TextView tvTitle, tvContent;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_health_edu_info);

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

        progressBar.setVisibility(View.VISIBLE);

        //Find view by id
        logo = findViewById(R.id.logo_edu_info);
        tvTitle = findViewById(R.id.title_edu_info);
        tvContent = findViewById(R.id.content_edu_info);

        //Receive data
        Intent intent = getIntent();
        title = intent.getExtras().getString("Title");
        content = intent.getExtras().getString("Content");
        logoKey = intent.getExtras().getString("LogoKey");

        /*--Setting the values--*/
        //Set Health Education Title
        tvTitle.setText(title);

        //Set Health Education Content
        content = content.replace("\\n", "\n");
        tvContent.setText(content);

        //Locate user's file location at Firebase Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        //Retrieve health education logo
        StorageReference logoImgRef = storageReference.child("health_edu/" + logoKey + "/logo.png");

        //Set health education logo
        logoImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(logo);
            }
        });

        progressBar.setVisibility(View.GONE);
    }
}