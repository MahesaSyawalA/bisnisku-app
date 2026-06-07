package bisnisku.app.controllers;

import bisnisku.app.connection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class LeaderboardController {

    public Map<String, Object> getLeaderboardData() {
        Map<String, Object> data = new HashMap<>();

        // 1. Set default podium jika data kosong / belum ada
        data.put("top1Name", "-");
        data.put("top2Name", "-");
        data.put("top3Name", "-");

        // Buat model tabel kosong
        DefaultTableModel model = new DefaultTableModel(new String[]{"Nama", "Nominal", "Tanggal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection conn = connection.getKoneksi()) {
            if (conn == null) {
                System.err.println("Gagal terhubung ke database!");
                return data; // Return map default
            }

            // Query Leaderboard Global
            String sql = "SELECT b.nama_bisnis, "
                    + "(b.modal_awal + COALESCE(p.total_pemasukan, 0) - COALESCE(t.total_pengeluaran, 0)) AS saldo "
                    + "FROM bisnis_profile b "
                    + "LEFT JOIN (SELECT id_user, SUM(total_pendapatan) AS total_pemasukan FROM pemasukan_harian GROUP BY id_user) p "
                    + "  ON b.user_id = p.id_user "
                    + "LEFT JOIN (SELECT user_id, SUM(nominal) AS total_pengeluaran FROM transaksi GROUP BY user_id) t "
                    + "  ON b.user_id = t.user_id "
                    + "ORDER BY saldo DESC";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

                int rank = 1;
                while (rs.next()) {
                    String namaBisnis = rs.getString("nama_bisnis");
                    long saldoBersih = rs.getLong("saldo");
                    String tanggal = "-"; // Menyesuaikan logic asli kamu

                    // Masukkan data ke model tabel
                    model.addRow(new Object[]{namaBisnis, "Rp " + String.format("%,d", saldoBersih), tanggal});

                    // Format nama untuk desain UI Label (Width: 60px)
                    String formattedName = "<html><div style='text-align: center; width: 60px; word-wrap: break-word;'>" + namaBisnis + "</div></html>";

                    // Alokasikan ke podium
                    if (rank == 1) {
                        data.put("top1Name", formattedName);
                    } else if (rank == 2) {
                        data.put("top2Name", formattedName);
                    } else if (rank == 3) {
                        data.put("top3Name", formattedName);
                    }

                    rank++;
                }
            }

            // Masukkan model tabel yang sudah diisi ke dalam Map
            data.put("tableModel", model);

        } catch (Exception e) {
            System.err.println("Error memuat Leaderboard: " + e.getMessage());
        }

        return data;
    }
}
