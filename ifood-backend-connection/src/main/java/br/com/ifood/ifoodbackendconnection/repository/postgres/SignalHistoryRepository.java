package br.com.ifood.ifoodbackendconnection.repository.postgres;

import br.com.ifood.ifoodbackendconnection.domain.SignalHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "signalHistory")
public interface SignalHistoryRepository extends CrudRepository<SignalHistory, Long> {

    @Query(value = "SELECT c FROM SignalHistory c " +
            "JOIN c.restaurant r  WHERE r.code = :code")
    List<SignalHistory> findSignalHistory(@Param("code") String restaurantCode);
}