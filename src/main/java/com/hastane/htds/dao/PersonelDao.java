package com.hastane.htds.dao;

import com.hastane.htds.db.DatabaseConnection;
import com.hastane.htds.model.Personel;
import com.hastane.htds.model.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PersonelDao {

    /**
     * Verilen TC Kimlik Numarasına sahip ve aktif olan personeli veritabanından sorgular.
     */
    public Personel findByTcNo(String tcNo) {
        String sql = "SELECT p.id, p.tc_no, p.kullanici_adi, p.ad, p.soyad, p.sifre_hash, p.rol, p.departman_id, p.dahili_no, p.aktif, p.olusturma_tarihi, " +
                     "d.ad AS departman_ad " +
                     "FROM personel p " +
                     "LEFT JOIN departman d ON p.departman_id = d.id " +
                     "WHERE p.tc_no = ? AND p.aktif = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tcNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Personel personel = new Personel();
                    personel.setId(rs.getInt("id"));
                    personel.setTcNo(rs.getString("tc_no"));
                    personel.setKullaniciAdi(rs.getString("kullanici_adi"));
                    personel.setAd(rs.getString("ad"));
                    personel.setSoyad(rs.getString("soyad"));
                    personel.setSifreHash(rs.getString("sifre_hash"));
                    
                    String rolStr = rs.getString("rol");
                    if (rolStr != null) {
                        try {
                            personel.setRol(Rol.valueOf(rolStr.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Geçersiz rol değeri: " + rolStr);
                        }
                    }
                    
                    int deptId = rs.getInt("departman_id");
                    if (rs.wasNull()) {
                        personel.setDepartmanId(null);
                    } else {
                        personel.setDepartmanId(deptId);
                    }
                    
                    personel.setDahiliNo(rs.getString("dahili_no"));
                    personel.setDepartmanAd(rs.getString("departman_ad"));
                    personel.setAktif(rs.getBoolean("aktif"));
                    
                    java.sql.Timestamp ts = rs.getTimestamp("olusturma_tarihi");
                    if (ts != null) {
                        personel.setOlusturmaTarihi(ts.toLocalDateTime());
                    }

                    return personel;
                }
            }
        } catch (SQLException e) {
            System.err.println("PersonelDao.findByTcNo sorgusunda hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verilen Kullanıcı Adına sahip ve aktif olan personeli veritabanından sorgular.
     */
    public Personel findByKullaniciAdi(String kullaniciAdi) {
        String sql = "SELECT p.id, p.tc_no, p.kullanici_adi, p.ad, p.soyad, p.sifre_hash, p.rol, p.departman_id, p.dahili_no, p.aktif, p.olusturma_tarihi, " +
                     "d.ad AS departman_ad " +
                     "FROM personel p " +
                     "LEFT JOIN departman d ON p.departman_id = d.id " +
                     "WHERE p.kullanici_adi = ? AND p.aktif = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kullaniciAdi);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Personel personel = new Personel();
                    personel.setId(rs.getInt("id"));
                    personel.setTcNo(rs.getString("tc_no"));
                    personel.setKullaniciAdi(rs.getString("kullanici_adi"));
                    personel.setAd(rs.getString("ad"));
                    personel.setSoyad(rs.getString("soyad"));
                    personel.setSifreHash(rs.getString("sifre_hash"));
                    
                    String rolStr = rs.getString("rol");
                    if (rolStr != null) {
                        try {
                            personel.setRol(Rol.valueOf(rolStr.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Geçersiz rol değeri: " + rolStr);
                        }
                    }
                    
                    int deptId = rs.getInt("departman_id");
                    if (rs.wasNull()) {
                        personel.setDepartmanId(null);
                    } else {
                        personel.setDepartmanId(deptId);
                    }
                    
                    personel.setDahiliNo(rs.getString("dahili_no"));
                    personel.setDepartmanAd(rs.getString("departman_ad"));
                    personel.setAktif(rs.getBoolean("aktif"));
                    
                    java.sql.Timestamp ts = rs.getTimestamp("olusturma_tarihi");
                    if (ts != null) {
                        personel.setOlusturmaTarihi(ts.toLocalDateTime());
                    }

                    return personel;
                }
            }
        } catch (SQLException e) {
            System.err.println("PersonelDao.findByKullaniciAdi sorgusunda hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Hastanedeki tüm aktif personelleri listeler (Rehber ve Yönetim için).
     */
    public List<Personel> findAllActive() {
        List<Personel> list = new ArrayList<>();
        String sql = "SELECT p.id, p.tc_no, p.kullanici_adi, p.ad, p.soyad, p.sifre_hash, p.rol, p.departman_id, p.dahili_no, p.aktif, p.olusturma_tarihi, " +
                     "d.ad AS departman_ad " +
                     "FROM personel p " +
                     "LEFT JOIN departman d ON p.departman_id = d.id " +
                     "WHERE p.aktif = 1 " +
                     "ORDER BY p.ad ASC, p.soyad ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Personel p = new Personel();
                p.setId(rs.getInt("id"));
                p.setTcNo(rs.getString("tc_no"));
                p.setKullaniciAdi(rs.getString("kullanici_adi"));
                p.setAd(rs.getString("ad"));
                p.setSoyad(rs.getString("soyad"));
                p.setSifreHash(rs.getString("sifre_hash"));
                
                String rolStr = rs.getString("rol");
                if (rolStr != null) {
                    p.setRol(Rol.valueOf(rolStr.toUpperCase()));
                }
                
                int deptId = rs.getInt("departman_id");
                p.setDepartmanId(rs.wasNull() ? null : deptId);
                
                p.setDahiliNo(rs.getString("dahili_no"));
                p.setDepartmanAd(rs.getString("departman_ad"));
                p.setAktif(rs.getBoolean("aktif"));

                java.sql.Timestamp ts = rs.getTimestamp("olusturma_tarihi");
                if (ts != null) {
                    p.setOlusturmaTarihi(ts.toLocalDateTime());
                }

                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("PersonelDao.findAllActive sorgusunda hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Nöbetçi / Aktif teknisyenleri getirir.
     */
    public List<Personel> findTeknisyenler() {
        List<Personel> list = new ArrayList<>();
        String sql = "SELECT p.id, p.tc_no, p.kullanici_adi, p.ad, p.soyad, p.sifre_hash, p.rol, p.departman_id, p.dahili_no, p.aktif, p.olusturma_tarihi, " +
                     "d.ad AS departman_ad " +
                     "FROM personel p " +
                     "LEFT JOIN departman d ON p.departman_id = d.id " +
                     "WHERE p.aktif = 1 AND p.rol = 'TEKNISYEN' " +
                     "ORDER BY p.ad ASC LIMIT 2";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Personel p = new Personel();
                p.setId(rs.getInt("id"));
                p.setAd(rs.getString("ad"));
                p.setSoyad(rs.getString("soyad"));
                p.setDahiliNo(rs.getString("dahili_no"));
                p.setDepartmanAd(rs.getString("departman_ad"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("PersonelDao.findTeknisyenler sorgusunda hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Yeni personel ekleme.
     */
    public boolean insert(Personel p) {
        String sql = "INSERT INTO personel (tc_no, kullanici_adi, ad, soyad, sifre_hash, rol, departman_id, dahili_no, aktif, olusturma_tarihi) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getTcNo());
            ps.setString(2, p.getKullaniciAdi());
            ps.setString(3, p.getAd());
            ps.setString(4, p.getSoyad());
            ps.setString(5, p.getSifreHash());
            ps.setString(6, p.getRol().name());
            if (p.getDepartmanId() == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, p.getDepartmanId());
            }
            ps.setString(8, p.getDahiliNo());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        p.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("PersonelDao.insert hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Personel bilgilerini güncelleme.
     */
    public boolean update(Personel p) {
        String sql = "UPDATE personel SET tc_no = ?, kullanici_adi = ?, ad = ?, soyad = ?, rol = ?, departman_id = ?, dahili_no = ?, aktif = ?, sifre_hash = ? " +
                     "WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTcNo());
            ps.setString(2, p.getKullaniciAdi());
            ps.setString(3, p.getAd());
            ps.setString(4, p.getSoyad());
            ps.setString(5, p.getRol().name());
            if (p.getDepartmanId() == null) {
                ps.setNull(6, java.sql.Types.INTEGER);
            } else {
                ps.setInt(6, p.getDepartmanId());
            }
            ps.setString(7, p.getDahiliNo());
            ps.setBoolean(8, p.isAktif());
            ps.setString(9, p.getSifreHash());
            ps.setInt(10, p.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("PersonelDao.update hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Personel pasifleştirme (Silme).
     */
    public boolean delete(int id) {
        String sql = "UPDATE personel SET aktif = 0 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("PersonelDao.delete hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
