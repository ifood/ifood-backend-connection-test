package com.ifood.rest.health;

import com.ifood.DateFormatter;
import com.ifood.domain.ConnectionHealthSignal;
import com.ifood.domain.ConnectionHealthCurrentStatus;
import com.ifood.service.health.HealthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
class HealthController {

    @Autowired
    private HealthService healthService;

    @ApiOperation(value="Receive the signal sent by the restaurant.")
    @RequestMapping(value = "/connection/health/{restaurant_code}", method = RequestMethod.POST)
    public ResponseEntity receiveHealthSignal(@ApiParam("Restaurant unique code") @PathVariable("restaurant_code") String restaurantCode){
        healthService.receiveHealthSignal(restaurantCode);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value="Search by the restaurant health for a given pediod.")
    @RequestMapping(value = "/connection/health/history/{restaurant_code}/{start_date}/{end_date}", method = RequestMethod.GET)
    public List<ConnectionHealthSignal> fetchHealthHistory(
            @ApiParam("Restaurant unique code") @PathVariable("restaurant_code") String restaurantCode,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("start_date") String startDate,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("end_date") String endDate){
        return healthService.findHealthHistory(restaurantCode, new DateFormatter().format(startDate), new DateFormatter().format(endDate));
    }


    @ApiOperation(value="Inform if the restaurants in the given list are online or not")
    @RequestMapping(value = "/connection/health/online/list/{restaurant_codes}", method = RequestMethod.GET)
    public List<ConnectionHealthCurrentStatus> checkIfOnline(@ApiParam("Restaurants' codes") @PathVariable("restaurant_codes") List<String> restaurantCodes){
        return healthService.checkRestaurantsConnection(restaurantCodes);
    }

    @ApiOperation(value="Search by the restaurants health for a given pediod.")
    @RequestMapping(value = "/connection/health/history/list", method = RequestMethod.POST)
    public List<RestaurantHealthHistoryFeatureResponse>  fetchHealthHistory(
            @RequestBody RestaurantHealthHistoriesListRequest restaurantHealthHistoriesListRequest){

        return restaurantHealthHistoriesListRequest.getRestaurantCodes().stream() //
                .map(restaurantCode -> {
                    List<ConnectionHealthSignal> connectionHealthHistories = healthService.findHealthHistory(restaurantCode, restaurantHealthHistoriesListRequest.getStartDate(), restaurantHealthHistoriesListRequest.getEndDate());
                    if (connectionHealthHistories != null && !connectionHealthHistories.isEmpty()) {
                        return new RestaurantHealthHistoryFeatureResponse(restaurantCode, connectionHealthHistories);
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
