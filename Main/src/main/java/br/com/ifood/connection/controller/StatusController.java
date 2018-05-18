package br.com.ifood.connection.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import br.com.ifood.connection.controller.exception.handler.ErrorMessage;
import br.com.ifood.connection.controller.response.OnlineStatusResponse;
import br.com.ifood.connection.controller.validator.annotation.IdList;
import br.com.ifood.connection.service.StatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Status controller", tags = {"Status"})
@RequestMapping(value = "/status", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@RestController
@Validated
public class StatusController {

    private final StatusService statusService;

    @ApiOperation(value = "Online status of the restaurants", //
        notes =
            "Returns the current status [true/false] for the restaurants based on the ids passed as parameter"
                + ".<br/> It will return true for the restaurant that has sent a keepalive on the last X minute "
                + "(being X the timeout to consider the restaurant offline) and does not have a unavailability "
                + "schedule for now.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request", response = ErrorMessage.class)
    })
    @GetMapping
    public OnlineStatusResponse getOnlineStatus(
        @ApiParam(value = "The list of restaurants ids separated by comma (',')", required = true)
        //
        @RequestParam("ids") @IdList String ids) {

        return statusService.getOnlineStatus(ids);
    }
}
