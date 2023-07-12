package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpaById(int mpaId) {
        log.info("Получение MPA под id {}", mpaId);
        final String sql = "Select * FROM mpa WHERE id = ?";
        List<Mpa> mpa = jdbcTemplate.query(sql, mpaRowMapper(), mpaId);
        if (mpa.size() != 1) {
            log.error("MPA Рейтинг под id {} не найден", mpaId);
            throw new MpaNotFoundException(String.format("MPA Рейтинг под id %s не найден", mpaId)); // Выбрасываем новую ошибку
        }
        return mpa.get(0);
    }

    @Override
    public List<Mpa> getAllMpa() {
        log.info("Получение списка всех MPA Рейтингов");
        String sql = "SELECT * FROM mpa ORDER by id";
        List<Mpa> mpaList = jdbcTemplate.query(sql, mpaRowMapper());
        log.info("Список всех пользователей получен");
        return mpaList;
    }

    private RowMapper<Mpa> mpaRowMapper() {
        return (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        };
    }
}
