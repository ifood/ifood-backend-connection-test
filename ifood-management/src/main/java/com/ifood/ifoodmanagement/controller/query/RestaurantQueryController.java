package com.ifood.ifoodmanagement.controller.query;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.service.query.IRestaurantQueryService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @ApiParam(name = "code")
                @PathVariable String code,
            @ApiParam(name = "available")
                @RequestParam(defaultValue = "false") boolean available,
            @ApiParam(name = "from")
                @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
                @RequestParam(required = false, value = "from") DateTime from,
            @ApiParam(name = "to")
                @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
                @RequestParam(required = false, value = "to") DateTime to) {

        final List<ClientKeepAliveLog> logHistory = restaurantQueryService.
                fetchRestaurantAvailabilityHistory(code, available, from, to);

        return ResponseEntity.ok(logHistory);
    }

    @ApiOperation(value = "Fetch Restaurants online/offline status", response = Restaurant.class, responseContainer = "List", tags = {"restaurant"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header", defaultValue = MediaType.APPLICATION_JSON_VALUE),
    })
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok", response = Restaurant.class),})
    @GetMapping(value = "/restaurant/onlinestatus", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity fetchRestaurantsOnlineStatus(
            @ApiParam(name = "codes") @RequestParam(value = "code") List<String> code) {

        final List<Restaurant> restaurantsByOnlineStatus =
                restaurantQueryService.fetchRestaurantsOnlineStatus(code);

        return ResponseEntity.ok(restaurantsByOnlineStatus);
    }
}
