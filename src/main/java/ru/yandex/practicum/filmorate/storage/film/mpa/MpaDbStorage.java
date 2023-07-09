package ru.yandex.practicum.filmorate.storage.film.mpa;

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
        Mpa mpa = null;
        String sqlId = "Select mpa_id FROM mpa WHERE mpa_id = ?";
        List<Integer> mpaIdList = jdbcTemplate.query(sqlId, new Object[]{mpaId}, (rs, rowNum) -> rs.getInt("mpa_id"));
        if (mpaIdList.isEmpty()) {
            log.error("MPA Рейтинг под id {} не найден", mpaId);
            throw new MpaNotFoundException(String.format("MPA Рейтинг под id %s не найден", mpaId)); // Выбрасываем новую ошибку
        } else if (mpaIdList.get(0) == mpaId) {
            String sql = "SELECT * FROM mpa WHERE mpa_id  = ?";
            mpa = jdbcTemplate.queryForObject(sql, mpaRowMapper(), mpaId);
            log.info("MPA Рейтинг с id {} получен: {}", mpaId, mpa);
        }
        return mpa;
    }

    @Override
    public List<Mpa> getAllMpa() {
        log.info("Получение списка всех MPA Рейтингов");
        String sql = "SELECT * FROM mpa ORDER by mpa_id";
        List<Mpa> mpaList = jdbcTemplate.query(sql, mpaRowMapper());
        log.info("Список всех пользователей получен");
        return mpaList;
    }

    private RowMapper<Mpa> mpaRowMapper() {
        return (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            return mpa;
        };
    }
}
