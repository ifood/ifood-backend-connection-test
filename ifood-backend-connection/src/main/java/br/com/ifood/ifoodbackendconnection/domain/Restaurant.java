package br.com.ifood.ifoodbackendconnection.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @SequenceGenerator(name = "pk_seq", sequenceName = "restaurant_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=SEQUENCE, generator="pk_seq")
    private Long id;

    @NotNull
    @Column(unique = true)
    private String code;

    @NotNull
    private String name;

    @OneToMany(mappedBy="restaurant", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<SignalHistory> signalHistory;

    @OneToMany(mappedBy="restaurant", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<UnavailabilitySchedule> unavailabilitySchedules;

    public Restaurant(String code) {
        this.code = code;
    }

    public Restaurant(String code, String name, Set<SignalHistory> signalHistory, Set<UnavailabilitySchedule> unavailabilitySchedules) {
        this.code = code;
        this.name = name;
        this.signalHistory = signalHistory;
        this.unavailabilitySchedules = unavailabilitySchedules;
    }
}