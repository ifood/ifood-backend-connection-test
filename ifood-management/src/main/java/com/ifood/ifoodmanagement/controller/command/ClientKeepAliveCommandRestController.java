package com.ifood.ifoodmanagement.controller.command;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.service.command.IKeepAliveCommandService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientKeepAliveCommandRestController {

    private final IKeepAliveCommandService keepAliveCommandService;

    @ApiOperation(value = "Create an Restaurant",response = Restaurant.class, tags = {"restaurant"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header", defaultValue = MediaType.APPLICATION_JSON_VALUE)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = ClientKeepAliveLog.class),
            @ApiResponse(code = 422, message = "Unprocessable Entity"),
    })
    @PostMapping(value = "/clientKeepAlive", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity create(
            @ApiParam(name = "clientKeepAliveLog") @RequestBody ClientKeepAliveLog clientKeepAliveLog) {

        final String keepAliveLogId =
                keepAliveCommandService.insertClientKeepAliveLog(clientKeepAliveLog);
        try {
            return ResponseEntity.created(new URI(keepAliveLogId)).build();
        } catch (URISyntaxException e) {
           return ResponseEntity.unprocessableEntity().build();
        }
    }
}
