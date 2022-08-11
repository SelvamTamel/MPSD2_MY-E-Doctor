package com.example.myedoctor;

public class Class_Patient {
    // userType 1 = Class_Doctor
    // userType 2 = Class_Patient

    public String email, fullName, phoneNumber, gender, dob, address;
    public int userType;

    public Class_Patient(){

    }

    public Class_Patient(String email, String fullName, String phoneNumber, String gender, String dob, String address){
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.userType = 2;
    }
}