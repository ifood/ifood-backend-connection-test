package com.ifood.rest.report;

import com.ifood.DateFormatter;
import com.ifood.domain.ConnectionReport;
import com.ifood.service.report.ReportService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ReportController {

    @Autowired
    private ReportService reportService;

    @RequestMapping(value="/connection/report/{restaurant_code}/{start_date}/{end_date}", method = RequestMethod.GET)
    public ResponseEntity fetchReport(
            @ApiParam("Restaurant unique code") @PathVariable("restaurant_code") String restaurantCode,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("start_date") String startDate,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("end_date") String endDate) {
        ConnectionReport report = reportService.fetchReport(
                restaurantCode,
                new DateFormatter().format(startDate),
                new DateFormatter().format(endDate)
        );

        return ResponseEntity.ok(report);
    }
}
