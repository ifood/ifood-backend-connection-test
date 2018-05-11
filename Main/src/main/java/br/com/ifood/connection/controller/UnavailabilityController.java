package br.com.ifood.connection.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifood.connection.controller.exception.handler.ErrorMessage;
import br.com.ifood.connection.controller.response.UnavailabilityHistoryResponse;
import br.com.ifood.connection.controller.response.dto.UnavailabilityDTO;
import br.com.ifood.connection.data.entity.StatusEntity;
import br.com.ifood.connection.data.repository.StatusRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@Api(value = "Endpoint for listing and deleting schedules of unavailabilities", tags = { "Unavailability" })
@RequestMapping(value = "/unavailability", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@RestController
@Validated
public class UnavailabilityController {

    private final StatusRepository statusRepository;

    @ApiOperation(value = "Gets all unavailability schedules", //
            notes = "Returns all the history os unavailability schedules.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad request", response = ErrorMessage.class)
    })
    @GetMapping
    public UnavailabilityHistoryResponse getUnavailabilityHistory(@RequestParam @Min(1L) Long restaurantId) {

        Instant dtLimit = Instant.now();

        List<StatusEntity> statusList = statusRepository
                .findScheduleHistory(restaurantId, dtLimit);

        return new UnavailabilityHistoryResponse(
                statusList.stream()
                        .map(s -> this.toUnavailability(s, dtLimit))
                        .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Deletes a schedule of unavailability", //
            notes = "Deletes a specific schedule of unavailability based on the uuid passed as parameter")
    @DeleteMapping(path = "/{uuid}")
    public void deleteSchedule(@PathVariable String uuid) {

        statusRepository.deleteById(UUID.fromString(uuid));

    }

    /**
     * Converts to the response DTO and limits the dtEnds case it ends after dtLimit.<br/>
     * Since the unavailability just counts to past moments, this method filter schedules that ends in the future and
     * limit the time range to the present.<br/>
     *
     * <pre>
     * Schedule
     * -----------------
     * | 10:00 - 14:00 |
     * -----------------
     *
     * Now: 11:50
     *
     * ---------------------------------
     * | Unavailable   | Schedule      |
     * | 10:00 - 11:50 | 11:51 - 14:00 |
     * ---------------------------------
     * </pre>
     */
    private UnavailabilityDTO toUnavailability(StatusEntity statusEntity, Instant dtLimit) {

        Instant dtStarts = statusEntity.getDtInits();
        Instant dtEnds = statusEntity.getDtEnds();

        if (dtLimit.isBefore(dtEnds)) {
            dtEnds = dtLimit;
        }

        return new UnavailabilityDTO(dtStarts, dtEnds);
    }
}
