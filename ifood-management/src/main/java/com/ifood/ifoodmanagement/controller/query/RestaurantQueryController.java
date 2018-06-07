package com.ifood.ifoodmanagement.controller.query;

import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.service.IRestaurantQueryService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Api(value = "Ifood Restaurant API")
@RestController
@RequiredArgsConstructor
public class RestaurantQueryController {

    private final IRestaurantQueryService restaurantQueryService;

    @ApiOperation(value = "Fetch a Restaurant",response = Restaurant.class, tags = {"restaurant"})
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header", defaultValue = MediaType.APPLICATION_JSON_VALUE),
        @ApiImplicitParam(name = "Application-Id",required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = Restaurant.class),
        @ApiResponse(code = 404, message = "Not found")
    })
    @GetMapping(value = "/restaurant/{code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Restaurant> fetchRestaurant(
            @RequestHeader(name = "Application-Id") String appId,
            @ApiParam(name = "code") @PathVariable String code) {

        return restaurantQueryService.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ApiOperation(value = "Fetch all Restaurants",response = Restaurant.class, responseContainer = "List", tags = {"restaurant"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header", defaultValue = MediaType.APPLICATION_JSON_VALUE),
            @ApiImplicitParam(name = "Application-Id",required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok", response = Restaurant.class),})
    @GetMapping(value = "/restaurant", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity fetchAllRestaurants(
            @RequestHeader(name = "Application-Id") String appId,
            @ApiParam(name = "code") @PathVariable String code) {

        return ResponseEntity.ok(restaurantQueryService.findAll());
    }

    @ApiOperation(value = "Fetch a Restaurant availability history", response = ClientKeepAliveLog.class, responseContainer = "List", tags = {"restaurantStatusLog"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header", defaultValue = MediaType.APPLICATION_JSON_VALUE),
            @ApiImplicitParam(name = "Application-Id",required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok", response = ClientKeepAliveLog.class)})
    @GetMapping(value = "/restaurant/history/{code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity fetchRestaurantAvailabilityHistory(
            @RequestHeader(name = "Application-Id") String appId,
            @ApiParam(name = "code") @PathVariable String code,
            @ApiParam(name = "status") @RequestParam(required = false) String status,
            @ApiParam(name = "startDate") @RequestParam(required = false, value = "startDate") String startDate,
            @ApiParam(name = "endDate") @RequestParam(required = false, value = "endDate") String endDate) {

        final String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        final DateTime startDateFormatted = DateTimeFormat.forPattern(pattern).parseDateTime(startDate);
        final DateTime endDateFormatted = !Objects.isNull(endDate) ?
                DateTimeFormat.forPattern(pattern).parseDateTime(endDate) : null;

        return ResponseEntity.ok(restaurantQueryService.
                fetchRestaurantAvailabilityHistory(code, status, new Interval(startDateFormatted, endDateFormatted)));
    }

    @ApiOperation(value = "Fetch Restaurants online/offline status", response = Restaurant.class, responseContainer = "List", tags = {"restaurant"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header", defaultValue = MediaType.APPLICATION_JSON_VALUE),
            @ApiImplicitParam(name = "Application-Id",required = true, dataType = "string", paramType = "header")
    })
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok", response = Restaurant.class),})
    @GetMapping(value = "/restaurant/onlinestatus", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity fetchRestaurantsOnlineStatus(
            @RequestHeader(name = "Application-Id") String appId,
            @ApiParam(name = "restaurantCodes") @RequestParam List<String> restaurantCodes) {

        return ResponseEntity.ok(restaurantQueryService.fetchRestaurantsOnlineStatus(restaurantCodes));
    }
}
