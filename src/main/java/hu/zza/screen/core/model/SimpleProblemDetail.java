package hu.zza.screen.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleProblemDetail {
    private final int status;
    private final String detail;

    SimpleProblemDetail(
            @JsonProperty("status") int status,
            @JsonProperty("detail") String detail
    ) {
        this.status = status;
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return String.format("HTTP %d : %s", status, detail);
    }
}
