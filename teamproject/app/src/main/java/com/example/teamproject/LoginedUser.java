package com.example.teamproject;

public class LoginedUser {

    public static String id, name;

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        LoginedUser.id = id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        LoginedUser.name = name;
    }
}
