package br.com.ifood.ifoodbackendconnection.controller.unavailability.request;

import br.com.ifood.ifoodbackendconnection.domain.UnavailabilityReason;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnavailabilityScheduleRequest {

    @ApiParam("Restaurant unique code")
    private String restaurantCode;

    @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)")
    private String startDateTime;

    @ApiParam("(Format: yyyy-MM-dd'T'HH:mm)")
    private String endDateTime;

    private UnavailabilityReason unavailabilityReason;
}