package br.com.ifood.ifoodbackendconnection;


import br.com.ifood.ifoodbackendconnection.utilities.DateFormatter;
import com.jayway.restassured.http.ContentType;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@Transactional
//@Rollback
//@ActiveProfiles("integration-test")
public class MqttKeepAliveSignalIntegrationTest {

    public static final String HEALTH_CHECK_TOPIC = "ifood/backend/connection/alivesignal";
    private static final String BASE_PATH = "/ifood-backend-connection/api/v1/";

    @Test
    public void shouldAcceptHealthSignalAndRegister() throws MqttException {
        String restaurantCode = "5a981e63-4b5c-4423-857c-b275507cddcd";
        sendHealthSignal(restaurantCode);
        checkHealthSignal(restaurantCode);
    }

    @Test
    public void shouldAcceptHealthSignalFromMultipleRestaurantAndRegister() throws MqttException {
        String restaurantCode1 = "5a981e63-4b5c-4423-857c-b275507cddcd";
        String restaurantCode2 = "2f2837b6-00f6-4569-8843-1d4d19bf028f";
        sendHealthSignal(restaurantCode1);
        sendHealthSignal(restaurantCode2);

        checkHealthSignal(restaurantCode1, restaurantCode2);
    }

    private void checkHealthSignal(String restaurantCode) {
        LocalDateTime startDate = LocalDateTime.now().minusHours(1);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);

        when()
                .get(BASE_PATH+"backend/connection/history/restaurant/{code}/{start_date}/{end_date}", restaurantCode,
                        DateFormatter.format(startDate),
                        DateFormatter.format(endDate))
        .then()
                .statusCode(200)
                .body("[0].receivedSignal", not(nullValue()));
    }

    private void checkHealthSignal(String restaurantCode1, String restaurantCode2) {
        LocalDateTime startDate = LocalDateTime.now().minusHours(1);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);

        when()
                .get(BASE_PATH+"backend/connection/history/restaurant?code=" + restaurantCode1 + "," + restaurantCode2 +
                        "&start_date=" + DateFormatter.format(startDate) + "&end_date=" + DateFormatter.format(endDate))
        .then()
                .statusCode(200)
                .body("[0].signalHistory.receivedSignal", not(nullValue()));
    }

    private void sendHealthSignal(String restaurantCode) throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        MqttMessage message = new MqttMessage();
        message.setPayload(restaurantCode.getBytes());
        client.publish(HEALTH_CHECK_TOPIC, message);
        client.disconnect();
    }
}