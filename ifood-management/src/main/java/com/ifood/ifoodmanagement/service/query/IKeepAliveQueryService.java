package com.ifood.ifoodmanagement.service.query;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;

import java.util.List;

public interface IKeepAliveQueryService {

    List<ClientKeepAliveLog> fetchAllByCode(String code);
}
