package com.ifood.database.repository;

import com.ifood.database.entity.ConnectionHealthSignalEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface HealthRepository {

    void insertSignalRegistry(ConnectionHealthSignalEntity connectionHealthSignalEntity);

    List<ConnectionHealthSignalEntity> findHealthHistory(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate);

}
