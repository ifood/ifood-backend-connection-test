package br.com.ifood.connection.service.impl;

import static br.com.ifood.connection.controller.response.converter.UnavailabilityConverter.adjustDtEnd;
import static br.com.ifood.connection.controller.response.converter.UnavailabilityConverter.adjustDtStartAndEnd;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import br.com.ifood.connection.controller.UnavailabilityController;
import br.com.ifood.connection.controller.pagination.PagedResourceFactory;
import br.com.ifood.connection.controller.response.UnavailabilityHistoryResponse;
import br.com.ifood.connection.controller.response.converter.UnavailabilityConverter;
import br.com.ifood.connection.data.entity.StatusEntity;
import br.com.ifood.connection.data.repository.StatusRepository;
import br.com.ifood.connection.service.UnavailabilityService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class UnavailabilityServiceImpl implements UnavailabilityService {

    private final StatusRepository statusRepository;

    private final PagedResourceFactory<UnavailabilityHistoryResponse> pagedResourceFactory;

    private final UnavailabilityConverter converter;

    @Override
    public UnavailabilityHistoryResponse getPagedUnavailabilityHistory(Long restaurantId,
        Pageable pageable) {

        Instant dtLimit = Instant.now();

        Page<StatusEntity> statusEntityPage = statusRepository
            .findPagedScheduleHistory(restaurantId, dtLimit, pageable);

        UnavailabilityHistoryResponse response = converter
            .convert(statusEntityPage, adjustDtEnd(dtLimit));

        return pagedResourceFactory
            .createPagedResource()
            .of(statusEntityPage)
            .on(response)
            .based(methodOn(UnavailabilityController.class)
                .getPagedUnavailabilityHistory(restaurantId, Pageable.unpaged())
            ).build();
    }

    @Override
    public UnavailabilityHistoryResponse getPagedUnavailabilityHistorySpecificPeriod(
        Long restaurantId, Instant dtStart, Instant dtEnd, Pageable pageable) {

        Page<StatusEntity> statusEntityPage = statusRepository
            .findPagedScheduleHistorySpecificPeriod(restaurantId, dtStart, dtEnd, pageable);

        UnavailabilityHistoryResponse response = converter
            .convert(statusEntityPage, adjustDtStartAndEnd(dtStart, dtEnd));

        return pagedResourceFactory
            .createPagedResource()
            .of(statusEntityPage)
            .on(response)
            .based(methodOn(UnavailabilityController.class)
                .getPagedUnavailabilityHistorySpecificPeriod(restaurantId, dtStart, dtEnd,
                    pageable)
            ).build();
    }

    @Override
    public void deleteSchedule(String uuid) {

        statusRepository.deleteById(UUID.fromString(uuid));

    }
}
