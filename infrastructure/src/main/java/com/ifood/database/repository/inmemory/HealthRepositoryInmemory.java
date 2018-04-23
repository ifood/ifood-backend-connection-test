package com.ifood.database.repository.inmemory;

import com.ifood.database.entity.ConnectionHealthSignalEntity;
import com.ifood.database.repository.HealthRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
class HealthRepositoryInmemory implements HealthRepository {

    private final List<ConnectionHealthSignalEntity> connectionHistories = new ArrayList<>();

    @Override
    public void insertSignalRegistry(ConnectionHealthSignalEntity connectionHealthSignalEntity) {
        connectionHistories.add(connectionHealthSignalEntity);
    }

    @Override
    public List<ConnectionHealthSignalEntity> findHealthHistory(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate) {
        return connectionHistories.stream()
                .filter(connectionHealthSignalEntity ->
                        connectionHealthSignalEntity.getCode().equals(restaurantCode)
                        && connectionHealthSignalEntity.getReceivedAt().isBefore(endDate)
                        && connectionHealthSignalEntity.getReceivedAt().isAfter(startDate)
                ).collect(Collectors.toList());
    }

}
