package com.ifood.ifoodmanagement.domain;

public enum QoSEnum {

    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACTLY_ONCE(2);

    public int getLevel() {
        return level;
    }

    private int level;

    QoSEnum(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "QoS level: [" + level + "]";
    }
}
