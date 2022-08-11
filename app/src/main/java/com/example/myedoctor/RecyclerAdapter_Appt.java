package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerAdapter_Appt extends RecyclerView.Adapter<RecyclerAdapter_Appt.MyViewHolder> {
    Context context;
    ArrayList<Class_Appt> list;
    int displayId, type, state;

    //Display ID 1 = Doctor_virtual_appt, 2 = Doctor_my_appt, 3 = Doctor_physical_appt / Doctor_dashboard_physical_appt
    // 4 = main_virtual_appt_info (doctor)
    //type 1 = Physical Appointment, 2 = Virtual Appointment
    public RecyclerAdapter_Appt(Context context, ArrayList<Class_Appt> list, int displayId) {
        this.context = context;
        this.list = list;
        this.displayId = displayId;
    }

    //doctor_physical_appt_list_view
    @NonNull
    @Override
    public RecyclerAdapter_Appt.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(context).inflate(R.layout.doctor_appt_list_view, parent, false);

        return new RecyclerAdapter_Appt.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter_Appt.MyViewHolder holder, int position) {

        //Retrieve virtual appointment information
        Class_Appt virtualAppt_info = list.get(position);
        type = virtualAppt_info.getType();

        //Get created date and time of the appointment
        DateFormat dateFormat = new SimpleDateFormat("dd MMM , HH:mm");
        Date date = new Date(virtualAppt_info.getCreatedTimeMillis());
        String formatedDate = dateFormat.format(date);

        //Set data
        holder.name.setText(virtualAppt_info.getPatientName());
        holder.symptoms.setText(virtualAppt_info.getSymptoms());
        holder.createdTime.setText(formatedDate);

        //Set Appointment Type
        if (type == 1) {
            holder.apptType.setText("Physical Apppintment");
        } else if (type == 2) {
            holder.apptType.setText("Virtual Apppintment");
        }

        //Set Appointment State
        state = virtualAppt_info.getState();
        if (state == 1){
            holder.tvState.setText("pending");
        } else if (state == 2){
            holder.tvState.setText("accepted");
        } else if (state == 3){
            holder.tvState.setText("declined");
        } else if (state == 4){
            holder.tvState.setText("finished");
        } else if (state == 5){
            holder.tvState.setText("done");
        } else if (state == 6){
            holder.tvState.setText("expired");
        } else if (state == 7 || state == 8) {
            holder.tvState.setText("APPT END");
        }else {
            holder.tvState.setText("error");
        }

        //When user press either one virtual appointment info, display the specific data on screen
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (displayId == 1) {
                    intent = new Intent(context, Doctor_virtual_appt_info.class);

                } else if (displayId == 2 && virtualAppt_info.getType() == 1) {
                    //Physical Appt Card View activity
                    intent = new Intent(context, Main_my_appt_physical_info.class);
                    intent.putExtra("State", virtualAppt_info.getState());

                } else if (displayId == 2 && virtualAppt_info.getType() == 2) {
                    //Virtual Appt Card View activity
                    intent = new Intent(context, Main_my_appt_virtual_info.class);
                    intent.putExtra("aptTimeMillis", virtualAppt_info.getAptTimeMillis());
                    intent.putExtra("State", virtualAppt_info.getState());

                } else {
                    intent = new Intent(context, Doctor_physical_appt_info.class);
                }

                //Passing data to the user activity
                intent.putExtra("Name", virtualAppt_info.getPatientName());
                intent.putExtra("Date", virtualAppt_info.getApptDate());
                intent.putExtra("Time", virtualAppt_info.getApptTime());
                intent.putExtra("Gender", virtualAppt_info.getPatientGender());
                intent.putExtra("DOB", virtualAppt_info.getPatientDOB());
                intent.putExtra("Symptoms", virtualAppt_info.getSymptoms());
                intent.putExtra("SymptomsPeriod", virtualAppt_info.getSymptomsPeriod());
                intent.putExtra("AptID", virtualAppt_info.getAptId());
                intent.putExtra("DoctorID", virtualAppt_info.getDoctorId());

                //Start the activity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, symptoms, apptType, createdTime, tvState;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_appt);
            symptoms = itemView.findViewById(R.id.symptoms_appt);
            createdTime = itemView.findViewById(R.id.createdTime_appt);
            apptType = itemView.findViewById(R.id.apptType_appt);
            tvState = itemView.findViewById(R.id.state_appt);

            cardView = itemView.findViewById(R.id.recycler_list_appt);
        }
    }
}
