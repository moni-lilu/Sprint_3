package praktikum.services.scooter;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class ReceiveAnOrderByNumberTest {
    private OrderDataRequest orderRequestData = new OrderDataRequest("Naruto", "Uchiha", "Konoha, 14", 4, "+7 800 355 35 35", 5, "2022-07-06", "Come back to Konoha", new String[] {"BLACK"});
    int track;
    Track trackData;
    private RequestLoggingFilter requestFilter;
    private ResponseLoggingFilter responseFilter;
    OrderDataRespons orderDataRespons;

    @Before
    public void setUp() {

        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        requestFilter = new RequestLoggingFilter();
        responseFilter = new ResponseLoggingFilter();

    }

    public void orderDelete() {
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(trackData)
                .when()
                .put("/api/v1/orders/cancel?track={track}", track)
                .then().statusCode(200);
    }

    @Test
    public void shouldResponseContainsObjectWithOrder() {

        track = given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(orderRequestData)
                .when()
                .post("/api/v1/orders")
                .then().statusCode(201)
                .extract().body().path("track");

        orderDataRespons = given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .queryParam("t", Integer.toString(track))
                .when()
                .get("/api/v1/orders/track")
                .body().as(OrderDataRespons.class);

        trackData = new Track(track);

        Assert.assertEquals(track, orderDataRespons.getOrder().getTrack());

        orderDelete();
    }

    @Test
    public void shouldReturnStatusCode400IfRequestWithoutNumber() {
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .queryParam("t", "")
                .when()
                .get("/api/v1/orders/track")
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldReturnMessageNotEnoughDataToSearchIfRequestWithoutNumber() {
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .queryParam("t", "")
                .when()
                .get("/api/v1/orders/track")
                .then()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    public void shouldReturnStatusCode404IfRequestWithWrongNumber() {
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .queryParam("t", "000")
                .when()
                .get("/api/v1/orders/track")
                .then()
                .statusCode(404);
    }

    @Test
    public void shouldReturnMessageOrderIsNotFoundIfRequestWithWrongNumber() {
        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .queryParam("t", "000")
                .when()
                .get("/api/v1/orders/track")
                .then()
                .assertThat()
                .body("message", equalTo("Заказ не найден"));
    }
}

