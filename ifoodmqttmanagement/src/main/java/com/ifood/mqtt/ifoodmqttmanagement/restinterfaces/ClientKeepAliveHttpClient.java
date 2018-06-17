package com.ifood.mqtt.ifoodmqttmanagement.restinterfaces;

import com.ifood.mqtt.ifoodmqttmanagement.domain.ClientKeepAliveLog;
import feign.Headers;
import feign.RequestLine;

public interface ClientKeepAliveHttpClient {

    @RequestLine("POST /clientKeepAlive")
    @Headers("Content-Type: application/json")
    void create(ClientKeepAliveLog keepAliveLog);
}
