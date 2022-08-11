package com.example.myedoctor;

public class Class_PatientData {
    String bloodSugar, heartRate, bloodPressure, cholesterol;

    public Class_PatientData() {

    }

    public Class_PatientData(String bloodSugar, String heartRate, String bloodPressure, String cholesterol) {
        this.bloodSugar = bloodSugar;
        this.heartRate = heartRate;
        this.bloodPressure = bloodPressure;
        this.cholesterol = cholesterol;
    }

    public String getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(String bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(String cholesterol) {
        this.cholesterol = cholesterol;
    }
}


