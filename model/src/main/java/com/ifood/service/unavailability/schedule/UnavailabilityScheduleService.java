package com.ifood.service.unavailability.schedule;

import com.ifood.domain.UnavailabilityReason;
import com.ifood.domain.UnavailabilitySchedule;

import java.time.LocalDateTime;
import java.util.List;

public interface UnavailabilityScheduleService {

    String insertSchedule(String restaurantCode, UnavailabilityReason reason, LocalDateTime startDate, LocalDateTime endDate);

    List<UnavailabilitySchedule> fetchUnavailabilitySchedule(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate);

    void deleteSchedule(String scheduleCode);
}
