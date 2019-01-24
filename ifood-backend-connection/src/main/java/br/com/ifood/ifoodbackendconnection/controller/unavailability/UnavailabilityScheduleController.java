package br.com.ifood.ifoodbackendconnection.controller.unavailability;

import br.com.ifood.ifoodbackendconnection.controller.unavailability.request.UnavailabilityScheduleRequest;
import br.com.ifood.ifoodbackendconnection.controller.unavailability.response.UnavailabilityScheduleResponse;
import br.com.ifood.ifoodbackendconnection.service.UnavailabilityScheduleService;
import br.com.ifood.ifoodbackendconnection.utilities.DateFormatter;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestController
@Api(tags = "UnavailabilitySchedule", description = "Unavailability Schedule")
public class UnavailabilityScheduleController {

    private static final String BASE_PATH = "/api/v1/";

    private UnavailabilityScheduleService unavailabilityScheduleService;

    @Autowired
    public UnavailabilityScheduleController(UnavailabilityScheduleService unavailabilityScheduleService) {
        this.unavailabilityScheduleService = unavailabilityScheduleService;
    }

    @ApiOperation(value = "Create a new unavailability schedule")
    @RequestMapping(value = BASE_PATH+"backend/connection/schedule/unavailability", method = RequestMethod.POST)
    public ResponseEntity createUnavailabilitySchedule(@RequestBody UnavailabilityScheduleRequest unavailabilitySchedule) {

        return unavailabilityScheduleService.save(
                unavailabilitySchedule.getRestaurantCode(),
                unavailabilitySchedule.getUnavailabilityReason(),
                DateFormatter.format(unavailabilitySchedule.getStartDateTime()),
                DateFormatter.format(unavailabilitySchedule.getEndDateTime())
        ).applyAndGet(errors -> status(BAD_REQUEST).body(errors), restaurantStatus -> status(OK).body(restaurantStatus));
    }

    @ApiOperation(value = "Get unavailability schedule list a given restaurant and period.")
    @RequestMapping(value = BASE_PATH+"backend/connection/schedule/unavailability/restaurant/{code}/{start_date}/{end_date}", method = RequestMethod.GET)
    public List<UnavailabilityScheduleResponse> fetchUnavailabilitySchedule(
            @ApiParam("Single code") @PathVariable("code") String restaurantCode,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("start_date") String startDate,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("end_date") String endDate) {

        return unavailabilityScheduleService.fetchUnavailabilitySchedule(
                restaurantCode,
                DateFormatter.format(startDate),
                DateFormatter.format(endDate)
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete a unavailability schedule.")
    @RequestMapping(value = BASE_PATH+"backend/connection/schedule/unavailability/{code}", method = RequestMethod.DELETE)
    @ApiResponses({
            @ApiResponse(code = 204, message = "Unavailability Schedule successfully deleted"),
            @ApiResponse(code = 404, message = "Unavailability Schedule not found")
    })
    public ResponseEntity<Void> deleteUnavailabilitySchedule(@ApiParam("Single schedule code") @PathVariable("code") String code) {
        try {
            unavailabilityScheduleService.deleteScheduleBy(code);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            log.error("Error while deleting a measure type: " + ex.getMessage());
            return notFound().build();
        }
    }
}