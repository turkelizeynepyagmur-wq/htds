package com.hastane.htds.dao;

import com.hastane.htds.db.DatabaseConnection;
import com.hastane.htds.model.ArizaLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArizaLogDao {

    /**
     * Belirli bir arızaya ait tüm işlem geçmişini (logları) tarih sırasına göre getirir.
     */
    public List<ArizaLog> findByArizaId(int arizaId) {
        List<ArizaLog> list = new ArrayList<>();
        String sql = "SELECT l.id, l.ariza_id, l.personel_id, l.eski_durum, l.yeni_durum, l.aciklama, l.tarih, " +
                     "p.ad, p.soyad " +
                     "FROM ariza_log l " +
                     "INNER JOIN personel p ON l.personel_id = p.id " +
                     "WHERE l.ariza_id = ? " +
                     "ORDER BY l.tarih ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, arizaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ArizaLog log = new ArizaLog();
                    log.setId(rs.getInt("id"));
                    log.setArizaId(rs.getInt("ariza_id"));
                    log.setPersonelId(rs.getInt("personel_id"));
                    log.setEskiDurum(rs.getString("eski_durum"));
                    log.setYeniDurum(rs.getString("yeni_durum"));
                    log.setAciklama(rs.getString("aciklama"));
                    
                    java.sql.Timestamp ts = rs.getTimestamp("tarih");
                    if (ts != null) {
                        log.setTarih(ts.toLocalDateTime());
                    }
                    
                    log.setPersonelAdSoyad(rs.getString("ad") + " " + rs.getString("soyad"));
                    list.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("ArizaLogDao.findByArizaId hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}
