package com.example.inventoryapi.tests.stockManagement;

import com.example.inventoryapi.tests.productManagement.CreateProductTest;
import com.example.inventoryapi.tests.utils.TestBase;
import com.example.inventoryapi.pojo.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateOrderTest extends TestBase {

    private static final int MAX_RETRIES = 10;
    private static final int RETRY_SLEEP_MS = 500;
    private static final int EXCESSIVE_QUANTITY = 99999;

    @Test(groups = {"smoke", "regression"},
            dependsOnMethods = {"com.example.inventoryapi.tests.productManagement.CreateProductTest.testCreateProductWithDynamicUUID"})
    public void testCreateOrder() throws Exception {
        String productId = waitForProductId();
        Assert.assertNotNull(productId, "Product ID should not be null. Ensure testCreateProductWithDynamicUUID sets it.");
        Assert.assertFalse(productId.isEmpty(), "Product ID should not be empty.");

        OrderRequest orderRequest = new OrderRequest(productId, 5, "buy");
        ObjectMapper objectMapper = new ObjectMapper();
        String createOrderJson = objectMapper.writeValueAsString(orderRequest);

        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createOrderJson)
                .post("/orders");

        createResponse.then().statusCode(201);
        createResponse.then().body("productId", Matchers.equalTo(productId));
        createResponse.then().body("quantity", Matchers.equalTo(5));
        createResponse.then().body("orderType", Matchers.equalTo("buy"));

        System.out.println("Order created successfully with Product ID: " + productId);
    }

    @Test(groups = {"regression"})
    public void testCreateOrder400_BadRequest() {
        String badJson = "{}";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(badJson)
                .post("/orders");

        Assert.assertEquals(response.statusCode(), 400, "Expected 400 Bad Request for empty payload");
    }

    @Test(groups = {"regression"})
    public void testCreateOrder401_MissingAuthorization() throws Exception {
        String productId = waitForProductId();
        OrderRequest orderRequest = new OrderRequest(productId, 5, "buy");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(orderRequest);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(json)
                .post("/orders");

        int status = response.statusCode();
        Assert.assertTrue(status == 401 || status == 404, "Expected 401 or 404, but got: " + status);
    }

    @Test(groups = {"regression"})
    public void testCreateOrder400_InsufficientStock() throws Exception {
        String productId = waitForProductId();
        Assert.assertNotNull(productId, "Product ID should not be null for insufficient stock test.");

        OrderRequest orderRequest = new OrderRequest(productId, EXCESSIVE_QUANTITY, "sell");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(orderRequest);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(json)
                .post("/orders");

        int status = response.statusCode();
        Assert.assertTrue(status == 400 || status == 404,
                "Expected 400 (Insufficient Stock) or 404 (Product Not Found), but got: " + status);
    }

    @Test(groups = {"regression"})
    public void testCreateOrder404_ProductNotFound() throws Exception {
        OrderRequest orderRequest = new OrderRequest("non-existent-product-id", 1, "buy");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(orderRequest);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(json)
                .post("/orders");

        Assert.assertEquals(response.statusCode(), 404, "Expected 404 for non-existent product ID");
    }

    @Test(groups = {"regression"})
    public void testCreateOrder_MalformedJson_ServerErrorOrBadRequest() {
        // Missing closing brace, quantity not a number
        String malformedJson = "{ \"productId\": 123, \"quantity\": \"notANumber\" ";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(malformedJson)
                .post("/orders");

        int status = response.statusCode();
        Assert.assertTrue(status == 400 || status == 500,
                "Expected 400 (Bad Request) or 500 (Internal Server Error), but got: " + status);
    }

    private String waitForProductId() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String productId = CreateProductTest.dynamicProductId;
            if (productId != null && !productId.isEmpty()) {
                return productId;
            }
            try {
                Thread.sleep(RETRY_SLEEP_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for productId", e);
            }
            System.out.println("Waiting for productId to be set... attempt " + (i + 1));
        }
        return null;
    }
}
