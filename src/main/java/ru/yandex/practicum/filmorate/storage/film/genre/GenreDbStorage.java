package ru.yandex.practicum.filmorate.storage.film.genre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(int id) {
        log.info("Получение жанра под id {}", id);
        Genre genre = null;
        String sqlId = "Select genre_id FROM genres WHERE genre_id = ?";
        List<Integer> genreIdList = jdbcTemplate.query(sqlId, new Object[]{id}, (rs, rowNum) -> rs.getInt("genre_id"));

        if (genreIdList.isEmpty()) {
            log.error("Жанр под id {} не найден", id);
            throw new GenreNotFoundException(String.format("Жанр под id %s не найден", id));
        } else if (genreIdList.get(0) == id) {
            String sql = "SELECT * FROM genres WHERE genre_id  = ?";
            genre = jdbcTemplate.queryForObject(sql, genreRowMapper(), id);
            log.info("Жанр под id {} получен: {}", id, genre);
        }
        return genre;
    }

    @Override
    public List<Genre> getAllGenres() {
        log.info("Получение списка всех Жанров");
        String sql = "SELECT * FROM genres ORDER by genre_id";
        List<Genre> genresList = jdbcTemplate.query(sql, genreRowMapper());
        log.info("Список всех Жанров получен");
        return genresList;
    }

    @Override
    public void deleteGenresByFilmId (int id){
        log.info("Удаление всех Жанров фильма под {} id", id);
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql,id);
        log.info("Все Жанры фильма под {} id удалены", id);
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        };
    }
}
