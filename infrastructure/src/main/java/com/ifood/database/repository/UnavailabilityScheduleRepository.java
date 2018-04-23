package com.ifood.database.repository;

import com.ifood.database.entity.UnavailabilityScheduleEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface UnavailabilityScheduleRepository {

    boolean exists();

    List<UnavailabilityScheduleEntity> fetchUnavailabilitySchedule(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate);

    void deleteSchedule(String scheduleId);

    void saveUnavailabilitySchedule(UnavailabilityScheduleEntity unavailabilityScheduleEntity);
}
