package praktikum.services.scooter;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierDeleteTest {
    int userId;
    ScooterApiClient client;
    @Before
    public void baseUri() {
        client = new ScooterApiClient();
    }
    public void setUp() {

        client.courierRegistration()
                .then().statusCode(201);
        userId = client.courierAuthorization()
                .then()
                .extract().body().path("id");
    }

    @Test
    public void shouldReturnStatusCode200IfDeleteWasSuccess() {

        setUp();
        client.courierDelete(userId)
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldReturnOkTrueIfDeleteWasSuccess() {

        setUp();
        client.courierDelete(userId)
                .then()
                .assertThat().body("ok", equalTo(true));
    }

    @Test
    public void shouldReturnStatusCode404IfRequestWasWithWrongId() {

        setUp();
        client.courierDelete(userId)
                .then()
                .statusCode(200);
        client.courierDelete(userId)
                .then()
                .statusCode(404);
    }

    @Test
    public void shouldReturnThereIsNoCourierWithSuchIdIfRequestWasWithWrongId() {

        setUp();
        client.courierDelete(userId)
                .then()
                .statusCode(200);
        client.courierDelete(userId)
                .then()
                .assertThat().body("message", equalTo("Курьера с таким id нет."));
    }

    @Test
    public void shouldReturnStatusCode400IfRequestWasWithoutId() {
        requestWithoutId()
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldReturnMessageNotEnoughDataToCourierDeleteIfRequestWasWithoutId() {
        requestWithoutId()
                .then()
                .assertThat().body("message", equalTo("Недостаточно данных для удаления курьера"));
    }
    public Response requestWithoutId() {
        return given()
                .filters(client.getRequestFilter(), client.getResponseFilter())
                .baseUri(client.getBaseURI())
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier");
    }
}
