package br.com.ifood.connection.data.repository;

import br.com.ifood.connection.data.entity.RestaurantEntity;
import org.springframework.data.repository.CrudRepository;

public interface RestaurantRepository extends CrudRepository<RestaurantEntity, Long> {

}
