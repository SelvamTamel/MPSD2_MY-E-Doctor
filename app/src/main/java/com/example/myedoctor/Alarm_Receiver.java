package com.example.myedoctor;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Alarm_Receiver extends BroadcastReceiver {

    private String medicationTitle, doctorName;
    private int alarmId;

    // ID 1 = patient virtual appointment reminder
    // ID 2 = patient medication reminder
    @Override
    public void onReceive(Context context, Intent intent) {
        alarmId = intent.getIntExtra("AlarmID", 0);
        medicationTitle = intent.getStringExtra("MedicationTitle");
        doctorName = intent.getStringExtra("DoctorName");

        // Create an Intent for the activity I want to start
        Intent resultIntent = new Intent();
        if (alarmId == 1){
            resultIntent = new Intent(context, Patient_my_appt.class);
        }

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = null;
        if (alarmId == 1){
            builder = new NotificationCompat.Builder(context, "reminderID")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Virtual Appointment Reminder")
                    .setContentText("Consultation Room for your Appointment with " + doctorName + " is ready to join.")
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        } else if (alarmId == 2){
            builder = new NotificationCompat.Builder(context, "reminderID")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Medication Reminder")
                    .setContentText("Remember to use your medication - " + medicationTitle)
                    .setAutoCancel(false)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        NotificationManagerCompat notoficationManagerCompat = NotificationManagerCompat.from(context);
        notoficationManagerCompat.notify(123, builder.build());
    }
}