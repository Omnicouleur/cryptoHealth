package com.ahmed.hSafe.entities;

public class HealthInfo {
    String date;
    String calories;
    String steps;
    String heartBeat;

    public HealthInfo(String date, String calories, String steps, String heartBeat) {
        this.date = date;
        this.calories = calories;
        this.steps = steps;
        this.heartBeat = heartBeat;
    }
}
