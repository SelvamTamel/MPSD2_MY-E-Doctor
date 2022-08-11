package com.example.myedoctor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecyclerAdapter_Reminder extends RecyclerView.Adapter<RecyclerAdapter_Reminder.MyViewHolder> {

    Context context;
    ArrayList<Class_Reminder> list;
    String interval;

    public RecyclerAdapter_Reminder(Context context, ArrayList<Class_Reminder> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerAdapter_Reminder.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.patient_reminder_list_view, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter_Reminder.MyViewHolder holder, int position) {
        //Retrieve reminder information
        Class_Reminder reminder_info = list.get(position);

        int intervalId = reminder_info.getReminderInterval();
        if (intervalId == 1){
            interval = "Daily";
        } else if (intervalId == 2){
            interval = "2 Days";
        } else if (intervalId == 3){
            interval = "3 Days";
        }

        //Set medicine title
        holder.medicineTitle.setText(reminder_info.getMedicineTitle());
        holder.timeReminder.setText(reminder_info.getTime());
        holder.intervalReminder.setText(interval);
        holder.medicineType.setText("( " + reminder_info.getMedicineType() + " )");

        String alarmID = String.valueOf(reminder_info.getAlarmID());

        holder.btnDeleteReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancel Alarm
                cancelAlarm(reminder_info.getAlarmID());

                //Delete from database
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("patients");
                String userId = user.getUid();
                userReference.child(userId).
                        child("medicine_reminder").child(alarmID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context, "Deleted Reminder from database.", Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(context, "Failed to Dalete Reminder from database.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView medicineTitle, timeReminder, intervalReminder, medicineType;
        CardView cardView;
        ImageButton btnDeleteReminder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            medicineTitle = itemView.findViewById(R.id.medicine_title_reminder);
            timeReminder = itemView.findViewById(R.id.time_reminder);
            intervalReminder = itemView.findViewById(R.id.interval_reminder);
            btnDeleteReminder = itemView.findViewById(R.id.btn_dlt_reminder);
            medicineType = itemView.findViewById(R.id.medicine_type_reminer);

            cardView = itemView.findViewById(R.id.recycler_list_reminder);
        }
    }

    private void cancelAlarm(int alarmID) {
        Intent intent = new Intent(context, Alarm_Receiver.class);
        PendingIntent pendingIntent;
        AlarmManager alarmManager = null;

        //For API 23 or higher, set mutable flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "Reminder Cancelled", Toast.LENGTH_SHORT).show();
    }

}