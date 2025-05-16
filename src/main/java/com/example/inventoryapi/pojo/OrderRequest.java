package com.example.inventoryapi.pojo;

public class OrderRequest {
    private String productId;
    private int quantity;
    private String orderType;

    public OrderRequest() {}

    public OrderRequest(String productId, int quantity, String orderType) {
        this.productId = productId;
        this.quantity = quantity;
        this.orderType = orderType;
    }

    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getOrderType() {
        return orderType;
    }
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
