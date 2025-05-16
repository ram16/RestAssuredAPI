package com.example.inventoryapi.pojo;

public class ProductRequest {
    private String name;
    private double price;
    private String productType;
    private int quantity;

    public ProductRequest() {}

    public ProductRequest(String name, double price, String productType, int quantity) {
        this.name = name;
        this.price = price;
        this.productType = productType;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getProductType() {
        return productType;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
