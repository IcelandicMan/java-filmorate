package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Feed {
    @JsonProperty("eventId")
    private Integer id;
    @NotNull
    @JsonProperty("timestamp")
    private Long time;
    @NotNull
    private Integer userId;
    @NotBlank
    @JsonProperty("eventType")
    private String event;
    @NotBlank
    private String operation;
    @NotNull
    private Integer entityId;

    public Feed(Integer id, Long time, Integer userId, String event, String operation, Integer entityId) {
        this.id = id;
        this.time = time;
        this.userId = userId;
        this.event = event;
        this.operation = operation;
        this.entityId = entityId;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("time", LocalDateTime.now());
        values.put("event", event);
        values.put("user_id", userId);
        values.put("operation", operation);
        values.put("entity_id", entityId);
        return values;
    }
}
