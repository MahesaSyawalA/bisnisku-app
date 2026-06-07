-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 07, 2026 at 10:49 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_bisnisku`
--
CREATE DATABASE IF NOT EXISTS `db_bisnisku`;
USE `db_bisnisku`;
-- --------------------------------------------------------

--
-- Table structure for table `bisnis_profile`
--

CREATE TABLE `bisnis_profile` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `nama_bisnis` varchar(100) NOT NULL,
  `nama_pemilik` varchar(100) NOT NULL,
  `modal_awal` decimal(15,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bisnis_profile`
--

INSERT INTO `bisnis_profile` (`id`, `user_id`, `nama_bisnis`, `nama_pemilik`, `modal_awal`) VALUES
(1, 1, 'Warung Kopi Semesta', 'Budi Santoso', 10000000.00),
(2, 2, 'Butik Sari Indah', 'Sari Dewi', 15000000.00),
(3, 3, 'Bengkel Motor Doni Jaya', 'Doni Prasetyo', 8000000.00),
(4, 4, 'Trifsyle', 'Mahesa Syawal Abdurahman', 8000000.00),
(5, 7, 'wakop ju', 'Juju', 900000000.00);

-- --------------------------------------------------------

--
-- Table structure for table `pemasukan_harian`
--

CREATE TABLE `pemasukan_harian` (
  `id_pemasukan` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `tanggal` date NOT NULL,
  `total_pendapatan` bigint(20) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pemasukan_harian`
--

INSERT INTO `pemasukan_harian` (`id_pemasukan`, `id_user`, `tanggal`, `total_pendapatan`, `created_at`) VALUES
(1, 1, '2026-05-27', 50000, '2026-05-27 08:47:46'),
(2, 1, '2026-05-27', 40000, '2026-05-27 09:17:03'),
(3, 1, '2026-05-27', 330000, '2026-05-27 09:18:45'),
(4, 1, '2026-05-27', 250000, '2026-05-27 09:47:07'),
(5, 1, '2026-05-27', 260000, '2026-05-27 09:47:26'),
(6, 1, '2026-05-27', 190000, '2026-05-27 09:50:00'),
(7, 1, '2026-05-27', 595000, '2026-05-27 09:50:38'),
(8, 1, '2026-05-27', 120000, '2026-05-27 13:33:50'),
(9, 1, '2026-05-27', 185000, '2026-05-27 14:42:42'),
(10, 1, '2026-05-31', 60000, '2026-05-31 09:48:26'),
(11, 1, '2026-06-01', 35000, '2026-06-01 08:04:26'),
(12, 1, '2026-06-01', 90000, '2026-06-01 08:06:02'),
(13, 1, '2026-06-01', 100000, '2026-06-01 09:42:56'),
(14, 1, '2026-06-01', 140000, '2026-06-01 09:48:51'),
(15, 1, '2026-06-01', 50000, '2026-06-01 16:28:34'),
(16, 1, '2026-06-01', 55000, '2026-06-01 16:28:43'),
(17, 1, '2026-06-02', 90000, '2026-06-01 22:09:11'),
(18, 1, '2026-06-02', 80000, '2026-06-01 22:09:19'),
(19, 1, '2026-06-07', 451, '2026-06-06 19:39:04'),
(20, 1, '2026-06-07', 295, '2026-06-07 08:11:05');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `nominal` decimal(15,2) NOT NULL,
  `kategori` varchar(50) NOT NULL,
  `tanggal` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id`, `user_id`, `nama`, `nominal`, `kategori`, `tanggal`) VALUES
(1, 1, 'Beli biji kopi robusta 10kg', 850000.00, 'Operasional', '2025-04-01'),
(2, 1, 'Beli gula pasir & susu kaleng', 320000.00, 'Operasional', '2025-04-03'),
(3, 1, 'Beli cup & sedotan (1 pack)', 175000.00, 'Operasional', '2025-04-05'),
(4, 1, 'Tagihan listrik April', 430000.00, 'Utilitas', '2025-04-07'),
(5, 1, 'Tagihan air April', 85000.00, 'Utilitas', '2025-04-07'),
(6, 1, 'Paket internet bulanan', 189000.00, 'Utilitas', '2025-04-08'),
(7, 1, 'Servis mesin espresso', 250000.00, 'Operasional', '2025-04-10'),
(8, 1, 'Nonton bioskop', 120000.00, 'Gaya Hidup', '2025-04-11'),
(9, 1, 'Beli biji kopi arabika 5kg', 620000.00, 'Operasional', '2025-04-15'),
(10, 1, 'Makan siang di restoran', 95000.00, 'Gaya Hidup', '2025-04-04'),
(11, 1, 'Belanja baju online', 350000.00, 'Gaya Hidup', '2025-04-14'),
(12, 1, 'Gaji barista — Riko', 1500000.00, 'Gaji Karyawan', '2025-04-30'),
(13, 1, 'Gaji barista — Sinta', 1500000.00, 'Gaji Karyawan', '2025-04-30'),
(14, 1, 'Print nota & struk', 45000.00, 'Lain-lain', '2025-04-06'),
(15, 1, 'Biaya parkir bulanan', 80000.00, 'Lain-lain', '2025-04-09'),
(16, 1, 'Beli biji kopi robusta 10kg', 850000.00, 'Operasional', '2025-05-01'),
(17, 1, 'Biaya parkir bulanan', 80000.00, 'Lain-lain', '2025-05-02'),
(18, 1, 'Makan malam fine dining', 450000.00, 'Gaya Hidup', '2025-05-03'),
(19, 1, 'Beli gula & bahan minuman', 290000.00, 'Operasional', '2025-05-04'),
(20, 1, 'Tagihan listrik Mei', 455000.00, 'Utilitas', '2025-05-06'),
(21, 1, 'Tagihan air Mei', 90000.00, 'Utilitas', '2025-05-06'),
(22, 1, 'Paket internet bulanan', 189000.00, 'Utilitas', '2025-05-08'),
(24, 1, 'Belanja sepatu baru', 599000.00, 'Gaya Hidup', '2025-05-09'),
(25, 1, 'Beli alat tulis kantor', 55000.00, 'Lain-lain', '2025-05-13'),
(26, 1, 'Nonton konser musik', 350000.00, 'Gaya Hidup', '2025-05-17'),
(27, 1, 'Jajan kafe & dessert', 185000.00, 'Gaya Hidup', '2025-05-21'),
(28, 1, 'Belanja skincare', 275000.00, 'Gaya Hidup', '2025-05-25'),
(29, 1, 'Gaji barista — Riko', 1500000.00, 'Gaji Karyawan', '2025-05-31'),
(31, 2, 'Beli stok baju supplier', 3200000.00, 'Operasional', '2025-04-02'),
(32, 2, 'Beli hanger & etalase', 450000.00, 'Operasional', '2025-04-03'),
(33, 2, 'Tagihan listrik April', 310000.00, 'Utilitas', '2025-04-05'),
(34, 2, 'Paket internet toko', 150000.00, 'Utilitas', '2025-04-05'),
(35, 2, 'Gaji karyawan — Ani', 1200000.00, 'Gaji Karyawan', '2025-04-30'),
(36, 2, 'Makan siang & kopi', 85000.00, 'Gaya Hidup', '2025-04-08'),
(37, 2, 'Belanja tas baru', 750000.00, 'Gaya Hidup', '2025-04-20'),
(38, 2, 'Biaya print katalog', 120000.00, 'Lain-lain', '2025-04-10'),
(39, 2, 'Beli stok baju koleksi baru', 4100000.00, 'Operasional', '2025-05-02'),
(40, 2, 'Tagihan listrik Mei', 330000.00, 'Utilitas', '2025-05-05'),
(41, 2, 'Paket internet toko', 150000.00, 'Utilitas', '2025-05-05'),
(42, 2, 'Makan di mal', 210000.00, 'Gaya Hidup', '2025-05-10'),
(43, 2, 'Beli aksesoris pribadi', 480000.00, 'Gaya Hidup', '2025-05-15'),
(44, 2, 'Gaji karyawan — Ani', 1200000.00, 'Gaji Karyawan', '2025-05-31'),
(45, 2, 'Biaya pengiriman barang', 175000.00, 'Lain-lain', '2025-05-18'),
(46, 3, 'Beli oli & spare part', 980000.00, 'Operasional', '2025-04-01'),
(47, 3, 'Beli peralatan servis', 650000.00, 'Operasional', '2025-04-04'),
(48, 3, 'Tagihan listrik April', 275000.00, 'Utilitas', '2025-04-06'),
(49, 3, 'Gaji mekanik — Joko', 1300000.00, 'Gaji Karyawan', '2025-04-30'),
(50, 3, 'Jajan makan siang', 65000.00, 'Gaya Hidup', '2025-04-09'),
(51, 3, 'Beli seragam mekanik', 300000.00, 'Lain-lain', '2025-04-12'),
(52, 3, 'Beli stok oli besar', 1500000.00, 'Operasional', '2025-05-01'),
(53, 3, 'Beli spare part motor', 870000.00, 'Operasional', '2025-05-05'),
(54, 3, 'Tagihan listrik Mei', 290000.00, 'Utilitas', '2025-05-06'),
(55, 3, 'Gaji mekanik — Joko', 1300000.00, 'Gaji Karyawan', '2025-05-31'),
(56, 3, 'Makan & hiburan', 430000.00, 'Gaya Hidup', '2025-05-14'),
(57, 3, 'Servis kendaraan pribadi', 550000.00, 'Gaya Hidup', '2025-05-20'),
(58, 3, 'Biaya administrasi', 95000.00, 'Lain-lain', '2025-05-08'),
(66, 7, 'gaji Bu ratna', 1000000.00, 'Gaji Karyawan', '2026-05-23'),
(67, 7, 'biji kopi arabika', 120000.00, 'Operasional', '2026-05-23'),
(68, 1, 'Kopi kapal api ', 40000.00, 'Gaya Hidup', '2026-06-24'),
(71, 1, 'biji kopi', 90000.00, 'Operasional', '2026-06-07');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `created_at`) VALUES
(1, 'budi123', 'kopi1234', '2026-05-20 14:58:07'),
(2, 'sari_butik', 'butik456', '2026-05-20 14:58:07'),
(3, 'doni_bengkel', 'bengkel789', '2026-05-20 14:58:08'),
(4, 'mahesa123', 'mahesa123', '2026-05-21 16:17:50'),
(5, 'Masukan Username', 'jPasswordField1', '2026-05-21 16:36:51'),
(7, 'juju', 'juju123', '2026-05-23 11:06:03');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bisnis_profile`
--
ALTER TABLE `bisnis_profile`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `pemasukan_harian`
--
ALTER TABLE `pemasukan_harian`
  ADD PRIMARY KEY (`id_pemasukan`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bisnis_profile`
--
ALTER TABLE `bisnis_profile`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `pemasukan_harian`
--
ALTER TABLE `pemasukan_harian`
  MODIFY `id_pemasukan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=72;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bisnis_profile`
--
ALTER TABLE `bisnis_profile`
  ADD CONSTRAINT `bisnis_profile_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
