package com.ifood.ifoodclient.controller.command;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.service.command.IRestaurantCommandService;
import com.ifood.ifoodclient.service.query.IRestaurantQueryService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @ApiOperation(value = "Partially updates an Restaurant", response = Restaurant.class, tags = {"restaurant"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header",defaultValue = MediaType.APPLICATION_JSON_VALUE),
            @ApiImplicitParam(name = "Application-Id",required = true, dataType = "string", paramType = "header")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = Restaurant.class),
            @ApiResponse(code = 400, message = "Bad request"),
    })
    @PatchMapping(value = "/restaurant/{code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity patch(
            @RequestHeader(name = "Application-Id") String appId,
            @ApiParam(value = "code", required = true) @PathVariable("customerId") String code,
            @ApiParam(name = "restaurant", required = true) @Valid @RequestBody Restaurant restaurant) {

        final Optional<Restaurant> existingRestaurant = queryService.findByCode(code);
        if (!existingRestaurant.isPresent())
            return ResponseEntity.badRequest().build();

        final Restaurant patched = commandService.patch(existingRestaurant.get(), restaurant);

        return ResponseEntity.ok(patched);
    }
}
