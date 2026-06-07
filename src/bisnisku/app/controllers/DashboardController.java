package bisnisku.app.controllers;

import bisnisku.app.connection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class DashboardController {

    public Map<String, Object> getDashboardData(int userId) {

        Map<String, Object> data = new HashMap<>();

        try (Connection conn = connection.getKoneksi()) {
            if (conn == null) {
                return data;
            }

            double modalAwal = 0;
            String namaBisnis = "";
            double totalKeluar = 0;
            double totalPemasukan = 0;

            String sqlProfil = "SELECT modal_awal, nama_bisnis FROM bisnis_profile WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlProfil)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        modalAwal = rs.getDouble("modal_awal");
                        namaBisnis = rs.getString("nama_bisnis");
                    }
                }
            }

            String sqlKeluar = "SELECT COALESCE(SUM(nominal),0) AS total FROM transaksi WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlKeluar)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalKeluar = rs.getDouble("total");
                    }
                }
            }

            String sqlMasuk = "SELECT COALESCE(SUM(total_pendapatan),0) AS total FROM pemasukan_harian WHERE id_user = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlMasuk)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalPemasukan = rs.getDouble("total");
                    }
                }
            }

            double saldoSaatIni = modalAwal + totalPemasukan - totalKeluar;
            double profit = totalPemasukan - totalKeluar;
            double persen = (modalAwal > 0) ? (saldoSaatIni / modalAwal) * 100 : 0;

            data.put("namaBisnis", namaBisnis);
            data.put("modalAwalStr", "RP. " + String.format("%,.0f", modalAwal));
            data.put("totalKeluarStr", "RP. " + String.format("%,.0f", totalKeluar));
            data.put("totalPemasukanStr", "RP. " + String.format("%,.0f", totalPemasukan));
            data.put("saldoSaatIniStr", "RP. " + String.format("%,.0f", saldoSaatIni));
            data.put("isSaldoNegative", saldoSaatIni < 0);

            int progressValue = (int) Math.min(persen, 100);
            data.put("progressValue", Math.max(progressValue, 0));

            if (profit > 0) {
                data.put("levelText", "Level: Bisnis Berkembang");
                data.put("levelDescText", "Profit +" + String.format("%.1f", persen - 100) + "% dari modal awal");
                data.put("levelStatus", 1);
                data.put("iconPath", "/bisnisku/app/assets/trending-up.png");

            } else if (persen >= 50) {
                data.put("levelText", "Level: Perlu Waspada");
                data.put("levelDescText", "Saldo tersisa " + String.format("%.1f", persen) + "% dari modal");
                data.put("levelStatus", 2);
                data.put("iconPath", "/bisnisku/app/assets/trending-down.png");

            } else {
                data.put("levelText", "Level: Kondisi Kritis");
                data.put("levelDescText", "Kerugian bisnis mulai besar");
                data.put("levelStatus", 3);
                data.put("iconPath", "/bisnisku/app/assets/siren.png");
            }

            String sqlTransaksiTerakhir = "SELECT nominal FROM transaksi WHERE user_id = ? ORDER BY id DESC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sqlTransaksiTerakhir)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data.put("transaksiTerakhirStr", "RP. " + String.format("%,.0f", rs.getDouble("nominal")));
                    } else {
                        data.put("transaksiTerakhirStr", "RP. 0");
                    }
                }
            }

            String sqlTabel = "SELECT nama, nominal, tanggal FROM transaksi WHERE user_id = ? ORDER BY tanggal DESC";
            try (PreparedStatement ps = conn.prepareStatement(sqlTabel)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {

                    DefaultTableModel model = new DefaultTableModel(new String[]{"Nama", "Nominal", "Tanggal"}, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("nama"),
                            "RP. " + String.format("%,.0f", rs.getDouble("nominal")),
                            rs.getDate("tanggal")
                        });
                    }
                    data.put("tableModel", model);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error Loading Dashboard Data: " + e.getMessage());
        }

        return data;
    }
}
