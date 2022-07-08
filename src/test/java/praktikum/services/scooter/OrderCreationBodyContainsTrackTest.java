package praktikum.services.scooter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OrderCreationBodyContainsTrackTest {
    Integer track;
    ScooterApiClient client;
    @Before
    public void apiClient() {
        client = new ScooterApiClient();
    }
    @Test
    public void shouldResponseContainsTrack() {
        track = client.takeTrackNumber()
                .then().statusCode(201)
                .extract().body().path("track");
        Assert.assertNotNull(track);
        client.orderCancel(track)
                .then().statusCode(200);
    }
}
