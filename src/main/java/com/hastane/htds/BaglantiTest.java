package com.hastane.htds;

import com.hastane.htds.db.DatabaseConnection;

import java.sql.Connection;

/**
 * GECICI test sinifi - sadece veritabani baglantisinin calisip
 * calismadigini dogrulamak icin. Baglanti calistigini gordukten
 * sonra bu sinifi silebiliriz, projenin kalici bir parcasi degil.
 */
public class BaglantiTest {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println(">>> BAGLANTI BASARILI: " + conn);
        } catch (Exception e) {
            System.out.println(">>> BAGLANTI HATASI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}