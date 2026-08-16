package com.hastane.htds.dao;

import com.hastane.htds.db.DatabaseConnection;
import com.hastane.htds.model.Ariza;
import com.hastane.htds.model.Durum;
import com.hastane.htds.model.IsTipi;
import com.hastane.htds.model.Oncelik;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.hastane.htds.model.Rol;
import com.hastane.htds.model.Personel;

public class ArizaDao {

    private static final String BASE_SELECT = 
            "SELECT a.id, a.baslik, a.aciklama, a.kategori_id, a.departman_id, a.oncelik, a.durum, " +
            "a.bildiren_personel_id, a.atanan_teknisyen_id, a.ip_adresi, a.is_tipi, a.olusturma_tarihi, a.cozum_hedef_tarihi, a.guncelleme_tarihi, " +
            "k.ad AS kategori_ad, d.ad AS departman_ad, " +
            "pb.ad AS bildiren_ad, pb.soyad AS bildiren_soyad, " +
            "pt.ad AS teknisyen_ad, pt.soyad AS teknisyen_soyad " +
            "FROM ariza a " +
            "INNER JOIN kategori k ON a.kategori_id = k.id " +
            "LEFT JOIN departman d ON a.departman_id = d.id " +
            "INNER JOIN personel pb ON a.bildiren_personel_id = pb.id " +
            "LEFT JOIN personel pt ON a.atanan_teknisyen_id = pt.id ";

    /**
     * Yeni bir arıza bildirimi kaydeder.
     */
    public boolean insert(Ariza ariza) {
        String sql = "INSERT INTO ariza (baslik, aciklama, kategori_id, departman_id, oncelik, durum, " +
                     "bildiren_personel_id, atanan_teknisyen_id, ip_adresi, is_tipi, olusturma_tarihi, cozum_hedef_tarihi, guncelleme_tarihi) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ariza.getBaslik());
            ps.setString(2, ariza.getAciklama());
            ps.setInt(3, ariza.getKategoriId());
            
            if (ariza.getDepartmanId() != null) {
                ps.setInt(4, ariza.getDepartmanId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            
            ps.setString(5, ariza.getOncelik().name());
            ps.setString(6, ariza.getDurum().name());
            ps.setInt(7, ariza.getBildirenPersonelId());
            
            if (ariza.getAtananTeknisyenId() != null) {
                ps.setInt(8, ariza.getAtananTeknisyenId());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            
            ps.setString(9, ariza.getIpAdresi());
            ps.setString(10, ariza.getIsTipi() != null ? ariza.getIsTipi().name() : "ARIZA");
            
            int hours = 24;
            switch(ariza.getOncelik()) {
                case ACIL: hours = 1; break;
                case YUKSEK: hours = 4; break;
                case NORMAL: hours = 24; break;
                case DUSUK: hours = 48; break;
            }
            java.sql.Timestamp hedef = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now().plusHours(hours));
            ps.setTimestamp(11, hedef);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("ArizaDao.insert hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sistemdeki tüm arızaları listeler (ADMIN için).
     */
    public List<Ariza> findAll() {
        List<Ariza> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY a.olusturma_tarihi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(parseAriza(rs));
            }
        } catch (SQLException e) {
            System.err.println("ArizaDao.findAll hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Belirli bir personelin bildirdiği arızaları listeler (PERSONEL için).
     */
    public List<Ariza> findByBildiren(int personelId) {
        List<Ariza> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE a.bildiren_personel_id = ? ORDER BY a.olusturma_tarihi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, personelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parseAriza(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ArizaDao.findByBildiren hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Bir departmanın tüm arızalarını listeler (YÖNETİCİ için).
     */
    public List<Ariza> findByDepartman(int departmanId) {
        List<Ariza> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE a.departman_id = ? ORDER BY a.olusturma_tarihi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parseAriza(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ArizaDao.findByDepartman hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Teknisyenin üzerine atanan veya henüz atanmamış olan tüm arızaları listeler.
     */
    public List<Ariza> findByTeknisyen(int teknisyenId) {
        List<Ariza> list = new ArrayList<>();
        String sql = BASE_SELECT + 
                     "WHERE a.atanan_teknisyen_id = ? OR a.atanan_teknisyen_id IS NULL " +
                     "ORDER BY a.olusturma_tarihi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teknisyenId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(parseAriza(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ArizaDao.findByTeknisyen hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Arızanın durumunu ve teknisyen atamasını günceller, işlem geçmişini ariza_log tablosuna yazar (Transaction).
     */
    public boolean updateStatusAndAssignment(int arizaId, Durum yeniDurum, Integer teknisyenId, int guncelleyenId, String aciklama) {
        String queryCurrent = "SELECT durum, atanan_teknisyen_id FROM ariza WHERE id = ?";
        String updateAriza = "UPDATE ariza SET durum = ?, atanan_teknisyen_id = ?, guncelleme_tarihi = NOW() WHERE id = ?";
        String insertLog = "INSERT INTO ariza_log (ariza_id, personel_id, eski_durum, yeni_durum, aciklama, tarih) " +
                            "VALUES (?, ?, ?, ?, ?, NOW())";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Transaction başlat

            String eskiDurum = null;
            // 1. Mevcut durumu sorgula
            try (PreparedStatement psCurrent = conn.prepareStatement(queryCurrent)) {
                psCurrent.setInt(1, arizaId);
                try (ResultSet rs = psCurrent.executeQuery()) {
                    if (rs.next()) {
                        eskiDurum = rs.getString("durum");
                    } else {
                        throw new SQLException("Arıza kaydı bulunamadı: ID " + arizaId);
                    }
                }
            }

            // 2. Arızayı güncelle
            try (PreparedStatement psUpdate = conn.prepareStatement(updateAriza)) {
                psUpdate.setString(1, yeniDurum.name());
                if (teknisyenId != null) {
                    psUpdate.setInt(2, teknisyenId);
                } else {
                    psUpdate.setNull(2, java.sql.Types.INTEGER);
                }
                psUpdate.setInt(3, arizaId);
                psUpdate.executeUpdate();
            }

            // 3. Log tablosuna ekle
            try (PreparedStatement psLog = conn.prepareStatement(insertLog)) {
                psLog.setInt(1, arizaId);
                psLog.setInt(2, guncelleyenId);
                psLog.setString(3, eskiDurum);
                psLog.setString(4, yeniDurum.name());
                psLog.setString(5, aciklama != null && !aciklama.isBlank() ? aciklama : "Durum güncellendi.");
                psLog.executeUpdate();
            }

            conn.commit(); // Başarılıysa kaydet
            return true;

        } catch (SQLException e) {
            System.err.println("ArizaDao.updateStatusAndAssignment işlem hatası (Rollback yapılıyor): " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Kullanıcı rolüne göre arızaların durum dağılımı istatistiklerini getirir.
     */
    public Map<String, Integer> getStats(Personel user) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("bekleyen", 0);
        stats.put("islemde", 0);
        stats.put("cozulen", 0);

        if (user == null) return stats;

        Rol rol = user.getRol();
        String sql = "SELECT durum, COUNT(*) as adet FROM ariza a ";
        String where = "";

        if (rol == Rol.YONETICI) {
            where = "WHERE a.departman_id = ? ";
        } else if (rol == Rol.TEKNISYEN) {
            where = "WHERE a.atanan_teknisyen_id = ? OR a.atanan_teknisyen_id IS NULL ";
        } else if (rol == Rol.PERSONEL) {
            where = "WHERE a.bildiren_personel_id = ? ";
        }

        sql += where + "GROUP BY durum";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (rol == Rol.YONETICI) {
                ps.setInt(1, user.getDepartmanId() != null ? user.getDepartmanId() : 0);
            } else if (rol == Rol.TEKNISYEN) {
                ps.setInt(1, user.getId());
            } else if (rol == Rol.PERSONEL) {
                ps.setInt(1, user.getId());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String durumStr = rs.getString("durum").toUpperCase();
                    int adet = rs.getInt("adet");

                    if (durumStr.equals("YENI") || durumStr.equals("ATANDI")) {
                        stats.put("bekleyen", stats.get("bekleyen") + adet);
                    } else if (durumStr.equals("ISLEMDE")) {
                        stats.put("islemde", stats.get("islemde") + adet);
                    } else if (durumStr.equals("COZULDU") || durumStr.equals("KAPATILDI")) {
                        stats.put("cozulen", stats.get("cozulen") + adet);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ArizaDao.getStats hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Isı Haritası için departman bazlı aktif arızaların dağılımını getirir.
     */
    public Map<String, Integer> getActiveFaultsByDepartment() {
        Map<String, Integer> deptStats = new HashMap<>();
        String sql = "SELECT d.ad, COUNT(a.id) as adet " +
                     "FROM ariza a " +
                     "LEFT JOIN departman d ON a.departman_id = d.id " +
                     "WHERE a.durum IN ('YENI', 'ATANDI', 'ISLEMDE') " +
                     "GROUP BY d.ad";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String deptName = rs.getString("ad");
                if (deptName == null) deptName = "Genel / Belirsiz";
                deptStats.put(deptName, rs.getInt("adet"));
            }
        } catch (SQLException e) {
            System.err.println("ArizaDao.getActiveFaultsByDepartment hatası: " + e.getMessage());
        }
        return deptStats;
    }

    /**
     * Yönetici Dashboard'u için ilgili departmanın kategori veya öncelik bazlı arıza dağılımı.
     * Basitlik açısından kategorilere göre arıza sayılarını dönebiliriz.
     */
    public Map<String, Integer> getDepartmentFaultStats(int departmanId) {
        Map<String, Integer> catStats = new HashMap<>();
        String sql = "SELECT k.ad, COUNT(a.id) as adet " +
                     "FROM ariza a " +
                     "INNER JOIN kategori k ON a.kategori_id = k.id " +
                     "WHERE a.departman_id = ? AND a.durum != 'KAPATILDI' " +
                     "GROUP BY k.ad";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    catStats.put(rs.getString("ad"), rs.getInt("adet"));
                }
            }
        } catch (SQLException e) {
            System.err.println("ArizaDao.getDepartmentFaultStats hatası: " + e.getMessage());
        }
        return catStats;
    }

    // Ortak Parser Metodu
    private Ariza parseAriza(ResultSet rs) throws SQLException {
        Ariza a = new Ariza();
        a.setId(rs.getInt("id"));
        a.setBaslik(rs.getString("baslik"));
        a.setAciklama(rs.getString("aciklama"));
        a.setKategoriId(rs.getInt("kategori_id"));
        
        int deptId = rs.getInt("departman_id");
        a.setDepartmanId(rs.wasNull() ? null : deptId);
        
        a.setOncelik(Oncelik.valueOf(rs.getString("oncelik").toUpperCase()));
        a.setDurum(Durum.valueOf(rs.getString("durum").toUpperCase()));
        
        String tip = rs.getString("is_tipi");
        if (tip != null && !tip.isBlank()) {
            a.setIsTipi(IsTipi.valueOf(tip.toUpperCase()));
        } else {
            a.setIsTipi(IsTipi.ARIZA);
        }
        
        a.setBildirenPersonelId(rs.getInt("bildiren_personel_id"));
        
        int tekId = rs.getInt("atanan_teknisyen_id");
        a.setAtananTeknisyenId(rs.wasNull() ? null : tekId);
        
        java.sql.Timestamp t1 = rs.getTimestamp("olusturma_tarihi");
        if (t1 != null) a.setOlusturmaTarihi(t1.toLocalDateTime());
        
        java.sql.Timestamp tSla = rs.getTimestamp("cozum_hedef_tarihi");
        if (tSla != null) a.setCozumHedefTarihi(tSla.toLocalDateTime());
        
        java.sql.Timestamp t2 = rs.getTimestamp("guncelleme_tarihi");
        if (t2 != null) a.setGuncellemeTarihi(t2.toLocalDateTime());

        // Joinli alanlar
        a.setKategoriAd(rs.getString("kategori_ad"));
        a.setDepartmanAd(rs.getString("departman_ad") != null ? rs.getString("departman_ad") : "Genel");
        
        String bAd = rs.getString("bildiren_ad");
        String bSoyad = rs.getString("bildiren_soyad");
        a.setBildirenAdSoyad(bAd + " " + bSoyad);
        
        String tAd = rs.getString("teknisyen_ad");
        String tSoyad = rs.getString("teknisyen_soyad");
        if (tAd != null && tSoyad != null) {
            a.setTeknisyenAdSoyad(tAd + " " + tSoyad);
        } else {
            a.setTeknisyenAdSoyad("Atanmadı");
        }

        a.setIpAdresi(rs.getString("ip_adresi"));

        return a;
    }
}
