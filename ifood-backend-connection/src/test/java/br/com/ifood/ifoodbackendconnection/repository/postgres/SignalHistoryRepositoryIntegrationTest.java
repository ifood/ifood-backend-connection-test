package br.com.ifood.ifoodbackendconnection.repository.postgres;

import br.com.ifood.ifoodbackendconnection.domain.Restaurant;
import br.com.ifood.ifoodbackendconnection.domain.SignalHistory;
import br.com.ifood.ifoodbackendconnection.domain.SignalHistoryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
@Rollback
@ActiveProfiles("integration-test")
public class SignalHistoryRepositoryIntegrationTest {

    @Autowired
    private SignalHistoryRepository signalHistoryRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void shouldFindSignalHistory() {
        String code = "aaa472a3-0544-4e68-a3eb-3740d42ece7d";

        Restaurant restaurant = new Restaurant(code, "Brew’d Awakening Coffeehaus", null, null);

        Restaurant restaurantCreated = restaurantRepository.save(restaurant);

        SignalHistory signalHistory = new SignalHistoryBuilder()
                .withReceivedSignal(LocalDateTime.now())
                .withRestaurant(restaurantCreated)
                .build();
        signalHistoryRepository.save(signalHistory);

        List<SignalHistory> signalHistoryReturned = signalHistoryRepository.findSignalHistory(code);

        assertEquals(1, signalHistoryReturned.size());
    }
}
