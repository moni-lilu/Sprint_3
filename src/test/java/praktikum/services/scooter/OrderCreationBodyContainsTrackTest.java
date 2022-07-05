package praktikum.services.scooter;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class OrderCreationBodyContainsTrackTest {

    private OrderDataRequest orderRequestData = new OrderDataRequest("Naruto", "Uchiha", "Konoha, 14", 4, "+7 800 355 35 35", 5, "2022-07-06", "Come back to Konoha", new String[] {"BLACK"});
    int track;
    Track trackData;
    private RequestLoggingFilter requestFilter;
    private ResponseLoggingFilter responseFilter;

    @Before
    public void setUp() {

        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        requestFilter = new RequestLoggingFilter();
        responseFilter = new ResponseLoggingFilter();

    }

    @Test
    public void shouldResponseContainsTrack() {
        track = given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(orderRequestData)
                .when()
                .post("/api/v1/orders")
                .then().statusCode(201)
                .extract().body().path("track");

        trackData = new Track(track);

        Assert.assertNotNull(track);

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(trackData)
                .when()
                .put("/api/v1/orders/cancel?track={track}", track)
                .then().statusCode(200);
    }
}
