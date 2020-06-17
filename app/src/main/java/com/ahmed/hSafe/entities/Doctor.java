package com.ahmed.hSafe.entities;

public class Doctor {
    private String name;
    private String uid;
    private String specialty;
    private String city;
    private String publicAddress;
    private String job;
    private String gender;

    public Doctor(String name, String uid, String specialty, String city, String publicAddress, String job, String gender) {
        this.name = name;
        this.uid = uid;
        this.specialty = specialty;
        this.city = city;
        this.publicAddress = publicAddress;
        this.job = job;
        this.gender = gender;
    }
    public Doctor() {
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public void setPublicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
