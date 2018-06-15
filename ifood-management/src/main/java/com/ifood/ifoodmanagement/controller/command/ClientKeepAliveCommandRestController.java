package com.ifood.ifoodmanagement.controller.command;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.service.command.IKeepAliveCommandService;
import com.ifood.ifoodmanagement.service.command.IRestaurantCommandService;
import com.ifood.ifoodmanagement.service.query.IRestaurantQueryService;
import com.ifood.ifoodmanagement.util.IfoodUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientKeepAliveCommandRestController {

    private final IKeepAliveCommandService keepAliveCommandService;
    private final IRestaurantCommandService restaurantCommandService;
    private final IRestaurantQueryService queryService;

    @ApiOperation(value = "Create an clientKeepAliveLog for a given restaurant",response = ClientKeepAliveLog.class, tags = {"clientKeepAliveLog"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header", defaultValue = MediaType.APPLICATION_JSON_VALUE)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = ClientKeepAliveLog.class),
            @ApiResponse(code = 422, message = "Unprocessable Entity"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @PostMapping(value = "/clientKeepAlive", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity create(
            @ApiParam(name = "clientKeepAliveLog") @RequestBody ClientKeepAliveLog clientKeepAliveLog) {

        try {
            final String code = clientKeepAliveLog.getRestaurantCode();

            // Insert keepAlive log
            final String keepAliveLogId =
                    keepAliveCommandService.insertClientKeepAliveLog(clientKeepAliveLog);

            if (!Objects.isNull(keepAliveLogId)){

                // patch restaurant
                final Optional<Restaurant> existingRestaurant = queryService.findByCode(code);

                final DateTime lastModified = DateTime.now();
                final boolean isOnline = IfoodUtil.isRestaurantOnline(existingRestaurant.get().isAvailable(), lastModified);

                Restaurant toPatch = Restaurant.builder()
                        .online(isOnline)
                        .lastModified(lastModified)
                        .build();
                restaurantCommandService.patch(existingRestaurant.get(), toPatch);
            }
            return ResponseEntity.created(new URI(keepAliveLogId)).build();
        } catch (URISyntaxException e) {
           return ResponseEntity.unprocessableEntity().build();
        } catch (Exception ex){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: Could not execute operation");
        }
    }
}
