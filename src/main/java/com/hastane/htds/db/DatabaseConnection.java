package com.hastane.htds.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Veritabani baglantisini yoneten TEK sinif.
 *
 * NEDEN AYRI BIR SINIF: yarin baglanti bilgileri degisirse
 * (sunucu adresi, port, kullanici adi) sadece BURAYI degistiririz.
 * DAO siniflari (PersonelDao, ArizaDao vs.) bu sinifi cagirir ama
 * MySQL'in URL formatini falan hic bilmez - onlar sadece
 * "bana bir baglanti ver" der.
 *
 * NEDEN "static" metod: bu sinifin birden fazla ornegine (instance)
 * ihtiyacimiz yok, sadece tek bir davranisi var (baglanti vermek).
 * Bu yuzden nesne olusturmaya gerek kalmadan direkt
 * DatabaseConnection.getConnection() seklinde cagiracagiz.
 */
public class DatabaseConnection {

    // XAMPP varsayilan ayarlari: kullanici "root", sifre BOS.
    // Eger XAMPP'de MySQL'e sifre koyduysan, PASSWORD degerini guncelle.
    private static final String URL = "jdbc:mysql://localhost:3306/htds_db?useSSL=false&serverTimezone=Europe/Istanbul&characterEncoding=UTF-8&useUnicode=true";
    private static final String KULLANICI = "root";
    private static final String SIFRE = "12345";

    // Constructor'i private yapiyoruz - bu sinifin "new DatabaseConnection()"
    // ile nesnesi olusturulmasin istiyoruz, sadece static metodu kullanilsin.
    private DatabaseConnection() {
    }

    private static boolean isMigrated = false;

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, KULLANICI, SIFRE);
        
        // HTDS: Otomatik Veritabanı Göçü (Migration)
        if (!isMigrated) {
            try {
                java.sql.ResultSet rs = conn.getMetaData().getColumns(null, null, "arizalar", "is_tipi");
                if (!rs.next()) {
                    try (java.sql.Statement stmt = conn.createStatement()) {
                        stmt.execute("ALTER TABLE arizalar ADD COLUMN is_tipi VARCHAR(50) DEFAULT 'ARIZA' AFTER durum");
                    }
                }
            } catch (Exception e) {
                System.out.println("HTDS Veritabanı Güncelleme Hatası (Yok sayılabilir): " + e.getMessage());
            }
            isMigrated = true;
        }
        return conn;
    }
}
