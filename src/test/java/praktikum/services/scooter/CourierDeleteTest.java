package praktikum.services.scooter;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierDeleteTest {
    static String userLogin = "NEW-courier";
    static String userPassword = "123456";
    static String userFirstName = "Alex";
    RegistrationData registrationData = new RegistrationData(userLogin, userPassword, userFirstName);
    AuthorizationData authorizationData = new AuthorizationData(userLogin, userPassword);
    int userId;
    private RequestLoggingFilter requestFilter;
    private ResponseLoggingFilter responseFilter;

    @Before
    public void baseUriAndFilters() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        requestFilter = new RequestLoggingFilter();
        responseFilter = new ResponseLoggingFilter();
    }

    public void setUp() {

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

    @Test
    public void shouldReturnStatusCode200IfDeleteWasSuccess() {

        setUp();

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/{userId}", userId)
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldReturnOkTrueIfDeleteWasSuccess() {

        setUp();

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/{userId}", userId)
                .then()
                .assertThat().body("ok", equalTo(true));
    }

    @Test
    public void shouldReturnStatusCode404IfRequestWasWithWrongId() {

        setUp();

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/{userId}", userId)
                .then()
                .statusCode(200);

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/{userId}", userId)
                .then()
                .statusCode(404);
    }

    @Test
    public void shouldReturnThereIsNoCourierWithSuchIdIfRequestWasWithWrongId() {

        setUp();

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/{userId}", userId)
                .then()
                .statusCode(200);

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/{userId}", userId)
                .then()
                .assertThat().body("message", equalTo("Курьера с таким id нет."));
    }

    @Test
    public void shouldReturnStatusCode400IfRequestWasWithoutId() {
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier")
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldReturnMessageNotEnoughDataToCourierDeleteIfRequestWasWithoutId() {
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier")
                .then()
                .assertThat().body("message", equalTo("Недостаточно данных для удаления курьера"));
    }
}
