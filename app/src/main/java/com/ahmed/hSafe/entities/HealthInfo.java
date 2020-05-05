package com.ahmed.hSafe.entities;

public class HealthInfo {
    private String date;
    private String calories;
    private String steps;
    private String heartBeat;

    public HealthInfo(String date, String calories, String steps, String heartBeat) {
        this.date = date;
        this.calories = calories;
        this.steps = steps;
        this.heartBeat = heartBeat;
    }

    public String getDate() {
        return date;
    }

    public String getCalories() {
        return calories;
    }

    public String getSteps() {
        return steps;
    }

    public String getHeartBeat() {
        return heartBeat;
    }
}
