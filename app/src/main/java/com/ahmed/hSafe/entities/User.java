package com.ahmed.hSafe.entities;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String name;
    public String gender;
    public int age;
    public String city;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String gender,int age, String city) {
        this.name = name;
        this.gender = gender;
        this.city = city;
        this.age = age;
    }

}