package br.com.ifood.connection.data.repository;

import br.com.ifood.connection.data.entity.StatusEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StatusRepository extends CrudRepository<StatusEntity, UUID> {

    /**
     * Select the schedules from the restaurant that have the date/time dtSchedule between.
     *
     * @param dtSchedule the date/time that has to be between the schedule
     */
    @Query("select s from StatusEntity s where s.restaurantId = :restaurantId and s.type = " +
        "br.com.ifood.connection.data.entity.status.StatusType.UNAVAILABLE and " +
        " s.dtInits <= :dtSchedule and s.dtEnds >= :dtSchedule")
    Optional<StatusEntity> findSpecificSchedule(@Param("restaurantId") Long restaurantId,
        @Param("dtSchedule") Instant dtSchedule);

    @Query("select s from StatusEntity s where s.restaurantId = :restaurantId and s.type = " +
        "br.com.ifood.connection.data.entity.status.StatusType.UNAVAILABLE and " +
        " s.dtInits < :dtLimit")
    Page<StatusEntity> findPagedScheduleHistory(@Param("restaurantId") Long restaurantId,
        @Param("dtLimit") Instant dtLimit, Pageable pageable);

    @Query("select s from StatusEntity s where s.restaurantId = :restaurantId and s.type = " +
        "br.com.ifood.connection.data.entity.status.StatusType.UNAVAILABLE and " +
        " (s.dtInits between :dtStart and :dtEnd or s.dtEnds between :dtStart and :dtEnd)")
    Page<StatusEntity> findPagedScheduleHistorySpecificPeriod(@Param("restaurantId") Long
        restaurantId, @Param("dtStart") Instant dtStart, @Param("dtEnd") Instant dtEnd,
        Pageable pageable);

    @Query("select s from StatusEntity s where s.restaurantId = :restaurantId and " +
        "s.dtInits < :dtLimit")
    List<StatusEntity> findOnlineAndUnavailable(@Param("restaurantId") Long restaurantId,
        @Param("dtLimit") Instant dtLimit);

}
