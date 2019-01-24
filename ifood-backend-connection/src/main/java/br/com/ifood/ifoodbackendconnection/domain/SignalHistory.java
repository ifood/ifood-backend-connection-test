package br.com.ifood.ifoodbackendconnection.domain;

import br.com.ifood.ifoodbackendconnection.utilities.LocalDateTimeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
import java.time.Duration;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "signal_history")
public class SignalHistory implements Comparable<SignalHistory> {

    @Id
    @SequenceGenerator(name="mtt_seq", sequenceName="signal_history_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=SEQUENCE, generator="mtt_seq")
    private Long id;

    @NotNull
    @Column(name = "received_signal", columnDefinition = "TIMESTAMP")
    @Convert(converter = LocalDateTimeConverter.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime receivedSignal;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonBackReference
    private Restaurant restaurant;

   public SignalHistory(Restaurant restaurant, LocalDateTime receivedSignal) {
        this.restaurant = restaurant;
        this.receivedSignal = receivedSignal;
    }

    public Duration calDifferenceBetween(SignalHistory nextSignal) {
        return Duration.between(this.receivedSignal, nextSignal.receivedSignal);
    }

    @Override
    public int compareTo(SignalHistory otherSignalHistory) {
        return this.receivedSignal.compareTo(otherSignalHistory.receivedSignal);
    }
}