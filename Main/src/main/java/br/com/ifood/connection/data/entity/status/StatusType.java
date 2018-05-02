package br.com.ifood.connection.data.entity.status;

public enum StatusType {
    ONLINE(20), UNAVAILABLE(30);

    private int type;

    StatusType(int type) {
        this.type = type;
    }

    public final int type() {
        return type;
    }

    public static final StatusType valueOf(int type) {
        for (StatusType statusType : values()) {
            if (statusType.type() == type) {
                return statusType;
            }
        }

        throw new IllegalArgumentException(String.format("The number %d is invalid for this enum.",
            type));
    }
}
