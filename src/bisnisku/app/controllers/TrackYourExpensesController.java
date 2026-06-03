/*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bisnisku.app.controllers;

import bisnisku.app.UserSession;
import bisnisku.app.connection;
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

    public boolean isSaldoCukup(double nominalPengeluaran) {
        int userId = UserSession.getUserId();
        double modalAwal = 0;
        double totalPemasukan = 0;
        double totalKeluar = 0;

        try {
            // 1. Ambil Modal Awal
            String sqlModal = "SELECT modal_awal FROM bisnis_profile WHERE user_id = ?";
            try (PreparedStatement ps = conn.getConnection().prepareStatement(sqlModal)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        modalAwal = rs.getDouble("modal_awal");
                    }
                }
            }

            // 2. Ambil Total Pemasukan
            String sqlMasuk = "SELECT COALESCE(SUM(total_pendapatan),0) AS total FROM pemasukan_harian WHERE id_user = ?";
            try (PreparedStatement ps = conn.getConnection().prepareStatement(sqlMasuk)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalPemasukan = rs.getDouble("total");
                    }
                }
            }

            // 3. Ambil Total Pengeluaran Saat Ini
            String sqlKeluar = "SELECT COALESCE(SUM(nominal),0) AS total FROM transaksi WHERE user_id = ?";
            try (PreparedStatement ps = conn.getConnection().prepareStatement(sqlKeluar)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalKeluar = rs.getDouble("total");
                    }
                }
            }

            // 4. Hitung Saldo dan Bandingkan
            double saldoSaatIni = modalAwal + totalPemasukan - totalKeluar;

            // Jika saldo saat ini lebih besar atau sama dengan nominal yang mau dikeluarkan, return true
            return saldoSaatIni >= nominalPengeluaran;

        } catch (Exception e) {
            System.err.println("Error cek saldo: " + e.getMessage());
            return false;
        }
    }

    public boolean simpanTransaksi(String nama, double nominal, String kategori, java.sql.Date tanggal) {

        String sql = "INSERT INTO transaksi (user_id, nama, nominal, kategori, tanggal) VALUES (?,?, ?, ?, ?)";

        try (PreparedStatement ps = conn.getConnection().prepareStatement(sql)) {

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
        String[] kolom = {"ID", "Nama", "Nominal", "Kategori"};
        DefaultTableModel model = new DefaultTableModel(null, kolom);
        int userId = UserSession.getUserId();

        String sql = "SELECT id, nama, nominal, kategori FROM transaksi WHERE user_id = ? ORDER BY id DESC LIMIT 10";

        try (PreparedStatement ps = conn.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nm = rs.getString("nama");
                    String nom = "Rp " + String.format("%,.0f", rs.getDouble("nominal"));
                    String ktg = rs.getString("kategori");

                    model.addRow(new Object[]{id, nm, nom, ktg});
                }
            }

        } catch (Exception e) {
            System.err.println("Error Select: " + e.getMessage());
        }

        return model;
    }

    public boolean deleteTransaksi(int id) {
        String sql = "DELETE FROM transaksi WHERE id = ? AND user_id = ?";

        try (PreparedStatement ps = conn.getConnection().prepareStatement(sql)) {

            int userId = UserSession.getUserId();

            ps.setInt(1, id);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error Delete: " + e.getMessage());
            return false;
        }
    }

}
