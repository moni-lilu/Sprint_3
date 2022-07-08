package praktikum.services.scooter;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class OrdersListGettingTest {
    private RequestLoggingFilter requestFilter = new RequestLoggingFilter();
    private ResponseLoggingFilter responseFilter = new ResponseLoggingFilter();
    @BeforeClass
    public static void baseUri() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }
    @Test
    public void responseShouldContainsOrdersList() {
        final OrdersListResponse ordersListResponse = given()
                .filters(requestFilter, responseFilter)
                .header("Content-type", "application/json")
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .extract().as(OrdersListResponse.class);

        Assert.assertNotNull(ordersListResponse.getOrders());
    }
}
