package com.ifood.rest.unavailability;

import com.ifood.DateFormatter;
import com.ifood.domain.UnavailabilityReason;
import io.swagger.annotations.ApiParam;

import java.time.LocalDateTime;

class UnavailabilityScheduleInsertFeatureRequest {

    @ApiParam("Restaurant unique code")
    private String restaurantCode;

    @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)")
    private String scheduleStart;

    @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)")
    private String scheduleEnd;

    private UnavailabilityReason unavailabilityReason;

    public UnavailabilityScheduleInsertFeatureRequest() {
    }

    public UnavailabilityScheduleInsertFeatureRequest(String restaurantCode, String scheduleStart, String scheduleEnd, UnavailabilityReason unavailabilityReason) {
        this.restaurantCode = restaurantCode;
        this.scheduleStart = scheduleStart;
        this.scheduleEnd = scheduleEnd;
        this.unavailabilityReason = unavailabilityReason;
    }

    public String getRestaurantCode() {
        return restaurantCode;
    }

    public LocalDateTime getScheduleStart() {
        return new DateFormatter().format(scheduleStart);
    }

    public LocalDateTime getScheduleEnd() {
        return new DateFormatter().format(scheduleEnd);
    }

    public UnavailabilityReason getUnavailabilityReason() {
        return unavailabilityReason;
    }
}
