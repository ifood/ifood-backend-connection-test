package com.ifood.ifoodmanagement.service.command;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.repository.ClientKeepAliveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ifood.ifoodmanagement.util.IfoodUtil.isRestaurantOnline;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeepAliveCommandService implements IKeepAliveCommandService {

    private final ClientKeepAliveRepository keepAliveRepository;

    @Override
    public String insertClientKeepAliveLog(ClientKeepAliveLog clientKeepAliveLog) {

        final ClientKeepAliveLog newclientKeepAliveLog = ClientKeepAliveLog
                .builder()
                .restaurantCode(clientKeepAliveLog.getRestaurantCode())
                .available(clientKeepAliveLog.isAvailable())
                .online(isRestaurantOnline(clientKeepAliveLog.isAvailable(), clientKeepAliveLog.getLastModified()))
                .build();

        keepAliveRepository.save(newclientKeepAliveLog);
        log.info(String.format("Inserted KeepAlive log for restaurant [%s]", clientKeepAliveLog.getRestaurantCode()));

        return newclientKeepAliveLog.getId();
    }
}
