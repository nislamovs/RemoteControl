package com.rest;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.jayway.restassured.RestAssured.given;
import static org.cthul.matchers.CthulMatchers.containsPattern;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class AppControllerTest extends AbstractIntegrationTest {

    @Test
    public void titlePageTest() {
        given()
        .when()
                .get("/")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body(containsString("html"))
                .body(containsString("head"))
                .body(containsString("body"))
                .body(containsString("script"))
                .body(containsString("CRUD Example"));
    }

    @Test
    public void titlePagePartialsTest() {
        given()
        .when()
                .get("/partials")
        .then()
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()))
                .body("error", equalTo(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo("No message available"))
                .body("path", equalTo("/DashboardIO/partials"))
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void titlePagePartialsTest_invalidPageNumber() {
        given()
        .when()
                .get("/partials/5")
        .then()
                .body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .body("error", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body("exception", equalTo("javax.servlet.ServletException"))
                .body("message", containsPattern("Check your ViewResolver setup!"))
                .body("path", equalTo("/DashboardIO/partials/5"))
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
