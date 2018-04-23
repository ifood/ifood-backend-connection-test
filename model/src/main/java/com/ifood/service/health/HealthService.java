package com.ifood.service.health;

import com.ifood.domain.ConnectionHealthCurrentStatus;
import com.ifood.domain.ConnectionHealthSignal;

import java.time.LocalDateTime;
import java.util.List;

public interface HealthService {

    void receiveHealthSignal(String restaurantCode);

    List<ConnectionHealthSignal> findHealthHistory(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate);

    List<ConnectionHealthCurrentStatus> checkRestaurantsConnection(List<String> restaurantCodes);
}
