package br.com.ifood.connection.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import br.com.ifood.connection.controller.response.UnavailabilityHistoryResponse;
import br.com.ifood.connection.controller.response.dto.ScheduleDTO;
import br.com.ifood.connection.controller.response.dto.UnavailabilityDTO;
import br.com.ifood.connection.data.entity.StatusEntity;
import br.com.ifood.connection.data.repository.StatusRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Endpoint for listing and deleting schedules of unavailabilities")
@RestController
@RequestMapping(value = "/unavailability", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UnavailabilityResource {

    private final StatusRepository statusRepository;

    @ApiOperation(value = "Gets the history of unavailabilities of a restaurant")
    @GetMapping
    public UnavailabilityHistoryResponse getUnavailabilityHistory(@RequestParam Long
        restaurantId) {

        List<StatusEntity> statusList = statusRepository
            .findUnavailabilityHistory(restaurantId, Instant.now());

        return new UnavailabilityHistoryResponse(
            statusList.stream()
                .map(this::toUnavailability)
                .collect(Collectors.toList())
        );
    }

    @ApiOperation(value = "Deletes a schedule of unavailability")
    @DeleteMapping
    public Boolean deleteSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        statusRepository.deleteById(UUID.fromString(scheduleDTO.getUuid()));

        return Boolean.TRUE;
    }

    /**
     * Converts to the response DTO
     */
    private UnavailabilityDTO toUnavailability(StatusEntity statusEntity) {

        Instant now = Instant.now();

        Instant dtStarts = statusEntity.getDtInits();
        Instant dtEnds = statusEntity.getDtEnds();

        if (now.isBefore(dtEnds)) {
            dtEnds = now;
        }

        return new UnavailabilityDTO(dtStarts, dtEnds);
    }
}
