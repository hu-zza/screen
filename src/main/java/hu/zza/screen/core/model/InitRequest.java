package hu.zza.screen.core.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InitRequest {
    @JsonProperty("task_id")
    private final UUID taskId;

    public InitRequest(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getTaskId() {
        return taskId;
    }
}
