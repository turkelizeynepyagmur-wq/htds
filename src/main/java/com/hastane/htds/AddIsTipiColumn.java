package com.hastane.htds;

import com.hastane.htds.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;

public class AddIsTipiColumn {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
             
             // First check if column exists
             java.sql.ResultSet rs = conn.getMetaData().getColumns(null, null, "arizalar", "is_tipi");
             if (!rs.next()) {
                 String sql = "ALTER TABLE arizalar ADD COLUMN is_tipi VARCHAR(50) DEFAULT 'ARIZA' AFTER durum";
                 stmt.executeUpdate(sql);
                 System.out.println("is_tipi column added successfully.");
             } else {
                 System.out.println("is_tipi column already exists.");
             }
             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
