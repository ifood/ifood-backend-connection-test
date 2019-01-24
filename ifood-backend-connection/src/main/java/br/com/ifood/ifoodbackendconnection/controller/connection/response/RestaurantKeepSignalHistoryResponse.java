package br.com.ifood.ifoodbackendconnection.controller.connection.response;


import br.com.ifood.ifoodbackendconnection.domain.SignalHistory;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("restaurantKeepSignalHistory")
public class RestaurantKeepSignalHistoryResponse {

    private String code;
    private List<SignalHistory> signalHistory;
}
