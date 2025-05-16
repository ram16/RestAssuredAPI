package com.example.inventoryapi.tests.auth_Ignore;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.*;


import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class InventoryManagementTests {
    public static String token;


    public static void setup() {
        RestAssured.baseURI = "https://apiforshopsinventorymanagementsystem.onrender.com";

        // Login and obtain token
        Response loginResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"user01\", \"password\": \"secpassword*\"}")
                .post("/auth/login");

        loginResponse.then().statusCode(200).body("message", containsString("logged in successful"));
        token = loginResponse.jsonPath().getString("token");

        System.out.println("Token: " + token);
    }




    @Test
    public void testCreateProductWithDynamicUUID() {
        // Step 1: Create the product
        String uniqueProductName = "Test Product " + UUID.randomUUID();
        String createJson = "{\n" +
                "  \"name\": \"" + uniqueProductName + "\",\n" +
                "  \"price\": 19.99,\n" +
                "  \"productType\": \"games\",\n" +
                "  \"quantity\": 10\n" +
                "}";

        // Send create product request
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .when()
                .post("/products");

        // Validate the response
        createResponse.then().statusCode(201); // Assert status code is 201 Created
        createResponse.then().body("name", equalTo(uniqueProductName));
        createResponse.then().body("price", equalTo(19.99f));
        createResponse.then().body("productType", equalTo("games"));
        createResponse.then().body("quantity", equalTo(10));

    }


    @Test
    public void testCreateAndUpdateProductWithDynamicUUID() {
        // Step 1: Create the product
        String uniqueProductName = "Test Product " + UUID.randomUUID();
        String createJson = "{\n" +
                "  \"name\": \"" + uniqueProductName + "\",\n" +
                "  \"price\": 19.99,\n" +
                "  \"productType\": \"games\",\n" +
                "  \"quantity\": 10\n" +
                "}";

        // Send create product request
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .when()
                .post("/products");

        // Validate the response
        createResponse.then().statusCode(201); // Assert status code is 201 Created
        createResponse.then().body("name", equalTo(uniqueProductName));
        createResponse.then().body("price", equalTo(19.99f));
        createResponse.then().body("productType", equalTo("games"));
        createResponse.then().body("quantity", equalTo(10));

        // Extract product ID from the create response
        String productId = createResponse.path("productId");

        // Step 2: Update the product
        String updatedProductName = "Updated Product " + UUID.randomUUID(); // Dynamic UUID for update
        String updateJson = "{\n" +
                "  \"name\": \"" + updatedProductName + "\",\n" +
                "  \"price\": 29.99,\n" +
                "  \"productType\": \"games\",\n" +
                "  \"quantity\": 20\n" +
                "}";

        // Send update product request
        Response updateResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(updateJson)
                .when()
                .put("/products/" + productId);

        // Log the response for debugging
        System.out.println("Update Response Body: " + updateResponse.getBody().asString());

        // Validate the response for update
        updateResponse.then().statusCode(200); // Assert status code is 200 OK
        updateResponse.then().body("name", equalTo(updatedProductName));
        updateResponse.then().body("price", equalTo(29.99f));
        updateResponse.then().body("quantity", equalTo(20));
        updateResponse.then().body("productType", equalTo("games"));

        // Optional: If the API returns a message, assert it
        if (updateResponse.getBody().asString().contains("message")) {
            updateResponse.then().body("message", containsString("Product updated"));
        }
    }







    @Test
    public void testGetProduct() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("products", notNullValue());
    }

    @Test
    public void testCreateAndDeleteProduct() {
        // Step 1: Create the product
        String uniqueProductName = "Test Product " + UUID.randomUUID();
        String createJson = "{\n" +
                "  \"name\": \"" + uniqueProductName + "\",\n" +
                "  \"price\": 19.99,\n" +
                "  \"productType\": \"games\",\n" +
                "  \"quantity\": 10\n" +
                "}";

        // Send create product request
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .when()
                .post("/products");

        // Validate the response for creation
        createResponse.then().statusCode(201); // Assert status code is 201 Created
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
        deleteResponse.then().statusCode(200); // Assert status code is 200 OK
        deleteResponse.then().body("message", containsString("Product deleted"));
    }

    @Test
    public void testGetProductById() {
        // Step 1: Create the product
        String uniqueProductName = "Test Product " + UUID.randomUUID();
        String createJson = "{\n" +
                "  \"name\": \"" + uniqueProductName + "\",\n" +
                "  \"price\": 19.99,\n" +
                "  \"productType\": \"games\",\n" +
                "  \"quantity\": 10\n" +
                "}";

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

        // Step 2: Fetch the product by ID
        Response getResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/products/" + productId);

        // Log the response for debugging
        System.out.println("Get Response Body: " + getResponse.getBody().asString());

        // Validate the response for fetching the product
        getResponse.then().statusCode(200); // Assert status code is 200 OK
        getResponse.then().body("name", equalTo(uniqueProductName));
        getResponse.then().body("price", equalTo(19.99f));
        getResponse.then().body("productType", equalTo("games"));
        getResponse.then().body("quantity", equalTo(10));
    }

    @Test
    public void testPlaceBuyOrder() {
        String orderJson = "{\n" +
                "  \"orderType\": \"buy\",\n" +
                "  \"productId\": \"1\",\n" +
                "  \"quantity\": 10\n" +
                "}";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(orderJson)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .body("message", containsString("Order placed"));
    }

    @Test
    public void testPlaceSellOrder() {
        String orderJson = "{\n" +
                "  \"orderType\": \"sell\",\n" +
                "  \"productId\": \"1\",\n" +
                "  \"quantity\": 5\n" +
                "}";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(orderJson)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .body("message", containsString("Order placed"));
    }

    @Test
    public void testGetStockDetails() {
        String productId = "1"; // Replace with a valid product ID from your API

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/stock/" + productId)
                .then()
                .statusCode(200)
                .body("stock", greaterThanOrEqualTo(0));
    }
}
