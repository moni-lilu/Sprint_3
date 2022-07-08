package praktikum.services.scooter;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ScooterApiClient {
    public static final String baseURI = "http://qa-scooter.praktikum-services.ru/";
    private final RequestLoggingFilter requestFilter = new RequestLoggingFilter();
    private final ResponseLoggingFilter responseFilter = new ResponseLoggingFilter();
    RegistrationData registrationData = new RegistrationData("james007bond", "007", "James");
    AuthorizationData authorizationData = new AuthorizationData("james007bond", "007");
    OrderDataRequest orderRequestData = new OrderDataRequest("Naruto", "Uchiha", "Konoha, 14", 4, "+7 800 355 35 35", 5, "2022-07-06", "Come back to Konoha", new String[] {"BLACK"});

    public RequestLoggingFilter getRequestFilter() {
        return requestFilter;
    }
    public ResponseLoggingFilter getResponseFilter() {
        return responseFilter;
    }
    public String getBaseURI() {
        return baseURI;
    }
    public void setAuthorizationDataLogin(String login) {
        authorizationData.setLogin(login);
    }
    public void setAuthorizationDataPassword(String password) {
        authorizationData.setPassword(password);
    }
    public void setRegistrationDataLogin(String login) {
        registrationData.setLogin(login);
    }
    public void setRegistrationDataPassword(String password) {
        registrationData.setPassword(password);
    }
    public Response courierRegistration() {
        return given()
                .filters(requestFilter, responseFilter)
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .and()
                .body(registrationData)
                .when()
                .post("/api/v1/courier");
    }

    public Response courierAuthorization() {
        return given()
                .filters(requestFilter, responseFilter)
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .and()
                .body(authorizationData)
                .when()
                .post("/api/v1/courier/login");
    }

    public Response courierDelete(int courierId) {
        return given()
                .filters(requestFilter, responseFilter)
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/{courierId}", courierId);
    }

    public Response takeTrackNumber() {
        return given()
                .filters(requestFilter, responseFilter)
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .and()
                .body(orderRequestData)
                .when()
                .post("/api/v1/orders");
    }

    public Response takeOrderId(int trackNumber) {
        return given()
                .filters(requestFilter, responseFilter)
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders/track?t={trackNumber}", trackNumber);
    }

    public Response orderFinish(int orderId) {
        return given()
                .filters(requestFilter, responseFilter)
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/finish/{orderId}", orderId);
    }

    public Response orderCancel(int trackNumber) {
        Track track = new Track(trackNumber);
        return given()
                .filters(requestFilter, responseFilter)
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .and()
                .body(track)
                .when()
                .put("/api/v1/orders/cancel?track={trackNumber}", trackNumber);
    }

    public Response orderAccept(int orderId, int courierId) {
        return given()
                .filters(requestFilter, responseFilter)
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .when()
                .put("/api/v1/orders/accept/{orderId}?courierId={courierId}", orderId, courierId);
    }
}
