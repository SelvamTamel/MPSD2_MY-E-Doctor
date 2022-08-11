package com.example.myedoctor;

public class Class_Doctor {
    // userType 1 = Class_Doctor
    // userType 2 = Class_Patient

    public String email, fullName, phoneNumber, specialise, bio;
    public int userType;

    public Class_Doctor(){

    }

    public Class_Doctor(String email, String fullName, String phoneNumber, String specialise, String bio){
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.specialise = specialise;
        this.bio = bio;
        this.userType = 1;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSpecialise() {
        return specialise;
    }

    public void setSpecialise(String specialise) {
        this.specialise = specialise;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

}
