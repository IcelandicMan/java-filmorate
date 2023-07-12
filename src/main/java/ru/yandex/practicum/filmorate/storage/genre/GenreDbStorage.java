package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Slf4j
@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(int id) {
        log.info("Получение жанра под id {}", id);
        final String sql = "SELECT * FROM genres WHERE id = ?";
        final List<Genre> genre = jdbcTemplate.query(sql, genreRowMapper(), id);
        if (genre.size() != 1) {
            log.error("Жанр под id {} не найден", id);
            throw new GenreNotFoundException(String.format("Жанр под id %s не найден", id));
        }
        return genre.get(0);
    }

    @Override
    public List<Genre> getAllGenres() {
        log.info("Получение списка всех Жанров");
        final String sql = "SELECT * FROM genres ORDER by id";
        final List<Genre> genresList = jdbcTemplate.query(sql, genreRowMapper());
        log.info("Список всех Жанров получен");
        return genresList;
    }

    @Override
    public void load(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "SELECT * FROM genres g, film_genres AS fg " +
                "WHERE fg.genre_id = g.id AND fg.film_id IN (" + inSql + ")";
        jdbcTemplate.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getInt("film_id"));
            film.getGenres().add(makeGenre(rs, 0));// Тут я так и не понял как подставить мой  genreRowMapper(),
            // который возвращает RowMapper<Genre>
        }, films.stream().map(Film::getId).toArray());
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        };
    }

    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("name"));
    }
}
