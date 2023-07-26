package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Slf4j
@Component
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Director getDirectorById(int id) {
        log.info("Получение режиссёра с id {}", id);
        List<Director> directorsQuery = jdbcTemplate.query("SELECT * " +
                "FROM directors d " +
                "WHERE d.id = ? ", rowMapperDirector(), id);
        if (directorsQuery.isEmpty()) {
            log.info("Режиссёр с id {} не найден", id);
            throw new  DirectorNotFoundException(String.format("Режиссёр c id %s не найден", id));
        }
        log.info("Режиссёр с id {} получен: {}", id, directorsQuery.get(0));
        return directorsQuery.get(0);
    }

    @Override
    public List<Director> getDirectors() {
        log.info("Получение списка всех режиссёров");
        List<Director> directorsQuery = jdbcTemplate.query("SELECT * " +
                "FROM directors d " +
            "GROUP BY d.id", rowMapperDirector());
        log.info("Список всех режиссёров получен");
        return directorsQuery;
    }

    @Override
    public Director createDirector(Director director) {
        log.info("Создание режиссёра: {}", director);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", director.getName());
        Integer id = (Integer) insert.executeAndReturnKey(params);
        log.info("Создан режиссёр с id: {}", id);
        return getDirectorById(id);
    }

    @Override
    public void deleteDirectorById(Integer id) {
        log.info("Удаление режиссёра с id {}", id);
        jdbcTemplate.update("DELETE FROM directors WHERE id = ?", id);
        log.info("Режиссёр с id {} удалён", id);
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("Обновление режиссёра с id {}", director.getId());
        int update = jdbcTemplate.update(" UPDATE directors SET NAME = ? " +
                        "WHERE id = ?;",
                director.getName(),
                director.getId());
        if (update == 0) {
            throw new  DirectorNotFoundException(String.format("Режиссёр c id %s не найден", director.getId()));
        }
        log.info("Режиссёр с id {} обновлён: {}", director.getId(), director);
        return getDirectorById(director.getId());
    }

    @Override
    public void load(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "SELECT d.id id, " +
            "d.name name, " +
            "fd.film_id " +
            "FROM directors d, film_directors AS fd " +
            "WHERE fd.director_id = d.id AND fd.film_id IN (" + inSql + ")";
        jdbcTemplate.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getInt("film_id"));
            film.getDirectors().add(makeDirector(rs));
        }, films.stream().map(Film::getId).toArray());
    }

    @SneakyThrows
    private Director makeDirector(ResultSet rs) {
        return new Director(rs.getInt("id"),
            rs.getString("name"));
    }

    private RowMapper<Director> rowMapperDirector() {
        return (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name"));
    }
}
