package com.rest;

import com.rest.model.User;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;

public class RestApiControllerTest extends AbstractIntegrationTest {


    @Test
    public void apiInvalidPathTest() {
        given()
        .when()
                .post("/api/sdrba/")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()))
                .body("error", equalTo("Not Found"))
                .body("message", containsString("No message available"))
                .body("path", equalTo("/DashboardIO/api/sdrba/"));
    }

    @Test
    public void retrieveAllUsersTest_normalCase() {
        given()
        .when()
                .get("/api/user/")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id[0]", equalTo(1))
                .body("name[0]", equalTo("Sam"))
                .body("age[0]", equalTo(30))
                .body("salary[0]", equalTo(70000.0f));
    }

    @Test
    public void retrieveAllUsersTest_wrongHttpVerb() {
        given()
        .when()
                .post("/api/user/")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("error", equalTo("Bad Request"))
                .body("exception", containsString("HttpMessageNotReadableException"))
                .body("message", containsString("Required request body is missing"))
                .body("path", equalTo("/DashboardIO/api/user/"));
    }

    @Test
    public void retrieveSingleUserTest_normalCase() {
        given()
        .when()
                .get("/api/user/1")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(1))
                .body("name", equalTo("Sam"))
                .body("age", equalTo(30))
                .body("salary", equalTo(70000.0f));
    }

    @Test
    public void retrieveSingleUserTest_wrongId() {
        given()
        .when()
                .get("/api/user/9999")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errorMessage", equalTo("User with id 9999 not found"));
    }

    @Test
    public void retrieveSingleUserTest_wrongHttpVerb() {
        given()
        .when()
                .post("/api/user/555")
        .then()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .body("status", equalTo(HttpStatus.METHOD_NOT_ALLOWED.value()))
                .body("error", equalTo("Method Not Allowed"))
                .body("exception", containsString("HttpRequestMethodNotSupportedException"))
                .body("message", containsString("Request method 'POST' not supported"))
                .body("path", equalTo("/DashboardIO/api/user/555"));
    }

    @Test
    public void createUserTest_normalCase() {

        String randUName = getRandomUsername();
        User user = new User();
        user.setName(randUName);
        user.setSalary(44433.0);
        user.setAge(40);

        given().body(user)
        .when()
                .post("api/user/")
        .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
        .when()
                .get("/api/getuser/"+randUName)
        .then()
                .body("name", equalTo(randUName));
    }

    @Test
    public void createUserTest_alreadyExists() {
        String randUName = getRandomUsername();
        User user = new User();
        user.setName(randUName);
        user.setSalary(44433.0);
        user.setAge(40);

        given().body(user).when().post("api/user/").then().statusCode(HttpStatus.CREATED.value());

        given()
                .body(user)
        .when()
                .post("api/user/")
        .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("errorMessage", equalTo("Unable to create. A user with name " + randUName + " already exists."));
    }

    @Test
    public void createUserTest_invalidUser() {
        User user = new User();
        user.setSalary(44433.0);
        user.setAge(40);

        given().body(user)
        .when()
                .post("api/user/")
        .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .body("error", equalTo("Internal Server Error"))
                .body("exception", containsString("ConstraintViolationException"))
                .body("message", containsString("Validation failed for classes"))
                .body("path", equalTo("/DashboardIO/api/user/"));
    }

    @Test
    public void updateUserTest_normalCase() {

        String randUName = getRandomUsername();
        Integer age = 40;
        Double salary = 44433.0;

        User user = new User();
        user.setName(randUName);
        user.setSalary(salary);
        user.setAge(age);

        given().body(user)
                .when()
                .post("api/user/")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        Integer userId = given().when().get("/api/getuser/"+randUName).then().extract().path("id");

        user.setSalary(salary+100.0);
        user.setAge(age+5);

        given().body(user)
        .when()
                .put("api/user/"+userId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(user.getName()))
                .body("age", equalTo(user.getAge()))
                .body("salary", equalTo((float)user.getSalary()));
    }

    @Test
    public void updateUserTest_invalidId() {

        String randUName = getRandomUsername();

        User user = new User();
        user.setName(randUName);
        user.setSalary(44433.0);
        user.setAge(40);

        given().body(user)
                .when()
                .put("api/user/9999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errorMessage", equalTo("Unable to update. User with id 9999 not found."));
    }

    @Test
    public void updateUserTest_invalidData() {

        User user = new User();
        user.setAge(40);

        given().body(user)
                .when()
                .put("api/user/1")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .body("error", equalTo("Internal Server Error"))
                .body("exception", containsString("TransactionSystemException"))
                .body("message", containsString("Could not commit JPA transaction"))
                .body("path", equalTo("/DashboardIO/api/user/1"));
    }

    @Test
    public void deleteUserTest_normalCase() {

        String randUName = getRandomUsername();
        User user = new User();
        user.setName(randUName);
        user.setSalary(44433.0);
        user.setAge(40);

        given().body(user).when().post("api/user/").then().statusCode(HttpStatus.CREATED.value());
        Integer userId = given().when().get("/api/getuser/"+randUName).then().extract().path("id");
        given().when().delete("api/user/"+userId).then().statusCode(HttpStatus.NO_CONTENT.value());
        given().when().get("/api/user/"+userId).then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void deleteUserTest_invalidId() {

        given()
        .when()
                .delete("api/user/99999")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errorMessage", containsString("Unable to delete."));
    }

    @Test
    public void retrieveUserByUsernameTest_normalCase() {

        String randUName = getRandomUsername();
        User user = new User();
        user.setName(randUName);
        user.setSalary(44433.0);
        user.setAge(40);

        given().body(user).when().post("api/user/").then().statusCode(HttpStatus.CREATED.value());

        given()
                .when()
                .get("api/getuser/"+randUName)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(user.getName()))
                .body("age", equalTo(user.getAge()))
                .body("salary", equalTo((float)user.getSalary()));
    }

    @Test
    public void retrieveUserByUsernameTest_invalidUsername() {

        String randUName = getRandomUsername();

        given()
                .when()
                .get("api/getuser/"+randUName)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errorMessage", equalTo("User with username " + randUName + " not found"));
    }
}
