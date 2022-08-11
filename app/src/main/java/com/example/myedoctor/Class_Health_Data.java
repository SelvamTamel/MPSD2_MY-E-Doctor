package com.example.myedoctor;

public class Class_Health_Data {

    public  String bloodSugar, bloodPressure, cholesterol, heartRate;

    public Class_Health_Data (){

    }

    public Class_Health_Data (String bloodSugar, String bloodPressure, String cholesterol, String heartRate){
        this.bloodPressure = bloodPressure;
        this.bloodSugar = bloodSugar;
        this.cholesterol = cholesterol;
        this.heartRate = heartRate;
    }

    public String getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(String bloodSugar) {
        this.bloodSugar = bloodSugar;
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

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }
}
