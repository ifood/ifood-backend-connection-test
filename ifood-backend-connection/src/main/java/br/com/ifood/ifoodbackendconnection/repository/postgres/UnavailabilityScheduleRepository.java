package br.com.ifood.ifoodbackendconnection.repository.postgres;

import br.com.ifood.ifoodbackendconnection.domain.UnavailabilitySchedule;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository(value = "unavailabilitySchedule")
public interface UnavailabilityScheduleRepository extends CrudRepository<UnavailabilitySchedule, Long> {

    @Query(value = "SELECT u FROM UnavailabilitySchedule u " +
            "JOIN u.restaurant r  WHERE r.code = :code")
    List<UnavailabilitySchedule> fetchUnavailabilitySchedule(@Param("code") String restaurantCode);

    @Modifying
    @Transactional
    void deleteByScheduleCode(String scheduleCode);
}