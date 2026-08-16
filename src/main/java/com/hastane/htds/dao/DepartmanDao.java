package com.hastane.htds.dao;

import com.hastane.htds.db.DatabaseConnection;
import com.hastane.htds.model.Departman;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DepartmanDao {

    /**
     * Veritabanındaki tüm departmanları listeler.
     */
    public List<Departman> findAll() {
        List<Departman> departmanlar = new ArrayList<>();
        String sql = "SELECT id, ad FROM departman ORDER BY ad ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Departman d = new Departman();
                d.setId(rs.getInt("id"));
                d.setAd(rs.getString("ad"));
                departmanlar.add(d);
            }

        } catch (Exception e) {
            System.err.println("DepartmanDao.findAll hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return departmanlar;
    }
}
