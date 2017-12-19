package com.rest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

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
                .body("\"Dashboard IO\"", equalTo(properties.getProperty("application.version")))
                .body("\"Status\"", equalTo("OK"))
                .body("\"Environment\"", equalTo(properties.getProperty("application.environment")))
                .body("\"Deployment date\"", matchesPattern(REGEX_DATETIME));
    }

    @Test
    public void sendingEmailTest() {
        String keyword = RandomStringUtils.randomAlphabetic(16);

        given().param("keyword", keyword)
                .when().get("/sys/sendemail/").then().statusCode(HttpStatus.OK.value());

        delayMs(3000);
        Map<String, String> mailData = emailInboxContentIsOk();

        assert(mailData.get("Subject")).equals(properties.getProperty("email.subject"));
        assert(mailData.get("From")).equals(properties.getProperty("email.from"));
        assert(mailData.get("Body")).contains(keyword);

    }

    @Test
    public void checkProperties() {
        properties.forEach((k, v) -> System.out.println(k + " : " + v));
    }
}
