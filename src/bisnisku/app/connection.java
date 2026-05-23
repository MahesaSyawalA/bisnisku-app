/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bisnisku.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Mahesa
 */
public class connection {

    Connection con;

    public connection() {
        String id, pass, driver, url;
        id = "root";
        pass = "";
        driver = "com.mysql.cj.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/db_bisnisku?userTimezone=true&server=UTC";

        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, id, pass);
            if (con != null) {  
//                System.out.println("Koneksi berhasil");
            } else {
                System.out.println("Koneksi Gagal");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getKoneksi() {
        Connection con = null;
        String id = "root";
        String pass = "";
        String driver = "com.mysql.cj.jdbc.Driver";
        // Sesuaikan nama database Anda: db_pbo
        String url = "jdbc:mysql://localhost:3306/db_bisnisku";

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, id, pass);
            System.out.println("Koneksi berhasil");
        } catch (Exception e) {
            System.out.println("Koneksi Gagal: " + e.getMessage());
        }
        return con; // Sekarang return ini valid karena ini method, bukan constructor
    }

    public static void main(String[] args) {
        connection c = new connection();
    }
}
