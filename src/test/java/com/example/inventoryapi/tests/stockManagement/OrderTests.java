package com.example.inventoryapi.tests.stockManagement;

import com.example.inventoryapi.tests.productManagement.CreateProductTest;
import com.example.inventoryapi.tests.utils.TestBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OrderTests extends TestBase {

    @Test(groups = {"smoke", "regression"}, dependsOnMethods = {"com.example.inventoryapi.tests.stockManagement.CreateOrderTest.testCreateOrder"})
    public void testGetStockLevel() {
        Assert.assertNotNull(CreateProductTest.dynamicProductId, "Product ID should not be null. Ensure the dependent test ran successfully.");
        Assert.assertNotNull(token, "Authorization token should not be null.");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .get("/orders/product/" + CreateProductTest.dynamicProductId);

        System.out.println("Response Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body().asString());

        Assert.assertEquals(response.statusCode(), 200, "Expected status code 200 but got " + response.statusCode());
        // Defensive: Only check body if status is 200
        if (response.statusCode() == 200) {
            response.then().body("currentStock", equalTo(5)); // Assuming initial was 10, ordered 5
        }
    }

    @Test(groups = {"regression"})
    public void testGetOrders401_Unauthorized() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .get("/orders")
                .then()
                .statusCode(401);
    }

    @Test(groups = {"regression"})
    public void testGetOrders500_InternalServerError() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .header("X-Force-Error", "true")
                .get("/orders")
                .then()
                .statusCode(500);
    }
}
