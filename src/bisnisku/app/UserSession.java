/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bisnisku.app;

/**
 *
 * @author Mahesa
 */
public class UserSession {
    private static int userId;    
    private static double modalAwal;
    private static String namaPemilik;
    private static String namaBisnis;
    
    // Getter dan Setter untuk mengisi dan mengambil data session
    public static int getUserId() { return userId; }
    public static void setUserId(int userId) { UserSession.userId = userId; }

    public static String getNamaBisnis() { return namaBisnis; }
    public static void setNamaBisnis(String namaBisnis) { UserSession.namaBisnis = namaBisnis; }

    public static String getNamaPemilik() { return namaPemilik; }
    public static void setNamaPemilik(String namaPemilik) { UserSession.namaPemilik = namaPemilik; }

    public static double getModal() { return modalAwal; }
    public static void setModal(double modalAwal) { UserSession.modalAwal = modalAwal; }
    
    // Fungsi untuk menghapus session saat user Logout
    public static void clearSession() {
        userId = 0;
        modalAwal = 0.0;
        namaPemilik = null;
        namaBisnis = null;
    }
}
