package com.ifood.service.health;

import com.ifood.database.entity.ConnectionHealthSignalEntity;
import com.ifood.database.repository.HealthRepository;
import com.ifood.database.repository.RestaurantRepository;
import com.ifood.domain.ConnectionHealthCurrentStatus;
import com.ifood.domain.ConnectionHealthSignal;
import com.ifood.domain.RestaurantAvailability;
import com.ifood.domain.UnavailabilitySchedule;
import com.ifood.ignite.repository.HealthRepositoryIgnite;
import com.ifood.service.unavailability.schedule.UnavailabilityScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
class HealthServiceImpl implements HealthService {

    @Autowired
    private HealthRepository healthRepository;

    @Autowired
    private HealthRepositoryIgnite healthRepositoryIgnite;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UnavailabilityScheduleService unavailabilityScheduleRepository;

    @Override
    public void receiveHealthSignal(final String restaurantCode) {
        Assert.notNull(restaurantCode, "The restaurant code should not be null");

        boolean notExists = restaurantRepository.notExists(restaurantCode);
        if (notExists)
            throw new IllegalArgumentException("Invalid restaurant code");

        healthRepository.insertSignalRegistry(new ConnectionHealthSignalEntity(restaurantCode, LocalDateTime.now()));

        healthRepositoryIgnite.save(restaurantCode, Boolean.TRUE);
    }

    @Override
    public List<ConnectionHealthSignal> findHealthHistory(final String restaurantCode, final LocalDateTime startDate, final LocalDateTime endDate) {
        Assert.notNull(restaurantCode, "The restaurant code should not be null");
        Assert.notNull(startDate, "The start date should not be null");
        Assert.notNull(endDate, "The end date should not be null");
        Assert.isTrue(startDate.isBefore(endDate), "Invalid start date / end date");

        return healthRepository.findHealthHistory(restaurantCode, startDate, endDate).stream() //
                .map(ConnectionHealthSignal::new).collect(Collectors.toList());
    }

    @Override
    public List<ConnectionHealthCurrentStatus> checkRestaurantsConnection(final List<String> restaurantCodes) {
        Assert.notNull(restaurantCodes, "The restaurant codes should not be null");
        Assert.notEmpty(restaurantCodes, "The restaurant codes should not be empty");

        final List<ConnectionHealthCurrentStatus> connectionHealths = new ArrayList<>();
        final LocalDateTime currentTime = LocalDateTime.now();
        final LocalDateTime maxTimeOfUnavailability = currentTime.plusMinutes(2);

        restaurantCodes.forEach(restaurantCode -> {
            boolean currentHealth = healthRepositoryIgnite.exists(restaurantCode);

            List<UnavailabilitySchedule> unavailabilitySchedules = unavailabilityScheduleRepository.fetchUnavailabilitySchedule(restaurantCode, currentTime, maxTimeOfUnavailability);
            RestaurantAvailability restaurantAvailability = new RestaurantAvailability(unavailabilitySchedules);

            ConnectionHealthCurrentStatus connectionHealth = new ConnectionHealthCurrentStatus(restaurantCode, currentHealth, restaurantAvailability);
            connectionHealths.add(connectionHealth);
        });

        return connectionHealths;
    }

}
