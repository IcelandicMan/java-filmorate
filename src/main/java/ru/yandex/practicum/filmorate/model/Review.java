package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
public class Review {
    private int reviewId;
    @NotNull
    @JsonProperty("isPositive")
    private Boolean isPositive;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
    @NotNull
    private String content;
    private int useful;

    public Review(int id, Integer userId, Integer filmId, Boolean isPositive, String content) {
        this.reviewId = id;
        this.userId = userId;
        this.filmId = filmId;
        this.isPositive = isPositive;
        this.content = content;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        return values;
    }
}
