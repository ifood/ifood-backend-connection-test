package com.ifood.rest.unavailability;

import com.ifood.DateFormatter;
import com.ifood.domain.UnavailabilitySchedule;
import com.ifood.service.unavailability.schedule.UnavailabilityScheduleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class UnavailabilityScheduleController {

    @Autowired
    private UnavailabilityScheduleService unavailabilityScheduleService;

    @ApiOperation(value = "Fetch the repository repository for a given period.")
    @RequestMapping(value = "/connection/schedule/unavailability/{restaurant_code}/{start_date}/{end_date}", method = RequestMethod.GET)
    public List<UnavailabilitySchedule> fetchUnavailabilitySchedule(
            @ApiParam("Restaurant unique code") @PathVariable("restaurant_code") String restaurantCode,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("start_date") String startDate,
            @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)") @PathVariable("end_date") String endDate) {
        return unavailabilityScheduleService.fetchUnavailabilitySchedule(restaurantCode, new DateFormatter().format(startDate), new DateFormatter().format(endDate));
    }

    @ApiOperation(value = "Insert an repository repository.")
    @RequestMapping(value = "/connection/schedule/unavailability", method = RequestMethod.POST)
    public String insertUnavailabilitySchedule(
            @RequestBody UnavailabilityScheduleInsertFeatureRequest unavailabilityScheduleFeature) {

        return unavailabilityScheduleService.insertSchedule(
                unavailabilityScheduleFeature.getRestaurantCode(),
                unavailabilityScheduleFeature.getUnavailabilityReason(),
                unavailabilityScheduleFeature.getScheduleStart(),
                unavailabilityScheduleFeature.getScheduleEnd());

    }

    @ApiOperation(value = "Delete an repository schedule.")
    @RequestMapping(value = "/connection/schedule/unavailability/{schedule_code}", method = RequestMethod.POST)
    public ResponseEntity deleteUnavailabilitySchedule(@ApiParam("Schedule unique code") @PathVariable("schedule_code") String scheduleCode) {
        unavailabilityScheduleService.deleteSchedule(scheduleCode);
        return ResponseEntity.ok().build();
    }

}
