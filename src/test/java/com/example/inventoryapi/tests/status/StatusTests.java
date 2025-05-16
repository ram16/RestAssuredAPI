package com.example.inventoryapi.tests.status;

import com.example.inventoryapi.tests.utils.TestBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.*;
import static org.testng.Assert.assertNotNull;
import static org.hamcrest.Matchers.equalTo;

// WireMock imports
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StatusTests extends TestBase {

    @Test(groups = {"smoke", "regression"})
    public void testStatusHealthy() {
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
    public void testStatusServerUnavailableWithWireMock() {
        WireMockServer wireMockServer = new WireMockServer(8089); // Use a test port
        wireMockServer.start();
        configureFor("localhost", 8089);

        stubFor(get(urlEqualTo("/status"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"Unhealthy\",\"dbStatus\":\"Disconnected\"}")));

        try {
            RestAssured.baseURI = "http://localhost:8089";
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .get("/status")
                    .then()
                    .statusCode(503)
                    .contentType(ContentType.JSON)
                    .body("status", equalTo("Unhealthy"))
                    .body("dbStatus", equalTo("Disconnected"));
        } finally {
            wireMockServer.stop();
            RestAssured.baseURI = null; // Reset to default
        }
    }
}
