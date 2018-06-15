package com.ifood.ifoodclient.controller.command;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.service.command.ifood.IRestaurantCommandService;
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
            @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header", defaultValue = MediaType.APPLICATION_JSON_VALUE)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = Restaurant.class),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @PatchMapping(value = "/restaurant/{code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity patch(
            @ApiParam(value = "code", required = true) @PathVariable("code") String code,
            @ApiParam(name = "restaurant", required = true) @Valid @RequestBody Restaurant restaurant) {

        log.info("PATCH method invoked for restaurant [" + code + "]");
        final Optional<Restaurant> existingRestaurant = queryService.findByCode(code);

        return existingRestaurant.map(r -> {
            final Restaurant patched = commandService.patch(r, restaurant);
            return ResponseEntity.ok(patched);
        }).orElse(ResponseEntity.badRequest().build());
    }
}
