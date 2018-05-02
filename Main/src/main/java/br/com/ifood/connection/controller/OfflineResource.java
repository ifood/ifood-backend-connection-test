package br.com.ifood.connection.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import br.com.ifood.connection.controller.response.dto.OfflineRankDTO;
import br.com.ifood.connection.data.entity.RestaurantEntity;
import br.com.ifood.connection.data.repository.RestaurantRepository;
import br.com.ifood.connection.grid.OfflineTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Returns the rank of the ranking based on their offline status")
@RestController
@RequestMapping(value = "/offlines", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OfflineResource {

    private final Ignite ignite;

    private final RestaurantRepository restaurantRepository;

    @ApiOperation(value = "Gets the restaurant rank")
    @GetMapping
    public OfflineRankDTO getOfflineRank() {

        IgniteCompute compute = ignite.compute();

        Iterable<RestaurantEntity> all = restaurantRepository.findAll();

        return new OfflineRankDTO(compute.execute(OfflineTask.class, all));
    }
}
