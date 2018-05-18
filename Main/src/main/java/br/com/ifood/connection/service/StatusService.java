package br.com.ifood.connection.service;

import br.com.ifood.connection.controller.response.OnlineStatusResponse;
import org.springframework.stereotype.Service;

@Service
public interface StatusService {

    OnlineStatusResponse getOnlineStatus(String ids);
}
