package com.garage.turbotowingapp;

import java.util.ArrayList;

public class User {

    private String firstName;
    private String lastName;
    private String password;
    private String zipcode;
    private String email;
    private byte[] profilepic;
    private String phonenum;
    private long userid;
    private ArrayList<Vehicle> vehiclelist;
    private ArrayList<Vehicle> savedvehicles;

    public User(String firstName, String lastName, String password,String email,String phoneNumber, byte[] profilepicpath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.phonenum = phoneNumber;
        this.email = email;
        this.profilepic = profilepicpath;
        vehiclelist = new ArrayList<>();
        savedvehicles = new ArrayList<>();

    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword(){
        return this.password;
    }

    public String getEmail(){
        return this.email;
    }

    public String getZipcode(){
        return this.zipcode;
    }

    public byte[] getProfilePic(){
        return this.profilepic;
    }

    public long getUserId(){
        return this.userid;
    }

    public String getPhoneNumber(){
        return this.phonenum;
    }


    public ArrayList<Vehicle> getSavedVehicles(){
        return this.savedvehicles;
    }

    public ArrayList<Vehicle> getVehiclelist(){
        return this.vehiclelist;
    }

    public void setFirstName(String fname){
        this.firstName = fname;
    }
    public void setLastName(String lname) {
        this.lastName = lname;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setZipcode(String zipcode){
        this.zipcode = zipcode;
    }

    public void setProfilePic(byte[] pic){
        this.profilepic = pic;
    }

    public void setUserId(long userId){
        this.userid = userId;
    }

    public void setPhoneNumber(String num){
        this.phonenum = num;
    }

}
