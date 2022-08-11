package com.example.myedoctor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Patient_virtual_appt_2 extends AppCompatActivity implements View.OnClickListener {

    //Variables to store data received from Patient_virtual_appt_1 (RecyclerAdapter-Our_Doctors)
    String doctorId;

    //Variables in Patient_virtual_appt_2
    private TextView input_symptoms, input_date, input_time;
    private Spinner input_symptomsPeriod;
    private Button btnBook;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private Calendar cal;

    //Variables to store user input
    private String symptoms, symptomsPeriod, apptDate, apptTime;

    //Variables to set appointment
    private String patientId, patientName, patientGender, patientDOB, patientPhone;

    //Variables for reminder
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private DatabaseReference doctorReference;
    private String doctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_virtual_appt_form_2);

        //Store data received from Sign Up Class_Patient Page 1
        doctorId = getIntent().getStringExtra("DoctorId");

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnBook = findViewById(R.id.btn_book);
        btnBook.setOnClickListener(this);

        input_symptoms = findViewById(R.id.virtualAppt_input_symptoms);
        input_symptoms.setOnClickListener(this);

        input_symptomsPeriod = findViewById(R.id.virtualAppt_input_symptomPeriod);

        input_date = findViewById(R.id.virtualAppt_input_date);
        input_date.setOnClickListener(this);

        input_time = findViewById(R.id.virtualAppt_input_time);
        input_time.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);

        cal = Calendar.getInstance();
        symptomsPeriodDropdownList();

        //Locate doctor's credential location at Firebase Real-Time DB
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("patients");
        patientId = user.getUid();

        //Retrieve user's credentials from Real-Time DB
        userReference.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Create an instance to store data retrieved from Firebase
                Class_Patient classPatientProfile = snapshot.getValue(Class_Patient.class);

                if (classPatientProfile != null) {
                    patientName = classPatientProfile.fullName;
                    patientPhone = classPatientProfile.phoneNumber;
                    patientGender = classPatientProfile.gender;
                    patientDOB = classPatientProfile.dob;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Patient_virtual_appt_2.this, "Database Went Wrong.", Toast.LENGTH_LONG).show();
            }
        });

        //Get In Charge Doctor's name from firebase
        doctorReference = FirebaseDatabase.getInstance().getReference("doctors");
        doctorReference.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Class_Doctor doctor_info = snapshot.getValue(Class_Doctor.class);
                // Store doctor name
                doctorName = "Dr. " + doctor_info.getFullName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Patient_virtual_appt_2.this, "Database Went Wrong.(Doctor)", Toast.LENGTH_LONG).show();
            }
        });

        //Create notification channel (API 26 and above is required)
        createNotificationChannel();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                btnBack.requestFocusFromTouch();
                finish();
                break;
            case R.id.virtualAppt_input_date:
                showDatePicker();
                break;
            case R.id.virtualAppt_input_time:
                showTimePicker();
                break;
            case R.id.btn_book:
                if (checkInput()) {
                    setAppointment();
                    createDialog();
                }
                break;
        }
    }

    //Method to display drop down list (spinner) for Symptoms Period
    private void symptomsPeriodDropdownList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // Don't display the last item. It is used as hint.
            }
        };
        // Display the drop down list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("1 Day");
        adapter.add("3 Days");
        adapter.add("5 Days");
        adapter.add("More than 1 Week");
        adapter.add("Select period"); // This is hint

        input_symptomsPeriod.setAdapter(adapter);
        input_symptomsPeriod.setSelection(adapter.getCount()); // Dislpay Hint

        // Store the patientGender value
        input_symptomsPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                symptomsPeriod = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Patient_virtual_appt_2.this, "Nothing has been selected.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkInput() {
        boolean check = false;

        symptoms = input_symptoms.getText().toString();

        if (symptoms.isEmpty()) {
            input_symptoms.setError("This field must not leave blank.");
            input_symptoms.requestFocus();
        } else if (symptomsPeriod.isEmpty()) {
            Toast.makeText(Patient_virtual_appt_2.this, "You must select symptoms period.", Toast.LENGTH_SHORT).show();
            input_symptomsPeriod.requestFocus();
        } else if (apptDate.isEmpty()) {
            input_date.setError("Date is required.");
            input_date.requestFocus();
        } else if (apptTime.isEmpty()) {
            input_time.setError("Time is required.");
            input_date.requestFocus();
        } else {
            check = true;
        }
        return check;
    }

    private void showDatePicker() {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(Patient_virtual_appt_2.this,
                android.R.style.Theme_Holo_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Log.d("MainActivity", "onDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);
                        apptDate = day + "/" + (month + 1) + "/" + year;
                        input_date.setText(apptDate);

                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, day);
                    }
                },
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showTimePicker() {
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(Patient_virtual_appt_2.this,
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
                        } else if (hh == 12) {
                            am_pm = "PM";
                        } else {
                            am_pm = "AM";
                        }
                        apptTime = (String.format("%02d", hh) + ":" + String.format("%02d", minute) + " " + am_pm);
                        input_time.setText(apptTime);
                    }
                },
                hour, minute, false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setAppointment() {
        progressBar.setVisibility(View.VISIBLE);

        Class_Appt appointmentDetail = new Class_Appt(cal.getTimeInMillis(), System.currentTimeMillis(), doctorId, patientId, patientName, patientPhone,
                patientGender, patientDOB, symptoms, symptomsPeriod, apptDate, apptTime, 1, 2);

        //Add New Appointment information to Firebase Real-Time DB
        FirebaseDatabase.getInstance().getReference("virtual_apt")
                .push()
                .setValue(appointmentDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Patient_virtual_appt_2.this, "Appointment is set! Waiting for doctor to confirm.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(Patient_virtual_appt_2.this, "Failed to set appointment. Please Try Again.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
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

    private void createDialog() {
        Dialog dialog = new Dialog(Patient_virtual_appt_2.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_reminder);

        Button btnYes = dialog.findViewById(R.id.btn_yes);
        Button btnNo = dialog.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReminder();
                dialog.dismiss();
                startActivity(new Intent(Patient_virtual_appt_2.this, Patient_dashboard.class));
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                startActivity(new Intent(Patient_virtual_appt_2.this, Patient_dashboard.class));
            }
        });
        dialog.show();
    }

    private void setReminder() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), Alarm_Receiver.class);
        intent.putExtra("AlarmID", 1);
        intent.putExtra("DoctorName", doctorName);

        //For API 23 or higher, set mutable flag
        //Set the reminder time 5 mins before the appointment time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        //Notification will show every 15 mins if user didn't press the notification
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 300000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

        Toast.makeText(this, "Reminder Set Successfully.", Toast.LENGTH_SHORT).show();
    }
}