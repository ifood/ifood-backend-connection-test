package com.ifood.ifoodmanagement.controller.query;

import com.ifood.ifoodmanagement.domain.ReportItem;
import com.ifood.ifoodmanagement.error.ApiException;
import com.ifood.ifoodmanagement.service.query.ReportInsightsService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportInsightsController {

    private final ReportInsightsService reportInsightsService;

    @ApiOperation(value = "Fetch insights from a specific restaurant", response = ReportItem.class, responseContainer = "List", tags = {"restaurantStatusLog"})
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Content-Type",required = true, dataType = "string", paramType = "header", defaultValue = MediaType.APPLICATION_JSON_VALUE),
    })
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok", response = ReportItem.class)})
    @GetMapping(value = "/reports/{code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity fetchRestaurantReportInsights(
            @ApiParam(name = "code")
            @PathVariable String code) {

        try {
            final ReportItem reportInsights = reportInsightsService.fetchRestaurantReportInsights(code);
            return ResponseEntity.ok(reportInsights);
        } catch (ApiException ex){
            return ResponseEntity.notFound().build();
        }
    }
}
