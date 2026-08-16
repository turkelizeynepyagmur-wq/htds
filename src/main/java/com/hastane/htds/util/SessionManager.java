package com.hastane.htds.util;

import com.hastane.htds.model.Personel;

public class SessionManager {
    private static Personel currentUser;

    public static void setCurrentUser(Personel user) {
        currentUser = user;
    }

    public static Personel getCurrentUser() {
        return currentUser;
    }
    
    public static void logout() {
        currentUser = null;
    }
}
