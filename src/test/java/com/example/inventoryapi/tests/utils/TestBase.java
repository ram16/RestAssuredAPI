package com.example.inventoryapi.tests.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.*;
import static org.testng.Assert.assertNotNull;

public class TestBase {

    public static String token;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://apiforshopsinventorymanagementsystem.onrender.com";

        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"user01\", \"password\": \"secpassword*\"}")
                .post("/auth/login");

        System.out.println("Status code: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody().asString());

        token = response.jsonPath().getString("token");
        System.out.println("Token: " + token);
        assertNotNull(token, "Token should not be null");
    }
}
