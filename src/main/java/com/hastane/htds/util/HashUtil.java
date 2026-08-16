package com.hastane.htds.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashUtil {

    private HashUtil() {
        // Yardımcı sınıf, nesnesi oluşturulmasın
    }

    /**
     * Şifreyi SHA-256 kullanarak hash'ler.
     *
     * @param password Düz metin şifre
     * @return 64 karakterli hex formatında hash değeri
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Şifre hash'leme sırasında hata oluştu", e);
        }
    }
}
