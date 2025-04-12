-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 30, 2025 at 10:13 AM
-- Server version: 8.0.38
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `laptop_store`
--

-- --------------------------------------------------------

--
-- Table structure for table `authorities`
--

CREATE TABLE `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `authorities`
--

INSERT INTO `authorities` (`username`, `authority`) VALUES
('admin', 'ROLE_ADMIN'),
('guest', 'ROLE_GUEST'),
('pham', 'ROLE_USER'),
('phong', 'ROLE_CUSTOMER'),
('thanh', 'ROLE_CUSTOMER');

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `cart_id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` bigint NOT NULL,
  `added_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `total_amount` bigint NOT NULL,
  `status` enum('PENDING','COMPLETED','CANCELLED') DEFAULT 'PENDING'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `username`, `order_date`, `total_amount`, `status`) VALUES
(1, 'phong', '2025-03-21 17:28:28', 35000000, 'PENDING'),
(2, 'thanh', '2025-03-21 17:28:28', 27000000, 'COMPLETED'),
(3, 'phong', '2025-03-21 18:12:18', 36000000, 'PENDING'),
(4, 'phong', '2025-03-21 18:24:59', 35999000, 'PENDING'),
(5, 'phong', '2025-03-21 18:25:05', 41999000, 'PENDING'),
(6, 'phong', '2025-03-25 11:06:12', 37999000, 'COMPLETED'),
(7, 'admin', '2025-03-26 06:50:44', 19999000, 'CANCELLED'),
(8, 'admin', '2025-03-27 14:27:20', 102980000, 'COMPLETED'),
(9, 'admin', '2025-03-27 14:30:59', 35980000, 'COMPLETED'),
(10, 'phong', '2025-03-30 07:37:20', 198960000, 'PENDING'),
(11, 'pham', '2025-03-30 07:38:06', 50990000, 'PENDING'),
(12, 'thanh', '2025-03-30 07:38:52', 26999000, 'CANCELLED'),
(13, 'thanh', '2025-03-30 07:41:13', 45990000, 'CANCELLED'),
(14, 'thanh', '2025-03-30 07:42:23', 45990000, 'PENDING');

-- --------------------------------------------------------

--
-- Table structure for table `order_details`
--

CREATE TABLE `order_details` (
  `order_detail_id` int NOT NULL,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` bigint NOT NULL
) ;

--
-- Dumping data for table `order_details`
--

INSERT INTO `order_details` (`order_detail_id`, `order_id`, `product_id`, `quantity`, `price`) VALUES
(1, 1, 1, 1, 35000000),
(2, 2, 2, 1, 27000000),
(3, 3, 3, 2, 18000000),
(4, 4, 5, 1, 35999000),
(5, 5, 8, 1, 41999000),
(6, 6, 4, 1, 37999000),
(7, 7, 7, 1, 19999000),
(8, 8, 11, 2, 51490000),
(9, 9, 14, 2, 17990000),
(10, 10, 13, 1, 45990000),
(11, 10, 12, 3, 50990000),
(12, 11, 12, 1, 50990000),
(13, 12, 2, 1, 26999000),
(14, 13, 13, 1, 45990000),
(15, 14, 13, 1, 45990000);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `brand` varchar(100) NOT NULL,
  `cpu` varchar(100) NOT NULL,
  `ram` varchar(50) NOT NULL,
  `storage` varchar(50) NOT NULL,
  `gpu` varchar(100) NOT NULL,
  `screen_size` varchar(50) NOT NULL,
  `battery_life` varchar(50) NOT NULL,
  `weight` decimal(38,2) DEFAULT NULL,
  `price` bigint NOT NULL,
  `stock_quantity` int NOT NULL,
  `category` enum('LAPTOP_GAMING','LAPTOP_OFFICE') NOT NULL,
  `image_url` varchar(255) DEFAULT NULL
) ;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `brand`, `cpu`, `ram`, `storage`, `gpu`, `screen_size`, `battery_life`, `weight`, `price`, `stock_quantity`, `category`, `image_url`) VALUES
(1, 'Laptop Dell XPS 13', 'Dell', 'Intel Core i7-1255U', '16GB', '512GB SSD', 'Intel Iris Xe', '13.4 inch', '12h', 1.20, 32999000, 10, 'LAPTOP_OFFICE', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_10.png'),
(2, 'Laptop MacBook Air M2', 'Apple', 'Apple M2', '8GB', '256GB SSD', 'Apple GPU 8-core', '13.6 inch', '18h', 1.24, 26999000, 14, 'LAPTOP_OFFICE', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/g/r/group_533_1.png'),
(3, 'Laptop ASUS ROG Strix G15', 'ASUS', 'AMD Ryzen 7 6800H', '16GB', '1TB SSD', 'NVIDIA RTX 3060', '15.6 inch', '8h', 2.40, 32999000, 8, 'LAPTOP_GAMING', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_d_i_2__2_16.png'),
(4, 'Laptop HP Spectre x360', 'HP', 'Intel Core i7-1255U', '16GB', '1TB SSD', 'Intel Iris Xe', '13.5 inch OLED', '10h', 1.35, 37999000, 5, 'LAPTOP_OFFICE', 'https://cdn2.cellphones.com.vn/x/media/catalog/product/l/a/laptop-hp-spectre-x360-14-ef0030tu-6k773pa-cu-dep-1_4.png'),
(5, 'Laptop Lenovo ThinkPad X1 Carbon', 'Lenovo', 'Intel Core i7-1260P', '16GB', '512GB SSD', 'Intel Iris Xe', '14 inch', '15h', 1.10, 35999000, 7, 'LAPTOP_OFFICE', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/g/r/group_659_33.png'),
(6, 'Laptop Acer Predator Helios 300', 'Acer', 'Intel Core i7-12700H', '16GB', '1TB SSD', 'NVIDIA RTX 3070 Ti', '15.6 inch', '6h', 2.50, 29999000, 12, 'LAPTOP_GAMING', 'https://cdn2.cellphones.com.vn/x/media/catalog/product/l/a/laptop-acer-predator-helios-300_5-ksp.jpg'),
(7, 'Laptop MSI GF63 Thin', 'MSI', 'Intel Core i5-11400H', '8GB', '512GB SSD', 'NVIDIA GTX 1650', '15.6 inch', '7h', 1.80, 19999000, 20, 'LAPTOP_GAMING', 'https://cdn2.cellphones.com.vn/x/media/catalog/product/d/c/dcdsc_1__2_2.png'),
(8, 'Laptop Gigabyte AORUS 15', 'Gigabyte', 'Intel Core i7-12700H', '32GB', '1TB SSD', 'NVIDIA RTX 3070 Ti', '15.6 inch', '5h', 2.35, 41999000, 6, 'LAPTOP_GAMING', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_d_i_1__3_37.png'),
(11, 'Laptop Lenovo Gaming Legion 5 Pro 16IRX9 83DF0046VN', 'Lenovo', 'Intel Core i9-14900HX', '32GB', '1TB', 'NVIDIA GeForce RTX 4070', '16 inch', '8h', 2.50, 51490000, 6, 'LAPTOP_GAMING', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/g/r/group_509_70_.png'),
(12, 'Laptop ASUS ROG Strix G16 G614JIR-N4193W', 'ASUS', 'Intel Core i9 14900HX', '32GB', '1TB', 'NVIDIA GeForce RTX 4070', '16 inch', '9h', 2.00, 50990000, 0, 'LAPTOP_GAMING', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_d_i_2__2_16.png'),
(13, 'Laptop Lenovo Gaming Legion 5 Pro 16IRX9 83DF0047VN', 'Lenovo', 'Intel Core i9-14900HX', '32GB', '1TB', 'NVIDIA GeForce RTX 4060', '16 inch', '8h', 2.00, 45990000, 0, 'LAPTOP_GAMING', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/g/r/group_509_78_.png'),
(14, 'Laptop HP Gaming Victus 15-FA1139TX 8Y6W3PA', 'HP', 'Intel Core i5-12450H', '32GB', '512GB', 'NVIDIA GeForce RTX 2050', '15.6 ', '5h', 2.22, 17990000, 0, 'LAPTOP_GAMING', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/g/r/group_509_78_.png');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `fullname` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `address` text,
  `enabled` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`username`, `password`, `fullname`, `email`, `phone`, `address`, `enabled`) VALUES
('admin', '{bcrypt}$2a$10$KRUBXIjq4b5pbhI/PSXR1Oxmp10JKjyf.9m6gLfRR8Sv2VkwXOS5G', 'Admin', 'admin@example.com', '0999000000', '123 Admin Street', b'1'),
('guest', '{bcrypt}$2a$12$2lDWUzVfpUBP/T7K8wnzYujJ0nAN/rUkn03saEuyklvLSqVF/KDL.', 'Khách hàng', 'guest@example.com', '0999999999', 'Địa chỉ khách', b'1'),
('pham', '{bcrypt}$2a$10$OeZ4Cd1BrtjLBtpk3kVeiOGXwc9yI5xfTxJatinZAFJwpDpCfHXTi', 'Phạm Thanh Phong', 'pham12543@gmai.com', '0911221132', '13B Khu Phố 6 Thị Trấn Ba Trị Ba Trì, Bến Tre', b'1'),
('phong', '{bcrypt}$2a$10$RjsRj0lLfejuNYorh1JXl.IWL6BkJTxZc6e3.ch.0fKonYuJQKvcC', 'Nguyễn Văn Phong', 'phong@example.com', '0911111111', '456 Phong Street', b'1'),
('thanh', '{bcrypt}$2a$12$UG6GfAeTvqA1m/xZoV4rt..AkkeLziMLR7KiYvUgKr0GBDhErnoCK', 'Trần Văn Thanh', 'thanh@example.com', '0922222222', '789 Thanh Street', b'1');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `authorities`
--
ALTER TABLE `authorities`
  ADD UNIQUE KEY `ix_auth_username` (`username`,`authority`);

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`cart_id`),
  ADD KEY `username` (`username`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `username` (`username`);

--
-- Indexes for table `order_details`
--
ALTER TABLE `order_details`
  ADD PRIMARY KEY (`order_detail_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `phone` (`phone`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cart`
--
ALTER TABLE `cart`
  MODIFY `cart_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `order_details`
--
ALTER TABLE `order_details`
  MODIFY `order_detail_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `authorities`
--
ALTER TABLE `authorities`
  ADD CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE;

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE,
  ADD CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE;

--
-- Constraints for table `order_details`
--
ALTER TABLE `order_details`
  ADD CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
