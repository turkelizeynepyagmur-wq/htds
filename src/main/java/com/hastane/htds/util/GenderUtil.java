package com.hastane.htds.util;

import java.util.Locale;

public class GenderUtil {

    /**
     * Verilen isme göre cinsiyet emojisi (kadın için 👩‍💼, erkek için 👨‍💼) döner.
     */
    public static String getGenderEmoji(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            return "\uD83D\uDC64"; // 👤
        }

        String name = firstName.trim().toLowerCase(Locale.forLanguageTag("tr-TR"));

        // Sistemdeki kadın isimleri listesi
        if (name.equals("ayşe") || 
            name.equals("fatma") || 
            name.equals("elif") || 
            name.equals("zeynep") || 
            name.equals("canan") || 
            name.equals("aslı") || 
            name.equals("melis") || 
            name.equals("büşra") || 
            name.equals("seda")) {
            return "\uD83D\uDC69"; // 👩
        }

        // Sistemdeki erkek isimleri (diğer isimler varsayılan olarak erkek kabul edilir)
        return "\uD83D\uDC68"; // 👨
    }
}
