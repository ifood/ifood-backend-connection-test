package br.com.ifood.connection.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import br.com.ifood.connection.controller.exception.handler.ErrorMessage;
import br.com.ifood.connection.controller.response.UnavailabilityHistoryResponse;
import br.com.ifood.connection.service.UnavailabilityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.Instant;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Endpoint for listing and deleting schedules of unavailabilities", tags = {
    "Unavailability"})
@RequestMapping(value = "/unavailabilities", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@RestController
@Validated
public class UnavailabilityController {

    private final UnavailabilityService unavailabilityService;

    @ApiOperation(value = "Gets all unavailability schedules", //
        notes = "Returns all the history os unavailability schedules paged with a default limit "
            + "of 5 unavailabilities per page and a max of 20.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request", response = ErrorMessage.class)
    })
    @GetMapping
    public UnavailabilityHistoryResponse getPagedUnavailabilityHistory(
        @RequestParam @Min(1L) Long restaurantId, Pageable pageable) {

        return unavailabilityService.getPagedUnavailabilityHistory(restaurantId, pageable);
    }

    @ApiOperation(value = "Gets all unavailability schedules", //
        notes = "Returns all the history os unavailability schedules paged with a default limit "
            + "of 5 unavailabilities per page and a max of 20.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request", response = ErrorMessage.class)
    })
    @GetMapping(params = {"restaurantId", "start", "end"})
    public UnavailabilityHistoryResponse getPagedUnavailabilityHistorySpecificPeriod(
        @RequestParam() @Min(1L) Long restaurantId,
        @RequestParam("start") @Past Instant dtStart,
        @RequestParam("end") @PastOrPresent Instant dtEnd,
        Pageable pageable) {

        return unavailabilityService.getPagedUnavailabilityHistorySpecificPeriod(restaurantId,
            dtStart, dtEnd, pageable);
    }

    @ApiOperation(value = "Deletes a schedule of unavailability", //
        notes = "Deletes a specific schedule of unavailability based on the uuid passed as parameter")
    @DeleteMapping(path = "/{uuid}")
    public void deleteSchedule(@PathVariable String uuid) {

        unavailabilityService.deleteSchedule(uuid);

    }

}
