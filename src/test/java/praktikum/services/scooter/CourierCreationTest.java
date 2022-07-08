package praktikum.services.scooter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierCreationTest {
    Boolean objectIsCreated = true;
    ScooterApiClient client;
    @Before
    public void baseUri() {
        client = new ScooterApiClient();
    }
    @After
    public void dataDelete() {

        if (objectIsCreated) {
            int userId = client.courierAuthorization()
                    .then()
                    .extract().body().path("id");
            client.courierDelete(userId)
                    .then()
                    .assertThat().statusCode(200);
        }

    }

    @Test
    public void courierSuccessfulCreationStatusCode() {
        client.courierRegistration()
                .then().statusCode(201);
    }

    @Test
    public void successfulRequestReturnsOkTrue() {
        client.courierRegistration()
                .then().assertThat().body("ok", equalTo(true));
    }

    @Test
    public void  courierWithSameLoginIsNotCreatedStatusCode() {
        client.courierRegistration()
                .then().statusCode(201);
        client.courierRegistration()
                .then().statusCode(409);
    }

    @Test
    public void  courierWithSameLoginIsNotCreatedMessage() {
        client.courierRegistration()
                .then().statusCode(201);
        client.courierRegistration()
                .then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    public void returnErrorCodeIfLoginIsNotPassed() {
        objectIsCreated = false;
        client.setRegistrationDataLogin(null);
        client.courierRegistration()
                .then().statusCode(400);
    }

    @Test
    public void returnErrorMessageIfLoginIsNotPassed() {
        objectIsCreated = false;
        client.setRegistrationDataLogin(null);
        client.courierRegistration()
                .then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void returnErrorCodeIfPasswordIsNotPassed() {
        objectIsCreated = false;
        client.setRegistrationDataPassword(null);
        client.courierRegistration()
                .then().statusCode(400);
    }

    @Test
    public void returnErrorMessageIfPasswordIsNotPassed() {
        objectIsCreated = false;
        client.setRegistrationDataPassword(null);
        client.courierRegistration()
                .then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
