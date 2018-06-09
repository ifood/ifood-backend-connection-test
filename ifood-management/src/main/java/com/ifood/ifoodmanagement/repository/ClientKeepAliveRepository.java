package com.ifood.ifoodmanagement.repository;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientKeepAliveRepository extends MongoRepository<ClientKeepAliveLog, String> {

    List<ClientKeepAliveLog> findByRestaurantCodeAndAvailable(String restaurantCode, String availabilityStatus);

    List<ClientKeepAliveLog> findByRestaurantCodeAndAvailableAndLastModifiedBetween(String restaurantCode, String availabilityStatus, DateTime start, DateTime end);
}
