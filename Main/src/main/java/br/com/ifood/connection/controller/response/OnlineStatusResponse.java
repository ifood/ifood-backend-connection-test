package br.com.ifood.connection.controller.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnlineStatusResponse {

    private List<Boolean> status;
}
