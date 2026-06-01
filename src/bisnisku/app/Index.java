/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bisnisku.app;

import bisnisku.app.controllers.TrackYourExpensesController;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Mahesa
 */
public class Index extends javax.swing.JFrame {

    connection conn;

    private TrackYourExpensesController controller;
    private final String[] KATEGORI = {"Operasional", "Gaji Karyawan", "Utilitas", "Gaya Hidup", "Lain-lain"};
    private final java.awt.Color WARNA_AKTIF = new java.awt.Color(204, 204, 204);
    private final java.awt.Color WARNA_TIDAK_AKTIF = new java.awt.Color(102, 102, 102);

    //kebutuhan Tab Pemasukan
    private long totalSaldo = 0;
    private long nilaiPerTap = 5000;
    private javax.swing.Timer animTimer;
    private int startY1 = -1;
    private int startY2 = -1;

    private void refreshTableJp2() {
        jP2Table1.setModel(controller.getModelTransaksi());
    }

    private void switchTab(javax.swing.JComponent panelAktif, javax.swing.JComponent tabAktif) {
        jP1.setVisible(false);
        jP2.setVisible(false);
        jP3.setVisible(false);
        jP4.setVisible(false);
        jP5.setVisible(false);
        jP6.setVisible(false);

        tabPemasukan.setBackground(WARNA_TIDAK_AKTIF);
        tabKategori.setBackground(WARNA_TIDAK_AKTIF);
        tabDashboard.setBackground(WARNA_TIDAK_AKTIF);
        tabTransaksi.setBackground(WARNA_TIDAK_AKTIF);
        tabRekap.setBackground(WARNA_TIDAK_AKTIF);
        tabLeaderboard.setBackground(WARNA_TIDAK_AKTIF);

        if (panelAktif != null) {
            panelAktif.setVisible(true);
        }
        if (tabAktif != null) {
            tabAktif.setBackground(WARNA_AKTIF);
        }

        jPanel2.revalidate();
        jPanel2.repaint();
    }

    /**
     * Creates new form Index
     */
    public Index() {

        // Variable session 
        String namaPemilik = UserSession.getNamaPemilik();
        String namaBisnis = UserSession.getNamaBisnis();
        double modalAwal = UserSession.getModal();
        int userId = UserSession.getUserId();

        initComponents();
        // Clear placeholder Nama
        jP2NameExpenseField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (jP2NameExpenseField.getText().equals("Contoh: Beli biji Kopi 10kg")) {
                    jP2NameExpenseField.setText("");
                    jP2NameExpenseField.setForeground(new java.awt.Color(204, 204, 204));
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (jP2NameExpenseField.getText().isEmpty()) {
                    jP2NameExpenseField.setText("Contoh: Beli biji Kopi 10kg");
                    jP2NameExpenseField.setForeground(new java.awt.Color(153, 153, 153));
                }
            }
        });

        // Clear placeholder Nominal
        jP2NominalField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (jP2NominalField.getText().equals("Contoh: 850000")) {
                    jP2NominalField.setText("");
                    jP2NominalField.setForeground(new java.awt.Color(204, 204, 204));
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (jP2NominalField.getText().isEmpty()) {
                    jP2NominalField.setText("Contoh: 850000");
                    jP2NominalField.setForeground(new java.awt.Color(153, 153, 153));
                }
            }
        });
        conn = new connection();
        this.controller = new TrackYourExpensesController();
        this.setLocationRelativeTo(null);

        //Kebutuhan jP1        
        jP1loadData(userId);

        //Kebutuhan jP3
        setupCategoryComboBox();
        int currentMonth = jP3MonthDropdown.getMonth();
        int currentYear = jP3YearDropdown.getYear();
        loadDataByCategory(userId, "Semua Kategori", currentMonth, currentYear, false);

        jP3CategoriesDropdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateFilterData();
            }
        });

        jP3MonthDropdown.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if ("month".equals(evt.getPropertyName())) {
                    updateFilterData();
                }
            }
        });

        jP3YearDropdown.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if ("year".equals(evt.getPropertyName())) {
                    updateFilterData();
                }
            }
        });

        //kebutuhan jP4 
        setupComboBox();
        loadData(userId, "Semua Bulan");

        //kebutuhan jP5 
        plusIconPemasukan.setForeground(new java.awt.Color(0, 0, 0, 0));
        jsubHead2.setForeground(new java.awt.Color(0, 0, 0, 0));
        titlePemasukan.setText(namaBisnis);

        jP1.setVisible(true);
        jP2.setVisible(false);
        jP3.setVisible(false);
        jP4.setVisible(false);

        tabDashboard.setBackground(new java.awt.Color(204, 204, 204));

    }

    private static boolean checkSession() {
        int userId = UserSession.getUserId();

        if (userId <= 0) {
            return false;
        } else {
            return true;
        }
    }

    // Animation plus icon 
    private void playFloatAnimation() {
        java.awt.Toolkit.getDefaultToolkit().beep();

        if (animTimer != null && animTimer.isRunning()) {
            animTimer.stop();
        }

        if (startY1 == -1) {
            startY1 = plusIconPemasukan.getY();
            startY2 = jsubHead2.getY();
        }

        // Kembalikan posisi ke awal
        plusIconPemasukan.setLocation(plusIconPemasukan.getX(), startY1);
        jsubHead2.setLocation(jsubHead2.getX(), startY2);

        // MUNCULKAN ICON: Ubah warnanya kembali ke warna hijau (0, 204, 0)
        plusIconPemasukan.setForeground(new java.awt.Color(0, 204, 0));
        jsubHead2.setForeground(new java.awt.Color(0, 204, 0));

        animTimer = new javax.swing.Timer(20, new java.awt.event.ActionListener() {
            int frame = 0;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                frame++;

                // Gerakkan Y ke atas
                plusIconPemasukan.setLocation(plusIconPemasukan.getX(), plusIconPemasukan.getY() - 3);
                jsubHead2.setLocation(jsubHead2.getX(), jsubHead2.getY() - 2);

                // Setelah animasi selesai (20 frame)
                if (frame >= 20) {
                    // SEMBUNYIKAN ICON: Ubah warnanya jadi transparan lagi
                    plusIconPemasukan.setForeground(new java.awt.Color(0, 0, 0, 0));
                    jsubHead2.setForeground(new java.awt.Color(0, 0, 0, 0));

                    animTimer.stop();
                }
            }
        });

        animTimer.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jTitle = new javax.swing.JLabel();
        tabDashboard = new javax.swing.JPanel();
        titleDashboard = new javax.swing.JLabel();
        tabTransaksi = new javax.swing.JPanel();
        titleTransaksi = new javax.swing.JLabel();
        tabKategori = new javax.swing.JPanel();
        titleKategori = new javax.swing.JLabel();
        tabRekap = new javax.swing.JPanel();
        titleRekap = new javax.swing.JLabel();
        tabPemasukan = new javax.swing.JPanel();
        titleRekap1 = new javax.swing.JLabel();
        tabLeaderboard = new javax.swing.JPanel();
        titleRekap2 = new javax.swing.JLabel();
        logoutButton = new javax.swing.JPanel();
        titleRekap3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jP4 = new javax.swing.JScrollPane();
        jPanel15 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jP4ModalAwal = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jP4Saldo = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jP4Table = new javax.swing.JTable();
        jP4MonthFilter = new javax.swing.JComboBox<>();
        jP3 = new javax.swing.JScrollPane();
        jPanel19 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jP3CategoriesDropdown = new javax.swing.JComboBox<>();
        jP3MonthDropdown = new com.toedter.calendar.JMonthChooser();
        jP3YearDropdown = new com.toedter.calendar.JYearChooser();
        jScrollPane3 = new javax.swing.JScrollPane();
        jP3Table = new javax.swing.JTable();
        jP3ShowData = new javax.swing.JLabel();
        jP3TotalData = new javax.swing.JLabel();
        jP3CheckAllMonth = new javax.swing.JCheckBox();
        jP2 = new javax.swing.JScrollPane();
        jPanel18 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jP2NameExpenseField = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jP2NominalField = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jP2CategoryDropdown = new javax.swing.JComboBox<>();
        jPanel9 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jP2DateChooser1 = new com.toedter.calendar.JDateChooser();
        jP2Button1 = new javax.swing.JButton();
        jP2Button2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jP2Table1 = new javax.swing.JTable();
        jP1 = new javax.swing.JScrollPane();
        jPanel25 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        levelPanel = new javax.swing.JPanel();
        jP1level = new javax.swing.JLabel();
        jP1levelDesc = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jP1namaBisnis = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jScrollPane2 = new javax.swing.JScrollPane();
        jP1table = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jP1modalAwal = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jP1totalKeluar = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jP1transaksiTerakhir = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jP1totalPemasukan = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jP1saldoSaatIni = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jP5 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        titlePemasukan = new javax.swing.JLabel();
        jsubHead = new javax.swing.JLabel();
        jgambar = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        saldoPemasukan = new javax.swing.JLabel();
        jsaldoHead = new javax.swing.JLabel();
        finishButtonPemasukan = new javax.swing.JButton();
        tapButtonPemasukan = new javax.swing.JButton();
        plusIconPemasukan = new javax.swing.JLabel();
        jsubHead2 = new javax.swing.JLabel();
        jP6 = new javax.swing.JScrollPane();
        jPanel30 = new javax.swing.JPanel();
        jHeading1 = new javax.swing.JLabel();
        jsubHead1 = new javax.swing.JLabel();
        jP6ShowLeaderboardButton = new javax.swing.JButton();
        jPanel31 = new javax.swing.JPanel();
        jPanel32 = new javax.swing.JPanel();
        jHeading3 = new javax.swing.JLabel();
        jP6Top1 = new javax.swing.JLabel();
        jPanel33 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        jHeading2 = new javax.swing.JLabel();
        jP6Top3 = new javax.swing.JLabel();
        jPanel35 = new javax.swing.JPanel();
        jPanel36 = new javax.swing.JPanel();
        jHeading4 = new javax.swing.JLabel();
        jP6Top2 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jP6LeaderboardTable = new javax.swing.JTable();
        jgambar2 = new javax.swing.JLabel();
        jgambar1 = new javax.swing.JLabel();
        jgambar3 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(700, 0));

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(170, 300));

        jPanel6.setBackground(new java.awt.Color(51, 51, 51));

        jTitle.setFont(new java.awt.Font("Poppins Medium", 1, 20)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bisnisku/app/assets/logobinikuuu.png"))); // NOI18N
        jTitle.setText("Bisnisku");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jTitle)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jTitle)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        tabDashboard.setBackground(new java.awt.Color(102, 102, 102));
        tabDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabDashboardMouseClicked(evt);
            }
        });

        titleDashboard.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        titleDashboard.setForeground(new java.awt.Color(255, 255, 255));
        titleDashboard.setText("Dashboard");

        javax.swing.GroupLayout tabDashboardLayout = new javax.swing.GroupLayout(tabDashboard);
        tabDashboard.setLayout(tabDashboardLayout);
        tabDashboardLayout.setHorizontalGroup(
            tabDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabDashboardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleDashboard)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabDashboardLayout.setVerticalGroup(
            tabDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabDashboardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabTransaksi.setBackground(new java.awt.Color(102, 102, 102));
        tabTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabTransaksiMouseClicked(evt);
            }
        });

        titleTransaksi.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        titleTransaksi.setForeground(new java.awt.Color(255, 255, 255));
        titleTransaksi.setText("Transaksi");

        javax.swing.GroupLayout tabTransaksiLayout = new javax.swing.GroupLayout(tabTransaksi);
        tabTransaksi.setLayout(tabTransaksiLayout);
        tabTransaksiLayout.setHorizontalGroup(
            tabTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabTransaksiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleTransaksi)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabTransaksiLayout.setVerticalGroup(
            tabTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabTransaksiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabKategori.setBackground(new java.awt.Color(102, 102, 102));
        tabKategori.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabKategoriMouseClicked(evt);
            }
        });

        titleKategori.setBackground(new java.awt.Color(153, 153, 153));
        titleKategori.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        titleKategori.setForeground(new java.awt.Color(255, 255, 255));
        titleKategori.setText("Kategori");

        javax.swing.GroupLayout tabKategoriLayout = new javax.swing.GroupLayout(tabKategori);
        tabKategori.setLayout(tabKategoriLayout);
        tabKategoriLayout.setHorizontalGroup(
            tabKategoriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabKategoriLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleKategori)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabKategoriLayout.setVerticalGroup(
            tabKategoriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabKategoriLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleKategori, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabRekap.setBackground(new java.awt.Color(102, 102, 102));
        tabRekap.setForeground(new java.awt.Color(255, 255, 255));
        tabRekap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabRekapMouseClicked(evt);
            }
        });

        titleRekap.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        titleRekap.setForeground(new java.awt.Color(255, 255, 255));
        titleRekap.setText("Rekap");

        javax.swing.GroupLayout tabRekapLayout = new javax.swing.GroupLayout(tabRekap);
        tabRekap.setLayout(tabRekapLayout);
        tabRekapLayout.setHorizontalGroup(
            tabRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabRekapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleRekap)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabRekapLayout.setVerticalGroup(
            tabRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabRekapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleRekap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPemasukan.setBackground(new java.awt.Color(102, 102, 102));
        tabPemasukan.setForeground(new java.awt.Color(255, 255, 255));
        tabPemasukan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabPemasukanMouseClicked(evt);
            }
        });

        titleRekap1.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        titleRekap1.setForeground(new java.awt.Color(255, 255, 255));
        titleRekap1.setText("Pemasukan");

        javax.swing.GroupLayout tabPemasukanLayout = new javax.swing.GroupLayout(tabPemasukan);
        tabPemasukan.setLayout(tabPemasukanLayout);
        tabPemasukanLayout.setHorizontalGroup(
            tabPemasukanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPemasukanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleRekap1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabPemasukanLayout.setVerticalGroup(
            tabPemasukanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPemasukanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleRekap1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabLeaderboard.setBackground(new java.awt.Color(102, 102, 102));
        tabLeaderboard.setForeground(new java.awt.Color(255, 255, 255));
        tabLeaderboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabLeaderboardMouseClicked(evt);
            }
        });

        titleRekap2.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        titleRekap2.setForeground(new java.awt.Color(255, 255, 255));
        titleRekap2.setText("Leaderboard");

        javax.swing.GroupLayout tabLeaderboardLayout = new javax.swing.GroupLayout(tabLeaderboard);
        tabLeaderboard.setLayout(tabLeaderboardLayout);
        tabLeaderboardLayout.setHorizontalGroup(
            tabLeaderboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabLeaderboardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleRekap2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabLeaderboardLayout.setVerticalGroup(
            tabLeaderboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabLeaderboardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleRekap2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        logoutButton.setBackground(new java.awt.Color(102, 102, 102));
        logoutButton.setForeground(new java.awt.Color(255, 255, 255));
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutButtonMouseClicked(evt);
            }
        });

        titleRekap3.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        titleRekap3.setForeground(new java.awt.Color(255, 255, 255));
        titleRekap3.setText("Logout");

        javax.swing.GroupLayout logoutButtonLayout = new javax.swing.GroupLayout(logoutButton);
        logoutButton.setLayout(logoutButtonLayout);
        logoutButtonLayout.setHorizontalGroup(
            logoutButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoutButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleRekap3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        logoutButtonLayout.setVerticalGroup(
            logoutButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoutButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleRekap3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabKategori, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabRekap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabPemasukan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabLeaderboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(logoutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tabDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabRekap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabLeaderboard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));

        jP4.setBackground(new java.awt.Color(48, 48, 46));
        jP4.setMaximumSize(new java.awt.Dimension(530, 32767));

        jPanel15.setBackground(new java.awt.Color(48, 48, 46));
        jPanel15.setDoubleBuffered(false);
        jPanel15.setMaximumSize(new java.awt.Dimension(515, 32767));
        jPanel15.setPreferredSize(new java.awt.Dimension(515, 538));

        jLabel6.setFont(new java.awt.Font("Poppins", 1, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(221, 255, 255));
        jLabel6.setText("Breakdown Pengeluaran");

        jPanel16.setBackground(new java.awt.Color(38, 38, 37));

        jLabel21.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(221, 255, 255));
        jLabel21.setText("Modal Awal");

        jP4ModalAwal.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jP4ModalAwal.setForeground(new java.awt.Color(221, 255, 255));
        jP4ModalAwal.setText("RP. 10jt");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jP4ModalAwal))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jP4ModalAwal)
                .addContainerGap())
        );

        jPanel17.setBackground(new java.awt.Color(38, 38, 37));

        jLabel24.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(221, 255, 255));
        jLabel24.setText("Saldo Saat ini");

        jP4Saldo.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jP4Saldo.setForeground(new java.awt.Color(30, 158, 117));
        jP4Saldo.setText("RP. 10jt");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jP4Saldo))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jP4Saldo)
                .addContainerGap())
        );

        jLabel10.setFont(new java.awt.Font("Poppins", 1, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(221, 255, 255));
        jLabel10.setText("Rekap Bulanan");

        jP4Table.setBackground(new java.awt.Color(51, 51, 51));
        jP4Table.setForeground(new java.awt.Color(255, 255, 255));
        jP4Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nama", "Nominal", "", "Tanggal"
            }
        ));
        jP4Table.setSelectionForeground(new java.awt.Color(51, 51, 51));
        jScrollPane4.setViewportView(jP4Table);

        jP4MonthFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jP4MonthFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jP4MonthFilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jP4MonthFilter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jP4MonthFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jP4.setViewportView(jPanel15);

        jPanel19.setBackground(new java.awt.Color(48, 48, 46));
        jPanel19.setMaximumSize(new java.awt.Dimension(515, 32767));
        jPanel19.setPreferredSize(new java.awt.Dimension(515, 566));

        jLabel11.setFont(new java.awt.Font("Poppins Medium", 1, 20)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Filter Per Kategori");

        jP3CategoriesDropdown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jP3Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jP3Table);

        jP3ShowData.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jP3ShowData.setForeground(new java.awt.Color(255, 255, 255));
        jP3ShowData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jP3ShowData.setText("5 data ditemukan");

        jP3TotalData.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jP3TotalData.setForeground(new java.awt.Color(255, 255, 255));
        jP3TotalData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jP3TotalData.setText("Total: Rp 3,22 jt");

        jP3CheckAllMonth.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jP3CheckAllMonth.setForeground(new java.awt.Color(255, 255, 255));
        jP3CheckAllMonth.setText("Semua Bulan");
        jP3CheckAllMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jP3CheckAllMonthActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jP3ShowData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jP3TotalData))
                    .addComponent(jLabel11)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jP3CategoriesDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jP3CheckAllMonth)
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jP3MonthDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jP3YearDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jP3YearDropdown, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jP3MonthDropdown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jP3CategoriesDropdown))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP3CheckAllMonth)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jP3ShowData)
                    .addComponent(jP3TotalData))
                .addContainerGap(103, Short.MAX_VALUE))
        );

        jP3.setViewportView(jPanel19);

        jPanel18.setBackground(new java.awt.Color(48, 48, 46));
        jPanel18.setMaximumSize(new java.awt.Dimension(515, 32767));
        jPanel18.setPreferredSize(new java.awt.Dimension(515, 893));

        jLabel12.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Catat Pengeluaran Baru");

        jPanel4.setBackground(new java.awt.Color(48, 48, 46));

        jLabel4.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("Nama Pengeluaran");

        jP2NameExpenseField.setBackground(new java.awt.Color(48, 48, 46));
        jP2NameExpenseField.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        jP2NameExpenseField.setForeground(new java.awt.Color(204, 204, 204));
        jP2NameExpenseField.setText("Contoh: Beli biji Kopi 10kg");
        jP2NameExpenseField.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 2, true), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        jP2NameExpenseField.setPreferredSize(new java.awt.Dimension(176, 32));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jP2NameExpenseField, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP2NameExpenseField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBackground(new java.awt.Color(48, 48, 46));

        jLabel7.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 204, 204));
        jLabel7.setText("Nominal (Rp)");

        jP2NominalField.setBackground(new java.awt.Color(48, 48, 46));
        jP2NominalField.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        jP2NominalField.setForeground(new java.awt.Color(204, 204, 204));
        jP2NominalField.setText("Contoh: 850000");
        jP2NominalField.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 2, true), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        jP2NominalField.setPreferredSize(new java.awt.Dimension(176, 32));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jP2NominalField, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP2NominalField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(48, 48, 46));

        jLabel8.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(204, 204, 204));
        jLabel8.setText("Kategori");

        jP2CategoryDropdown.setBackground(new java.awt.Color(48, 48, 46));
        jP2CategoryDropdown.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        jP2CategoryDropdown.setForeground(new java.awt.Color(204, 204, 204));
        jP2CategoryDropdown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Operasional", "Gaji Karyawan", "Utilitas", "Gaya Hidup", "Lain-lain" }));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jP2CategoryDropdown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP2CategoryDropdown, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(48, 48, 46));

        jLabel9.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 204, 204));
        jLabel9.setText("Tanggal");

        jP2DateChooser1.setBackground(new java.awt.Color(48, 48, 46));
        jP2DateChooser1.setForeground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addContainerGap(442, Short.MAX_VALUE))
            .addComponent(jP2DateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP2DateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
        );

        jP2Button1.setBackground(new java.awt.Color(48, 48, 46));
        jP2Button1.setFont(new java.awt.Font("Poppins SemiBold", 0, 15)); // NOI18N
        jP2Button1.setForeground(new java.awt.Color(204, 204, 204));
        jP2Button1.setText("Simpan");
        jP2Button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jP2Button1ActionPerformed(evt);
            }
        });

        jP2Button2.setBackground(new java.awt.Color(48, 48, 46));
        jP2Button2.setFont(new java.awt.Font("Poppins SemiBold", 0, 15)); // NOI18N
        jP2Button2.setForeground(new java.awt.Color(204, 204, 204));
        jP2Button2.setText("Batal");

        jLabel2.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("Terakhir dicatat");

        jP2Table1.setBackground(new java.awt.Color(48, 48, 46));
        jP2Table1.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        jP2Table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nama", "Nominal", "Kategori"
            }
        ));
        jScrollPane1.setViewportView(jP2Table1);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel18Layout.createSequentialGroup()
                            .addComponent(jP2Button1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jP2Button2, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE))
                        .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jP2Button1)
                    .addComponent(jP2Button2))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        jP2.setViewportView(jPanel18);

        jP1.setBackground(new java.awt.Color(48, 48, 46));

        jPanel25.setBackground(new java.awt.Color(48, 48, 46));
        jPanel25.setMaximumSize(new java.awt.Dimension(515, 32767));
        jPanel25.setPreferredSize(new java.awt.Dimension(515, 751));

        jPanel3.setBackground(new java.awt.Color(48, 48, 46));
        jPanel3.setMaximumSize(new java.awt.Dimension(515, 32767));

        levelPanel.setBackground(new java.awt.Color(225, 244, 238));
        levelPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(54, 67, 64)));
        levelPanel.setForeground(new java.awt.Color(255, 244, 238));

        jP1level.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        jP1level.setForeground(new java.awt.Color(60, 114, 89));
        jP1level.setText("Level: Pengusaha sehat");

        jP1levelDesc.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jP1levelDesc.setForeground(new java.awt.Color(60, 114, 89));
        jP1levelDesc.setText("Saldo masih ada di atas 80% modal");

        javax.swing.GroupLayout levelPanelLayout = new javax.swing.GroupLayout(levelPanel);
        levelPanel.setLayout(levelPanelLayout);
        levelPanelLayout.setHorizontalGroup(
            levelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(levelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(levelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jP1level)
                    .addComponent(jP1levelDesc))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        levelPanelLayout.setVerticalGroup(
            levelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(levelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jP1level)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP1levelDesc)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(221, 255, 255));
        jLabel1.setText("Kesehatan Keuangan");

        jP1namaBisnis.setBackground(new java.awt.Color(204, 204, 204));
        jP1namaBisnis.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jP1namaBisnis.setForeground(new java.awt.Color(221, 255, 255));
        jP1namaBisnis.setText("Nama Bisnis");

        jProgressBar1.setBackground(new java.awt.Color(37, 37, 35));
        jProgressBar1.setForeground(new java.awt.Color(29, 157, 117));

        jP1table.setBackground(new java.awt.Color(51, 51, 51));
        jP1table.setForeground(new java.awt.Color(255, 255, 255));
        jP1table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nama", "Nominal", "Tanggal"
            }
        ));
        jP1table.setSelectionForeground(new java.awt.Color(51, 51, 51));
        jScrollPane2.setViewportView(jP1table);

        jPanel7.setBackground(new java.awt.Color(48, 48, 46));

        jPanel10.setBackground(new java.awt.Color(38, 38, 37));

        jLabel19.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(221, 255, 255));
        jLabel19.setText("Modal Awal");

        jP1modalAwal.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jP1modalAwal.setForeground(new java.awt.Color(221, 255, 255));
        jP1modalAwal.setText("RP. 10jt");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jP1modalAwal))
                .addContainerGap(121, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jP1modalAwal)
                .addContainerGap())
        );

        jPanel11.setBackground(new java.awt.Color(38, 38, 37));

        jLabel20.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(221, 255, 255));
        jLabel20.setText("Total Keluar");

        jP1totalKeluar.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jP1totalKeluar.setForeground(new java.awt.Color(255, 123, 103));
        jP1totalKeluar.setText("RP. 10jt");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jP1totalKeluar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jP1totalKeluar)
                .addContainerGap())
        );

        jPanel13.setBackground(new java.awt.Color(38, 38, 37));

        jLabel22.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(221, 255, 255));
        jLabel22.setText("Transaksi Terakhir");

        jP1transaksiTerakhir.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jP1transaksiTerakhir.setForeground(new java.awt.Color(221, 255, 255));
        jP1transaksiTerakhir.setText("RP. 10jt");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jP1transaksiTerakhir))
                .addContainerGap(121, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jP1transaksiTerakhir)
                .addContainerGap())
        );

        jPanel14.setBackground(new java.awt.Color(38, 38, 37));

        jLabel23.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(221, 255, 255));
        jLabel23.setText("Total Pemasukan");

        jP1totalPemasukan.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jP1totalPemasukan.setForeground(new java.awt.Color(30, 158, 117));
        jP1totalPemasukan.setText("RP. 10jt");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(jP1totalPemasukan))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jP1totalPemasukan)
                .addContainerGap())
        );

        jPanel29.setBackground(new java.awt.Color(38, 38, 37));

        jLabel25.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(221, 255, 255));
        jLabel25.setText("Saldo Saat ini");

        jP1saldoSaatIni.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jP1saldoSaatIni.setForeground(new java.awt.Color(30, 158, 117));
        jP1saldoSaatIni.setText("RP. 10jt");

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(jP1saldoSaatIni))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jP1saldoSaatIni)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setBackground(new java.awt.Color(204, 204, 204));
        jLabel5.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(221, 255, 255));
        jLabel5.setText("Ringkasan bisnis");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jP1namaBisnis))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(levelPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(21, 21, 21))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jP1namaBisnis)
                    .addComponent(jLabel5))
                .addGap(20, 20, 20)
                .addComponent(levelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(191, 191, 191))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jP1.setViewportView(jPanel25);

        jP5.setBackground(new java.awt.Color(48, 48, 46));
        jP5.setMaximumSize(new java.awt.Dimension(530, 32767));

        jPanel5.setBackground(new java.awt.Color(49, 49, 47));
        jPanel5.setMaximumSize(new java.awt.Dimension(515, 32767));
        jPanel5.setPreferredSize(new java.awt.Dimension(515, 536));

        titlePemasukan.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        titlePemasukan.setForeground(new java.awt.Color(255, 255, 255));
        titlePemasukan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titlePemasukan.setText("Nama Bisnismu");

        jsubHead.setFont(new java.awt.Font("Poppins Light", 1, 12)); // NOI18N
        jsubHead.setForeground(new java.awt.Color(155, 155, 151));
        jsubHead.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jsubHead.setText("mulai \"klik\" agar bisnismu maju");

        jgambar.setFont(new java.awt.Font("Poppins Light", 1, 12)); // NOI18N
        jgambar.setForeground(new java.awt.Color(155, 155, 151));
        jgambar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bisnisku/app/assets/BisniskuPemasukan.png"))); // NOI18N

        jPanel12.setBackground(new java.awt.Color(38, 38, 37));

        saldoPemasukan.setFont(new java.awt.Font("Poppins Medium", 1, 14)); // NOI18N
        saldoPemasukan.setForeground(new java.awt.Color(255, 255, 255));
        saldoPemasukan.setText("Rp. 0");

        jsaldoHead.setFont(new java.awt.Font("Poppins Medium", 1, 12)); // NOI18N
        jsaldoHead.setForeground(new java.awt.Color(0, 204, 51));
        jsaldoHead.setText("Pemasukan Anda :");

        finishButtonPemasukan.setBackground(new java.awt.Color(38, 38, 37));
        finishButtonPemasukan.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        finishButtonPemasukan.setForeground(new java.awt.Color(204, 204, 204));
        finishButtonPemasukan.setText("selesaikan hari ini");
        finishButtonPemasukan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                finishButtonPemasukanMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(saldoPemasukan, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(finishButtonPemasukan)
                .addContainerGap())
            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel12Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jsaldoHead)
                    .addContainerGap(219, Short.MAX_VALUE)))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saldoPemasukan)
                .addContainerGap())
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(finishButtonPemasukan)
                .addContainerGap(16, Short.MAX_VALUE))
            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel12Layout.createSequentialGroup()
                    .addGap(7, 7, 7)
                    .addComponent(jsaldoHead)
                    .addContainerGap(31, Short.MAX_VALUE)))
        );

        tapButtonPemasukan.setBackground(new java.awt.Color(49, 49, 47));
        tapButtonPemasukan.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        tapButtonPemasukan.setForeground(new java.awt.Color(204, 204, 204));
        tapButtonPemasukan.setText("Tap Untuk Menjual");
        tapButtonPemasukan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tapButtonPemasukanMouseClicked(evt);
            }
        });

        plusIconPemasukan.setFont(new java.awt.Font("Poppins Light", 1, 48)); // NOI18N
        plusIconPemasukan.setForeground(new java.awt.Color(0, 204, 0));
        plusIconPemasukan.setText("+");

        jsubHead2.setFont(new java.awt.Font("Poppins Light", 1, 24)); // NOI18N
        jsubHead2.setForeground(new java.awt.Color(0, 204, 0));
        jsubHead2.setText("+");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(167, 167, 167)
                        .addComponent(tapButtonPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titlePemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addComponent(jgambar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jsubHead2)
                                        .addComponent(plusIconPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jsubHead, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(titlePemasukan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsubHead)
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jgambar, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(plusIconPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jsubHead2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(tapButtonPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(85, Short.MAX_VALUE))
        );

        jP5.setViewportView(jPanel5);

        jP6.setBackground(new java.awt.Color(48, 48, 46));
        jP6.setMaximumSize(new java.awt.Dimension(530, 32767));

        jPanel30.setBackground(new java.awt.Color(49, 49, 47));
        jPanel30.setMaximumSize(new java.awt.Dimension(515, 32767));
        jPanel30.setPreferredSize(new java.awt.Dimension(515, 750));

        jHeading1.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        jHeading1.setForeground(new java.awt.Color(255, 255, 255));
        jHeading1.setText("Leaderboard");

        jsubHead1.setFont(new java.awt.Font("Poppins Light", 1, 12)); // NOI18N
        jsubHead1.setForeground(new java.awt.Color(155, 155, 151));
        jsubHead1.setText("Seberapa jauh kamu melangkah?");

        jP6ShowLeaderboardButton.setBackground(new java.awt.Color(38, 38, 37));
        jP6ShowLeaderboardButton.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jP6ShowLeaderboardButton.setForeground(new java.awt.Color(204, 204, 204));
        jP6ShowLeaderboardButton.setText("Lihat Leaderboard");
        jP6ShowLeaderboardButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jP6ShowLeaderboardButtonMouseClicked(evt);
            }
        });

        jPanel31.setBackground(new java.awt.Color(0, 51, 153));
        jPanel31.setMaximumSize(new java.awt.Dimension(130, 32767));

        jHeading3.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        jHeading3.setText("1");

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
            .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel32Layout.createSequentialGroup()
                    .addGap(34, 34, 34)
                    .addComponent(jHeading3)
                    .addContainerGap(35, Short.MAX_VALUE)))
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 73, Short.MAX_VALUE)
            .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel32Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(jHeading3)
                    .addContainerGap(23, Short.MAX_VALUE)))
        );

        jP6Top1.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jP6Top1.setForeground(new java.awt.Color(255, 255, 255));
        jP6Top1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jP6Top1.setText("Pebisnis1");

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel31Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jP6Top1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP6Top1)
                .addContainerGap(107, Short.MAX_VALUE))
        );

        jPanel33.setBackground(new java.awt.Color(153, 0, 153));
        jPanel33.setMaximumSize(new java.awt.Dimension(130, 32767));

        jHeading2.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        jHeading2.setText("3");

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 81, Short.MAX_VALUE)
            .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel34Layout.createSequentialGroup()
                    .addGap(34, 34, 34)
                    .addComponent(jHeading2)
                    .addContainerGap(35, Short.MAX_VALUE)))
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 73, Short.MAX_VALUE)
            .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel34Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(jHeading2)
                    .addContainerGap(23, Short.MAX_VALUE)))
        );

        jP6Top3.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jP6Top3.setForeground(new java.awt.Color(255, 255, 255));
        jP6Top3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jP6Top3.setText("Pebisnis3");

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jP6Top3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP6Top3)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel35.setBackground(new java.awt.Color(0, 153, 153));
        jPanel35.setMaximumSize(new java.awt.Dimension(130, 32767));

        jHeading4.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        jHeading4.setText("2");

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
            .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel36Layout.createSequentialGroup()
                    .addGap(34, 34, 34)
                    .addComponent(jHeading4)
                    .addContainerGap(35, Short.MAX_VALUE)))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 73, Short.MAX_VALUE)
            .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel36Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(jHeading4)
                    .addContainerGap(23, Short.MAX_VALUE)))
        );

        jP6Top2.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jP6Top2.setForeground(new java.awt.Color(255, 255, 255));
        jP6Top2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jP6Top2.setText("Pebisnis2");

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jP6Top2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP6Top2)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        jP6LeaderboardTable.setBackground(new java.awt.Color(51, 51, 51));
        jP6LeaderboardTable.setForeground(new java.awt.Color(255, 255, 255));
        jP6LeaderboardTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nama", "Nominal", "Tanggal"
            }
        ));
        jP6LeaderboardTable.setSelectionForeground(new java.awt.Color(51, 51, 51));
        jScrollPane6.setViewportView(jP6LeaderboardTable);

        jgambar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bisnisku/app/assets/aiFoto.png"))); // NOI18N

        jgambar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bisnisku/app/assets/aiFoto.png"))); // NOI18N

        jgambar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bisnisku/app/assets/aiFoto.png"))); // NOI18N

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jsubHead1)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jHeading1)
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel30Layout.createSequentialGroup()
                                        .addGap(17, 17, 17)
                                        .addComponent(jgambar2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel30Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel30Layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addComponent(jgambar1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel30Layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(jP6ShowLeaderboardButton))
                                    .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jgambar3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(34, Short.MAX_VALUE))
            .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel30Layout.createSequentialGroup()
                    .addContainerGap(370, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(45, 45, 45)))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jHeading1)
                    .addComponent(jP6ShowLeaderboardButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(jsubHead1)
                        .addGap(5, 5, 5)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addComponent(jgambar2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addComponent(jgambar1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(jgambar3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
            .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel30Layout.createSequentialGroup()
                    .addGap(170, 170, 170)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(480, Short.MAX_VALUE)))
        );

        jP6.setViewportView(jPanel30);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jP1, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jP2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jP3, javax.swing.GroupLayout.Alignment.TRAILING))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jP4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(jP5, javax.swing.GroupLayout.DEFAULT_SIZE, 772, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jP6, javax.swing.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jP1, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jP2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jP3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jP4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(jP5, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jP6, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabDashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabDashboardMouseClicked
        // TODO add your handling code here:
        int userId = UserSession.getUserId();
        jP1loadData(userId);

        switchTab(jP1, tabDashboard);
    }//GEN-LAST:event_tabDashboardMouseClicked

    private void tabTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabTransaksiMouseClicked
        // TODO add your handling code here:
        switchTab(jP2, tabTransaksi);
    }//GEN-LAST:event_tabTransaksiMouseClicked

    private void tabKategoriMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabKategoriMouseClicked
        // TODO add your handling code here:
        switchTab(jP3, tabKategori);
    }//GEN-LAST:event_tabKategoriMouseClicked

    private void tabRekapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabRekapMouseClicked
        // TODO add your handling code here:
        int userId = UserSession.getUserId();
        setupComboBox();                        // refresh dropdown bulan
        loadData(userId, "Semua Bulan");        // load data tabel
        switchTab(jP4, tabRekap);
    }//GEN-LAST:event_tabRekapMouseClicked

    private void jP4MonthFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jP4MonthFilterActionPerformed
        // TODO add your handling code here:
        int userId = UserSession.getUserId();
        String selected = (String) jP4MonthFilter.getSelectedItem();
        loadData(userId, selected);
    }//GEN-LAST:event_jP4MonthFilterActionPerformed

    private void jP3CheckAllMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jP3CheckAllMonthActionPerformed
        // TODO add your handling code here:
        updateFilterData();
    }//GEN-LAST:event_jP3CheckAllMonthActionPerformed

    //Coding Kebuthan jP 1 Dashboard  
    public void jP1loadData(int userId) {

        try (Connection conn = connection.getKoneksi()) {

            if (conn == null) {
                return;
            }

            double modalAwal = 0;
            String namaBisnis = "";

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT modal_awal, nama_bisnis "
                    + "FROM bisnis_profile "
                    + "WHERE user_id = ?")) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        modalAwal = rs.getDouble("modal_awal");
                        namaBisnis = rs.getString("nama_bisnis");

                        jP1modalAwal.setText(
                                "RP. " + String.format("%,.0f", modalAwal)
                        );

                        jP1namaBisnis.setText(namaBisnis);
                    }
                }
            }

            double totalKeluar = 0;

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(SUM(nominal),0) AS total "
                    + "FROM transaksi "
                    + "WHERE user_id = ?")) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        totalKeluar = rs.getDouble("total");

                        jP1totalKeluar.setText(
                                "RP. " + String.format("%,.0f", totalKeluar)
                        );
                    }
                }
            }

            double totalPemasukan = 0;

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(SUM(total_pendapatan),0) AS total "
                    + "FROM pemasukan_harian "
                    + "WHERE id_user = ?")) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        totalPemasukan = rs.getDouble("total");

                        jP1totalPemasukan.setText(
                                "RP. " + String.format("%,.0f", totalPemasukan)
                        );
                    }
                }
            }

            double saldoSaatIni = modalAwal + totalPemasukan - totalKeluar;

            jP1saldoSaatIni.setText(
                    "RP. " + String.format("%,.0f", saldoSaatIni)
            );

            double profit = totalPemasukan - totalKeluar;

            jP1transaksiTerakhir.setText(
                    "RP. " + String.format("%,.0f", profit)
            );

            double persen = (saldoSaatIni / modalAwal) * 100;

            // Setelah hitung persen
            int progressValue = (int) Math.min(persen, 100); // cap di 100
            jProgressBar1.setMinimum(0);
            jProgressBar1.setMaximum(100);
            jProgressBar1.setValue(progressValue);

            if (profit > 0) {

                jP1level.setText("Level: Bisnis Berkembang 📈");

                jP1levelDesc.setText(
                        "Profit +" + String.format("%.1f", persen - 100) + "% dari modal awal"
                );

                levelPanel.setBackground(
                        new java.awt.Color(220, 255, 220)
                );

            } else if (persen >= 50) {

                jP1level.setText("Level: Perlu Waspada 📉");

                jP1levelDesc.setText(
                        "Saldo tersisa "
                        + String.format("%.1f", persen)
                        + "% dari modal"
                );

                levelPanel.setBackground(
                        new java.awt.Color(255, 243, 205)
                );

            } else {

                jP1level.setText("Level: Kondisi Kritis 🚨");

                jP1levelDesc.setText(
                        "Kerugian bisnis mulai besar"
                );

                levelPanel.setBackground(
                        new java.awt.Color(255, 220, 220)
                );
            }

            if (saldoSaatIni < 0) {

                jP1saldoSaatIni.setForeground(
                        new java.awt.Color(255, 123, 103)
                );

            } else {

                jP1saldoSaatIni.setForeground(
                        new java.awt.Color(30, 158, 117)
                );
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT nominal "
                    + "FROM transaksi "
                    + "WHERE user_id = ? "
                    + "ORDER BY id DESC LIMIT 1")) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        double transaksiTerakhir = rs.getDouble("nominal");

                        jP1transaksiTerakhir.setText(
                                "RP. " + String.format("%,.0f", transaksiTerakhir)
                        );

                    } else {

                        jP1transaksiTerakhir.setText("RP. 0");
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT nama, nominal, tanggal "
                    + "FROM transaksi "
                    + "WHERE user_id = ? "
                    + "ORDER BY tanggal DESC")) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    DefaultTableModel model
                            = (DefaultTableModel) jP1table.getModel();

                    model.setRowCount(0);

                    while (rs.next()) {

                        String nama = rs.getString("nama");
                        double nominal = rs.getDouble("nominal");
                        Date tanggal = rs.getDate("tanggal");

                        model.addRow(new Object[]{
                            nama,
                            "RP. " + String.format("%,.0f", nominal),
                            tanggal
                        });
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }

    //Coding Kebutuhan jP 2 Transaksi
    private void jP2Button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jP2Button1ActionPerformed
        // TODO add your handling code here:
        try {

            String nama = jP2NameExpenseField.getText();
            double nominal = Double.parseDouble(jP2NominalField.getText());
            String kategori = jP2CategoryDropdown.getSelectedItem().toString();

            java.util.Date utilDate = jP2DateChooser1.getDate();
            if (utilDate == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal terlebih dahulu!");
                return;
            }
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            boolean sukses = controller.simpanTransaksi(nama, nominal, kategori, sqlDate);

            if (sukses) {
                javax.swing.JOptionPane.showMessageDialog(this, "Berhasil dicatat!");
                refreshTableJp2();

                jP2NameExpenseField.setText("");
                jP2NominalField.setText("");

            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Gagal menyimpan ke database.");
            }

        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Nominal harus berupa angka!");
        }
    }//GEN-LAST:event_jP2Button1ActionPerformed

    private void tabPemasukanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPemasukanMouseClicked
        // TODO add your handling code here:
        switchTab(jP5, tabPemasukan);
    }//GEN-LAST:event_tabPemasukanMouseClicked

    private void tabLeaderboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabLeaderboardMouseClicked
        switchTab(jP6, tabLeaderboard);
    }//GEN-LAST:event_tabLeaderboardMouseClicked

    private void tapButtonPemasukanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tapButtonPemasukanMouseClicked
        // TODO add your handling code here:
        totalSaldo += nilaiPerTap;
        saldoPemasukan.setText(
                "Rp. " + String.format("%,d", totalSaldo)
        );
        playFloatAnimation();
    }//GEN-LAST:event_tapButtonPemasukanMouseClicked

    private void finishButtonPemasukanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_finishButtonPemasukanMouseClicked
        if (totalSaldo == 0) {
            JOptionPane.showMessageDialog(this, "Anda belum menjual apapun hari ini!");
            return;
        }

        try {

            Connection conn = connection.getKoneksi();

            String sql = "INSERT INTO pemasukan_harian "
                    + "(id_user, tanggal, total_pendapatan) "
                    + "VALUES (?, CURDATE(), ?)";

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, 1);
            pst.setLong(2, totalSaldo);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(
                    this,
                    "Hari diselesaikan! Rp. " + totalSaldo + " berhasil disimpan."
            );
            totalSaldo = 0;
            saldoPemasukan.setText("Rp. 0");

            pst.close();
            conn.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Gagal menyimpan data: " + e.getMessage()
            );
        }
    }//GEN-LAST:event_finishButtonPemasukanMouseClicked

    private void jP6ShowLeaderboardButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jP6ShowLeaderboardButtonMouseClicked
        // TODO add your handling code here:
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) jP6LeaderboardTable.getModel();
        model.setRowCount(0);

        // Reset podium jika data kosong
        jP6Top1.setText("-");
        jP6Top2.setText("-");
        jP6Top3.setText("-");

        try {
            java.sql.Connection conn = bisnisku.app.connection.getKoneksi();

            if (conn == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Gagal terhubung ke database!");
                return;
            }

            // Query yang diperbaiki (Menggunakan Subquery agar tidak terjadi duplikasi Cartesian Product)
            // Menyesuaikan dengan skema tabel bisnis_profile, pemasukan_harian, dan transaksi
            String sql = "SELECT b.nama_bisnis, "
                    + "(b.modal_awal + COALESCE(p.total_pemasukan, 0) - COALESCE(t.total_pengeluaran, 0)) AS saldo "
                    + "FROM bisnis_profile b "
                    + "LEFT JOIN (SELECT id_user, SUM(total_pendapatan) AS total_pemasukan FROM pemasukan_harian GROUP BY id_user) p "
                    + "  ON b.user_id = p.id_user "
                    + "LEFT JOIN (SELECT user_id, SUM(nominal) AS total_pengeluaran FROM transaksi GROUP BY user_id) t "
                    + "  ON b.user_id = t.user_id "
                    + "ORDER BY saldo DESC";

            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);

            int rank = 1;
            while (rs.next()) {
                String namaBisnis = rs.getString("nama_bisnis");
                long saldoBersih = rs.getLong("saldo");
                String tanggal = "-";

                model.addRow(new Object[]{namaBisnis, "Rp " + saldoBersih, tanggal});

                // Ubah baris ini:
                // Lebar div HTML (80px) sekarang sama persis dengan dimensi JLabel (80px)
                String formattedName = "<html><div style='text-align: center; width: 60px; word-wrap: break-word;'>" + namaBisnis + "</div></html>";

                if (rank == 1) {
                    jP6Top1.setText(formattedName);
                } else if (rank == 2) {
                    jP6Top2.setText(formattedName);
                } else if (rank == 3) {
                    jP6Top3.setText(formattedName);
                }

                rank++;
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal memuat Leaderboard: " + e.getMessage());
        }
    }//GEN-LAST:event_jP6ShowLeaderboardButtonMouseClicked

    private void logoutButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutButtonMouseClicked
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {

            UserSession.clearSession();

            JOptionPane.showMessageDialog(
                    this,
                    "Berhasil logout",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE
            );

            LoginForm login = new LoginForm();
            login.setVisible(true);

            this.dispose();
        }
    }//GEN-LAST:event_logoutButtonMouseClicked

//    Coding Kebutuhan jP 3 Category
    private void setupCategoryComboBox() {
        // 1. Bersihkan item bawaan default ("Item 1", "Item 2", dst)
        jP3CategoriesDropdown.removeAllItems();

        // 2. Tambahkan opsi untuk menampilkan seluruh kategori
        jP3CategoriesDropdown.addItem("Semua Kategori");

        // 3. Masukkan semua list dari array KATEGORI global kamu
        for (String kat : KATEGORI) {
            jP3CategoriesDropdown.addItem(kat);
        }
    }

    public void loadDataByCategory(int userId, String kategori, int bulan, int tahun, boolean semuaBulan) {
        try (Connection conn = bisnisku.app.connection.getKoneksi()) {
            if (conn == null) {
                System.out.println("Error: Gagal mendapatkan koneksi database.");
                return;
            }

            // 1. Menyusun Query SQL secara dinamis
            StringBuilder sql = new StringBuilder(
                    "SELECT tanggal, nama, nominal, kategori FROM transaksi WHERE user_id = ?"
            );

            // Filter Kategori (Jika bukan "Semua Kategori")
            if (kategori != null && !kategori.equals("Semua Kategori")) {
                sql.append(" AND kategori = ?");
            }

            // Filter Waktu: Hanya ditambahkan jika pengguna TIDAK memilih "Semua Bulan"
            if (semuaBulan) {
                // Jika checkbox dicentang, HANYA filter berdasarkan tahun terpilih
                sql.append(" AND YEAR(tanggal) = ?");
            } else {
                // Jika tidak dicentang, filter berdasarkan bulan dan tahun
                sql.append(" AND MONTH(tanggal) = ? AND YEAR(tanggal) = ?");
            }

            sql.append(" ORDER BY tanggal DESC");

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                ps.setInt(paramIndex++, userId);

                if (kategori != null && !kategori.equals("Semua Kategori")) {
                    ps.setString(paramIndex++, kategori);
                }

                // Parameter waktu hanya diisi jika filter bulan aktif
                if (semuaBulan) {
                    // Jika dicentang, kirimkan parameter TAHUN saja
                    ps.setInt(paramIndex++, tahun);
                } else {
                    // Jika tidak dicentang, kirimkan parameter BULAN dan TAHUN
                    ps.setInt(paramIndex++, (bulan + 1));
                    ps.setInt(paramIndex++, tahun);
                }

                // 2. Eksekusi Query dan Olah Data
                try (ResultSet rs = ps.executeQuery()) {
                    javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                            new String[]{"Tanggal", "Nama", "Nominal", "Kat."}, 0);

                    int totalTransaksi = 0;
                    double totalNominal = 0;

                    while (rs.next()) {
                        double nominal = rs.getDouble("nominal");
                        totalNominal += nominal;
                        totalTransaksi++;

                        // Format Tanggal (dd/MM)
                        java.sql.Date dbDate = rs.getDate("tanggal");
                        String formattedDate = "";
                        if (dbDate != null) {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM");
                            formattedDate = sdf.format(dbDate);
                        }

                        // Format nominal rupiah singkat (rb / jt)
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

                    // Set data ke komponen JTable halaman jP3
                    jP3Table.setModel(model);

                    // Update Label Jumlah Transaksi
                    jP3ShowData.setText(totalTransaksi + " transaksi ditemukan");

                    // =============================================================
                    // FIX YANG KURANG: Memunculkan SUM Nominal ke jP3TotalData
                    // =============================================================
                    if (totalNominal >= 1000000) {
                        jP3TotalData.setText("Total: Rp " + String.format("%,.2f", totalNominal / 1000000) + " jt");
                    } else if (totalNominal >= 1000) {
                        jP3TotalData.setText("Total: Rp " + String.format("%,.0f", totalNominal / 1000) + " rb");
                    } else {
                        jP3TotalData.setText("Total: Rp " + String.format("%,.0f", totalNominal));
                    }
                    // =============================================================

                }
            }
        } catch (SQLException e) {
            System.out.println("Error di loadDataByCategory: " + e.getMessage());
        }
    }

    // Contoh trigger terpusat
    private void updateFilterData() {
        String kategori = (String) jP3CategoriesDropdown.getSelectedItem();
        int bulan = jP3MonthDropdown.getMonth();
        int tahun = jP3YearDropdown.getYear();
        int userId = UserSession.getUserId();

        // Checkbox dicentang = abaikan filter bulan dan tahun
        boolean semuaBulan = jP3CheckAllMonth.isSelected();

        // Panggil fungsi yang telah diperbarui
        loadDataByCategory(userId, kategori, bulan, tahun, semuaBulan);
    }

    private void jP3CategoriesDropdownActionPerformed(java.awt.event.ActionEvent evt) {
        updateFilterData();
    }

    private void jP3MonthDropdownPropertyChange(java.beans.PropertyChangeEvent evt) {
        // JMonthChooser biasanya menggunakan PropertyChangeListener untuk mendeteksi perubahan
        updateFilterData();
    }

    private void jP3YearDropdownPropertyChange(java.beans.PropertyChangeEvent evt) {
        // JYearChooser biasanya menggunakan PropertyChangeListener untuk mendeteksi perubahan
        updateFilterData();
    }

//    Coding Kebutuhan jP 4 Rekap
    private void setupComboBox() {
        int userId = UserSession.getUserId();

        try (Connection conn = bisnisku.app.connection.getKoneksi()) {
            if (conn == null) {
                System.out.println("Error: Gagal mendapatkan koneksi database.");
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT DISTINCT DATE_FORMAT(tanggal, '%Y-%m') AS bulan FROM transaksi WHERE user_id = ? ORDER BY bulan DESC")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    jP4MonthFilter.removeAllItems();
                    jP4MonthFilter.addItem("Semua Bulan");
                    while (rs.next()) {
                        jP4MonthFilter.addItem(rs.getString("bulan"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error di setupComboBox: " + e.getMessage());
        }
    }

    public void loadData(int userId, String bulan) {
        try (Connection conn = bisnisku.app.connection.getKoneksi()) {
            if (conn == null) {
                System.out.println("Error: Gagal mendapatkan koneksi database.");
                return;
            }

            // --- DEKLARASI VARIABEL UNTUK PERHITUNGAN SALDO ---
            double modalAwal = 0;
            double totalKeluar = 0;
            double totalPemasukan = 0;

            // 1. Ambil modal awal dari bisnis_profile
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT modal_awal FROM bisnis_profile WHERE user_id = ?")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        modalAwal = rs.getDouble("modal_awal");
                        jP4ModalAwal.setText("RP. " + String.format("%,.0f", modalAwal));
                        System.out.println("Modal Awal: " + modalAwal);
                    }
                }
            }

            // 2. Ambil total Pengeluaran dari tabel transaksi
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(SUM(nominal),0) AS total FROM transaksi WHERE user_id = ?")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalKeluar = rs.getDouble("total");
                    }
                }
            }

            // 3. Ambil total Pemasukan dari tabel pemasukan_harian
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(SUM(total_pendapatan),0) AS total FROM pemasukan_harian WHERE id_user = ?")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalPemasukan = rs.getDouble("total");
                    }
                }
            }

            // 4. Hitung Saldo (Modal + Pemasukan - Pengeluaran) & Atur Warna Teks
            double saldoTotal = modalAwal + totalPemasukan - totalKeluar;
            jP4Saldo.setText("RP. " + String.format("%,.0f", saldoTotal));

            if (saldoTotal < 0) {
                jP4Saldo.setForeground(new java.awt.Color(255, 123, 103)); // Merah untuk minus
            } else {
                jP4Saldo.setForeground(new java.awt.Color(30, 158, 117)); // Hijau untuk positif
            }

            // 5. Ambil data untuk tabel berdasarkan filter bulan
            String sql;
            boolean isFilterBulan = (bulan != null && !bulan.equals("Semua Bulan"));

            if (!isFilterBulan) {
                // Group by Bulan dan Kategori (Hanya 2 parameter: userId, userId)
                sql = "SELECT 'Pemasukan' AS kategori, SUM(total_pendapatan) AS total_nominal, "
                        + "DATE_FORMAT(tanggal, '%Y-%m') AS bulan_transaksi "
                        + "FROM pemasukan_harian WHERE id_user = ? "
                        + "UNION ALL "
                        + "SELECT kategori, SUM(nominal) AS total_nominal, "
                        + "DATE_FORMAT(tanggal, '%Y-%m') AS bulan_transaksi "
                        + "FROM transaksi WHERE user_id = ? "
                        + "GROUP BY bulan_transaksi, kategori "
                        + "ORDER BY bulan_transaksi DESC, total_nominal DESC";
            } else {
                // Group by Bulan dan Kategori, dengan filter bulan spesifik (4 parameter: userId, bulan, userId, bulan)
                sql = "SELECT 'Pemasukan' AS kategori, SUM(total_pendapatan) AS total_nominal, "
                        + "DATE_FORMAT(tanggal, '%Y-%m') AS bulan_transaksi "
                        + "FROM pemasukan_harian WHERE id_user = ? AND DATE_FORMAT(tanggal, '%Y-%m') = ? "
                        + "UNION ALL "
                        + "SELECT kategori, SUM(nominal) AS total_nominal, "
                        + "DATE_FORMAT(tanggal, '%Y-%m') AS bulan_transaksi "
                        + "FROM transaksi WHERE user_id = ? AND DATE_FORMAT(tanggal, '%Y-%m') = ? "
                        + "GROUP BY bulan_transaksi, kategori "
                        + "ORDER BY total_nominal DESC";
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                // Set parameter sesuai kondisi query di atas
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
                    // Tambahkan kolom "Bulan" ke dalam DefaultTableModel
                    javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                            new String[]{"Kategori", "Total Nominal", "Bulan"}, 0);

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("kategori"),
                            "RP. " + String.format("%,.0f", rs.getDouble("total_nominal")),
                            rs.getString("bulan_transaksi") // Ambil data alias bulan_transaksi dari SQL
                        });
                    }
                    jP4Table.setModel(model);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error di loadData: " + e.getMessage());
            e.printStackTrace(); // Tambahkan ini agar lebih mudah di-debug jika ada error lain
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Index.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Index.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Index.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Index.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (checkSession() == true) {
                    new Index().setVisible(true);
                } else {
                    new LoginForm().setVisible(true);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton finishButtonPemasukan;
    private javax.swing.JLabel jHeading1;
    private javax.swing.JLabel jHeading2;
    private javax.swing.JLabel jHeading3;
    private javax.swing.JLabel jHeading4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jP1;
    private javax.swing.JLabel jP1level;
    private javax.swing.JLabel jP1levelDesc;
    private javax.swing.JLabel jP1modalAwal;
    private javax.swing.JLabel jP1namaBisnis;
    private javax.swing.JLabel jP1saldoSaatIni;
    private javax.swing.JTable jP1table;
    private javax.swing.JLabel jP1totalKeluar;
    private javax.swing.JLabel jP1totalPemasukan;
    private javax.swing.JLabel jP1transaksiTerakhir;
    private javax.swing.JScrollPane jP2;
    private javax.swing.JButton jP2Button1;
    private javax.swing.JButton jP2Button2;
    private javax.swing.JComboBox<String> jP2CategoryDropdown;
    private com.toedter.calendar.JDateChooser jP2DateChooser1;
    private javax.swing.JTextField jP2NameExpenseField;
    private javax.swing.JTextField jP2NominalField;
    private javax.swing.JTable jP2Table1;
    private javax.swing.JScrollPane jP3;
    private javax.swing.JComboBox<String> jP3CategoriesDropdown;
    private javax.swing.JCheckBox jP3CheckAllMonth;
    private com.toedter.calendar.JMonthChooser jP3MonthDropdown;
    private javax.swing.JLabel jP3ShowData;
    private javax.swing.JTable jP3Table;
    private javax.swing.JLabel jP3TotalData;
    private com.toedter.calendar.JYearChooser jP3YearDropdown;
    private javax.swing.JScrollPane jP4;
    private javax.swing.JLabel jP4ModalAwal;
    private javax.swing.JComboBox<String> jP4MonthFilter;
    private javax.swing.JLabel jP4Saldo;
    private javax.swing.JTable jP4Table;
    private javax.swing.JScrollPane jP5;
    private javax.swing.JScrollPane jP6;
    private javax.swing.JTable jP6LeaderboardTable;
    private javax.swing.JButton jP6ShowLeaderboardButton;
    private javax.swing.JLabel jP6Top1;
    private javax.swing.JLabel jP6Top2;
    private javax.swing.JLabel jP6Top3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel jgambar;
    private javax.swing.JLabel jgambar1;
    private javax.swing.JLabel jgambar2;
    private javax.swing.JLabel jgambar3;
    private javax.swing.JLabel jsaldoHead;
    private javax.swing.JLabel jsubHead;
    private javax.swing.JLabel jsubHead1;
    private javax.swing.JLabel jsubHead2;
    private javax.swing.JPanel levelPanel;
    private javax.swing.JPanel logoutButton;
    private javax.swing.JLabel plusIconPemasukan;
    private javax.swing.JLabel saldoPemasukan;
    private javax.swing.JPanel tabDashboard;
    private javax.swing.JPanel tabKategori;
    private javax.swing.JPanel tabLeaderboard;
    private javax.swing.JPanel tabPemasukan;
    private javax.swing.JPanel tabRekap;
    private javax.swing.JPanel tabTransaksi;
    private javax.swing.JButton tapButtonPemasukan;
    private javax.swing.JLabel titleDashboard;
    private javax.swing.JLabel titleKategori;
    private javax.swing.JLabel titlePemasukan;
    private javax.swing.JLabel titleRekap;
    private javax.swing.JLabel titleRekap1;
    private javax.swing.JLabel titleRekap2;
    private javax.swing.JLabel titleRekap3;
    private javax.swing.JLabel titleTransaksi;
    // End of variables declaration//GEN-END:variables
}
