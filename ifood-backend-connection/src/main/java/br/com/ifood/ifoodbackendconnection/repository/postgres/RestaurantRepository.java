package br.com.ifood.ifoodbackendconnection.repository.postgres;

import br.com.ifood.ifoodbackendconnection.domain.Restaurant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository(value = "restaurant")
public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
    Optional<Restaurant> findByCode(@Param("code") String code);
}