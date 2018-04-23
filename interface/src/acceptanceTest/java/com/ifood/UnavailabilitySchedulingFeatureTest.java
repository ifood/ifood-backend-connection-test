package com.ifood;

import com.ifood.domain.UnavailabilityReason;
import com.jayway.restassured.http.ContentType;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UnavailabilitySchedulingFeatureTest {

    @Test
    public void shouldRetrieveSchedulesAdded() {
        String restaurantCode = "restaurant1";
        scheduleUnavailability(restaurantCode,"2018-04-03T01:00", "2018-04-10T13:00", UnavailabilityReason.HOLIDAYS);
        checkUnavailabilitySchedule(restaurantCode, "2018-04-03T01:30", "2018-04-11T13:00");
    }

    @Test
    public void shouldNotRetrieveSchedulesDeleted() {
        String restaurantCode = "restaurant1";
        String scheduleCode = scheduleUnavailability(restaurantCode, "2018-06-03T01:00", "2018-06-10T13:00", UnavailabilityReason.HOLIDAYS);
        deleteUnavailability(scheduleCode);

        try {
            checkUnavailabilitySchedule(restaurantCode, "2018-06-03T01:30", "2018-06-11T13:00");
        } catch (Exception ex) {
            assertTrue(true);
            return;
        }
        throw new IllegalStateException();
    }

    private void deleteUnavailability(String scheduleCode) {
        when()
                .post("/connection/schedule/unavailability/{schedule_code}", scheduleCode)
        .then()
                .statusCode(200);
    }

    private String scheduleUnavailability(String restaurantCode, String scheduleStart, String scheduleEnd, UnavailabilityReason unavailabilityReason) {
        return given()
                .contentType(ContentType.JSON)
                .content("{\n" +
                        "  \"restaurantCode\": \"" + restaurantCode +"\",\n" +
                        "  \"scheduleEnd\": \"" + scheduleEnd + "\",\n" +
                        "  \"scheduleStart\": \"" +scheduleStart+ "\",\n" +
                        "  \"unavailabilityReason\": \"" + unavailabilityReason.name() +"\"\n" +
                        "}")
        .when()
                .post("/connection/schedule/unavailability")
        .then()
                .statusCode(200)
        .extract().asString();
    }


    private void checkUnavailabilitySchedule(String restaurantCode,  String startDate, String endDate) {
        when()
                .get("/connection/schedule/unavailability/{restaurant_code}/{start_date}/{end_date}", restaurantCode, startDate, endDate)
        .then()
                .statusCode(200)
                .body("[0].code", not(nullValue()))
                .body("[0].scheduleStart", IsEqual.equalTo("2018-04-03T01:00"))
                .body("[0].scheduleEnd", IsEqual.equalTo("2018-04-10T13:00"));
    }
}
