package com.example.inventoryapi.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POJO for product response payload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {
    private String productId;
    private String name;
    private double price;
    private String productType;
    private int quantity;
    private String createdAt;

    public ProductResponse() {}

    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
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
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", productType='" + productType + '\'' +
                ", quantity=" + quantity +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
