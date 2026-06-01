package bisnisku.app.controllers;

import bisnisku.app.connection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class RekapController {

    // Method untuk mendapatkan daftar bulan dari transaksi
    public List<String> getAvailableMonths(int userId) {
        List<String> months = new ArrayList<>();
        months.add("Semua Bulan"); // Opsi default

        String sql = "SELECT DISTINCT DATE_FORMAT(tanggal, '%Y-%m') AS bulan FROM transaksi WHERE user_id = ? ORDER BY bulan DESC";

        try (Connection conn = connection.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (conn == null) return months;

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    months.add(rs.getString("bulan"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error di getAvailableMonths: " + e.getMessage());
        }

        return months;
    }

    // Method untuk mengambil data ringkasan rekap & tabel
    public Map<String, Object> getRekapData(int userId, String bulan) {
        Map<String, Object> data = new HashMap<>();

        try (Connection conn = connection.getKoneksi()) {
            if (conn == null) {
                System.out.println("Error: Gagal mendapatkan koneksi database.");
                return data;
            }

            double modalAwal = 0;
            double totalKeluar = 0;
            double totalPemasukan = 0;

            // 1. Ambil Modal Awal
            String sqlModal = "SELECT modal_awal FROM bisnis_profile WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlModal)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) modalAwal = rs.getDouble("modal_awal");
                }
            }

            // 2. Ambil Total Pengeluaran (Keseluruhan)
            String sqlKeluar = "SELECT COALESCE(SUM(nominal),0) AS total FROM transaksi WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlKeluar)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) totalKeluar = rs.getDouble("total");
                }
            }

            // 3. Ambil Total Pemasukan (Keseluruhan)
            String sqlMasuk = "SELECT COALESCE(SUM(total_pendapatan),0) AS total FROM pemasukan_harian WHERE id_user = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlMasuk)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) totalPemasukan = rs.getDouble("total");
                }
            }

            // 4. Kalkulasi Saldo dan Formatting
            double saldoTotal = modalAwal + totalPemasukan - totalKeluar;
            
            data.put("modalAwalStr", "RP. " + String.format("%,.0f", modalAwal));
            data.put("saldoTotalStr", "RP. " + String.format("%,.0f", saldoTotal));
            data.put("isSaldoNegative", saldoTotal < 0);

            // 5. Query Breakdown Tabel (Berdasarkan filter bulan)
            String sqlTabel;
            boolean isFilterBulan = (bulan != null && !bulan.equals("Semua Bulan"));

            if (!isFilterBulan) {
                sqlTabel = "SELECT 'Pemasukan' AS kategori, SUM(total_pendapatan) AS total_nominal, "
                        + "DATE_FORMAT(tanggal, '%Y-%m') AS bulan_transaksi "
                        + "FROM pemasukan_harian WHERE id_user = ? "
                        + "UNION ALL "
                        + "SELECT kategori, SUM(nominal) AS total_nominal, "
                        + "DATE_FORMAT(tanggal, '%Y-%m') AS bulan_transaksi "
                        + "FROM transaksi WHERE user_id = ? "
                        + "GROUP BY bulan_transaksi, kategori "
                        + "ORDER BY bulan_transaksi DESC, total_nominal DESC";
            } else {
                sqlTabel = "SELECT 'Pemasukan' AS kategori, SUM(total_pendapatan) AS total_nominal, "
                        + "DATE_FORMAT(tanggal, '%Y-%m') AS bulan_transaksi "
                        + "FROM pemasukan_harian WHERE id_user = ? AND DATE_FORMAT(tanggal, '%Y-%m') = ? "
                        + "UNION ALL "
                        + "SELECT kategori, SUM(nominal) AS total_nominal, "
                        + "DATE_FORMAT(tanggal, '%Y-%m') AS bulan_transaksi "
                        + "FROM transaksi WHERE user_id = ? AND DATE_FORMAT(tanggal, '%Y-%m') = ? "
                        + "GROUP BY bulan_transaksi, kategori "
                        + "ORDER BY total_nominal DESC";
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlTabel)) {
                if (!isFilterBulan) {
                    ps.setInt(1, userId);
                    ps.setInt(2, userId);
                } else {
                    ps.setInt(1, userId);
                    ps.setString(2, bulan);
                    ps.setInt(3, userId);
                    ps.setString(4, bulan);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel(
                            new String[]{"Kategori", "Total Nominal", "Bulan"}, 0);

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("kategori"),
                            "RP. " + String.format("%,.0f", rs.getDouble("total_nominal")),
                            rs.getString("bulan_transaksi")
                        });
                    }
                    data.put("tableModel", model);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error di RekapController: " + e.getMessage());
        }

        return data;
    }
}