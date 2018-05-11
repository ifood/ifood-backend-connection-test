package br.com.ifood.connection.data.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.ifood.connection.data.entity.StatusEntity;

public interface StatusRepository extends CrudRepository<StatusEntity, UUID> {

    /**
     * Select the schedules from the restaurant that have the date/time dtSchedule between.
     * 
     * @param restaurantId
     * @param dtSchedule
     *            the date/time that has to be between the schedule
     * @return
     */
    @Query("select s from StatusEntity s where s.restaurantId = :restaurantId and s.type = " +
            "br.com.ifood.connection.data.entity.status.StatusType.UNAVAILABLE and " +
            " s.dtInits <= :dtSchedule and s.dtEnds >= :dtSchedule")
    Optional<StatusEntity> findSpecificSchedule(@Param("restaurantId") Long restaurantId,
            @Param("dtSchedule") Instant dtSchedule);

    /**
     * Select the schedules from the restaurant that start before the date/time dtLimit.
     *
     * @param restaurantId
     * @param dtLimit
     *            the start date/time limit
     * @return
     */
    @Query("select s from StatusEntity s where s.restaurantId = :restaurantId and s.type = " +
            "br.com.ifood.connection.data.entity.status.StatusType.UNAVAILABLE and " +
            " s.dtInits < :dtLimit")
    List<StatusEntity> findScheduleHistory(@Param("restaurantId") Long restaurantId,
            @Param("dtLimit") Instant dtLimit);

    @Query("select s from StatusEntity s where s.restaurantId = :restaurantId and " +
            "s.dtInits < :dtLimit")
    List<StatusEntity> findOnlineAndUnavailable(@Param("restaurantId") Long restaurantId,
            @Param("dtLimit") Instant dtLimit);

}
