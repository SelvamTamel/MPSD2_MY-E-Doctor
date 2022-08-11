package com.example.myedoctor;

public class Class_Reminder {

    private String medicineTitle, medicineType, time;
    private int reminderInterval;
    private int alarmID;

    public Class_Reminder (){

    }

    public  Class_Reminder(String medicineTitle, String medicineType, int reminderInterval, int alarmID, String time){
        this.medicineTitle = medicineTitle;
        this.medicineType = medicineType;
        this.reminderInterval = reminderInterval;
        this.alarmID = alarmID;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMedicineTitle() {
        return medicineTitle;
    }

    public void setMedicineTitle(String medicineTitle) {
        this.medicineTitle = medicineTitle;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public int getReminderInterval() {
        return reminderInterval;
    }

    public void setReminderInterval(int reminderInterval) {
        this.reminderInterval = reminderInterval;
    }

    public int getAlarmID() {
        return alarmID;
    }

    public void setAlarmID(int alarmID) {
        this.alarmID = alarmID;
    }
}
