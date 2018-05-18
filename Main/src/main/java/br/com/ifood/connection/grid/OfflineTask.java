package br.com.ifood.connection.grid;

import br.com.ifood.connection.controller.response.dto.RestaurantOfflineSumDTO;
import br.com.ifood.connection.data.entity.RestaurantEntity;
import br.com.ifood.connection.data.entity.StatusEntity;
import br.com.ifood.connection.data.repository.StatusRepository;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import org.apache.ignite.IgniteException;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.resources.SpringApplicationContextResource;
import org.springframework.context.ApplicationContext;

@NoArgsConstructor
public class OfflineTask extends
    ComputeTaskSplitAdapter<Iterable<RestaurantEntity>, List<RestaurantOfflineSumDTO>> {

    @SpringApplicationContextResource
    private ApplicationContext springCtx;

    @Override
    public Collection<? extends ComputeJob> split(int gridSize,
        Iterable<RestaurantEntity> restaurants) {

        List<ComputeJob> jobs = new ArrayList<>();

        StatusRepository statusRepository = (StatusRepository) springCtx
            .getBean("statusRepository");

        OffsetDateTime openingStart = OffsetDateTime.now(ZoneOffset.of("-0300"))
            .truncatedTo(ChronoUnit.HOURS)
            .withHour(10)
            .withOffsetSameInstant(ZoneOffset.UTC);

        OffsetDateTime openingEnd = OffsetDateTime.now(ZoneOffset.of("-0300"))
            .truncatedTo(ChronoUnit.HOURS)
            .withHour(23)
            .withOffsetSameInstant(ZoneOffset.UTC);

        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime dayLimit = now.isBefore(openingEnd) ? now : openingEnd;

        for (RestaurantEntity restaurantEntity : restaurants) {
            jobs.add(new ComputeJobAdapter() {

                @Override
                public RestaurantOfflineSumDTO execute() throws IgniteException {

                    List<StatusEntity> onlineStatus = statusRepository
                        .findOnlineAndUnavailable(restaurantEntity.getId(), dayLimit.toInstant());

                    Long offlineTime = 0L;

                    StatusEntity last = null;

                    for (StatusEntity status : onlineStatus) {

                        if (last == null) {

                            if (status.getDtInits().isAfter(openingStart.toInstant())) {

                                offlineTime += Duration.between(status.getDtInits(), openingStart)
                                    .getSeconds();
                            }
                        } else {

                            if (last.getDtInits().compareTo(status.getDtEnds()) > 0 ||
                                status.getDtInits().compareTo(last.getDtEnds()) > 0) {

                                offlineTime += Duration.between(last.getDtEnds(),
                                    status.getDtInits()).getSeconds();
                            }
                        }

                        last = status;
                    }

                    if (last != null && last.getDtEnds().isBefore(dayLimit.toInstant())) {
                        offlineTime += Duration.between(last.getDtEnds(), dayLimit).getSeconds();
                    } else {
                        offlineTime = Duration.between(openingStart, dayLimit).getSeconds();
                    }

                    return new RestaurantOfflineSumDTO(restaurantEntity.getName(), offlineTime);
                }
            });
        }

        return jobs;
    }

    @Override
    public List<RestaurantOfflineSumDTO> reduce(List<ComputeJobResult> results) {
        ArrayList<RestaurantOfflineSumDTO> result = new ArrayList<>();

        results.stream()
            .map(ComputeJobResult::getData)
            .map(r -> (RestaurantOfflineSumDTO) r)
            .sorted(RestaurantOfflineSumDTO::compare)
            .map(result::add)
            .collect(Collectors.toList());

        return result;
    }
}