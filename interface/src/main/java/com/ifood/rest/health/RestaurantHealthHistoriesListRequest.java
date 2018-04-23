package com.ifood.rest.health;

import com.ifood.DateFormatter;
import io.swagger.annotations.ApiParam;

import java.time.LocalDateTime;
import java.util.List;

class RestaurantHealthHistoriesListRequest {
    @ApiParam("Restaurants' codes")
    private List<String> restaurantCodes;

    @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)")
    private String startDate;

    @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)")
    private String endDate;

    public List<String> getRestaurantCodes() {
        return restaurantCodes;
    }

    public LocalDateTime getStartDate() {
        return new DateFormatter().format(startDate);
    }

    public LocalDateTime getEndDate() {
        return new DateFormatter().format(endDate);
    }
}
