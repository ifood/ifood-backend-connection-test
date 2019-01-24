package br.com.ifood.ifoodbackendconnection.repository.postgres;

import br.com.ifood.ifoodbackendconnection.domain.Restaurant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
@Rollback
@ActiveProfiles("integration-test")
public class RestaurantRepositoryIntegrationTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void shouldFindRestaurantByCode() {
        String code = "aaa472a3-0544-4e68-a3eb-3740d42ece7d";

        Restaurant restaurant = new Restaurant(code, "Brew’d Awakening Coffeehaus", null, null);

        restaurantRepository.save(restaurant);

        Optional<Restaurant> restaurantFound = restaurantRepository.findByCode(code);

        assertTrue(restaurantFound.isPresent());
        assertEquals(restaurantFound.get().getCode(), code);
    }
}
