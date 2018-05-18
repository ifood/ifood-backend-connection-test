package br.com.ifood.connection.controller.response.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnavailabilityDTO {

    private Instant dtStarts;

    private Instant dtEnds;
}
