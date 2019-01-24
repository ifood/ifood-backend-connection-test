package br.com.ifood.ifoodbackendconnection.controller.connection;

import br.com.ifood.ifoodbackendconnection.controller.connection.response.RestaurantKeepSignalHistoryResponse;
import br.com.ifood.ifoodbackendconnection.domain.ConnectionStatus;
import br.com.ifood.ifoodbackendconnection.domain.ErrorList;
import br.com.ifood.ifoodbackendconnection.domain.SignalHistory;
import br.com.ifood.ifoodbackendconnection.service.HealthCheckService;
import br.com.ifood.ifoodbackendconnection.utilities.DateFormatter;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestController
@Api(tags = "RestaurantConnection", description = "Restaurant Connection")
public class RestaurantConnectionController {

    private static final String BASE_PATH = "/api/v1/";

    private HealthCheckService healthCheckService;

    @Autowired
    public RestaurantConnectionController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @ResponseStatus(CREATED)
    @ApiOperation(value="Create a new health signal")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Keep-alive signal was successfully created, response shows the new signal with it's attributes", response = SignalHistory.class),
            @ApiResponse(code = 400, message = "There were issues with the request", response = ErrorList.class)
    })
    @RequestMapping(value = BASE_PATH+"backend/connection/restaurant/{code}", method = RequestMethod.POST)
    public ResponseEntity createKeepSignal(@ApiParam("Single restaurant code") @PathVariable("code") String code) {

        return healthCheckService.saveSignalForRestaurant(code)
                .applyAndGet(errors -> status(BAD_REQUEST).body(errors),
                        createdSignal -> status(CREATED).body(createdSignal));
    }

    @ApiOperation(value="Get current connection status for the restaurant (online or offline) list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "List containing all connection status"),
            @ApiResponse(code = 404, message = "Connection Status not found")
    })
    @RequestMapping(value = BASE_PATH+"backend/connection/current/restaurant/{codes}", method = RequestMethod.GET)
    public ResponseEntity<List<ConnectionStatus>> fetchCurrentConnectionStatus(@ApiParam("Multiple Restaurant codes separated by comma")
                                                                                   @PathVariable("codes") List<String> restaurantCodes) {

        return ok(healthCheckService.fetchRestaurantConnectionsFor(restaurantCodes));
    }

    @ApiOperation(value="Get signal history for a given restaurant list and period.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "List containing Restaurant Keep Signal History"),
            @ApiResponse(code = 404, message = "Restaurant keep signal history not found")
    })
    @RequestMapping(value = BASE_PATH+"backend/connection/history/restaurant", method = RequestMethod.GET)
    public ResponseEntity<List<RestaurantKeepSignalHistoryResponse>> fetchRestaurantHealthHistory(
            @ApiParam("Multiple Restaurant codes separated by comma") @RequestParam("code") List<String> restaurantCodes,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @RequestParam("start_date") String startDate,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @RequestParam("end_date") String endDate) {
        try {
            return ok(restaurantCodes.stream()
                    .map(restaurantCode -> {
                        List<SignalHistory> connectionHealthHistories = healthCheckService.fetchSignalHistory(restaurantCode,
                                DateFormatter.format(startDate),
                                DateFormatter.format(endDate));

                        if (!connectionHealthHistories.isEmpty()) {
                            return new RestaurantKeepSignalHistoryResponse(restaurantCode, connectionHealthHistories);
                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList()));
        } catch (IllegalArgumentException ex) {
            log.error("Error while get Restaurant KeepSignal History: " + ex.getMessage());
            return notFound().build();
        }
    }

    @ApiOperation(value="Get signal history for a given restaurant and period")
    @ApiResponses({
            @ApiResponse(code = 200, message = "List containing Restaurant Keep Signal History"),
            @ApiResponse(code = 404, message = "Signal history not found")
    })
    @RequestMapping(value = BASE_PATH+"backend/connection/history/restaurant/{code}/{start_date}/{end_date}", method = RequestMethod.GET)
    public ResponseEntity<List<SignalHistory>> fetchRestaurantHealthHistory(
            @ApiParam("Single code") @PathVariable("code") String restaurantCode,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("start_date") String startDate,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("end_date") String endDate) {
        try {
            return ok(healthCheckService.fetchSignalHistory(restaurantCode, DateFormatter.format(startDate), DateFormatter.format(endDate)));
        } catch (IllegalArgumentException ex) {
            log.error("Error while get Restaurant Keep-alive signal history: " + ex.getMessage());
            return notFound().build();
        }
    }
}