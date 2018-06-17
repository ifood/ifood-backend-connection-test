package com.ifood.ifoodmanagement.repository;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ClientKeepAliveRepository extends MongoRepository<ClientKeepAliveLog, String> {

    List<ClientKeepAliveLog> findByRestaurantCodeAndAvailable(String restaurantCode, boolean available);

    List<ClientKeepAliveLog> findByRestaurantCodeAndAvailableAndLastModifiedBetween(
            String restaurantCode, boolean available, Date from, Date to);

    List<ClientKeepAliveLog> findByRestaurantCode(String code);
}
