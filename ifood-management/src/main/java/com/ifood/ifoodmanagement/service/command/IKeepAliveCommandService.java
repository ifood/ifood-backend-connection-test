package com.ifood.ifoodmanagement.service.command;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;

public interface IKeepAliveCommandService {

    String insertClientKeepAliveLog(ClientKeepAliveLog clientKeepAliveLog);
}
