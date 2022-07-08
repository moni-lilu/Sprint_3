package praktikum.services.scooter;
import io.restassured.response.Response;
import org.junit.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class ReceiveAnOrderByNumberTest {
    int track;
    Track trackData;
    OrderDataRespons orderDataRespons;
    ScooterApiClient client;
    @Before
    public void apiClient() {
        client = new ScooterApiClient();
    }
    public void orderDelete() {
        client.orderCancel(track)
                .then().statusCode(200);
    }
    @Test
    public void shouldResponseContainsObjectWithOrder() {

        track = client.takeTrackNumber()
                .then().statusCode(201)
                .extract().body().path("track");

        orderDataRespons = takeOrder(Integer.toString(track))
                .body().as(OrderDataRespons.class);

        trackData = new Track(track);

        Assert.assertEquals(track, orderDataRespons.getOrder().getTrack());

        orderDelete();
    }
    @Test
    public void shouldReturnStatusCode400IfRequestWithoutNumber() {
        takeOrder("")
                .then()
                .statusCode(400);
    }
    @Test
    public void shouldReturnMessageNotEnoughDataToSearchIfRequestWithoutNumber() {
        takeOrder("")
                .then()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для поиска"));
    }
    @Test
    public void shouldReturnStatusCode404IfRequestWithWrongNumber() {
        takeOrder("000")
                .then()
                .statusCode(404);
    }
    @Test
    public void shouldReturnMessageOrderIsNotFoundIfRequestWithWrongNumber() {
        takeOrder("000")
                .then()
                .assertThat()
                .body("message", equalTo("Заказ не найден"));
    }
    public Response takeOrder(String paramTrackNumber) {
        return given()
                .filters(client.getRequestFilter(), client.getResponseFilter())
                .baseUri(client.getBaseURI())
                .header("Content-type", "application/json")
                .and()
                .queryParam("t", paramTrackNumber)
                .when()
                .get("/api/v1/orders/track");
    }
}

