package br.com.ifood.connection.controller.response;

import br.com.ifood.connection.controller.response.dto.UnavailabilityDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

@Data
@AllArgsConstructor
public class UnavailabilityHistoryResponse extends ResourceSupport {

    private List<UnavailabilityDTO> unavailabilities;
}
