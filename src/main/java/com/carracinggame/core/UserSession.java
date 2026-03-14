package com.carracinggame.core;

public class UserSession {
    private static String username;

    public static void setUsername(String username) {
        UserSession.username = username;
    }

    public static String getUsername() {
        return username;
    }

    public static void clear() {
        username = null;
    }

    public static boolean isLoggedIn() {
        return username != null && !username.isBlank();
    }
}