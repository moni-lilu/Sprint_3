package praktikum.services.scooter;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static io.restassured.RestAssured.given;

@RunWith(Parameterized.class)
public class OrderCreationWithPreferColorTest {
    private final OrderDataRequest orderRequestData;
    int orderId;
    Track track;
    private RequestLoggingFilter requestFilter = new RequestLoggingFilter();
    private ResponseLoggingFilter responseFilter = new ResponseLoggingFilter();

    public OrderCreationWithPreferColorTest(OrderDataRequest orderRequestData) {
        this.orderRequestData = orderRequestData;
    }
    @BeforeClass
    public static void baseUri() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }
    @Parameterized.Parameters
    public static Object[] newOrderNumber() {
        return new Object[] {
                new OrderDataRequest("Naruto", "Uchiha", "Konoha, 14", 4, "+7 800 355 35 35", 5, "2022-07-06", "Come back to Konoha", new String[] {"BLACK"}),
                new OrderDataRequest("Naruto", "Uchiha", "Konoha, 14", 4, "+7 800 355 35 35", 5, "2022-07-06", "Come back to Konoha", new String[] {"GRAY"}),
                new OrderDataRequest("Naruto", "Uchiha", "Konoha, 14", 4, "+7 800 355 35 35", 5, "2022-07-06", "Come back to Konoha", new String[] {null}),
                new OrderDataRequest("Naruto", "Uchiha", "Konoha, 14", 4, "+7 800 355 35 35", 5, "2022-07-06", "Come back to Konoha", new String[] {"BLACK", "GRAY"}),
                new OrderDataRequest("Naruto", "Uchiha", "Konoha, 14", 4, "+7 800 355 35 35", 5, "2022-07-06", "Come back to Konoha", new String[] {"GRAY", "BLACK"})
        };
    }

    @Test
    public void shouldOrderCreatedWithCorrectColor() {
        orderId = given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(orderRequestData)
                .when()
                .post("/api/v1/orders")
                .then().statusCode(201)
                .extract().body().path("track");

        track = new Track(orderId);

        OrderDataRespons orderResponsData = given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders/track?t={orderId}", orderId)
                .body().as(OrderDataRespons.class);

        Assert.assertEquals(Arrays.toString(orderRequestData.getColor()) , Arrays.toString(orderResponsData.getOrder().getColor()));

        given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .and()
                .body(track)
                .when()
                .put("/api/v1/orders/cancel?track={orderId}", orderId)
                .then().statusCode(200);
    }
}
