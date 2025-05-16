package com.example.inventoryapi.tests.productManagement;

import com.example.inventoryapi.tests.utils.TestBase;
import com.example.inventoryapi.pojo.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class UpdateProductTest extends TestBase {

    @Test(groups = {"smoke", "regression"})
    public void testCreateAndUpdateProductWithDynamicUUID() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Step 1: Create the product
        String uniqueProductName = "Test Product " + UUID.randomUUID();
        ProductRequest createRequest = new ProductRequest(uniqueProductName, 19.99, "games", 10);
        String createJson = objectMapper.writeValueAsString(createRequest);

        // Send create product request
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .when()
                .post("/products");

        createResponse.then().statusCode(201);
        createResponse.then().body("name", equalTo(uniqueProductName));
        createResponse.then().body("price", equalTo(19.99f));
        createResponse.then().body("productType", equalTo("games"));
        createResponse.then().body("quantity", equalTo(10));

        String productId = createResponse.path("productId");

        // Step 2: Update the product
        String updatedProductName = "Updated Product " + UUID.randomUUID();
        ProductRequest updateRequest = new ProductRequest(updatedProductName, 29.99, "games", 20);
        String updateJson = objectMapper.writeValueAsString(updateRequest);

        Response updateResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(updateJson)
                .when()
                .put("/products/" + productId);

        System.out.println("Update Response Body: " + updateResponse.getBody().asString());

        updateResponse.then().statusCode(200);
        updateResponse.then().body("name", equalTo(updatedProductName));
        updateResponse.then().body("price", equalTo(29.99f));
        updateResponse.then().body("quantity", equalTo(20));
        updateResponse.then().body("productType", equalTo("games"));

        if (updateResponse.getBody().asString().contains("message")) {
            updateResponse.then().body("message", containsString("Product updated"));
        }
    }

    @Test(groups = {"regression"})
    public void testUpdateProduct400_BadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequest createRequest = new ProductRequest("BadUpdate", 10.0, "games", 1);
        String createJson = objectMapper.writeValueAsString(createRequest);
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .post("/products");
        String productId = createResponse.path("productId");

        // Send invalid update (missing fields)
        String badJson = "{}";
        int status = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(badJson)
                .put("/products/" + productId)
                .statusCode();
        // Accept 400 (bad request) or 404 (not found) as valid error responses
        Assert.assertTrue(status == 400 || status == 404, "Expected 400 or 404 but got " + status);
    }

    @Test(groups = {"regression"})
    public void testUpdateProduct401_Unauthorized() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequest createRequest = new ProductRequest("UnauthorizedUpdate", 10.0, "games", 1);
        String createJson = objectMapper.writeValueAsString(createRequest);
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .post("/products");
        String productId = createResponse.path("productId");

        // Valid update, missing Authorization header
        ProductRequest updateRequest = new ProductRequest("UnauthorizedUpdate", 20.0, "games", 2);
        String updateJson = objectMapper.writeValueAsString(updateRequest);
        int status = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .put("/products/" + productId)
                .statusCode();
        // Accept 401 (unauthorized) or 404 (not found) as valid error responses
        Assert.assertTrue(status == 401 || status == 404, "Expected 401 or 404 but got " + status);
    }

    @Test(groups = {"regression"})
    public void testUpdateProduct500_InternalServerError() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequest createRequest = new ProductRequest("ServerErrorUpdate", 10.0, "games", 1);
        String createJson = objectMapper.writeValueAsString(createRequest);
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .post("/products");
        String productId = createResponse.path("productId");

        // Malformed JSON
        String malformedJson = "{ \"name\": \"test\", \"price\": \"notANumber\" ";
        int status = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(malformedJson)
                .put("/products/" + productId)
                .statusCode();
        // Accept 500 (server error) or 400 (bad request) as valid error responses
        Assert.assertTrue(status == 500 || status == 400, "Expected 500 or 400 but got " + status);
    }
}
