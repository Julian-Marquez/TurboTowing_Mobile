package com.garage.turbotowingapp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    private String brand;
    private String engine;
    private int daysinstorage;
    private int age;
    private  String damage;
    private String description;
    private String model;
    private User owner;
    private String location;
    private LocalDate dateposted;
    private int ogdaysinstorage;
    private long vId;
    private List<byte[]> imageBytesList;
    private long ownerId;

    public Vehicle(String brand, String model, int age, String damage, String engine, int daysinstorage, String description,String location) {
        this.brand = brand;
        this.engine = engine;
        this.daysinstorage = daysinstorage;
        this.damage = damage;
        this.description = description;
        this.age = age;
        this.model = model;
        this.dateposted = LocalDate.now();
        this.ogdaysinstorage = daysinstorage;
        this.location = location;
        imageBytesList = new ArrayList<>();
    }


    public void setBrand(String name) {
        this.brand = name;
    }
    public String getBrand() {
        return this.brand;
    }
    public void setModel(String model){this.model = model;};
    public String getModel(){return this.model;};
    public void setEngine(String engine) {
        this.engine = engine;
    }
    public String getEngine() {
        return engine;
    }
    public void setDaysInStorage(int days) {
        this.daysinstorage = days;
    }
    public int getDaysInStorage() {
        return daysinstorage;
    }
    public void setAge(int age ) {
        this.age = age;
    }
    public int getAge() {
        return age;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setDamage(String damage) {
        this.damage = damage;
    }
    public String getDamage() {
        return this.damage;
    }
    public void setLocation(String location) {
        this.location= location;
    }
    public String getLocation() {
        return this.location;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
    public LocalDate getDatePosted() {
        return dateposted;
    }
    public void setDatePosted(LocalDate dateposted) {
        this.dateposted = dateposted;
    }
    public void setOwnerId(long ownerID){
        this.ownerId = ownerId;
    }

    public long getOwnerId(){return this.ownerId;};
    public User getOwner() {
        return this.owner;
    }
    public int getOgDaysInStorage(){
        return this.ogdaysinstorage;
    }
    public void setOgDaysInStorage(int ogdaysinstorage) {
        this.ogdaysinstorage = ogdaysinstorage;
    }
    public long getUuid() {
        return this.vId;
    }
    public void setUuid(long vId) {
        this.vId = vId;
    }


    public List<byte[]> getImageBytesList() {
        return imageBytesList;
    }
}
