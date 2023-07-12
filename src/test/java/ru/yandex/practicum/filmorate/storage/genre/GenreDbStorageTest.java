package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;
    @Test
    void getGenreById() {
        Genre genre = genreDbStorage.getGenreById(4);
        assertEquals("Триллер", genre.getName());
    }

    @Test
    void getAllGenres() {
        List<Genre> genreList = genreDbStorage.getAllGenres();
        assertEquals(6, genreList.size());
    }
}