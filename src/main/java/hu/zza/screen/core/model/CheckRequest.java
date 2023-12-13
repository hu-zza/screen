package hu.zza.screen.core.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckRequest {
    @JsonProperty("test_id")
    private final UUID testId;
    private final Object result;

    public CheckRequest(UUID testId, Object result) {
        this.testId = testId;
        this.result = result;
    }

    public UUID getTestId() {
        return testId;
    }

    public Object getResult() {
        return result;
    }
}
