package com.rest;

import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.cthul.matchers.CthulMatchers.matchesPattern;
import static org.hamcrest.core.IsEqual.equalTo;

public class DiagnosticsControllerTest extends AbstractIntegrationTest {

    @Test
    public void probecheckTest() {
        given()
        .when()
                .get("/sys/healthcheck")
        .then()
                .spec(HEALTHCHECK_SPEC)
                .body("\"Dashboard IO\"", equalTo(props.getProperty("application.version")))
                .body("\"Status\"", equalTo("OK"))
                .body("\"Environment\"", equalTo(props.getProperty("application.environment")))
                .body("\"Deployment date\"", matchesPattern(REGEX_DATETIME));
    }
}
