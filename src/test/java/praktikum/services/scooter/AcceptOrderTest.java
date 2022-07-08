package praktikum.services.scooter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class AcceptOrderTest {

    ScooterApiClient client;
    int courierId;
    int orderId;
    int trackNumber;
    @Before
    public void setUp() {
        client = new ScooterApiClient();
        courierId = 0;
    }
    @After
    public void courierAndOrderDelete() {
        if (courierId > 0) {courierDelete();}
    }
    public int courierId() {

        client.courierRegistration()
                .then()
                .statusCode(201);

        return client.courierAuthorization()
                .then()
                .extract().body().path("id");
    }
    public int trackNumber() {
        return client.takeTrackNumber()
                .then().statusCode(201)
                .extract().body().path("track");
    }
    public int orderId() {
        trackNumber = trackNumber();

        OrderDataRespons orderResponsData = client.takeOrderId(trackNumber)
                .body().as(OrderDataRespons.class);

        return orderResponsData.getOrder().getId();
    }
    public void courierDelete() {
        client.courierDelete(courierId)
                .then()
                .statusCode(200);
    }
    public void orderFinish() {
        client.orderFinish(orderId)
                .then().statusCode(200);
    }
    public void orderCancel(int trackNumber) {
        client.orderCancel(trackNumber)
                .then().statusCode(200);
    }
    @Test
    public void shouldReturnStatusCode200IfAcceptOrder() {
        courierId = courierId();
        orderId = orderId();
        client.orderAccept(orderId, courierId)
                .then()
                .statusCode(200);
        orderFinish();
    }
    @Test
    public void shouldReturnOkTrueIfAcceptOrder() {
        courierId = courierId();
        orderId = orderId();
        client.orderAccept(orderId, courierId)
                .then()
                .assertThat().body("ok", equalTo(true));
        orderFinish();
    }
    @Test
    public void shouldReturnStatusCode400IfAcceptOrderWithoutCourierId() {
        orderId = orderId();
        given()
                .filters(client.getRequestFilter(), client.getResponseFilter())
                .baseUri(client.getBaseURI())
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
                .filters(client.getRequestFilter(), client.getResponseFilter())
                .baseUri(client.getBaseURI())
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
                .filters(client.getRequestFilter(), client.getResponseFilter())
                .baseUri(client.getBaseURI())
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
                .filters(client.getRequestFilter(), client.getResponseFilter())
                .baseUri(client.getBaseURI())
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
        client.orderAccept(orderId+1000, courierId)
                .then()
                .statusCode(404);
        orderCancel(trackNumber);
    }
    @Test
    public void shouldReturnThereIsNoOrderWithSuchIdIfAcceptOrderWithWrongOrderId() {
        courierId = courierId();
        orderId = orderId();
        client.orderAccept(orderId+1000, courierId)
                .then()
                .assertThat().body("message", equalTo("Заказа с таким id не существует"));
        orderCancel(trackNumber);
    }
    @Test
    public void shouldReturnStatusCode404IfAcceptOrderWithWrongCourierId() {
        courierId = courierId();
        orderId = orderId();
        client.orderAccept(orderId, courierId+1000)
                .then()
                .statusCode(404);
        orderCancel(trackNumber);
    }
    @Test
    public void shouldReturnThereIsNoOrderWithSuchIdIfAcceptOrderWithWrongCourierId() {
        courierId = courierId();
        orderId = orderId();
        client.orderAccept(orderId, courierId+1000)
                .then()
                .assertThat().body("message", equalTo("Курьера с таким id не существует"));
        orderCancel(trackNumber);
    }
}
