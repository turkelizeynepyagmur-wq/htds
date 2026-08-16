package com.hastane.htds.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUpdate {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Veritabanına bağlanıldı.");

            // 1. Sütunu ekle (eğer yoksa)
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE personel ADD COLUMN kullanici_adi VARCHAR(50) UNIQUE AFTER tc_no;");
                System.out.println("kullanici_adi sütunu eklendi.");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate column name")) {
                    System.out.println("kullanici_adi sütunu zaten var.");
                } else {
                    throw e;
                }
            }

            // 2. Mevcut kayıtları ad.soyad formatında güncelle
            String selectSql = "SELECT id, ad, soyad FROM personel WHERE kullanici_adi IS NULL";
            String updateSql = "UPDATE personel SET kullanici_adi = ? WHERE id = ?";
            
            int guncellenen = 0;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 ResultSet rs = selectStmt.executeQuery();
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String ad = rs.getString("ad").toLowerCase().replaceAll("[^a-zğüşöçı]", "").trim();
                    String soyad = rs.getString("soyad").toLowerCase().replaceAll("[^a-zğüşöçı]", "").trim();
                    String usernameBase = ad + "." + soyad;
                    
                    // Benzersiz yapmak için (şimdilik basit bir ek ekleyelim eğer hata verirse diye ama genelde ad.soyad yeterli)
                    // Hızlıca ad.soyad yapalım
                    updateStmt.setString(1, usernameBase);
                    updateStmt.setInt(2, id);
                    try {
                        updateStmt.executeUpdate();
                        guncellenen++;
                    } catch (SQLException ex) {
                        // Eğer aynısından varsa sonuna id ekleyelim
                        updateStmt.setString(1, usernameBase + id);
                        updateStmt.executeUpdate();
                        guncellenen++;
                    }
                }
            }
            System.out.println("Toplam " + guncellenen + " kaydın kullanıcı adı güncellendi.");

            // 3. tc_no değerini kullanan hızlı test butonları için özel kullanıcı adlarını ayarlayalım
            // Admin: 10000000146 -> admin
            // Teknisyen: 10000000214 -> teknisyen
            // Personel: 10000000382 -> personel
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("UPDATE personel SET kullanici_adi = 'admin' WHERE tc_no = '10000000146'");
                stmt.execute("UPDATE personel SET kullanici_adi = 'teknisyen' WHERE tc_no = '10000000214'");
                stmt.execute("UPDATE personel SET kullanici_adi = 'personel' WHERE tc_no = '10000000382'");
                System.out.println("Test kullanıcıları özel kullanıcı adlarına güncellendi.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
