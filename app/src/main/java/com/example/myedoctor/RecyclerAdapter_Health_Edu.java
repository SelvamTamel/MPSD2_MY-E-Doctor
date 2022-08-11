package com.example.myedoctor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter_Health_Edu extends RecyclerView.Adapter<RecyclerAdapter_Health_Edu.MyViewHolder> {

    Context context;
    ArrayList<Class_Health_Edu> list;

    public RecyclerAdapter_Health_Edu(Context context, ArrayList<Class_Health_Edu> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.patient_health_edu_grid_view,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Retrieve health education information
        Class_Health_Edu healthEdu_info = list.get(position);

        //Set health education title
        holder.title.setText(healthEdu_info.getTitle());

        //Locate health education file location at Firebase Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        //Retrieve health education logo
        StorageReference logoImgRef = storageReference.child("health_edu/" + healthEdu_info.getLogoKey() + "/logo.png");

        //Set health education logo
        logoImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.logo);
            }
        });

        //When user press either one health education info, display the specific data on screen
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Patient_health_edu_info.class);

                //Passing data to the user activity
                intent.putExtra("Title", healthEdu_info.getTitle());
                intent.putExtra("Content", healthEdu_info.getContent());
                intent.putExtra("LogoKey", healthEdu_info.getLogoKey());

                //Start the activity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView logo;
        TextView title;
        CardView cardView;

        public MyViewHolder (@NonNull View itemView) {
            super (itemView);

            logo = itemView.findViewById(R.id.logo_health_edu);
            title = itemView.findViewById(R.id.title_health_edu);

            cardView = itemView.findViewById(R.id.recycler_health_edu);
        }
    }
}
