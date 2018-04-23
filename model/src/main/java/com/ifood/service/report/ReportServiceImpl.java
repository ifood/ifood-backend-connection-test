package com.ifood.service.report;

import com.ifood.domain.*;
import com.ifood.service.health.HealthService;
import com.ifood.service.unavailability.schedule.UnavailabilityScheduleService;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

@Service
class ReportServiceImpl implements ReportService {

    @Autowired
    private HealthService healthService;

    @Autowired
    private UnavailabilityScheduleService unavailabilityScheduleService;

    @Autowired
    private Ignite ignite;

    @Override
    public ConnectionReport fetchReport(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate) {
        Assert.notNull(restaurantCode, "The restaurant code should not be null");
        Assert.notNull(startDate, "The start date should not be null");
        Assert.notNull(endDate, "The end date should not be null");
        Assert.isTrue(startDate.isBefore(endDate), "The start date / end date is invalid");

        List<ConnectionHealthSignal> healthHistory = healthService.findHealthHistory(restaurantCode, startDate, endDate);
        RestaurantConnection restaurantConnection = new RestaurantConnection(healthHistory);

        List<UnavailabilitySchedule> unavailabilitySchedule = unavailabilityScheduleService.fetchUnavailabilitySchedule(restaurantCode, startDate, endDate);
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability(unavailabilitySchedule);

        return new ConnectionReport(restaurantAvailability, restaurantConnection, ignite);
    }

}
