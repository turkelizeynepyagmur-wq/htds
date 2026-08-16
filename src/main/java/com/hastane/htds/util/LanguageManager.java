package com.hastane.htds.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static Locale currentLocale = new Locale("tr", "TR");
    private static ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocale);

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("messages", currentLocale);
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }
}
