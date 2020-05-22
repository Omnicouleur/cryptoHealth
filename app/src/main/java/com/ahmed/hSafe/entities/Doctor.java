package com.ahmed.hSafe.entities;

public class Doctor {
    private String name;
    private String uid;
    private String specialty;
    private String city;
    private String publicAddress;

    public Doctor(String name, String specialty, String city, String publicAddress, String uid) {
        this.name = name;
        this.specialty = specialty;
        this.city = city;
        this.publicAddress = publicAddress;
        this.uid = uid;
    }

    public Doctor() {
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
