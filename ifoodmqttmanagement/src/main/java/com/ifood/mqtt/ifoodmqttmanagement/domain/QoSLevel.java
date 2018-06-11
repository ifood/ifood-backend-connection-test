package com.ifood.mqtt.ifoodmqttmanagement.domain;

public enum QoSLevel {

    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACTLY_ONCE(2);

    public int getLevel() {
        return level;
    }

    private int level;

    QoSLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "QoS level: [" + level + "]";
    }
}
