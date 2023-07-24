package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage{

    private JdbcTemplate jdbcTemplate;

    @Override
    public Director getDirectorById(int id) {
        List<Director> directorsQuery = jdbcTemplate.query("SELECT * " +
                "FROM directors d " +
                "WHERE d.id = ?", rowMapperDirector(), id);
        if (directorsQuery.isEmpty()) {
            throw new  DirectorNotFoundException(String.format("Режиссёр c id %s не найден", id));
        }
        return directorsQuery.get(0);
    }

    @Override
    public List<Director> getDirectors() {
        List<Director> directorsQuery = jdbcTemplate.query("SELECT * " +
                "FROM directors d ", rowMapperDirector());
        return directorsQuery;
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", director.getName());
        Integer id = (Integer) insert.executeAndReturnKey(params);
        return getDirectorById(id);
    }

    @Override
    public void deleteDirectorById(Integer id) {
        jdbcTemplate.update("DELETE FROM directors WHERE id = ?", id);
    }

    @Override
    public Director updateDirector(Director director) {
        int update = jdbcTemplate.update(" UPDATE directors SET NAME = ? " +
                        "WHERE id = ?;",
                director.getName(),
                director.getId());
        if (update == 0) {
            throw new  DirectorNotFoundException(String.format("Режиссёр c id %s не найден", director.getId()));
        }
        return getDirectorById(director.getId());
    }

    private RowMapper<Director> rowMapperDirector() {
        return (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name"));
    }
}
