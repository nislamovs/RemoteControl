package com.rest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.ResponseSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Properties;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.Matchers.containsString;

public class AbstractIntegrationTest {

    public static final String BUILD_PROPERTIES = "build.properties";
    public static final String DEV_ENVIRONMENT = "development";
    public static final String PROD_ENVIRONMENT = "production";
    public static final String REGEX_DATETIME= "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";

    public static final Properties props = new Properties();
    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    @BeforeClass
    public static void initializeData() throws IOException {
        props.load(ClassLoader.getSystemResourceAsStream(BUILD_PROPERTIES));
        String env = props.getProperty("application.environment").toLowerCase();
        if (!env.equals(PROD_ENVIRONMENT)) {
            env = DEV_ENVIRONMENT;
        }
        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream(env + ".properties"));
        RestAssured.baseURI = properties.getProperty("host");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        logger.info("Before class launched and initialized.");
        logger.info("Host : {}", properties.getProperty("host"));
    }

    @Before
    public void setUp() throws Exception {
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
    }

    public static final ResponseSpecification HEALTHCHECK_SPEC = new ResponseSpecBuilder().
            expectStatusCode(HttpStatus.OK.value()).
            expectBody(containsString("Dashboard IO")).
            expectBody(containsString("Environment")).
            expectBody(containsString("Deployment date")).
            expectBody(containsString("Status")).
            build();

    public static String getRandomUsername() {
        return "testuser-" + currentTimeMillis();
    }
}


