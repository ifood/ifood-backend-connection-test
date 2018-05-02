package br.com.ifood.connection.data.repository;

import br.com.ifood.connection.data.entity.StatusEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StatusRepository extends CrudRepository<StatusEntity, UUID> {

    @Query("select s from StatusEntity s where s.restaurantId = :restaurantId and s.type = " +
        "br.com.ifood.connection.data.entity.status.StatusType.UNAVAILABLE and " +
        " s.dtInits <= :dtSchedule and s.dtEnds >= :dtSchedule")
    Optional<StatusEntity> findSpecificSchedule(@Param("restaurantId") Long restaurantId,
        @Param("dtSchedule") Instant dtSchedule);

    @Query("select s from StatusEntity s where s.restaurantId = :restaurantId and s.type = " +
        "br.com.ifood.connection.data.entity.status.StatusType.UNAVAILABLE and " +
        " s.dtInits < :dtLimit")
    List<StatusEntity> findUnavailabilityHistory(@Param("restaurantId") Long restaurantId,
        @Param("dtLimit") Instant dtLimit);

    @Query("select s from StatusEntity s where s.restaurantId = :restaurantId and " +
        "s.dtInits < :dtLimit")
    List<StatusEntity> findOnlineAndUnavailable(@Param("restaurantId") Long
        restaurantId, @Param("dtLimit") Instant dtLimit);

}
