package com.hastane.htds.dao;

import com.hastane.htds.db.DatabaseConnection;
import com.hastane.htds.model.Kategori;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class KategoriDao {

    /**
     * Veritabanındaki tüm arıza kategorilerini listeler.
     */
    public List<Kategori> findAll() {
        List<Kategori> kategoriler = new ArrayList<>();
        String sql = "SELECT id, ad FROM kategori ORDER BY ad ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Kategori k = new Kategori();
                k.setId(rs.getInt("id"));
                k.setAd(rs.getString("ad"));
                kategoriler.add(k);
            }

        } catch (Exception e) {
            System.err.println("KategoriDao.findAll hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return kategoriler;
    }
}
