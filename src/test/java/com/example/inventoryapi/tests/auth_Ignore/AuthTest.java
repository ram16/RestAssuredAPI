package com.example.inventoryapi.tests.auth_Ignore;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.*;
import static org.testng.Assert.assertNotNull;


public class AuthTest {
    @Test
    public void testLoginAndGetToken() {
        RestAssured.baseURI = "https://apiforshopsinventorymanagementsystem.onrender.com"; // <-- set base URI

        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"user01\", \"password\": \"secpassword*\"}")
                .post("/auth/login");

        System.out.println("Status code: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody().asString());

        String token = response.jsonPath().getString("token");
        System.out.println("Token: " + token);
        assertNotNull(token, "Token should not be null");
    }
}