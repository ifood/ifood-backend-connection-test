package br.com.ifood.connection.service;

import br.com.ifood.connection.controller.response.UnavailabilityHistoryResponse;
import java.time.Instant;
import org.springframework.data.domain.Pageable;

public interface UnavailabilityService {

    UnavailabilityHistoryResponse getPagedUnavailabilityHistory(Long restaurantId,
        Pageable pageable);

    UnavailabilityHistoryResponse getPagedUnavailabilityHistorySpecificPeriod(
        Long restaurantId, Instant dtStart, Instant dtEnd, Pageable pageable);

    void deleteSchedule(String uuid);
}
