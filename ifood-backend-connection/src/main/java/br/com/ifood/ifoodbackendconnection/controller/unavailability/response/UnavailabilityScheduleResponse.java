package br.com.ifood.ifoodbackendconnection.controller.unavailability.response;

import br.com.ifood.ifoodbackendconnection.domain.UnavailabilitySchedule;
import br.com.ifood.ifoodbackendconnection.domain.UnavailabilityReason;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@JsonRootName(value = "unavailabilitySchedule")
public class UnavailabilityScheduleResponse {

    private String code;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endDateTime;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UnavailabilityReason unavailabilityReason;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String descriptionOfReason;

    public UnavailabilityScheduleResponse(UnavailabilitySchedule unavailabilitySchedule) {
        this.code = unavailabilitySchedule.getScheduleCode();
        this.unavailabilityReason = UnavailabilityReason.valueOf(unavailabilitySchedule.getReason());
        this.descriptionOfReason = UnavailabilityReason.valueOf(unavailabilitySchedule.getReason()).getDescription();
        this.startDateTime = unavailabilitySchedule.getStartDateTime();
        this.endDateTime = unavailabilitySchedule.getEndDateTime();
    }

    @JsonIgnore
    public boolean isUnavailable(LocalDateTime localDateTime) {
        return localDateTime.isAfter(startDateTime) && localDateTime.isBefore(endDateTime);
    }
}
