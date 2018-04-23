package com.ifood;


import com.jayway.restassured.http.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HealthCheckRestFeatureTest {

    @Test
    public void shouldAcceptHealthSignalAndRegister(){
        String restaurantCode = "restaurant1";
        sendHealthSignal(restaurantCode);
        checkHealthSignal(restaurantCode);
    }

    @Test
    public void shouldAcceptHealthSignalFromMultipleRestaurantAndRegister(){
        String restaurantCode1 = "restaurant1";
        String restaurantCode2 = "restaurant2";
        sendHealthSignal(restaurantCode1);
        sendHealthSignal(restaurantCode2);

        checkHealthSignal(restaurantCode1, restaurantCode2);
    }


    private void checkHealthSignal(String restaurantCode) {
        LocalDateTime startDate = LocalDateTime.now().minusHours(1);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);

        when()
                .get("/connection/health/history/{restaurant_code}/{start_date}/{end_date}", restaurantCode,
                        new DateFormatter().format(startDate),
                        new DateFormatter().format(endDate))
        .then()
                .statusCode(200)
                .body("[0].receivedAt", not(nullValue()));
    }

    private void checkHealthSignal(String restaurantCode1, String restaurantCode2) {
        LocalDateTime startDate = LocalDateTime.now().minusHours(1);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);

        given()
                .contentType(ContentType.JSON)
                .content("{\n" +
                "  \"restaurantCodes\": [\n" +
                "    \"" + restaurantCode1 + "\",\n" +
                "    \"" + restaurantCode2 + "\"\n" +
                "  ],\n" +
                "  \"startDate\": \"" + new DateFormatter().format(startDate) + "\",\n" +
                "  \"endDate\": \"" + new DateFormatter().format(endDate) + "\"\n" +
                "}")
        .when()
                .post("/connection/health/history/list")
        .then()
                .statusCode(200)
                .body("[0].connections.receivedAt", not(nullValue()));
    }

    private void sendHealthSignal(String restaurantCode) {
        when()
                .post("/connection/health/{restaurant_code}", restaurantCode)
        .then()
                .statusCode(200);
    }

}
