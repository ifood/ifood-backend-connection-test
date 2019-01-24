package br.com.ifood.ifoodbackendconnection.domain;

import br.com.ifood.ifoodbackendconnection.controller.unavailability.response.UnavailabilityScheduleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionStatus {

    private String restaurantCode;
    private boolean connected;
    private List<UnavailabilityScheduleResponse> unavailabilitySchedule;
}