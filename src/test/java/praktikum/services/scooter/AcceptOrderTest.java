package praktikum.services.scooter;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class AcceptOrderTest {
    private RequestLoggingFilter requestFilter = new RequestLoggingFilter();
    private ResponseLoggingFilter responseFilter = new ResponseLoggingFilter();
    RegistrationData registrationData = new RegistrationData("userLogin", "userPassword", "userFirstName");
    AuthorizationData authorizationData = new AuthorizationData("userLogin", "userPassword");
    OrderDataRequest orderRequestData = new OrderDataRequest("Naruto", "Uchiha", "Konoha, 14", 4, "+7 800 355 35 35", 5, "2022-07-06", "Come back to Konoha", new String[] {"BLACK"});

    int courierId;
    int orderId;
    int trackNumber;

    Track track;

    @Before
    public void baseUri() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        courierId = 0;
    }

    @After
    public void courierAndOrderDelete() {
        if (courierId > 0) {courierDelete();}
    }

    public int courierId() {

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(201);

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .extract().body().path("id");
    }

    public int trackNumber() {
        return given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(orderRequestData)
                .when()
                .post("/api/v1/orders")
                .then().statusCode(201)
                .extract().body().path("track");
    }
    public int orderId() {
        trackNumber = trackNumber();

        OrderDataRespons orderResponsData = given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders/track?t={trackNumber}", trackNumber)
                .body().as(OrderDataRespons.class);

        return orderResponsData.getOrder().getId();
    }

    public void courierDelete() {

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/{courierId}", courierId)
                .then()
                .statusCode(200);

    }

    public void orderFinish() {

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/finish/{orderId}", orderId)
                .then().statusCode(200);

    }

    public void orderCancel(int trackNumber) {

        track = new Track(trackNumber);

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(track)
                .when()
                .put("/api/v1/orders/cancel?track={trackNumber}", trackNumber)
                .then().statusCode(200);

    }


    @Test
    public void shouldReturnStatusCode200IfAcceptOrder() {
        courierId = courierId();
        orderId = orderId();
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept/{orderId}?courierId={courierId}", orderId, courierId)
                .then()
                .statusCode(200);
        orderFinish();
    }

    @Test
    public void shouldReturnOkTrueIfAcceptOrder() {
        courierId = courierId();
        orderId = orderId();
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept/{orderId}?courierId={courierId}", orderId, courierId)
                .then()
                .assertThat().body("ok", equalTo(true));
        orderFinish();
    }

    @Test
    public void shouldReturnStatusCode400IfAcceptOrderWithoutCourierId() {
        orderId = orderId();
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept/{orderId}?courierId=", orderId)
                .then()
                .statusCode(400);
        orderCancel(trackNumber);
    }

    @Test
    public void shouldReturnNotEnoughDataToSearchIfAcceptOrderWithoutCourierId() {
        orderId = orderId();
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept/{orderId}?courierId=", orderId)
                .then()
                .assertThat().body("message", equalTo("Недостаточно данных для поиска"));
        orderCancel(trackNumber);
    }

    @Test
    public void shouldReturnStatusCode400IfAcceptOrderWithoutOrderId() {
        courierId = courierId();
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept?courierId={courierId}", courierId)
                .then()
                .statusCode(400);
    }


    @Test
    public void shouldReturnNotEnoughDataToSearchIfAcceptOrderWithoutOrderId() {
        courierId = courierId();
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept?courierId={courierId}", courierId)
                .then()
                .assertThat().body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    public void shouldReturnStatusCode404IfAcceptOrderWithWrongOrderId() {
        courierId = courierId();
        orderId = orderId();
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept/{orderId}?courierId={courierId}", orderId+1000, courierId)
                .then()
                .statusCode(404);
        orderCancel(trackNumber);
    }

    @Test
    public void shouldReturnThereIsNoOrderWithSuchIdIfAcceptOrderWithWrongOrderId() {
        courierId = courierId();
        orderId = orderId();
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept/{orderId}?courierId={courierId}", orderId+1000, courierId)
                .then()
                .assertThat().body("message", equalTo("Заказа с таким id не существует"));
        orderCancel(trackNumber);
    }


    @Test
    public void shouldReturnStatusCode404IfAcceptOrderWithWrongCourierId() {
        courierId = courierId();
        orderId = orderId();
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept/{orderId}?courierId={courierId}", orderId, courierId+1000)
                .then()
                .statusCode(404);
        orderCancel(trackNumber);
    }

    @Test
    public void shouldReturnThereIsNoOrderWithSuchIdIfAcceptOrderWithWrongCourierId() {
        courierId = courierId();
        orderId = orderId();
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept/{orderId}?courierId={courierId}", orderId, courierId+1000)
                .then()
                .assertThat().body("message", equalTo("Курьера с таким id не существует"));
        orderCancel(trackNumber);
    }
}
