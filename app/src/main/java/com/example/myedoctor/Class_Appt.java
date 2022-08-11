package com.example.myedoctor;

public class Class_Appt {

    //state 1 = pending doctor to accept
    //state 2 = accepted
    //state 3 = decline
    //state 4 = done appointment

    //type 1 = physical
    //type 2 = virtual

    public String doctorId;
    public String patientId, patientName, patientGender, patientDOB, patientPhone;
    public String symptoms, symptomsPeriod;
    public String apptDate, apptTime;
    public int state, type;
    public String aptId;
    public long aptTimeMillis, createdTimeMillis;

    public Class_Appt(){

    }

    public Class_Appt(long aptTimeMillis, long createdTimeMillis, String doctorId, String patientId, String patientName, String patientPhone, String patientGender,
                      String patientDOB, String symptoms, String symptomsPeriod, String apptDate, String apptTime, int state, int type) {
        this.aptTimeMillis = aptTimeMillis;
        this.createdTimeMillis = createdTimeMillis;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.patientGender = patientGender;
        this.patientDOB = patientDOB;
        this.symptoms = symptoms;
        this.symptomsPeriod = symptomsPeriod;
        this.apptDate = apptDate;
        this.apptTime = apptTime;
        this.state = state;
        this.type = type;
        this.aptId = "";
    }

    public long getCreatedTimeMillis() {
        return createdTimeMillis;
    }

    public void setCreatedTimeMillis(long createdTimeMillis) {
        this.createdTimeMillis = createdTimeMillis;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getAptTimeMillis() {
        return aptTimeMillis;
    }

    public void setAptTimeMillis(long aptTimeMillis) {
        this.aptTimeMillis = aptTimeMillis;
    }

    public String getAptId() {
        return aptId;
    }

    public void setAptId(String aptId) {
        this.aptId = aptId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public String getPatientDOB() {
        return patientDOB;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getSymptomsPeriod() {
        return symptomsPeriod;
    }

    public String getApptDate() {
        return apptDate;
    }

    public String getApptTime() {
        return apptTime;
    }

    public int getState() {
        return state;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public void setPatientDOB(String patientDOB) {
        this.patientDOB = patientDOB;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public void setSymptomsPeriod(String symptomsPeriod) {
        this.symptomsPeriod = symptomsPeriod;
    }

    public void setApptDate(String apptDate) {
        this.apptDate = apptDate;
    }

    public void setApptTime(String apptTime) {
        this.apptTime = apptTime;
    }

    public void setState(int state) {
        this.state = state;
    }
}
