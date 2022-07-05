package praktikum.services.scooter;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.*;

import java.util.logging.Filter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CourierAuthorizationTest {
    static RegistrationData registrationData = new RegistrationData("james007bond", "007", "James");
    static AuthorizationData authorizationData = new AuthorizationData("james007bond", "007");
    int userId;
    private RequestLoggingFilter requestFilter;
    private ResponseLoggingFilter responseFilter;

    @Before
    public void setUp() {

        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        requestFilter = new RequestLoggingFilter();
        responseFilter = new ResponseLoggingFilter();

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(201);

        userId = given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .extract().body().path("id");

    }

    @After
    public void dataDelete() {

        authorizationData.setLogin("james007bond");
        authorizationData.setPassword("007");

        given()
                    .header("Content-type", "application/json")
                    .delete("/api/v1/courier/{userId}", userId)
                    .then()
                    .assertThat().statusCode(200);
    }

    @Test
    public void courierSuccessfulAuthorizationStatusCode200() {

        given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat().statusCode(200);
    }

    @Test
    public void courierSuccessfulAuthorizationReturnId() {

        given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .body("id", equalTo(userId));
    }

    @Test
    public void authorisationWithWrongLoginReturnStatusCode404() {

        authorizationData.setLogin("anotherLogin");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    public void authorisationWithWrongLoginReturnMassage() {

        authorizationData.setLogin("anotherLogin");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void authorisationWithoutLoginReturnStatusCode400() {

        authorizationData.setLogin(null);

        given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat().statusCode(400);
    }

    @Test
    public void authorisationWithoutLoginReturnMessage() {

        authorizationData.setLogin(null);

        given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }


    @Test
    public void authorisationWithWrongPasswordReturnStatusCode404() {

        authorizationData.setPassword("654321");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    public void authorisationWithWrongPasswordReturnMassage() {

        authorizationData.setPassword("654321");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void authorisationWithoutPasswordReturnStatusCode400() {

        authorizationData.setPassword("");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat().statusCode(400);
    }

    @Test
    public void authorisationWithoutPasswordReturnMessageNotEnoghDataToLogIn() {

        authorizationData.setPassword("");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
