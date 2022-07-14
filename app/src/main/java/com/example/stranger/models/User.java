package com.example.stranger.models;

public class User {
   private String uid;
   private String name;
   private String profile;
   private String city;
   private String email;
   private long coins;

    public User() {
    }

    public User(String uid, String name, String profile, String city, String email, long coins) {
        this.uid = uid;
        this.name = name;
        this.profile = profile;
        this.city = city;
        this.email = email;
        this.coins = coins;
    }

    public String getUid() {
        return uid;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
