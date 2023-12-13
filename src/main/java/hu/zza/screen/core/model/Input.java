package hu.zza.screen.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Input {
    private final Class<?> type;
    private final String value;

    Input(
            @JsonProperty("type") String type,
            @JsonProperty("value") String value
    ) throws ClassNotFoundException {
        this.type = Class.forName(type);
        this.value = value;
    }

    public Class<?> getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
