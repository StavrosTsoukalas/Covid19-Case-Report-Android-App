package com.example.covid_19app;

public class User {
    private String name, surname, gender, birthdate, address, city, email;
    private boolean worker;

    public User(){}

    public User(String name, String surname, String gender, String birthdate, String address, String city, String email, boolean worker) {
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.address = address;
        this.city = city;
        this.email = email;
        this.worker = worker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

//    public void setSurname(String surname) {
//        this.surname = surname;
//    }

    public String getGender() {
        return gender;
    }

//    public void setGender(String gender) {
//        this.gender = gender;
//    }

    public String getBirthdate() {
        return birthdate;
    }

//    public void setBirthdate(String birthdate) {
//        this.birthdate = birthdate;
//    }

    public String getAddress() {
        return address;
    }

//    public void setAddress(String address) {
//        this.address = address;
//    }

    public String getCity() {
        return city;
    }

//    public void setCity(String city) {
//        this.city = city;
//    }

    public String getEmail() {
        return email;
    }

//    public void setEmail(String email) {
//        this.email = email;
//    }

    public boolean isWorker() {
        return worker;
    }

//    public void setWorker(boolean worker) {
//        this.worker = worker;
//    }
}
