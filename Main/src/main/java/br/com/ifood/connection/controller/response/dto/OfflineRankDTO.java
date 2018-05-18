package br.com.ifood.connection.controller.response.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfflineRankDTO {

    private List<RestaurantOfflineSumDTO> offlineSums;
}
