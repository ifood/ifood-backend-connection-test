package com.ifood.schedule;

import com.ifood.entity.UnavailabilityScheduleEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UnavailabilityScheduleRepositoryIgnite implements UnavailabilityScheduleRepository {
    private static List<UnavailabilityScheduleEntity> list = new ArrayList<>();

    @Override
    public boolean exists(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate) {
        return false;
    }

    @Override
    public List<UnavailabilityScheduleEntity> fetchUnavailabilitySchedule(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate) {
        return list;
    }

    @Override
    public void deleteSchedule(String scheduleId) {

    }

    @Override
    public void saveUnavailabilitySchedule(UnavailabilityScheduleEntity unavailabilityScheduleEntity) {
        list.add(unavailabilityScheduleEntity);
    }
}
