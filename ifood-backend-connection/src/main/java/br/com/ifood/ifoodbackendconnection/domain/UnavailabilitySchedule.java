package br.com.ifood.ifoodbackendconnection.domain;

import br.com.ifood.ifoodbackendconnection.utilities.LocalDateTimeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "status_schedule")
public class UnavailabilitySchedule {

    @Id
    @SequenceGenerator(name="mtt_seq", sequenceName="status_schedule_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=SEQUENCE, generator="mtt_seq")
    private Long id;

    @NotNull
    private String reason;

    @NotNull
    @Column(name = "start_datetime", columnDefinition = "TIMESTAMP")
    @Convert(converter = LocalDateTimeConverter.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startDateTime;

    @NotNull
    @Column(name = "end_datetime", columnDefinition = "TIMESTAMP")
    @Convert(converter = LocalDateTimeConverter.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endDateTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull
    @Column(name = "schedule_code", unique = true)
    private String scheduleCode;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(nullable = false)
    @JsonBackReference
    private Restaurant restaurant;

    public UnavailabilitySchedule(Restaurant restaurant, String reason, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.restaurant = restaurant;
        this.reason = reason;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.scheduleCode = UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnavailabilitySchedule that = (UnavailabilitySchedule) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getReason(), that.getReason()) &&
                Objects.equals(getStartDateTime(), that.getStartDateTime()) &&
                Objects.equals(getEndDateTime(), that.getEndDateTime()) &&
                Objects.equals(getScheduleCode(), that.getScheduleCode()) &&
                Objects.equals(getRestaurant(), that.getRestaurant());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getReason(), getStartDateTime(), getEndDateTime(), getScheduleCode(), getRestaurant());
    }
}