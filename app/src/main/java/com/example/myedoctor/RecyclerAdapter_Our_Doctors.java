package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter_Our_Doctors extends RecyclerView.Adapter<RecyclerAdapter_Our_Doctors.MyViewHolder> {

    Context context;
    ArrayList<Class_Our_Doctors> list;
    int displayId;

    public RecyclerAdapter_Our_Doctors(Context context, ArrayList<Class_Our_Doctors> list, int displayId) {
        this.context = context;
        this.list = list;
        this.displayId = displayId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if (displayId == 1){
            v = LayoutInflater.from(context).inflate(R.layout.patient_our_doctors_grid_view, parent, false);
        }else if (displayId == 2 || displayId == 3){
            v = LayoutInflater.from(context).inflate(R.layout.patient_our_doctors_list_view, parent, false);
        }
        return new MyViewHolder(v, displayId);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Locate Doctors credential position
        Class_Our_Doctors ourDoctors_info = list.get(position);

        //Set Doctor Name
        holder.fullName.setText("Dr. " + ourDoctors_info.getFullName());

        //Set Doctor Specialise
        holder.specialise.setText(ourDoctors_info.getSpecialise());

        //Locate doctor's file location at Firebase Cloud Storage
        FirebaseAuth auth = FirebaseAuth.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        //Retrieve doctor's profile image from Cloud Storage
        StorageReference profileImgRef = storageReference.child("doctors/"+ourDoctors_info.getId()+"/profile.jpg");

        //Set Doctor Profile Img
        profileImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.profileImg);
            }
        });

        //When user press either one doctor profile, display the specific doctor info on screen
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (displayId == 3){
                    Intent intent = new Intent(context, Patient_virtual_appt_2.class);

                    //Pass data to another activity
                    intent.putExtra("DoctorId", ourDoctors_info.getId());

                    //Start the activity
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, Patient_our_doctors_info.class);

                    //Pass data to another activity
                    intent.putExtra("Name", ourDoctors_info.getFullName());
                    intent.putExtra("Bio", ourDoctors_info.getBio());
                    intent.putExtra("Specialise", ourDoctors_info.getSpecialise());
                    intent.putExtra("Id", ourDoctors_info.getId());

                    //Start the activity
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImg;
        TextView fullName, specialise;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, int display_id) {
            super(itemView);

            if (display_id == 1){
                profileImg = itemView.findViewById(R.id.profile_our_doctors);
                fullName = itemView.findViewById(R.id.name_our_doctors);
                specialise = itemView.findViewById(R.id.specialise_our_doctors);
                cardView = itemView.findViewById(R.id.recycler_our_doctors);
            }else if (display_id == 2 || display_id == 3){
                profileImg = itemView.findViewById(R.id.profile_list_our_doctors);
                fullName = itemView.findViewById(R.id.name_list_our_doctors);
                specialise = itemView.findViewById(R.id.specialise_list_our_doctors);
                cardView = itemView.findViewById(R.id.recycler_list_our_doctors);
            }
        }
    }
}
