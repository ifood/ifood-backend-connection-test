package br.com.ifood.connection.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import br.com.ifood.connection.controller.exception.handler.ErrorMessage;
import br.com.ifood.connection.controller.response.dto.OfflineRankDTO;
import br.com.ifood.connection.data.entity.RestaurantEntity;
import br.com.ifood.connection.data.repository.RestaurantRepository;
import br.com.ifood.connection.grid.OfflineTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Api(value = "Offline report controller", tags = {"Offline report"})
@RequestMapping(value = "/offlines", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@RestController
@Validated
public class OfflineController {

    private final Ignite ignite;

    private final RestaurantRepository restaurantRepository;

    @ApiOperation(value = "Restaurant offline rank", //
        notes = "Returns the list of all restaurants ordered by the amount of time they spent "
            + "offline.<br/>")
    @GetMapping
    public OfflineRankDTO getOfflineRank() {

        IgniteCompute compute = ignite.compute();

        Iterable<RestaurantEntity> all = restaurantRepository.findAll();

        return new OfflineRankDTO(compute.execute(OfflineTask.class, all));
    }

    @ApiOperation(value = "Restaurant offline rank", //
        notes = "Returns the list of all restaurants ordered by the amount of time they spent "
            + "offline on a specific day.<br/>")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request", response = ErrorMessage.class)
    })
    @GetMapping(params = {"year", "month", "day"})
    public OfflineRankDTO getOfflineRank(@RequestParam Integer year, @RequestParam Integer month,
        @RequestParam Integer day) {

        throw new NotImplementedException();
    }
}
