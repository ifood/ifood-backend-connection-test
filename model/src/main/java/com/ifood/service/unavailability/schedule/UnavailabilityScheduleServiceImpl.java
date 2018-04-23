package com.ifood.service.unavailability.schedule;

import com.ifood.database.entity.RestaurantEntity;
import com.ifood.database.entity.UnavailabilityScheduleEntity;
import com.ifood.database.repository.RestaurantRepository;
import com.ifood.database.repository.UnavailabilityScheduleRepository;
import com.ifood.domain.UnavailabilityReason;
import com.ifood.domain.UnavailabilitySchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
class UnavailabilityScheduleServiceImpl implements UnavailabilityScheduleService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UnavailabilityScheduleRepository unavailabilityScheduleRepository;

    @Override
    public String insertSchedule(String restaurantCode, UnavailabilityReason reason, LocalDateTime startDate, LocalDateTime endDate) {
        Assert.notNull(restaurantCode, "The restaurant code should not be null");
        Assert.notNull(reason, "The reason should not be null");
        Assert.notNull(startDate, "The start date should not be null");
        Assert.notNull(endDate, "The end date should not be null");
        Assert.isTrue(startDate.isBefore(endDate), "The start date / end date is invalid");

        RestaurantEntity restaurant = restaurantRepository.findRestaurant(restaurantCode);
        if(restaurant == null){
            throw new IllegalArgumentException("Restaurant not found.");
        }

        boolean unavailabilityExists = unavailabilityScheduleRepository.exists();
        if(unavailabilityExists){
            throw new IllegalArgumentException("Unavailability repository already exists.");
        }

        UnavailabilityScheduleEntity unavailabilityScheduleEntity = new UnavailabilityScheduleEntity(restaurant, reason.name(), startDate, endDate);
        unavailabilityScheduleRepository.saveUnavailabilitySchedule(unavailabilityScheduleEntity);

        return unavailabilityScheduleEntity.getCode();
    }

    @Override
    public List<UnavailabilitySchedule> fetchUnavailabilitySchedule(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate) {
        Assert.notNull(restaurantCode, "The restaurant code should not be null");
        Assert.notNull(startDate, "The start date should not be null");
        Assert.notNull(endDate, "The end date should not be null");
        Assert.isTrue(startDate.isBefore(endDate), "The start date / end date is invalid");

        boolean notExists = restaurantRepository.notExists(restaurantCode);
        if(notExists){
            throw new IllegalArgumentException("Restaurant not found.");
        }

        return unavailabilityScheduleRepository.fetchUnavailabilitySchedule(restaurantCode, startDate, endDate)
                .stream().map(UnavailabilitySchedule::new).collect(Collectors.toList());
    }

    @Override
    public void deleteSchedule(String scheduleCode) {
        Assert.notNull(scheduleCode, "The schedule code should not be null");
        unavailabilityScheduleRepository.deleteSchedule(scheduleCode);
    }
}
