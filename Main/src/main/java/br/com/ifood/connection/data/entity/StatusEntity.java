package br.com.ifood.connection.data.entity;

import br.com.ifood.connection.data.entity.status.StatusType;
import br.com.ifood.connection.data.entity.status.StatusTypeConverter;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Builder
@Entity
@Table(name = "status")
@NoArgsConstructor
@AllArgsConstructor
public class StatusEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id_status")
    private UUID id;

    @Column(name = "id_restaurant")
    private Long restaurantId;

    @Column(name = "dt_inits")
    private Instant dtInits;

    @Column(name = "dt_ends")
    private Instant dtEnds;

    @Convert(converter = StatusTypeConverter.class)
    private StatusType type;

}
