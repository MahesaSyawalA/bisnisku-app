/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bisnisku.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sanchie
 */
public class TrackYourExpensesController {

    connection conn;

    public TrackYourExpensesController() {
        conn = new connection();
    }

    public boolean simpanTransaksi(String nama, double nominal, String kategori, java.sql.Date tanggal) {

        String sql = "INSERT INTO transaksi (user_id, nama, nominal, kategori, tanggal) VALUES (?,?, ?, ?, ?)";

        try (PreparedStatement ps = conn.con.prepareStatement(sql)) {

            int userId = UserSession.getUserId();
            System.out.println(userId);

            ps.setInt(1, userId);
            ps.setString(2, nama);
            ps.setDouble(3, nominal);
            ps.setString(4, kategori);
            ps.setDate(5, tanggal);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error Insert: " + e.getMessage());
            return false;
        }
    }

    public DefaultTableModel getModelTransaksi() {
        String[] kolom = {"Nama", "Nominal", "Kategori"};
        DefaultTableModel model = new DefaultTableModel(null, kolom);
        int userId = UserSession.getUserId();

        String sql = "SELECT nama, nominal, kategori FROM transaksi WHERE user_id = ? ORDER BY id DESC LIMIT 10";

        try (PreparedStatement ps = conn.con.prepareStatement(sql)) {

            System.out.println(userId);

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nm = rs.getString("nama");

                    // Format mata uang Rupiah
                    String nom = "Rp " + String.format("%,.0f", rs.getDouble("nominal"));
                    String ktg = rs.getString("kategori");

                    model.addRow(new Object[]{nm, nom, ktg});
                }
            }

        } catch (Exception e) {
            System.err.println("Error Select: " + e.getMessage());
        }
        return model;
    }

}
