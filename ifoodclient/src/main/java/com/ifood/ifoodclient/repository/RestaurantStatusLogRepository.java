package com.ifood.ifoodclient.repository;

import com.ifood.ifoodclient.domain.RestaurantStatusLog;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantStatusLogRepository extends MongoRepository<RestaurantStatusLog, String> {

    List<RestaurantStatusLog> findByRestaurantCodeAndAvailable(String restaurantCode, String availabilityStatus);

    List<RestaurantStatusLog> findByRestaurantCodeAndAvailableAndLastModifiedBetween(String restaurantCode, String availabilityStatus, DateTime start, DateTime end);
}
