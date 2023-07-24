package ru.yandex.practicum.filmorate.storage.feed;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

@Component
@AllArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private JdbcTemplate jdbcTemplate;

    @Override
    public Feed addFeed(Feed feed) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feeds")
                .usingGeneratedKeyColumns("id");
        feed.setId((Integer) simpleJdbcInsert.executeAndReturnKey(feed.toMap()));
        return feed;
    }

    @Override
    public List<Feed> getFeeds(int userId) {
        return jdbcTemplate.query("SELECT * FROM feeds WHERE user_id = ? ORDER BY time", feedRowMapper(), userId);
    }

    private RowMapper<Feed> feedRowMapper() {
        return (rs, rowNum) -> new Feed(
                rs.getInt("id"),
                rs.getTimestamp("time").toInstant().toEpochMilli(),
                rs.getInt("user_id"),
                rs.getString("event"),
                rs.getString("operation"),
                rs.getInt("entity_id")
        );
    }
}
