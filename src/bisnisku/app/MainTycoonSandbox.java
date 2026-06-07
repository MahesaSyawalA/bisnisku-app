/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bisnisku.app;

//import controller
import bisnisku.app.controllers.TrackYourExpensesController;
import bisnisku.app.controllers.DashboardController;
import bisnisku.app.controllers.CategoryController;
import bisnisku.app.controllers.RekapController;
import bisnisku.app.controllers.IncomeController;
import bisnisku.app.controllers.LeaderboardController;

//import libs
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 *
 * @author Sanchie
 */
public class MainTycoonSandbox extends javax.swing.JFrame {

    //initial variable title windows    
    String titleWindow = "Dashboard";
    //initial variable title windows    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainTycoonSandbox.class.getName());

    //Controllers     
    private TrackYourExpensesController controller;
    private DashboardController dashboardController;
    private CategoryController categoryController;
    private RekapController rekapController;
    private IncomeController incomeController;
    private LeaderboardController leaderboardController;

    //Import connection    
    connection conn;

    // Kebutuhan logika & Pemasukan
    private final String[] KATEGORI = {"Operasional", "Gaji Karyawan", "Utilitas", "Gaya Hidup", "Lain-lain"};
    private long totalSaldo = 0;
    private long totalKlik = 0;
    private long nilaiPerTap = 5;
    private javax.swing.Timer animTimer;
    private int startY1 = -1;
    private int startY2 = -1;

    /**
     * Creates new form MainTycoonSandbox
     */
    public MainTycoonSandbox() {
        initComponents();
        
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.85);
        int height = (int) (screenSize.height * 0.85);
        this.setSize(width, height);
        this.setLocationRelativeTo(null); 
        
        //fix leaderboard
        jLabel14.setVisible(false);
        jPanel30.setLayout(new java.awt.BorderLayout());
        jPanel30.add(jPanel23, java.awt.BorderLayout.CENTER);
        jScrollPane7.setHorizontalScrollBarPolicy(
            javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        
        
        //set Init Title      
        this.setTitle("Bisnisku App - Dashboard");
        //set icon app         
        this.setTitle("Bisnisku App - " + titleWindow);
        try {
            java.awt.Image icon = java.awt.Toolkit.getDefaultToolkit().getImage(getClass().getResource("/bisnisku/app/assets/logobinikuuu.png"));
            this.setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Gambar icon gagal dimuat: " + e.getMessage());
        }

        //creating object controller        
        this.controller = new TrackYourExpensesController();
        this.dashboardController = new DashboardController();
        this.categoryController = new CategoryController();
        this.rekapController = new RekapController();
        this.incomeController = new IncomeController();
        this.leaderboardController = new LeaderboardController();

        //creating object connection        
        this.conn = new connection();

        int userId = UserSession.getUserId();
        String namaBisnis = UserSession.getNamaBisnis();

        // Sembunyikan icon animasi saat awal aplikasi dijalankan
        plusIconPemasukan.setForeground(new java.awt.Color(0, 0, 0, 0));
        jsubHead2.setForeground(new java.awt.Color(0, 0, 0, 0));
        titlePemasukan.setText(namaBisnis);

        // Load Data Awal
        jP1loadData(userId);
        setupCategoryComboBox();
        setupComboBox();
        loadData(userId, "Semua Bulan");

        int currentMonth = jP3MonthDropdown.getMonth();
        int currentYear = jP3YearDropdown.getYear();
        loadDataByCategory(userId, "Semua Kategori", currentMonth, currentYear, false);

        // Listener untuk JDateChooser/Kategori pada Filter
        jP3CategoriesDropdown.addActionListener(evt -> updateFilterData());
        jP3MonthDropdown.addPropertyChangeListener(evt -> {
            if ("month".equals(evt.getPropertyName())) {
                updateFilterData();
            }
        });
        jP3YearDropdown.addPropertyChangeListener(evt -> {
            if ("year".equals(evt.getPropertyName())) {
                updateFilterData();
            }
        });

        // Window Closing Confirmation
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                int confirm = JOptionPane.showConfirmDialog(
                        MainTycoonSandbox.this,
                        "Apakah Anda yakin ingin keluar dari aplikasi?",
                        "Konfirmasi Keluar",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Focus Listeners untuk Placeholders
        jP2NameExpenseField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (jP2NameExpenseField.getText().equals("Contoh: Beli biji Kopi 10kg")) {
                    jP2NameExpenseField.setText("");
                    jP2NameExpenseField.setForeground(new java.awt.Color(53, 100, 155));
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (jP2NameExpenseField.getText().isEmpty()) {
                    jP2NameExpenseField.setText("Contoh: Beli biji Kopi 10kg");
                    jP2NameExpenseField.setForeground(new java.awt.Color(153, 153, 153));
                }
            }
        });

        jP2NominalField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (jP2NominalField.getText().equals("Contoh: 850000")) {
                    jP2NominalField.setText("");
                    jP2NominalField.setForeground(new java.awt.Color(53, 100, 155));
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (jP2NominalField.getText().isEmpty()) {
                    jP2NominalField.setText("Contoh: 850000");
                    jP2NominalField.setForeground(new java.awt.Color(153, 153, 153));
                }
            }
        });

        // Table Mouse Click Listener (Untuk Hapus Transaksi)
        jP2Table1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = jP2Table1.getSelectedRow();

                if (row != -1) {
                    // Ambil nilai dari kolom 0 (ID) dan kolom 1 (Nama) sebagai Object terlebih dahulu
                    Object idObj = jP2Table1.getValueAt(row, 0);
                    Object namaObj = jP2Table1.getValueAt(row, 1);

                    // PENGECEKAN NULL: Jika salah satunya kosong, batalkan proses (return)
                    if (idObj == null || namaObj == null) {
                        return;
                    }

                    // Jika data aman (tidak null), baru kita convert ke int dan String
                    int id = Integer.parseInt(idObj.toString());
                    String nama = namaObj.toString();

                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Yakin ingin menghapus transaksi: " + nama + "?",
                            "Konfirmasi Hapus",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean berhasil = controller.deleteTransaksi(id);
                        if (berhasil) {
                            JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
                            refreshTableJp2();
                        } else {
                            JOptionPane.showMessageDialog(null, "Data gagal dihapus!");
                        }
                    }
                }
            }
        });
    }

    private void refreshTableJp2() {
        jP2Table1.setModel(controller.getModelTransaksi());
        jP2Table1.getColumnModel().getColumn(0).setMinWidth(0);
        jP2Table1.getColumnModel().getColumn(0).setMaxWidth(0);
        jP2Table1.getColumnModel().getColumn(0).setWidth(0);
    }

    private void playFloatAnimation() {
        java.awt.Toolkit.getDefaultToolkit().beep();
        if (animTimer != null && animTimer.isRunning()) {
            animTimer.stop();
        }

        if (startY1 == -1) {
            startY1 = plusIconPemasukan.getY();
            startY2 = jsubHead2.getY();
        }

        plusIconPemasukan.setLocation(plusIconPemasukan.getX(), startY1);
        jsubHead2.setLocation(jsubHead2.getX(), startY2);

        plusIconPemasukan.setForeground(new java.awt.Color(0, 204, 0));
        jsubHead2.setForeground(new java.awt.Color(0, 204, 0));

        animTimer = new javax.swing.Timer(20, new java.awt.event.ActionListener() {
            int frame = 0;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                frame++;
                plusIconPemasukan.setLocation(plusIconPemasukan.getX(), plusIconPemasukan.getY() - 3);
                jsubHead2.setLocation(jsubHead2.getX(), jsubHead2.getY() - 2);

                if (frame >= 20) {
                    plusIconPemasukan.setForeground(new java.awt.Color(0, 0, 0, 0));
                    jsubHead2.setForeground(new java.awt.Color(0, 0, 0, 0));
                    animTimer.stop();
                }
            }
        });
        animTimer.start();
    }

    public void jP1loadData(int userId) {
        Map<String, Object> data = dashboardController.getDashboardData(userId);
        if (data.isEmpty()) {
            return;
        }

        jP1namaBisnis.setText((String) data.get("namaBisnis"));
        jP1modalAwal.setText((String) data.get("modalAwalStr"));
        jP1totalKeluar.setText((String) data.get("totalKeluarStr"));
        jP1totalPemasukan.setText((String) data.get("totalPemasukanStr"));
        jP1saldoSaatIni.setText((String) data.get("saldoSaatIniStr"));
        jP1transaksiTerakhir.setText((String) data.get("transaksiTerakhirStr"));

        boolean isSaldoNegative = (boolean) data.get("isSaldoNegative");
        jP1saldoSaatIni.setForeground(isSaldoNegative ? new java.awt.Color(255, 123, 103) : new java.awt.Color(30, 158, 117));

        jProgressBar1.setMinimum(0);
        jProgressBar1.setMaximum(100);
        jProgressBar1.setValue((int) data.get("progressValue"));

        jP1level.setText((String) data.get("levelText"));
        jP1levelDesc.setText((String) data.get("levelDescText"));

        int levelStatus = (int) data.get("levelStatus");
        switch (levelStatus) {
            case 1 ->
                levelPanel.setBackground(new java.awt.Color(232, 240, 254)); // Hijau/Normal
            case 2 ->
                levelPanel.setBackground(new java.awt.Color(255, 243, 205)); // Kuning
            case 3 ->
                levelPanel.setBackground(new java.awt.Color(255, 220, 220)); // Merah
        }

        DefaultTableModel tableModel = (DefaultTableModel) data.get("tableModel");
        if (tableModel != null) {
            jP1table.setModel(tableModel);
        }
    }

    private void setupCategoryComboBox() {
        jP3CategoriesDropdown.removeAllItems();
        jP3CategoriesDropdown.addItem("Semua Kategori");
        for (String kat : KATEGORI) {
            jP3CategoriesDropdown.addItem(kat);
        }
    }

    public void loadDataByCategory(int userId, String kategori, int bulan, int tahun, boolean semuaBulan) {
        Map<String, Object> data = categoryController.getCategoryData(userId, kategori, bulan, tahun, semuaBulan);
        if (data.isEmpty()) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) data.get("tableModel");
        if (model != null) {
            jP3Table.setModel(model);
        }

        jP3ShowData.setText((String) data.get("totalTransaksiStr"));
        jP3TotalData.setText((String) data.get("totalNominalStr"));
    }

    private void updateFilterData() {
        String kategori = (String) jP3CategoriesDropdown.getSelectedItem();
        int bulan = jP3MonthDropdown.getMonth();
        int tahun = jP3YearDropdown.getYear();
        int userId = UserSession.getUserId();
        boolean semuaBulan = jP3CheckAllMonth.isSelected();
        loadDataByCategory(userId, kategori, bulan, tahun, semuaBulan);
    }

    private void setupComboBox() {
        int userId = UserSession.getUserId();
        List<String> months = rekapController.getAvailableMonths(userId);
        jP4MonthFilter.removeAllItems();
        for (String month : months) {
            jP4MonthFilter.addItem(month);
        }
    }

    public void loadData(int userId, String bulan) {
        Map<String, Object> data = rekapController.getRekapData(userId, bulan);
        if (data.isEmpty()) {
            return;
        }

        jP4ModalAwal.setText((String) data.get("modalAwalStr"));
        jP4Saldo.setText((String) data.get("saldoTotalStr"));

        boolean isSaldoNegative = (boolean) data.get("isSaldoNegative");
        jP4Saldo.setForeground(isSaldoNegative ? new java.awt.Color(255, 123, 103) : new java.awt.Color(30, 158, 117));

        DefaultTableModel model = (DefaultTableModel) data.get("tableModel");
        if (model != null) {
            jP4Table.setModel(model);
        }
    }

    private void exportPDF() {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);

            content.beginText();
            content.newLineAtOffset(220, 770);
            content.showText("LAPORAN KEUANGAN");
            content.endText();

            content.beginText();
            content.newLineAtOffset(50, 730);
            content.showText("Modal Awal : " + jP4ModalAwal.getText());
            content.endText();

            content.beginText();
            content.newLineAtOffset(50, 710);
            content.showText("Saldo Akhir : " + jP4Saldo.getText());
            content.endText();

            drawTable(content, jP4Table, 650, 50);
            content.close();

            document.save("LaporanKeuangan.pdf");
            document.close();
            JOptionPane.showMessageDialog(this, "PDF berhasil dibuat!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void drawTable(PDPageContentStream content, JTable table, float startY, float margin) throws IOException {
        float rowHeight = 25f;
        float tableWidth = 500f;
        int cols = table.getColumnCount();
        int rows = table.getRowCount();
        float colWidth = tableWidth / cols;
        float y = startY;

        for (int col = 0; col < cols; col++) {
            float x = margin + (col * colWidth);
            content.addRect(x, y, colWidth, rowHeight);
            content.beginText();
            content.newLineAtOffset(x + 5, y + 8);
            content.showText(table.getColumnName(col));
            content.endText();
        }
        content.stroke();
        y -= rowHeight;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = margin + (col * colWidth);
                content.addRect(x, y, colWidth, rowHeight);
                Object value = table.getValueAt(row, col);
                String text = value == null ? "" : value.toString();
                if (text.length() > 20) {
                    text = text.substring(0, 17) + "...";
                }

                content.beginText();
                content.newLineAtOffset(x + 5, y + 8);
                content.showText(text);
                content.endText();
            }
            content.stroke();
            y -= rowHeight;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane11 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        sidebarPanel = new javax.swing.JPanel();
        jTitle = new javax.swing.JLabel();
        jButtonDashboard = new javax.swing.JButton() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                if (isOpaque()) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();

                boolean originalOpaque = isOpaque();
                setOpaque(false);
                super.paintComponent(g);
                setOpaque(originalOpaque);
            }
        };
        jButtonTransaksi = new javax.swing.JButton() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                if (isOpaque()) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();

                boolean originalOpaque = isOpaque();
                setOpaque(false);
                super.paintComponent(g);
                setOpaque(originalOpaque);
            }
        };
        jButtonKategori = new javax.swing.JButton() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                if (isOpaque()) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();

                boolean originalOpaque = isOpaque();
                setOpaque(false);
                super.paintComponent(g);
                setOpaque(originalOpaque);
            }
        };
        jButtonRekap = new javax.swing.JButton() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                if (isOpaque()) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();

                boolean originalOpaque = isOpaque();
                setOpaque(false);
                super.paintComponent(g);
                setOpaque(originalOpaque);
            }
        };
        jButtonSimulasi = new javax.swing.JButton() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                if (isOpaque()) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();

                boolean originalOpaque = isOpaque();
                setOpaque(false);
                super.paintComponent(g);
                setOpaque(originalOpaque);
            }
        };
        jButtonPeringkat = new javax.swing.JButton() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                if (isOpaque()) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();

                boolean originalOpaque = isOpaque();
                setOpaque(false);
                super.paintComponent(g);
                setOpaque(originalOpaque);
            }
        };
        jButtonLogout = new javax.swing.JButton() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                if (isOpaque()) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();

                boolean originalOpaque = isOpaque();
                setOpaque(false);
                super.paintComponent(g);
                setOpaque(originalOpaque);
            }
        };
        mainContentPanel = new javax.swing.JPanel();
        menuDashboard = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel21 = new javax.swing.JPanel();
        levelPanel = new javax.swing.JPanel();
        jP1level = new javax.swing.JLabel();
        jP1levelDesc = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jP1namaBisnis = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jScrollPane3 = new javax.swing.JScrollPane();
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
        menuTransaksi = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
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
        jLabel2 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jP2Table1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jP2Button2 = new javax.swing.JButton();
        jP2Button1 = new javax.swing.JButton();
        menuKategori = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel19 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jP3Table = new javax.swing.JTable();
        jP3ShowData = new javax.swing.JLabel();
        jP3TotalData = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jP3YearDropdown = new com.toedter.calendar.JYearChooser();
        jP3MonthDropdown = new com.toedter.calendar.JMonthChooser();
        jP3CategoriesDropdown = new javax.swing.JComboBox<>();
        jP3CheckAllMonth = new javax.swing.JCheckBox();
        menuRekap = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
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
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        menuSimulasi = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        titlePemasukan = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        saldoPemasukan = new javax.swing.JLabel();
        jsaldoHead = new javax.swing.JLabel();
        finishButtonPemasukan = new javax.swing.JButton();
        jgambar = new javax.swing.JLabel();
        plusIconPemasukan = new javax.swing.JLabel();
        tapButtonPemasukan = new javax.swing.JButton();
        jsubHead = new javax.swing.JLabel();
        jsubHead2 = new javax.swing.JLabel();
        menuPeringkat = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jPanel30 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jP6ShowLeaderboardButton = new javax.swing.JButton();
        jgambar2 = new javax.swing.JLabel();
        jgambar1 = new javax.swing.JLabel();
        jHeading1 = new javax.swing.JLabel();
        jPanel33 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        jHeading2 = new javax.swing.JLabel();
        jP6Top3 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jP6LeaderboardTable = new javax.swing.JTable();
        jgambar3 = new javax.swing.JLabel();
        jsubHead1 = new javax.swing.JLabel();
        jPanel35 = new javax.swing.JPanel();
        jPanel36 = new javax.swing.JPanel();
        jHeading4 = new javax.swing.JLabel();
        jP6Top2 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        jPanel32 = new javax.swing.JPanel();
        jHeading3 = new javax.swing.JLabel();
        jP6Top1 = new javax.swing.JLabel();

        jScrollPane11.setViewportView(jEditorPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sidebarPanel.setBackground(new java.awt.Color(255, 255, 255));
        sidebarPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(229, 233, 232)));
        sidebarPanel.setPreferredSize(new java.awt.Dimension(140, 0));

        jTitle.setFont(new java.awt.Font("Poppins Medium", 1, 20)); // NOI18N
        jTitle.setForeground(new java.awt.Color(53, 100, 155));
        jTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bisnisku/app/assets/logobinikuuu.png"))); // NOI18N
        jTitle.setText("Bisnisku");

        jButtonDashboard.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButtonDashboard.setForeground(new java.awt.Color(53, 100, 155));
        jButtonDashboard.setText("Dashboard");
        jButtonDashboard.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 15, 1, 1));
        jButtonDashboard.setBorderPainted(false);
        jButtonDashboard.setContentAreaFilled(false);
        jButtonDashboard.setFocusPainted(false);
        jButtonDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonDashboard.setMargin(new java.awt.Insets(2, 30, 3, 14));
        jButtonDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonDashboardMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonDashboardMouseExited(evt);
            }
        });
        jButtonDashboard.addActionListener(this::jButtonDashboardActionPerformed);

        jButtonTransaksi.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButtonTransaksi.setForeground(new java.awt.Color(53, 100, 155));
        jButtonTransaksi.setText("Transaksi");
        jButtonTransaksi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 15, 1, 1));
        jButtonTransaksi.setBorderPainted(false);
        jButtonTransaksi.setContentAreaFilled(false);
        jButtonTransaksi.setFocusPainted(false);
        jButtonTransaksi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonTransaksi.setMargin(new java.awt.Insets(2, 30, 3, 14));
        jButtonTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonTransaksiMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonTransaksiMouseExited(evt);
            }
        });
        jButtonTransaksi.addActionListener(this::jButtonTransaksiActionPerformed);

        jButtonKategori.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButtonKategori.setForeground(new java.awt.Color(53, 100, 155));
        jButtonKategori.setText("Kategori");
        jButtonKategori.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 15, 1, 1));
        jButtonKategori.setBorderPainted(false);
        jButtonKategori.setContentAreaFilled(false);
        jButtonKategori.setFocusPainted(false);
        jButtonKategori.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonKategori.setMargin(new java.awt.Insets(2, 30, 3, 14));
        jButtonKategori.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonKategoriMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonKategoriMouseExited(evt);
            }
        });
        jButtonKategori.addActionListener(this::jButtonKategoriActionPerformed);

        jButtonRekap.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButtonRekap.setForeground(new java.awt.Color(53, 100, 155));
        jButtonRekap.setText("Rekap ");
        jButtonRekap.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 15, 1, 1));
        jButtonRekap.setBorderPainted(false);
        jButtonRekap.setContentAreaFilled(false);
        jButtonRekap.setFocusPainted(false);
        jButtonRekap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonRekap.setMargin(new java.awt.Insets(2, 30, 3, 14));
        jButtonRekap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonRekapMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonRekapMouseExited(evt);
            }
        });
        jButtonRekap.addActionListener(this::jButtonRekapActionPerformed);

        jButtonSimulasi.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButtonSimulasi.setForeground(new java.awt.Color(53, 100, 155));
        jButtonSimulasi.setText("Permainan");
        jButtonSimulasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 15, 1, 1));
        jButtonSimulasi.setBorderPainted(false);
        jButtonSimulasi.setContentAreaFilled(false);
        jButtonSimulasi.setFocusPainted(false);
        jButtonSimulasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonSimulasi.setMargin(new java.awt.Insets(2, 30, 3, 14));
        jButtonSimulasi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonSimulasiMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonSimulasiMouseExited(evt);
            }
        });
        jButtonSimulasi.addActionListener(this::jButtonSimulasiActionPerformed);

        jButtonPeringkat.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButtonPeringkat.setForeground(new java.awt.Color(53, 100, 155));
        jButtonPeringkat.setText("Peringkat");
        jButtonPeringkat.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 15, 1, 1));
        jButtonPeringkat.setBorderPainted(false);
        jButtonPeringkat.setContentAreaFilled(false);
        jButtonPeringkat.setFocusPainted(false);
        jButtonPeringkat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonPeringkat.setMargin(new java.awt.Insets(2, 30, 3, 14));
        jButtonPeringkat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonPeringkatMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonPeringkatMouseExited(evt);
            }
        });
        jButtonPeringkat.addActionListener(this::jButtonPeringkatActionPerformed);

        jButtonLogout.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButtonLogout.setForeground(new java.awt.Color(251, 44, 54));
        jButtonLogout.setText("Keluar");
        jButtonLogout.setBorder(null);
        jButtonLogout.setBorderPainted(false);
        jButtonLogout.setContentAreaFilled(false);
        jButtonLogout.setFocusPainted(false);
        jButtonLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonLogoutMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonLogoutMouseExited(evt);
            }
        });
        jButtonLogout.addActionListener(this::jButtonLogoutActionPerformed);

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonKategori, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonRekap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonSimulasi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonPeringkat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRekap, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSimulasi, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPeringkat, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(sidebarPanel, java.awt.BorderLayout.WEST);

        mainContentPanel.setBackground(new java.awt.Color(248, 250, 252));
        mainContentPanel.setLayout(new java.awt.CardLayout());

        menuDashboard.setBackground(new java.awt.Color(230, 246, 244));
        menuDashboard.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setBackground(new java.awt.Color(230, 246, 244));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel21.setBackground(new java.awt.Color(248, 250, 252));

        levelPanel.setBackground(new java.awt.Color(232, 240, 254));
        levelPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(54, 67, 64)));
        levelPanel.setForeground(new java.awt.Color(255, 244, 238));

        jP1level.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        jP1level.setForeground(new java.awt.Color(30, 58, 138));
        jP1level.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jP1level.setText("Level: Pengusaha sehat");
        jP1level.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jP1levelDesc.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jP1levelDesc.setForeground(new java.awt.Color(30, 58, 138));
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
        jLabel1.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(53, 100, 155));
        jLabel1.setText("Kesehatan Keuangan");

        jP1namaBisnis.setBackground(new java.awt.Color(204, 204, 204));
        jP1namaBisnis.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jP1namaBisnis.setForeground(new java.awt.Color(53, 100, 155));
        jP1namaBisnis.setText("Nama Bisnis");

        jProgressBar1.setBackground(new java.awt.Color(232, 240, 254));
        jProgressBar1.setForeground(new java.awt.Color(29, 157, 117));

        jP1table.setBackground(new java.awt.Color(232, 240, 254));
        jP1table.setForeground(new java.awt.Color(30, 58, 138));
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
        jScrollPane3.setViewportView(jP1table);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jPanel10.setBackground(new java.awt.Color(232, 240, 254));

        jLabel19.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(30, 58, 138));
        jLabel19.setText("Modal Awal");

        jP1modalAwal.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jP1modalAwal.setForeground(new java.awt.Color(30, 58, 138));
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
                .addContainerGap(142, Short.MAX_VALUE))
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

        jPanel11.setBackground(new java.awt.Color(232, 240, 254));

        jLabel20.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(30, 58, 138));
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

        jPanel13.setBackground(new java.awt.Color(232, 240, 254));

        jLabel22.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(30, 58, 138));
        jLabel22.setText("Transaksi Terakhir");

        jP1transaksiTerakhir.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jP1transaksiTerakhir.setForeground(new java.awt.Color(30, 58, 138));
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
                .addContainerGap(140, Short.MAX_VALUE))
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

        jPanel14.setBackground(new java.awt.Color(232, 240, 254));

        jLabel23.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(30, 58, 138));
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

        jPanel29.setBackground(new java.awt.Color(232, 240, 254));

        jLabel25.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(30, 58, 138));
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
        jLabel5.setForeground(new java.awt.Color(53, 100, 155));
        jLabel5.setText("Ringkasan bisnis");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jP1namaBisnis))
                    .addComponent(levelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane2.setViewportView(jPanel21);

        menuDashboard.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        mainContentPanel.add(menuDashboard, "card2");

        menuTransaksi.setBackground(new java.awt.Color(230, 246, 244));
        menuTransaksi.setLayout(new java.awt.BorderLayout());

        jScrollPane8.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));
        jPanel18.setForeground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(53, 100, 155));
        jLabel12.setText("Catat Pengeluaran Baru");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(53, 100, 155));
        jLabel4.setText("Nama Pengeluaran");

        jP2NameExpenseField.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        jP2NameExpenseField.setForeground(new java.awt.Color(53, 100, 155));
        jP2NameExpenseField.setText("Contoh: Beli biji Kopi 10kg");
        jP2NameExpenseField.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 2, true), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        jP2NameExpenseField.setPreferredSize(new java.awt.Dimension(176, 32));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jP2NameExpenseField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP2NameExpenseField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));
        jPanel20.setPreferredSize(new java.awt.Dimension(182, 57));

        jLabel7.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(53, 100, 155));
        jLabel7.setText("Nominal (Rp)");

        jP2NominalField.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        jP2NominalField.setForeground(new java.awt.Color(53, 100, 155));
        jP2NominalField.setText("Contoh: 850000");
        jP2NominalField.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 2, true), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        jP2NominalField.setPreferredSize(new java.awt.Dimension(176, 32));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jP2NominalField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP2NominalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setForeground(new java.awt.Color(53, 100, 155));

        jLabel8.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(53, 100, 155));
        jLabel8.setText("Kategori");

        jP2CategoryDropdown.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        jP2CategoryDropdown.setForeground(new java.awt.Color(53, 100, 155));
        jP2CategoryDropdown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Operasional", "Gaji Karyawan", "Utilitas", "Gaya Hidup", "Lain-lain" }));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jP2CategoryDropdown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP2CategoryDropdown, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(53, 100, 155));
        jLabel9.setText("Tanggal");

        jP2DateChooser1.setBackground(new java.awt.Color(48, 48, 46));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jP2DateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jP2DateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel2.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel2.setText("Terakhir dicatat");

        jP2Table1.setBackground(new java.awt.Color(232, 240, 254));
        jP2Table1.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        jP2Table1.setForeground(new java.awt.Color(53, 100, 155));
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
        jScrollPane9.setViewportView(jP2Table1);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jP2Button2.setFont(new java.awt.Font("Poppins SemiBold", 0, 15)); // NOI18N
        jP2Button2.setForeground(new java.awt.Color(53, 100, 155));
        jP2Button2.setText("Batal");
        jPanel1.add(jP2Button2);

        jP2Button1.setFont(new java.awt.Font("Poppins SemiBold", 0, 15)); // NOI18N
        jP2Button1.setForeground(new java.awt.Color(53, 100, 155));
        jP2Button1.setText("Simpan");
        jP2Button1.addActionListener(this::jP2Button1ActionPerformed);
        jPanel1.add(jP2Button1);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(26, 26, 26))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(134, Short.MAX_VALUE))
        );

        jScrollPane8.setViewportView(jPanel18);

        menuTransaksi.add(jScrollPane8, java.awt.BorderLayout.CENTER);

        mainContentPanel.add(menuTransaksi, "card3");

        menuKategori.setBackground(new java.awt.Color(230, 246, 244));
        menuKategori.setLayout(new java.awt.BorderLayout());

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));
        jPanel19.setPreferredSize(new java.awt.Dimension(515, 566));

        jLabel11.setFont(new java.awt.Font("Poppins Medium", 1, 20)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(53, 100, 155));
        jLabel11.setText("Filter Per Kategori");

        jP3Table.setForeground(new java.awt.Color(53, 100, 155));
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
        jP3Table.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jScrollPane10.setViewportView(jP3Table);

        jP3ShowData.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jP3ShowData.setForeground(new java.awt.Color(255, 255, 255));
        jP3ShowData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jP3ShowData.setText("5 data ditemukan");

        jP3TotalData.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jP3TotalData.setForeground(new java.awt.Color(255, 255, 255));
        jP3TotalData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jP3TotalData.setText("Total: Rp 3,22 jt");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jP3MonthDropdown.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jP3MonthDropdown.setForeground(new java.awt.Color(53, 100, 155));

        jP3CategoriesDropdown.setForeground(new java.awt.Color(53, 100, 155));
        jP3CategoriesDropdown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jP3CheckAllMonth.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jP3CheckAllMonth.setForeground(new java.awt.Color(53, 100, 155));
        jP3CheckAllMonth.setText("Semua Bulan");
        jP3CheckAllMonth.addActionListener(this::jP3CheckAllMonthActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jP3CategoriesDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jP3CheckAllMonth)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jP3MonthDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jP3YearDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jP3YearDropdown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jP3MonthDropdown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jP3CategoriesDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jP3CheckAllMonth))
        );

        jPanel3.add(jPanel2);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel19Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jP3ShowData)
                                .addGap(295, 295, 295)
                                .addComponent(jP3TotalData))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel19Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel11)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel19Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.LEADING))))
                .addGap(12, 12, 12))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jP3ShowData)
                    .addComponent(jP3TotalData))
                .addContainerGap(330, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel19);

        menuKategori.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        mainContentPanel.add(menuKategori, "card4");

        menuRekap.setBackground(new java.awt.Color(230, 246, 244));
        menuRekap.setLayout(new java.awt.BorderLayout());

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setDoubleBuffered(false);
        jPanel15.setPreferredSize(new java.awt.Dimension(515, 538));

        jLabel6.setFont(new java.awt.Font("Poppins", 1, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(30, 58, 138));
        jLabel6.setText("Breakdown Pengeluaran");

        jPanel16.setBackground(new java.awt.Color(232, 240, 254));

        jLabel21.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(30, 58, 138));
        jLabel21.setText("Modal Awal");

        jP4ModalAwal.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jP4ModalAwal.setForeground(new java.awt.Color(30, 58, 138));
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jPanel17.setBackground(new java.awt.Color(232, 240, 254));

        jLabel24.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(30, 58, 138));
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        jLabel10.setForeground(new java.awt.Color(30, 58, 138));
        jLabel10.setText("Rekap Bulanan");

        jScrollPane4.setBackground(new java.awt.Color(255, 255, 255));

        jP4Table.setForeground(new java.awt.Color(53, 100, 155));
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

        jP4MonthFilter.setForeground(new java.awt.Color(30, 58, 138));
        jP4MonthFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jP4MonthFilter.addActionListener(this::jP4MonthFilterActionPerformed);

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButton1.setForeground(new java.awt.Color(30, 58, 138));
        jButton1.setText("Export ke PDF");
        jButton1.addActionListener(this::jButton1ActionPerformed);
        jPanel5.add(jButton1);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(jP4MonthFilter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel10))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jP4MonthFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane5.setViewportView(jPanel15);

        menuRekap.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        mainContentPanel.add(menuRekap, "card5");

        menuSimulasi.setBackground(new java.awt.Color(230, 246, 244));
        menuSimulasi.setLayout(new java.awt.BorderLayout());

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setMaximumSize(new java.awt.Dimension(515, 32767));
        jPanel6.setPreferredSize(new java.awt.Dimension(515, 536));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 5));

        jPanel22.setBackground(new java.awt.Color(255, 255, 255));

        titlePemasukan.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        titlePemasukan.setForeground(new java.awt.Color(30, 58, 138));
        titlePemasukan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titlePemasukan.setText("Nama Bisnismu");

        jPanel12.setBackground(new java.awt.Color(232, 240, 254));

        saldoPemasukan.setBackground(new java.awt.Color(255, 255, 255));
        saldoPemasukan.setFont(new java.awt.Font("Poppins Medium", 1, 14)); // NOI18N
        saldoPemasukan.setForeground(new java.awt.Color(30, 58, 138));
        saldoPemasukan.setText("Rp. 0");

        jsaldoHead.setFont(new java.awt.Font("Poppins Medium", 1, 12)); // NOI18N
        jsaldoHead.setForeground(new java.awt.Color(30, 58, 138));
        jsaldoHead.setText("Pemasukan Anda :");

        finishButtonPemasukan.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        finishButtonPemasukan.setForeground(new java.awt.Color(30, 58, 138));
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

        jgambar.setFont(new java.awt.Font("Poppins Light", 1, 12)); // NOI18N
        jgambar.setForeground(new java.awt.Color(155, 155, 151));
        jgambar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bisnisku/app/assets/BisniskuPemasukan.png"))); // NOI18N

        plusIconPemasukan.setFont(new java.awt.Font("Poppins Light", 1, 48)); // NOI18N
        plusIconPemasukan.setForeground(new java.awt.Color(0, 204, 0));
        plusIconPemasukan.setText("+");

        tapButtonPemasukan.setBackground(new java.awt.Color(232, 240, 254));
        tapButtonPemasukan.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        tapButtonPemasukan.setForeground(new java.awt.Color(30, 58, 138));
        tapButtonPemasukan.setText("Tap Untuk Menjual");
        tapButtonPemasukan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tapButtonPemasukanMouseClicked(evt);
            }
        });
        tapButtonPemasukan.addActionListener(this::tapButtonPemasukanActionPerformed);

        jsubHead.setFont(new java.awt.Font("Poppins Light", 1, 12)); // NOI18N
        jsubHead.setForeground(new java.awt.Color(155, 155, 151));
        jsubHead.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jsubHead.setText("mulai \"klik\" agar bisnismu maju");

        jsubHead2.setFont(new java.awt.Font("Poppins Light", 1, 24)); // NOI18N
        jsubHead2.setForeground(new java.awt.Color(0, 204, 0));
        jsubHead2.setText("+");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(tapButtonPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(titlePemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel22Layout.createSequentialGroup()
                            .addComponent(jgambar)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jsubHead2)
                                .addComponent(plusIconPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jsubHead, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titlePemasukan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsubHead)
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jgambar, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(plusIconPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jsubHead2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(tapButtonPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel6.add(jPanel22);

        jScrollPane6.setViewportView(jPanel6);

        menuSimulasi.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        mainContentPanel.add(menuSimulasi, "card6");

        menuPeringkat.setBackground(new java.awt.Color(230, 246, 244));
        menuPeringkat.setLayout(new java.awt.BorderLayout());

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel30.setBackground(new java.awt.Color(255, 255, 255));
        jPanel30.setMaximumSize(new java.awt.Dimension(515, 32767));
        jPanel30.setPreferredSize(new java.awt.Dimension(515, 750));
        jPanel30.add(jLabel14);

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));

        jP6ShowLeaderboardButton.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jP6ShowLeaderboardButton.setForeground(new java.awt.Color(53, 100, 155));
        jP6ShowLeaderboardButton.setText("Lihat Leaderboard");
        jP6ShowLeaderboardButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jP6ShowLeaderboardButtonMouseClicked(evt);
            }
        });

        jgambar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bisnisku/app/assets/aiFoto.png"))); // NOI18N

        jgambar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bisnisku/app/assets/aiFoto.png"))); // NOI18N

        jHeading1.setFont(new java.awt.Font("Poppins Medium", 1, 18)); // NOI18N
        jHeading1.setForeground(new java.awt.Color(53, 100, 155));
        jHeading1.setText("Leaderboard");

        jPanel33.setBackground(new java.awt.Color(153, 204, 255));
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

        jP6LeaderboardTable.setForeground(new java.awt.Color(53, 100, 155));
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
        jScrollPane12.setViewportView(jP6LeaderboardTable);

        jgambar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bisnisku/app/assets/aiFoto.png"))); // NOI18N

        jsubHead1.setFont(new java.awt.Font("Poppins Light", 1, 12)); // NOI18N
        jsubHead1.setForeground(new java.awt.Color(155, 155, 151));
        jsubHead1.setText("Seberapa jauh kamu melangkah?");

        jPanel35.setBackground(new java.awt.Color(102, 204, 255));
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

        jPanel31.setBackground(new java.awt.Color(0, 204, 255));
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

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jsubHead1)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jHeading1)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel23Layout.createSequentialGroup()
                                        .addGap(17, 17, 17)
                                        .addComponent(jgambar2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel23Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel23Layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addComponent(jgambar1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel23Layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(jP6ShowLeaderboardButton))
                                    .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jgambar3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jHeading1)
                    .addComponent(jP6ShowLeaderboardButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jsubHead1)
                        .addGap(5, 5, 5)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(jgambar2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(jgambar1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jgambar3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel30.add(jPanel23);

        jScrollPane7.setViewportView(jPanel30);

        menuPeringkat.add(jScrollPane7, java.awt.BorderLayout.CENTER);

        mainContentPanel.add(menuPeringkat, "card7");

        getContentPane().add(mainContentPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDashboardActionPerformed
        // TODO add your handling code here:
        mainContentPanel.removeAll();
        mainContentPanel.add(menuDashboard);
        mainContentPanel.repaint();
        mainContentPanel.revalidate();

        resetSemuaButton();

        jButtonDashboard.setForeground(java.awt.Color.decode("#35649b"));
        jButtonDashboard.setOpaque(true);
        jButtonDashboard.setBackground(java.awt.Color.decode("#E8F0FE"));
        this.setTitle("Bisnisku App - Dashboard");
    }//GEN-LAST:event_jButtonDashboardActionPerformed

    private void jButtonTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTransaksiActionPerformed
        mainContentPanel.removeAll();
        mainContentPanel.add(menuTransaksi);
        mainContentPanel.repaint();
        mainContentPanel.revalidate();

        resetSemuaButton();

        jButtonTransaksi.setForeground(java.awt.Color.decode("#35649b"));
        jButtonTransaksi.setOpaque(true);
        jButtonTransaksi.setBackground(java.awt.Color.decode("#E8F0FE"));
        refreshTableJp2();
        this.setTitle("Bisnisku App - Transaksi");
    }//GEN-LAST:event_jButtonTransaksiActionPerformed

    private void jButtonDashboardMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonDashboardMouseEntered
        // TODO add your handling code here:
        jButtonDashboard.setOpaque(true);
        jButtonDashboard.setBackground(java.awt.Color.decode("#E8F0FE"));
    }//GEN-LAST:event_jButtonDashboardMouseEntered

    private void jButtonDashboardMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonDashboardMouseExited
        // TODO add your handling code here:
        if (!jButtonDashboard.getForeground().equals(java.awt.Color.decode("#35649b"))) {
            jButtonDashboard.setOpaque(false);
            jButtonDashboard.setBackground(null);
        }
    }//GEN-LAST:event_jButtonDashboardMouseExited

    private void jButtonTransaksiMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTransaksiMouseEntered
        // TODO add your handling code here:
        jButtonTransaksi.setOpaque(true);
        jButtonTransaksi.setBackground(java.awt.Color.decode("#E8F0FE"));
    }//GEN-LAST:event_jButtonTransaksiMouseEntered

    private void jButtonTransaksiMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTransaksiMouseExited
        // TODO add your handling code here:
        if (!jButtonTransaksi.getForeground().equals(java.awt.Color.decode("#35649b"))) {
            jButtonTransaksi.setOpaque(false);
            jButtonTransaksi.setBackground(null);
        }
    }//GEN-LAST:event_jButtonTransaksiMouseExited

    private void jButtonKategoriMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonKategoriMouseEntered
        // TODO add your handling code here:
        jButtonKategori.setOpaque(true);
        jButtonKategori.setBackground(java.awt.Color.decode("#E8F0FE"));
    }//GEN-LAST:event_jButtonKategoriMouseEntered

    private void jButtonKategoriMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonKategoriMouseExited
        // TODO add your handling code here:
        if (!jButtonKategori.getForeground().equals(java.awt.Color.decode("#35649b"))) {
            jButtonKategori.setOpaque(false);
            jButtonKategori.setBackground(null);
        }
    }//GEN-LAST:event_jButtonKategoriMouseExited

    private void jButtonKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonKategoriActionPerformed
        // TODO add your handling code here:
        mainContentPanel.removeAll();
        mainContentPanel.add(menuKategori);
        mainContentPanel.repaint();
        mainContentPanel.revalidate();

        resetSemuaButton();

        jButtonKategori.setForeground(java.awt.Color.decode("#35649b"));
        jButtonKategori.setOpaque(true);
        jButtonKategori.setBackground(java.awt.Color.decode("#E8F0FE"));
        this.setTitle("Bisnisku App - Kategori");
    }//GEN-LAST:event_jButtonKategoriActionPerformed

    private void jButtonRekapMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRekapMouseEntered
        // TODO add your handling code here:
        jButtonRekap.setOpaque(true);
        jButtonRekap.setBackground(java.awt.Color.decode("#E8F0FE"));
    }//GEN-LAST:event_jButtonRekapMouseEntered

    private void jButtonRekapMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRekapMouseExited
        // TODO add your handling code here:
        if (!jButtonRekap.getForeground().equals(java.awt.Color.decode("#35649b"))) {
            jButtonRekap.setOpaque(false);
            jButtonRekap.setBackground(null);
        }
    }//GEN-LAST:event_jButtonRekapMouseExited

    private void jButtonRekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRekapActionPerformed
        // TODO add your handling code here:
        mainContentPanel.removeAll();
        mainContentPanel.add(menuRekap);
        mainContentPanel.repaint();
        mainContentPanel.revalidate();

        resetSemuaButton();

        jButtonRekap.setForeground(java.awt.Color.decode("#35649b"));
        jButtonRekap.setOpaque(true);
        jButtonRekap.setBackground(java.awt.Color.decode("#E8F0FE"));
        this.setTitle("Bisnisku App - Rekap");
    }//GEN-LAST:event_jButtonRekapActionPerformed

    private void jButtonSimulasiMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSimulasiMouseEntered
        // TODO add your handling code here:
        jButtonSimulasi.setOpaque(true);
        jButtonSimulasi.setBackground(java.awt.Color.decode("#E8F0FE"));
    }//GEN-LAST:event_jButtonSimulasiMouseEntered

    private void jButtonSimulasiMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSimulasiMouseExited
        // TODO add your handling code here:
        if (!jButtonSimulasi.getForeground().equals(java.awt.Color.decode("#35649b"))) {
            jButtonSimulasi.setOpaque(false);
            jButtonSimulasi.setBackground(null);
        }
    }//GEN-LAST:event_jButtonSimulasiMouseExited

    private void jButtonSimulasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSimulasiActionPerformed
        // TODO add your handling code here:
        mainContentPanel.removeAll();
        mainContentPanel.add(menuSimulasi);
        mainContentPanel.repaint();
        mainContentPanel.revalidate();

        resetSemuaButton();

        jButtonSimulasi.setForeground(java.awt.Color.decode("#35649b"));
        jButtonSimulasi.setOpaque(true);
        jButtonSimulasi.setBackground(java.awt.Color.decode("#E8F0FE"));
        this.setTitle("Bisnisku App - Permainan");
    }//GEN-LAST:event_jButtonSimulasiActionPerformed

    private void jButtonPeringkatMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonPeringkatMouseEntered
        // TODO add your handling code here:
        jButtonPeringkat.setOpaque(true);
        jButtonPeringkat.setBackground(java.awt.Color.decode("#E8F0FE"));
    }//GEN-LAST:event_jButtonPeringkatMouseEntered

    private void jButtonPeringkatMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonPeringkatMouseExited
        // TODO add your handling code here:
        if (!jButtonPeringkat.getForeground().equals(java.awt.Color.decode("#35649b"))) {
            jButtonPeringkat.setOpaque(false);
            jButtonPeringkat.setBackground(null);
        }
    }//GEN-LAST:event_jButtonPeringkatMouseExited

    private void jButtonPeringkatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPeringkatActionPerformed
        // TODO add your handling code here:
        mainContentPanel.removeAll();
        mainContentPanel.add(menuPeringkat);
        mainContentPanel.repaint();
        mainContentPanel.revalidate();

        resetSemuaButton();

        jButtonPeringkat.setForeground(java.awt.Color.decode("#35649b"));
        jButtonPeringkat.setOpaque(true);
        jButtonPeringkat.setBackground(java.awt.Color.decode("#E8F0FE"));
        this.setTitle("Bisnisku App - Peringkat");
    }//GEN-LAST:event_jButtonPeringkatActionPerformed

    private void jButtonLogoutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLogoutMouseEntered
        // TODO add your handling code here:
        jButtonLogout.setOpaque(true);
        jButtonLogout.setBackground(java.awt.Color.decode("#FEE2E2"));
    }//GEN-LAST:event_jButtonLogoutMouseEntered

    private void jButtonLogoutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLogoutMouseExited
        // TODO add your handling code here:
        jButtonLogout.setOpaque(false);
        jButtonLogout.setBackground(null);
    }//GEN-LAST:event_jButtonLogoutMouseExited

    private void jButtonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogoutActionPerformed
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
    }//GEN-LAST:event_jButtonLogoutActionPerformed

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

            if (!controller.isSaldoCukup(nominal)) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Transaksi dibatalkan!\nSaldo Anda saat ini tidak mencukupi untuk pengeluaran ini.",
                        "Peringatan Saldo Minus",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            boolean sukses = controller.simpanTransaksi(nama, nominal, kategori, sqlDate);

            if (sukses) {
                javax.swing.JOptionPane.showMessageDialog(this, "Berhasil dicatat!");
                refreshTableJp2();

                jP2NameExpenseField.setText("");
                jP2NominalField.setText("");

                jP1loadData(UserSession.getUserId());
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Gagal menyimpan ke database.");
            }

        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Nominal harus berupa angka!");
        }
    }//GEN-LAST:event_jP2Button1ActionPerformed

    private void jP3CheckAllMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jP3CheckAllMonthActionPerformed
        // TODO add your handling code here:
        updateFilterData();
    }//GEN-LAST:event_jP3CheckAllMonthActionPerformed

    private void jP4MonthFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jP4MonthFilterActionPerformed
        // TODO add your handling code here:
        int userId = UserSession.getUserId();
        String selected = (String) jP4MonthFilter.getSelectedItem();
        loadData(userId, selected);
    }//GEN-LAST:event_jP4MonthFilterActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        exportPDF();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void finishButtonPemasukanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_finishButtonPemasukanMouseClicked
        if (totalSaldo == 0) {
            JOptionPane.showMessageDialog(this, "Anda belum menjual apapun hari ini!");
            return;
        }

        int userId = UserSession.getUserId();
        boolean sukses = incomeController.simpanPemasukan(userId, totalSaldo);
        if (sukses) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hari diselesaikan! Rp " + String.format("%,d", totalSaldo) + " berhasil disimpan."
            );

            totalSaldo = 0;
            saldoPemasukan.setText("Rp 0");

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal menyimpan data pemasukan ke database.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_finishButtonPemasukanMouseClicked

    private void tapButtonPemasukanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tapButtonPemasukanMouseClicked
        // TODO add your handling code here:
        totalKlik++;

        nilaiPerTap = (long) (5 * Math.pow(1.1, totalKlik / 25.0));
        totalSaldo += nilaiPerTap;
        saldoPemasukan.setText(
                "Rp. " + String.format("%,d", totalSaldo)
        );
        playFloatAnimation();
    }//GEN-LAST:event_tapButtonPemasukanMouseClicked

    private void tapButtonPemasukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tapButtonPemasukanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tapButtonPemasukanActionPerformed

    private void jP6ShowLeaderboardButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jP6ShowLeaderboardButtonMouseClicked
        // 1. Minta data dari Controller
        Map<String, Object> data = leaderboardController.getLeaderboardData();

        // Cek jika terjadi error koneksi dll (data tidak mengandung tabel model)
        if (!data.containsKey("tableModel")) {
            JOptionPane.showMessageDialog(this, "Gagal memuat Leaderboard!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Terapkan Data Tabel ke jP6LeaderboardTable
        DefaultTableModel model = (DefaultTableModel) data.get("tableModel");
        jP6LeaderboardTable.setModel(model);

        // 3. Terapkan Teks Podium
        jP6Top1.setText((String) data.get("top1Name"));
        jP6Top2.setText((String) data.get("top2Name"));
        jP6Top3.setText((String) data.get("top3Name"));
    }//GEN-LAST:event_jP6ShowLeaderboardButtonMouseClicked

    private void resetSemuaButton() {
        java.awt.Color warnaAbu = java.awt.Color.decode("#707071");

        jButtonDashboard.setForeground(warnaAbu);
        jButtonDashboard.setOpaque(false);
        jButtonDashboard.setBackground(null);

        jButtonTransaksi.setForeground(warnaAbu);
        jButtonTransaksi.setOpaque(false);
        jButtonTransaksi.setBackground(null);

        jButtonKategori.setForeground(warnaAbu);
        jButtonKategori.setOpaque(false);
        jButtonKategori.setBackground(null);

        jButtonRekap.setForeground(warnaAbu);
        jButtonRekap.setOpaque(false);
        jButtonRekap.setBackground(null);

        jButtonSimulasi.setForeground(warnaAbu);
        jButtonSimulasi.setOpaque(false);
        jButtonSimulasi.setBackground(null);

        jButtonPeringkat.setForeground(warnaAbu);
        jButtonPeringkat.setOpaque(false);
        jButtonPeringkat.setBackground(null);
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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(() -> new MainTycoonSandbox().setVisible(true));
        java.awt.EventQueue.invokeLater(() -> {
            if (UserSession.getUserId() > 0) {
                new MainTycoonSandbox().setVisible(true);
            } else {
                new LoginForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton finishButtonPemasukan;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonDashboard;
    private javax.swing.JButton jButtonKategori;
    private javax.swing.JButton jButtonLogout;
    private javax.swing.JButton jButtonPeringkat;
    private javax.swing.JButton jButtonRekap;
    private javax.swing.JButton jButtonSimulasi;
    private javax.swing.JButton jButtonTransaksi;
    private javax.swing.JEditorPane jEditorPane1;
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
    private javax.swing.JLabel jP1level;
    private javax.swing.JLabel jP1levelDesc;
    private javax.swing.JLabel jP1modalAwal;
    private javax.swing.JLabel jP1namaBisnis;
    private javax.swing.JLabel jP1saldoSaatIni;
    private javax.swing.JTable jP1table;
    private javax.swing.JLabel jP1totalKeluar;
    private javax.swing.JLabel jP1totalPemasukan;
    private javax.swing.JLabel jP1transaksiTerakhir;
    private javax.swing.JButton jP2Button1;
    private javax.swing.JButton jP2Button2;
    private javax.swing.JComboBox<String> jP2CategoryDropdown;
    private com.toedter.calendar.JDateChooser jP2DateChooser1;
    private javax.swing.JTextField jP2NameExpenseField;
    private javax.swing.JTextField jP2NominalField;
    private javax.swing.JTable jP2Table1;
    private javax.swing.JComboBox<String> jP3CategoriesDropdown;
    private javax.swing.JCheckBox jP3CheckAllMonth;
    private com.toedter.calendar.JMonthChooser jP3MonthDropdown;
    private javax.swing.JLabel jP3ShowData;
    private javax.swing.JTable jP3Table;
    private javax.swing.JLabel jP3TotalData;
    private com.toedter.calendar.JYearChooser jP3YearDropdown;
    private javax.swing.JLabel jP4ModalAwal;
    private javax.swing.JComboBox<String> jP4MonthFilter;
    private javax.swing.JLabel jP4Saldo;
    private javax.swing.JTable jP4Table;
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
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
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
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
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
    private javax.swing.JPanel mainContentPanel;
    private javax.swing.JPanel menuDashboard;
    private javax.swing.JPanel menuKategori;
    private javax.swing.JPanel menuPeringkat;
    private javax.swing.JPanel menuRekap;
    private javax.swing.JPanel menuSimulasi;
    private javax.swing.JPanel menuTransaksi;
    private javax.swing.JLabel plusIconPemasukan;
    private javax.swing.JLabel saldoPemasukan;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JButton tapButtonPemasukan;
    private javax.swing.JLabel titlePemasukan;
    // End of variables declaration//GEN-END:variables
}
