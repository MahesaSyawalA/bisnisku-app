package bisnisku.app.controllers;

import bisnisku.app.connection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class CategoryController {

    public Map<String, Object> getCategoryData(int userId, String kategori, int bulan, int tahun, boolean semuaBulan) {
        Map<String, Object> data = new HashMap<>();

        try (Connection conn = connection.getKoneksi()) {
            if (conn == null) {
                System.out.println("Error: Gagal mendapatkan koneksi database.");
                return data;
            }

            // 1. Menyusun Query SQL secara dinamis
            StringBuilder sql = new StringBuilder(
                    "SELECT tanggal, nama, nominal, kategori FROM transaksi WHERE user_id = ?"
            );

            if (kategori != null && !kategori.equals("Semua Kategori")) {
                sql.append(" AND kategori = ?");
            }

            if (semuaBulan) {
                sql.append(" AND YEAR(tanggal) = ?");
            } else {
                sql.append(" AND MONTH(tanggal) = ? AND YEAR(tanggal) = ?");
            }

            sql.append(" ORDER BY tanggal DESC");

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                ps.setInt(paramIndex++, userId);

                if (kategori != null && !kategori.equals("Semua Kategori")) {
                    ps.setString(paramIndex++, kategori);
                }

                if (semuaBulan) {
                    ps.setInt(paramIndex++, tahun);
                } else {
                    ps.setInt(paramIndex++, (bulan + 1));
                    ps.setInt(paramIndex++, tahun);
                }

                // 2. Eksekusi Query dan Olah Data
                try (ResultSet rs = ps.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel(
                            new String[]{"Tanggal", "Nama", "Nominal", "Kat."}, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false; 
                        }
                    };

                    int totalTransaksi = 0;
                    double totalNominal = 0;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

                    while (rs.next()) {
                        double nominal = rs.getDouble("nominal");
                        totalNominal += nominal;
                        totalTransaksi++;

                        // Format Tanggal
                        java.sql.Date dbDate = rs.getDate("tanggal");
                        String formattedDate = (dbDate != null) ? sdf.format(dbDate) : "";

                        // Format Nominal
                        String formattedNominal;
                        if (nominal >= 1000000) {
                            formattedNominal = "Rp " + String.format("%,.1f", nominal / 1000000) + " jt";
                        } else if (nominal >= 1000) {
                            formattedNominal = "Rp " + String.format("%,.0f", nominal / 1000) + " rb";
                        } else {
                            formattedNominal = "Rp " + String.format("%,.0f", nominal);
                        }

                        // Singkatan Kategori
                        String katRaw = rs.getString("kategori");
                        String katShort = katRaw;
                        if (katRaw.equalsIgnoreCase("Operasional")) {
                            katShort = "Op.";
                        } else if (katRaw.equalsIgnoreCase("Utilitas")) {
                            katShort = "Util.";
                        } else if (katRaw.equalsIgnoreCase("Gaya Hidup")) {
                            katShort = "GL";
                        } else if (katRaw.equalsIgnoreCase("Gaji Karyawan")) {
                            katShort = "Gaji";
                        }

                        model.addRow(new Object[]{
                            formattedDate,
                            rs.getString("nama"),
                            formattedNominal,
                            katShort
                        });
                    }

                    // 3. Masukkan hasil ke dalam Map
                    data.put("tableModel", model);
                    data.put("totalTransaksiStr", totalTransaksi + " transaksi ditemukan");

                    // Format Total Keseluruhan Nominal
                    if (totalNominal >= 1000000) {
                        data.put("totalNominalStr", "Total: Rp " + String.format("%,.2f", totalNominal / 1000000) + " jt");
                    } else if (totalNominal >= 1000) {
                        data.put("totalNominalStr", "Total: Rp " + String.format("%,.0f", totalNominal / 1000) + " rb");
                    } else {
                        data.put("totalNominalStr", "Total: Rp " + String.format("%,.0f", totalNominal));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error di getCategoryData: " + e.getMessage());
        }

        return data;
    }
}
