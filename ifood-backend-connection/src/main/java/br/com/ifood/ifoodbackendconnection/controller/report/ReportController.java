package br.com.ifood.ifoodbackendconnection.controller.report;

import br.com.ifood.ifoodbackendconnection.domain.ErrorList;
import br.com.ifood.ifoodbackendconnection.domain.RestaurantStatus;
import br.com.ifood.ifoodbackendconnection.service.ReportService;
import br.com.ifood.ifoodbackendconnection.utilities.DateFormatter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@Api(tags = "Report", description = "Ifood Report")
class ReportController {

    private static final String BASE_PATH = "/api/v1/";
    private ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Restaurant Status loaded", response = RestaurantStatus.class),
            @ApiResponse(code = 404, message = "There were issues with the request", response = ErrorList.class)
    })
    @RequestMapping(value=BASE_PATH+"backend/connection/report/restaurant/{code}/{start_date}/{end_date}", method = RequestMethod.GET)
    public ResponseEntity fetchReport(
            @ApiParam("Single restaurant code") @PathVariable("code") String restaurantCode,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("start_date") String startDate,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("end_date") String endDate) {

        return reportService.fetchReport(
                    restaurantCode,
                    DateFormatter.format(startDate),
                    DateFormatter.format(endDate))
                .applyAndGet(errors -> status(NOT_FOUND).body(errors), restaurantStatus -> status(OK).body(restaurantStatus));
    }
}