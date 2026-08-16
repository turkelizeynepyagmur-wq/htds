package com.hastane.htds;

import com.hastane.htds.db.DatabaseConnection;
import com.hastane.htds.util.HashUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DatabaseSeeder {

    public static void main(String[] args) {
        System.out.println(">>> Veritabanı tohumlama (seeding) başlatılıyor...");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. İlişkisel kısıtlamaları geçici olarak devre dışı bırakalım
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            // 2. Mevcut tabloları temizleyelim
            System.out.println("Mevcut veriler temizleniyor...");
            stmt.execute("TRUNCATE TABLE ariza_log");
            stmt.execute("TRUNCATE TABLE ariza");
            stmt.execute("TRUNCATE TABLE personel");
            stmt.execute("TRUNCATE TABLE kategori");
            stmt.execute("TRUNCATE TABLE departman");

            // Yabancı anahtar kontrolünü geri açalım
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            // 3. Departmanları ekleyelim
            System.out.println("Departmanlar ekleniyor...");
            String insertDept = "INSERT INTO departman (id, ad) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertDept)) {
                insertDepartment(ps, 1, "Bilgi İşlem (IT)");
                insertDepartment(ps, 2, "Teknik Servis");
                insertDepartment(ps, 3, "İnsan Kaynakları");
                insertDepartment(ps, 4, "Kardiyoloji Polikliniği");
                insertDepartment(ps, 5, "Dahiliye Polikliniği");
                insertDepartment(ps, 6, "Göz Hastalıkları Polikliniği");
                insertDepartment(ps, 7, "Kulak Burun Boğaz Polikliniği");
                insertDepartment(ps, 8, "Çocuk Sağlığı Polikliniği");
                insertDepartment(ps, 9, "Kadın Doğum Polikliniği");
                insertDepartment(ps, 10, "Genel Cerrahi Polikliniği");
                insertDepartment(ps, 11, "Acil Servis");
            }

            // 4. Kategorileri ekleyelim
            System.out.println("Kategoriler ekleniyor...");
            String insertKat = "INSERT INTO kategori (id, ad) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertKat)) {
                insertCategory(ps, 1, "Yazılım / Bilgi Sistemi");
                insertCategory(ps, 2, "Donanım / Bilgisayar");
                insertCategory(ps, 3, "Ağ / İnternet Hataları");
                insertCategory(ps, 4, "Tıbbi Cihaz Arızası");
            }

            // 5. Personelleri ekleyelim (TCKimlikValidator uyumlu, zenginleştirilmiş kayıtlar)
            System.out.println("Test kullanıcıları (Personel) ekleniyor...");
            String insertPers = "INSERT INTO personel (tc_no, ad, soyad, sifre_hash, rol, departman_id, dahili_no, aktif, olusturma_tarihi, kullanici_adi) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 1, NOW(), ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertPers)) {
                // Temel Kullanıcılar
                insertPersonel(ps, "10000000146", "Ahmet", "Yılmaz", HashUtil.hashPassword("12345"), "ADMIN", 1, "1001", "admin");
                insertPersonel(ps, "10000000214", "Mehmet", "Kaya", HashUtil.hashPassword("12345"), "TEKNISYEN", 2, "1002", "teknisyen");
                insertPersonel(ps, "10000000382", "Ayşe", "Demir", HashUtil.hashPassword("12345"), "PERSONEL", 3, "1003", "personel");
                insertPersonel(ps, "10000000450", "Fatma", "Şahin", HashUtil.hashPassword("12345"), "YONETICI", 4, "1004", "fatma.sahin");
                
                // Ekstra Zenginleştirilmiş Rehber ve Test Kayıtları
                insertPersonel(ps, "10000000528", "Serkan", "Bulut", HashUtil.hashPassword("12345"), "YONETICI", 4, "2001", "serkan.bulut");
                insertPersonel(ps, "10000000696", "Elif", "Yıldız", HashUtil.hashPassword("12345"), "YONETICI", 5, "2002", "elif.yildiz");
                insertPersonel(ps, "10000000764", "Murat", "Çelik", HashUtil.hashPassword("12345"), "YONETICI", 8, "2003", "murat.celik");
                insertPersonel(ps, "10000000832", "Zeynep", "Koç", HashUtil.hashPassword("12345"), "YONETICI", 6, "2004", "zeynep.koc");
                
                insertPersonel(ps, "10000000900", "Canan", "Aksu", HashUtil.hashPassword("12345"), "PERSONEL", 11, "3001", "canan.aksu");
                insertPersonel(ps, "20000000114", "Aslı", "Yılmaz", HashUtil.hashPassword("12345"), "PERSONEL", 4, "3002", "asli.yilmaz");
                insertPersonel(ps, "20000000282", "Ali", "Kurt", HashUtil.hashPassword("12345"), "TEKNISYEN", 2, "1022", "ali.kurt");
                insertPersonel(ps, "20000000350", "Hakan", "Şen", HashUtil.hashPassword("12345"), "TEKNISYEN", 1, "1011", "hakan.sen");
                
                insertPersonel(ps, "20000000428", "Melis", "Kaya", HashUtil.hashPassword("12345"), "PERSONEL", 3, "1033", "melis.kaya");
                insertPersonel(ps, "20000000596", "Ömer", "Aslan", HashUtil.hashPassword("12345"), "YONETICI", 10, "2005", "omer.aslan");
                insertPersonel(ps, "20000000664", "Büşra", "Demir", HashUtil.hashPassword("12345"), "PERSONEL", 7, "2006", "busra.demir");
                insertPersonel(ps, "20000000732", "Seda", "Güler", HashUtil.hashPassword("12345"), "PERSONEL", 9, "2007", "seda.guler");
            }

            System.out.println(">>> TOHUMLAMA BA??ARIYLA TAMAMLANDI!");

        } catch (Exception e) {
            System.err.println("Tohumlama sırasında hata oluştu:");
            e.printStackTrace();
        }
    }

    private static void insertDepartment(PreparedStatement ps, int id, String ad) throws Exception {
        ps.setInt(1, id);
        ps.setString(2, ad);
        ps.executeUpdate();
    }

    private static void insertCategory(PreparedStatement ps, int id, String ad) throws Exception {
        ps.setInt(1, id);
        ps.setString(2, ad);
        ps.executeUpdate();
    }

    private static void insertPersonel(PreparedStatement ps, String tc, String ad, String soyad, String hash, String rol, int deptId, String dahili, String kullaniciAdi) throws Exception {
        ps.setString(1, tc);
        ps.setString(2, ad);
        ps.setString(3, soyad);
        ps.setString(4, hash);
        ps.setString(5, rol);
        ps.setInt(6, deptId);
        ps.setString(7, dahili);
        ps.setString(8, kullaniciAdi);
        ps.executeUpdate();
    }
}
