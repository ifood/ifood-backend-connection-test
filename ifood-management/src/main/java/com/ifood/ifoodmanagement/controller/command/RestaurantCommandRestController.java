package com.ifood.ifoodmanagement.controller.command;

import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.error.ApiException;
import com.ifood.ifoodmanagement.service.command.IRestaurantCommandService;
import com.ifood.ifoodmanagement.service.query.IRestaurantQueryService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RestaurantCommandRestController {

    private final IRestaurantCommandService commandService;
    private final IRestaurantQueryService queryService;

    @ApiOperation(value = "Create an Restaurant",response = Restaurant.class, tags = {"restaurant"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header",defaultValue = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Restaurant.class),
            @ApiResponse(code = 409, message = "Conflict")
    })
    @PostMapping(value = "/restaurant", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity create(
            @RequestHeader(name = "Application-Id") String appId,
            @ApiParam(name = "restaurant") @Valid @RequestBody Restaurant restaurant) {

        final Optional<Restaurant> restaurantOptional = queryService.findByCode(restaurant.getCode());
        if (restaurantOptional.isPresent())
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiException.builder()
                            .code(ApiException.BUSINESS_RULE_ERROR)
                            .message("No restaurant found for given code."));

        final Restaurant persistedRestaurant = commandService.create(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED).body(persistedRestaurant);
    }

    @ApiOperation(value = "Partially updates an Restaurant", response = Restaurant.class, tags = {"restaurant"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header",defaultValue = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = Restaurant.class),
            @ApiResponse(code = 400, message = "Bad request"),
    })
    @PatchMapping(value = "/restaurant/{code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity patch(
            @ApiParam(value = "code", required = true) @PathVariable("customerId") String code,
            @ApiParam(name = "restaurant", required = true) @Valid @RequestBody Restaurant restaurant) {

        final Optional<Restaurant> existingRestaurant = queryService.findByCode(code);
        if (!existingRestaurant.isPresent())
            return ResponseEntity.badRequest().build();

        final Restaurant patched = commandService.patch(existingRestaurant.get(), restaurant);

        return ResponseEntity.ok(patched);
    }
}
