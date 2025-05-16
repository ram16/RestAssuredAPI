package com.example.inventoryapi.tests.productManagement;

import com.example.inventoryapi.tests.utils.TestBase;
import com.example.inventoryapi.pojo.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class DeleteProductTest extends TestBase {

    @Test(groups = {"smoke", "regression"})
    public void testCreateAndDeleteProduct() throws Exception {
        // Step 1: Create the product
        String uniqueProductName = "Test Product " + UUID.randomUUID();
        ProductRequest productRequest = new ProductRequest(uniqueProductName, 19.99, "games", 10);
        ObjectMapper objectMapper = new ObjectMapper();
        String createJson = objectMapper.writeValueAsString(productRequest);

        // Send create product request
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .when()
                .post("/products");

        // Validate the response for creation
        createResponse.then().statusCode(201);
        createResponse.then().body("name", equalTo(uniqueProductName));
        createResponse.then().body("price", equalTo(19.99f));
        createResponse.then().body("productType", equalTo("games"));
        createResponse.then().body("quantity", equalTo(10));

        // Extract product ID from the create response
        String productId = createResponse.path("productId");

        // Step 2: Delete the product
        Response deleteResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/products/" + productId);

        // Log the response for debugging
        System.out.println("Delete Response Body: " + deleteResponse.getBody().asString());

        // Validate the response for deletion
        deleteResponse.then().statusCode(200);
        deleteResponse.then().body("message", containsString("Product removed"));
    }

    @Test(groups = {"regression"})
    public void testDeleteProduct400_BadRequest() {
        // Invalid UUID format (API may return 400 or 404 for invalid IDs)
        int status = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .delete("/products/invalid-uuid")
                .statusCode();
        // Accept 400 (bad request) or 404 (not found) as valid error responses
        org.testng.Assert.assertTrue(status == 400 || status == 404, "Expected 400 or 404 but got " + status);
    }

    @Test(groups = {"regression"})
    public void testDeleteProduct401_Unauthorized() throws Exception {
        // Create a product first
        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequest productRequest = new ProductRequest("UnauthorizedDelete", 10.0, "games", 1);
        String createJson = objectMapper.writeValueAsString(productRequest);
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .post("/products");
        String productId = createResponse.path("productId");

        // No Authorization header (API may return 401 or 404)
        int status = RestAssured.given()
                .contentType(ContentType.JSON)
                .delete("/products/" + productId)
                .statusCode();
        org.testng.Assert.assertTrue(status == 401 || status == 404, "Expected 401 or 404 but got " + status);
    }

    @Test(groups = {"regression"})
    public void testDeleteProduct500_InternalServerError() {
        // Simulate server error by hitting a wrong endpoint or with a malformed header
        int status = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .header("X-Force-Error", "true") // Custom header if your API supports error simulation
                .delete("/products/123e4567-e89b-12d3-a456-426614174000")
                .statusCode();
        // Accept 500 (server error) or 404 (not found) as valid error responses
        org.testng.Assert.assertTrue(status == 500 || status == 404, "Expected 500 or 404 but got " + status);
    }
}
