package br.com.ifood.connection.mqtt.message;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
/**
 * The schedule of unavailability message.<br/>
 * dtStarts is the UTC date/time the schedule of unavailability will start.<br/>
 * dtEnds is the UTC date/time the schedule of unavailability will end.<br/>
 */
public class ScheduleMessage extends AbstractRestaurantMessage {

    private Instant dtStarts;

    private Instant dtEnds;

    public ScheduleMessage(Long restaurantId, Instant dtStarts, Instant dtEnds) {
        super(restaurantId);
        this.dtStarts = dtStarts;
        this.dtEnds = dtEnds;
    }
}
