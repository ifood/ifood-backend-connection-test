package br.com.ifood.connection.controller.response;

import br.com.ifood.connection.controller.response.dto.UnavailabilityDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnavailabilityHistoryResponse {

    private List<UnavailabilityDTO> unavailabilities;
}
