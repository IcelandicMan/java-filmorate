package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director getDirectorById(int id);

    List<Director> getDirectors();

    Director createDirector(Director director);

    void deleteDirectorById(Integer id);

    Director updateDirector(Director director);
}
