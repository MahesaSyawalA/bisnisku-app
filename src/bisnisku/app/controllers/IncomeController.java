
package bisnisku.app.controllers;

import bisnisku.app.connection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IncomeController {

    /**
     * Menyimpan total pendapatan harian ke database
     *
     * @param userId ID user yang sedang login
     * @param totalPendapatan Jumlah nominal pemasukan yang didapat
     * @return boolean true jika berhasil, false jika gagal
     */
    public boolean simpanPemasukan(int userId, long totalPendapatan) {
        // Validasi awal agar tidak menyimpan data kosong
        if (totalPendapatan <= 0) {
            return false;
        }

        String sql = "INSERT INTO pemasukan_harian (id_user, tanggal, total_pendapatan) VALUES (?, CURDATE(), ?)";

        try (Connection conn = connection.getKoneksi(); PreparedStatement pst = conn.prepareStatement(sql)) {

            if (conn == null) {
                return false;
            }

            // Bind parameter
            pst.setInt(1, userId);
            pst.setLong(2, totalPendapatan);

            // Eksekusi insert
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error Insert Pemasukan: " + e.getMessage());
            return false;
        }
    }
}
