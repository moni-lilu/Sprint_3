package praktikum.services.scooter;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierCreationTest {

    static String userLogin = "courier-four";
    static String userPassword = "123456a";
    static String userFirstName = "Two";
    RegistrationData registrationData = new RegistrationData(userLogin, userPassword, userFirstName);
    AuthorizationData authorizationData = new AuthorizationData(userLogin, userPassword);
    Boolean objectIsCreated = true;
    private RequestLoggingFilter requestFilter;
    private ResponseLoggingFilter responseFilter;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        requestFilter = new RequestLoggingFilter();
        responseFilter = new ResponseLoggingFilter();
    }

    @After
    public void dataDelete() {

        if (objectIsCreated) {
            int userId = given()
                    .filters(requestFilter, responseFilter)
                    .header("Content-type", "application/json")
                    .and()
                    .body(authorizationData)
                    .when()
                    .post("/api/v1/courier/login")
                    .then()
                    .extract().body().path("id");

            given()
                    .filters(requestFilter, responseFilter)
                    .header("Content-type", "application/json")
                    .delete("/api/v1/courier/{userId}", userId)
                    .then()
                    .assertThat().statusCode(200);
        }

    }

    @Test
    public void courierSuccessfulCreationStatusCode() {

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(201);

    }

    @Test
    public void successfulRequestReturnsOkTrue() {

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().body("ok", equalTo(true));

    }

    @Test
    public void  courierWithSameLoginIsNotCreatedStatusCode() {

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(201);

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(409);

    }

    @Test
    public void  courierWithSameLoginIsNotCreatedMessage() {

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(201);

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

    }

    @Test
    public void returnErrorCodeIfLoginIsNotPassed() {

        objectIsCreated = false;

        registrationData.setPassword(null);

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(400);

    }

    @Test
    public void returnErrorMessageIfLoginIsNotPassed() {

        objectIsCreated = false;

        registrationData.setPassword(null);

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));

    }

    @Test
    public void returnErrorCodeIfPasswordIsNotPassed() {

        objectIsCreated = false;

        registrationData.setLogin(null);

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(400);

    }

    @Test
    public void returnErrorMessageIfPasswordIsNotPassed() {

        objectIsCreated = false;

        registrationData.setLogin(null);

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));

    }
}
