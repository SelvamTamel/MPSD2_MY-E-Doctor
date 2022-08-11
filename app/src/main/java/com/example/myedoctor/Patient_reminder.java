package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Patient_reminder extends AppCompatActivity implements View.OnClickListener {

    private EditText etMedication;
    private Button btnPill, btnCapsules, btnInjection, btnSetReminder;
    private Button btnDaily, btn2Days, btn3Days;
    private TextView selectTime;
    private ImageButton btnBack;

    private Calendar cal;
    private String selectedTime, medicationType, medicationTitle;
    private int reminderInterval = -1, alarmId;
    private long reminderTimeMillis;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_reminder);

        etMedication = findViewById(R.id.et_medication);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnPill = findViewById(R.id.btn_pill);
        btnPill.setOnClickListener(this);

        btnCapsules = findViewById(R.id.btn_capsules);
        btnCapsules.setOnClickListener(this);

        btnDaily = findViewById(R.id.btn_daily);
        btnDaily.setOnClickListener(this);

        btn2Days = findViewById(R.id.btn_2days);
        btn2Days.setOnClickListener(this);

        btn3Days = findViewById(R.id.btn_3days);
        btn3Days.setOnClickListener(this);

        btnInjection = findViewById(R.id.btn_injection);
        btnInjection.setOnClickListener(this);

        btnSetReminder = findViewById(R.id.btn_set_reminder);
        btnSetReminder.setOnClickListener(this);

        selectTime = findViewById(R.id.tv_reminder_time);
        selectTime.setOnClickListener(this);

        //Get calendar instance
        cal = Calendar.getInstance();

        //Create notification channel (API 26 and above is required)
        createNotificationChannel();

        alarmId = (int)System.currentTimeMillis();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.btn_pill:
                medicationType = ("Pill");
                btnPill.setBackgroundColor(Color.parseColor("#A4D3F8"));
                btnCapsules.setBackgroundColor(Color.parseColor("#002AFF"));
                btnInjection.setBackgroundColor(Color.parseColor("#002AFF"));
                break;
            case R.id.btn_capsules:
                medicationType = ("Capsule");
                btnCapsules.setBackgroundColor(Color.parseColor("#A4D3F8"));
                btnInjection.setBackgroundColor(Color.parseColor("#002AFF"));
                btnPill.setBackgroundColor(Color.parseColor("#002AFF"));
                break;
            case R.id.btn_injection:
                medicationType = ("Injection");
                btnInjection.setBackgroundColor(Color.parseColor("#A4D3F8"));
                btnPill.setBackgroundColor(Color.parseColor("#002AFF"));
                btnCapsules.setBackgroundColor(Color.parseColor("#002AFF"));
                break;
            case R.id.btn_daily:
                reminderInterval = 1;
                btnDaily.setBackgroundColor(Color.parseColor("#A4D3F8"));
                btn2Days.setBackgroundColor(Color.parseColor("#002AFF"));
                btn3Days.setBackgroundColor(Color.parseColor("#002AFF"));
                break;
            case R.id.btn_2days:
                reminderInterval = 2;
                btn2Days.setBackgroundColor(Color.parseColor("#A4D3F8"));
                btnDaily.setBackgroundColor(Color.parseColor("#002AFF"));
                btn3Days.setBackgroundColor(Color.parseColor("#002AFF"));
                break;
            case R.id.btn_3days:
                reminderInterval = 3;
                btn3Days.setBackgroundColor(Color.parseColor("#A4D3F8"));
                btnDaily.setBackgroundColor(Color.parseColor("#002AFF"));
                btn2Days.setBackgroundColor(Color.parseColor("#002AFF"));
                break;
            case R.id.tv_reminder_time:
                showTimePicker();
                break;
            case R.id.btn_set_reminder:
                if (checkInput()){
                    setReminder(reminderInterval);
                    addToDatabase();
                }
                break;
        }
    }

    private void showTimePicker() {
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(Patient_reminder.this,
                android.R.style.Theme_Holo_Dialog,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        int hh = hour;
                        String am_pm = "";

                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);

                        if (hh > 12) {
                            hh = hh - 12;
                            am_pm = "PM";
                        } else if (hh == 0) {
                            hh = 12;
                            am_pm = "AM";
                        }else if (hh == 12) {
                            am_pm = "PM";
                        } else {
                            am_pm = "AM";
                        }
                        selectedTime = (String.format("%02d" ,hh) + ":" + String.format("%02d" ,minute) + " " + am_pm);
                        selectTime.setText(selectedTime);
                        reminderTimeMillis = cal.getTimeInMillis();
                    }
                },
                hour, minute, false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void setReminder(int intervalPeriod) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), Alarm_Receiver.class);
        intent.putExtra("AlarmID", 2);
        intent.putExtra("MedicationTitle", medicationTitle);

        //For API 23 or higher, set mutable flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        //Notification will show every 15 mins if user didn't press the notification
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY*intervalPeriod, pendingIntent);

        Toast.makeText(this, "Reminder set successfully.", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        //if API is 26 or higher, assign notification to a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyEDoctorReminderChannel";
            String description = "Channel For My E Dcotor";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("reminderID", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean checkInput(){
        boolean check = false;

        medicationTitle = etMedication.getText().toString();

        if (medicationTitle.isEmpty()){
            etMedication.setError("Medication Title must not leave blank.");
            etMedication.requestFocus();
        }else if (medicationType.isEmpty()){
            Toast.makeText(Patient_reminder.this, "You must select a Medication Type.", Toast.LENGTH_LONG).show();
        } else if (selectedTime.isEmpty()){
            selectTime.setError("You must select a reminder time.");
            selectTime.requestFocus();
        } else if (reminderInterval == -1){
            Toast.makeText(Patient_reminder.this, "You must select Notification Repeat Interval.", Toast.LENGTH_LONG).show();
        }else {
            check = true;
        }
        return check;
    }

    private void addToDatabase(){
        Class_Reminder reminderDetail = new Class_Reminder(medicationTitle, medicationType, reminderInterval, alarmId, selectedTime);

        //Locate user's credential location at Firebase Real-Time DB
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("patients");
        String userId = user.getUid();

        //Add New Appointment information to Firebase Real-Time DB
        userReference.child(userId).child("medicine_reminder").child(String.valueOf(alarmId)).
                setValue(reminderDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Patient_reminder.this, "Reminder Saved to database.", Toast.LENGTH_LONG).show();
                            finish();

                        } else {
                            Toast.makeText(Patient_reminder.this, "Failed to save reminder to database.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}