package com.ifood;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HealthCheckCurrentStatusFeatureTest {

    @Test
    public void shouldReturnIfARestaurantIsCurrentlyOnline() {
        String restaurantCode = "restaurant1";
        sendHealthSignal(restaurantCode);
        checkIfOnline(restaurantCode);
    }

    private void checkIfOnline(String restaurantCode) {
        Response post = given()
                .when()
                .get("/connection/health/online/list/restaurant1");

        LocalDateTime localDateTime = LocalDateTime.now();
        if(localDateTime .getHour() < 9 || localDateTime.getHour() == 23){
             post.then()
                    .body("[0].online", equalTo(Boolean.FALSE)) //TODO: Need to find a way to the test time be controllable
                    .statusCode(200);
        } else {
            post.then()
                    .body("[0].online", equalTo(Boolean.TRUE)) //TODO: Need to find a way to the test time be controllable
                    .statusCode(200);
        }
    }

    private void sendHealthSignal(String restaurantCode) {
        when()
                .post("/connection/health/{restaurant_code}", restaurantCode)
                .then()
                .statusCode(200);
    }

}
