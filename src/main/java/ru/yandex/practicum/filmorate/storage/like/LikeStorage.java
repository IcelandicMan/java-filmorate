package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {
    void addLike(int filmId, int userId);

    void updateRate(int filmId);

    void deleteLike(int filmId, int userId);

    List<Film> getFilmsByLikes(int count);
}
