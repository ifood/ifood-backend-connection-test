package com.ifood.ifoodmanagement.service.query;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.repository.ClientKeepAliveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeepAliveQueryService implements IKeepAliveQueryService {

    private final ClientKeepAliveRepository keepAliveRepository;

    @Override
    public List<ClientKeepAliveLog> fetchAllByCode(String code) {
        return keepAliveRepository.findByRestaurantCode(code);
    }
}
