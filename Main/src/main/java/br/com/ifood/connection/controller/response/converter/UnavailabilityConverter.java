package br.com.ifood.connection.controller.response.converter;

import br.com.ifood.connection.controller.response.UnavailabilityHistoryResponse;
import br.com.ifood.connection.controller.response.dto.UnavailabilityDTO;
import br.com.ifood.connection.data.entity.StatusEntity;
import java.time.Instant;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Converter from Page<StatusEntity> to UnavailabilityHistoryResponse
 */
@Component
public class UnavailabilityConverter {

    public UnavailabilityHistoryResponse convert(Page<StatusEntity> statusEntityPage,
        Function<StatusEntity, StatusEntity> adjustFunction) {

        return new UnavailabilityHistoryResponse(
            statusEntityPage.stream()
                .map(adjustFunction)
                .map(s -> new UnavailabilityDTO(s.getDtInits(), s.getDtEnds()))
                .collect(Collectors.toList()));
    }

    /**
     * Converts to the response DTO and limits the dtEnds case it ends after dtLimit.<br/> Since the
     * unavailability just counts to past moments, this method filter schedules that ends in the
     * future and limit the time range to the present.<br/>
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
    public static Function<StatusEntity, StatusEntity> adjustDtEnd(Instant dtEnd) {

        return statusEntity -> {
            Instant statusStart = statusEntity.getDtInits();
            Instant statusEnd = getAndAdjustStatusEndIfNeeded(statusEntity, dtEnd);

            return createStatusEntity(statusStart, statusEnd);
        };
    }

    /**
     * Converts to the response DTO and limits the dtInits and dtEnds in case they are outside the
     * limits of dtStart and dtEnd .<br/>
     *
     * <pre>
     * Schedule
     * -----------------
     * | 10:00 - 14:00 |
     * -----------------
     *
     * dtStart: 11:00
     * dtEnd:   13:00
     *
     * -------------------------------------------------
     * | Schedule      | Unavailable   | Schedule      |
     * | 10:00 - 10:59 | 11:00 - 13:00 | 13:01 - 14:00 |
     * -------------------------------------------------
     * </pre>
     *
     * In the example above, the unavailability for the schedule will point just the period 11:00
     * until 13:00.
     */
    public static Function<StatusEntity, StatusEntity> adjustDtStartAndEnd(Instant dtStart,
        Instant dtEnd) {

        return statusEntity -> {
            Instant statusStart = getAndAdjustStatusStartIfNeeded(statusEntity, dtStart);
            Instant statusEnd = getAndAdjustStatusEndIfNeeded(statusEntity, dtEnd);

            return createStatusEntity(statusStart, statusEnd);
        };
    }

    static StatusEntity createStatusEntity(Instant dtInits, Instant dtEnd) {
        return StatusEntity.builder()
            .dtInits(dtInits)
            .dtEnds(dtEnd)
            .build();
    }

    static Instant getAndAdjustStatusStartIfNeeded(StatusEntity statusEntity, Instant dtStart) {
        return dtStart.isAfter(statusEntity.getDtInits()) ?
            dtStart : statusEntity.getDtInits();
    }

    static Instant getAndAdjustStatusEndIfNeeded(StatusEntity statusEntity, Instant dtEnd) {
        return dtEnd.isBefore(statusEntity.getDtEnds()) ?
            dtEnd : statusEntity.getDtEnds();
    }
}
