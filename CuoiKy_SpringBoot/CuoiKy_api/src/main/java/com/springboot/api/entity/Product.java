package com.springboot.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Tên sản phẩm không được để trống")
    @Size(min = 3, message = "Tên sản phẩm phải có ít nhất 3 ký tự")
    @Column(nullable = false)
    private String name;

    @NotEmpty(message = "Thương hiệu không được để trống")
    @Column(nullable = false)
    private String brand;

    @NotEmpty(message = "CPU không được để trống")
    @Column(nullable = false)
    private String cpu;

    @NotEmpty(message = "RAM không được để trống")
    @Column(nullable = false)
    private String ram;

    @NotEmpty(message = "Bộ nhớ không được để trống")
    @Column(nullable = false)
    private String storage;

    @NotEmpty(message = "Card đồ họa không được để trống")
    @Column(nullable = false)
    private String gpu;

    @NotEmpty(message = "Kích thước màn hình không được để trống")
    @Column(name = "screen_size")
    private String screenSize;

    @NotEmpty(message = "Thời lượng pin không được để trống")
    @Column(name = "battery_life")
    private String batteryLife;

    @NotNull(message = "Trọng lượng không được để trống")
    @DecimalMin(value = "0.1", message = "Trọng lượng phải lớn hơn 0.1kg")
    @Column(nullable = false)
    private BigDecimal weight;

    @NotNull(message = "Giá bán không được để trống")
    @Min(value = 0, message = "Giá bán phải lớn hơn hoặc bằng 0")
    @Column(nullable = false)
    private Long price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @NotNull(message = "Danh mục không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(name = "image_url")
    private String imageUrl;

    public enum Category {
        LAPTOP_GAMING,
        LAPTOP_OFFICE
    }

    // Constructor mặc định
    public Product() {
    }

    // Constructor với tham số
    public Product(String name, String brand, String cpu, String ram, String storage, String gpu,
                  String screenSize, String batteryLife, BigDecimal weight, 
                  Long price, Integer stockQuantity, Category category, String imageUrl) {
        this.name = name;
        this.brand = brand;
        this.cpu = cpu;
        this.ram = ram;
        this.storage = storage;
        this.gpu = gpu;
        this.screenSize = screenSize;
        this.batteryLife = batteryLife;
        this.weight = weight;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getGpu() {
        return gpu;
    }

    public void setGpu(String gpu) {
        this.gpu = gpu;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getBatteryLife() {
        return batteryLife;
    }

    public void setBatteryLife(String batteryLife) {
        this.batteryLife = batteryLife;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", cpu='" + cpu + '\'' +
                ", ram='" + ram + '\'' +
                ", storage='" + storage + '\'' +
                ", gpu='" + gpu + '\'' +
                ", screenSize='" + screenSize + '\'' +
                ", batteryLife='" + batteryLife + '\'' +
                ", weight=" + weight +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", category=" + category +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
} 