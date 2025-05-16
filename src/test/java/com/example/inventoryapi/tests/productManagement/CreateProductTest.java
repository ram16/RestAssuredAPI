package com.example.inventoryapi.tests.productManagement;

import com.example.inventoryapi.tests.utils.TestBase;
import com.example.inventoryapi.pojo.ProductRequest;
import com.example.inventoryapi.pojo.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class CreateProductTest extends TestBase {

    public static String dynamicProductId;

    @Test(groups = {"smoke", "regression"})
    public void testCreateProductWithDynamicUUID() throws Exception {
        String uniqueProductName = "Test Product " + UUID.randomUUID();
        ProductRequest productRequest = new ProductRequest(uniqueProductName, 19.99, "games", 10);
        ObjectMapper objectMapper = new ObjectMapper();
        String createJson = objectMapper.writeValueAsString(productRequest);

        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .post("/products");

        System.out.println("Response Status Code: " + createResponse.statusCode());
        System.out.println("Response Body: " + createResponse.asString());

        createResponse.then().statusCode(201);

        // Deserialize response to POJO
        ProductResponse productResponse = objectMapper.readValue(createResponse.asString(), ProductResponse.class);

        Assert.assertEquals(productResponse.getName(), uniqueProductName);
        Assert.assertEquals(productResponse.getPrice(), 19.99, 0.01);
        Assert.assertEquals(productResponse.getProductType(), "games");
        Assert.assertEquals(productResponse.getQuantity(), 10);

        dynamicProductId = productResponse.getProductId();
        System.out.println("Extracted Product ID: " + dynamicProductId);
        Assert.assertNotNull(dynamicProductId, "Product ID should not be null");

        System.out.println("Test passed: Product ID successfully generated");
    }

    @Test(groups = {"regression"})
    public void testCreateProductWithInvalidPrice() throws Exception {
        ProductRequest productRequest = new ProductRequest("Invalid Price Product", 0, "games", 10);
        ObjectMapper objectMapper = new ObjectMapper();
        String createJson = objectMapper.writeValueAsString(productRequest);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createJson)
                .when()
                .post("/products");

        // Accept 400 (bad request) or 500 (server error) as valid error responses
        int status = response.statusCode();
        Assert.assertTrue(status == 400 || status == 500, "Expected 400 or 500 but got " + status);
    }

    @Test(groups = {"regression"})
    public void testCreateProductUnauthorized() throws Exception {
        ProductRequest productRequest = new ProductRequest("Unauthorized Product", 10.99, "games", 5);
        ObjectMapper objectMapper = new ObjectMapper();
        String createJson = objectMapper.writeValueAsString(productRequest);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                // No Authorization header
                .body(createJson)
                .when()
                .post("/products");

        // Accept 401 (unauthorized) or 404 (not found) as valid error responses
        int status = response.statusCode();
        Assert.assertTrue(status == 401 || status == 404, "Expected 401 or 404 but got " + status);
    }

    @Test(groups = {"regression"})
    public void testCreateProductExpectingServerError() {
        // Deliberately invalid JSON (valid syntax but incorrect structure)
        String serverBreakingJson = "{ \"name\": null, \"price\": \"invalid\", \"productType\": {}, \"quantity\": \"NaN\" }";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(serverBreakingJson)
                .when()
                .post("/products");

        // Accept 500 (server error) or 400 (bad request) as valid error responses
        int status = response.statusCode();
        Assert.assertTrue(status == 500 || status == 400, "Expected 500 or 400 but got " + status);
    }

    @Test(groups = {"regression"})
    public void testCreateProduct400_BadRequest() throws Exception {
        // Missing required fields
        String badJson = "{}";
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(badJson)
                .post("/products");

        // Accept 400 (bad request) or 404 (not found) as valid error responses
        int status = response.statusCode();
        Assert.assertTrue(status == 400 || status == 404, "Expected 400 or 404 but got " + status);
    }

    @Test(groups = {"regression"})
    public void testCreateProduct401_Unauthorized() throws Exception {
        // Valid payload, missing Authorization header
        ProductRequest productRequest = new ProductRequest("Unauthorized", 10.0, "games", 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(productRequest);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(json)
                .post("/products");

        // Accept 401 (unauthorized) or 404 (not found) as valid error responses
        int status = response.statusCode();
        Assert.assertTrue(status == 401 || status == 404, "Expected 401 or 404 but got " + status);
    }

    @Test(groups = {"regression"})
    public void testCreateProduct500_InternalServerError() {
        // Deliberately malformed JSON
        String malformedJson = "{ \"name\": \"test\", \"price\": \"notANumber\" ";
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(malformedJson)
                .post("/products");

        // Accept 500 (server error) or 400 (bad request) as valid error responses
        int status = response.statusCode();
        Assert.assertTrue(status == 500 || status == 400, "Expected 500 or 400 but got " + status);
    }

    @Test(groups = {"smoke", "regression"})
    public void testStatus() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .get("/status")
                .then()
                .statusCode(200)
                .body("status", equalTo("OK"))
                .body("dbStatus", equalTo("Connected"));
    }

    @Test(groups = {"regression"})
    public void testStatusServerNotFoundWithWireMock() {
        WireMockServer wireMockServer = new WireMockServer(8089); // Use a test port
        wireMockServer.start();
        configureFor("localhost", 8089);

        stubFor(get(urlEqualTo("/status"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"server not found\"}")));

        try {
            RestAssured.baseURI = "http://localhost:8089";
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .get("/status")
                    .then()
                    .statusCode(503)
                    .body("status", equalTo("server not found"));
        } finally {
            wireMockServer.stop();
            RestAssured.baseURI = null; // Reset to default
        }
    }
}
