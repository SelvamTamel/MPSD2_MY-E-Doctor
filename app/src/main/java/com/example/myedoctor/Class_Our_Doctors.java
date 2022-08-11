package com.example.myedoctor;

public class Class_Our_Doctors {

    public String fullName, specialise, bio, id;
    public Class_Our_Doctors(){

    }

    public Class_Our_Doctors(String fullName, String specialise, String bio,String id){
        this.fullName = fullName;
        this.specialise = specialise;
        this.bio = bio;
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
