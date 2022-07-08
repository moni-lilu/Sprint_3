package praktikum.services.scooter;
import org.junit.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierAuthorizationTest {
    int userId;
    ScooterApiClient client;
    @Before
    public void setUp() {
        client = new ScooterApiClient();
        client.courierRegistration()
                .then().statusCode(201);

        userId = client.courierAuthorization()
                .then()
                .extract().body().path("id");
    }

    @After
    public void dataDelete() {

        client.setAuthorizationDataLogin("james007bond");
        client.setAuthorizationDataPassword("007");

        client.courierDelete(userId)
                    .then()
                    .assertThat().statusCode(200);
    }

    @Test
    public void courierSuccessfulAuthorizationStatusCode200() {

        client.courierAuthorization()
                .then()
                .assertThat().statusCode(200);
    }

    @Test
    public void courierSuccessfulAuthorizationReturnId() {

        client.courierAuthorization()
                .then()
                .assertThat()
                .body("id", equalTo(userId));
    }

    @Test
    public void authorisationWithWrongLoginReturnStatusCode404() {

        client.setAuthorizationDataLogin("anotherLogin");

        client.courierAuthorization()
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    public void authorisationWithWrongLoginReturnMassage() {

        client.setAuthorizationDataLogin("anotherLogin");

        client.courierAuthorization()
                .then()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void authorisationWithoutLoginReturnStatusCode400() {

        client.setAuthorizationDataLogin(null);

        client.courierAuthorization()
                .then()
                .assertThat().statusCode(400);
    }

    @Test
    public void authorisationWithoutLoginReturnMessage() {

        client.setAuthorizationDataLogin(null);

        client.courierAuthorization()
                .then()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }


    @Test
    public void authorisationWithWrongPasswordReturnStatusCode404() {

        client.setAuthorizationDataPassword("654321");

        client.courierAuthorization()
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    public void authorisationWithWrongPasswordReturnMassage() {

        client.setAuthorizationDataPassword("654321");

        client.courierAuthorization()
                .then()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void authorisationWithoutPasswordReturnStatusCode400() {

        client.setAuthorizationDataPassword("");

        client.courierAuthorization()
                .then()
                .assertThat().statusCode(400);
    }

    @Test
    public void authorisationWithoutPasswordReturnMessageNotEnoghDataToLogIn() {

        client.setAuthorizationDataPassword("");

        client.courierAuthorization()
                .then()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
