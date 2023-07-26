package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Director {
    private final int id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    public Director(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
