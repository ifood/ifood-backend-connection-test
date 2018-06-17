package com.ifood.mqtt.ifoodmqttmanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    private String loggedIn;
    private String sendKeepAlive;
    private String available;

}
