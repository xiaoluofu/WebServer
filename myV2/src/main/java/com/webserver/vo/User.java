package com.webserver.vo;

public class User {
    private String username;
    private String password;
    private String nikename;
    private int age;

    public User(String username, String password, String nikename, int age) {
        this.username = username;
        this.password = password;
        this.nikename = nikename;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNikename() {
        return nikename;
    }

    public void setNikename(String nikename) {
        this.nikename = nikename;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return username + " " +password + " " +nikename + " " +age ;
    }
}
