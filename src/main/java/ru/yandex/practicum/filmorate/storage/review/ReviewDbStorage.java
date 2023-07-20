package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Component
@AllArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private JdbcTemplate jdbcTemplate;

    public Review createReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        review.setReviewId((Integer) simpleJdbcInsert.executeAndReturnKey(review.toMap()));
        jdbcTemplate.update("UPDATE reviews SET is_positive = ? WHERE id = ?", review.getIsPositive(), review.getReviewId());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        final String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return getReview(review.getReviewId());
    }

    @Override
    public void deleteReview(int reviewId) {
        jdbcTemplate.update("DELETE from reviews WHERE id = ?", reviewId);
    }

    @Override
    public Review getReview(int reviewId) {
        final String sql = "SELECT * " +
                "FROM reviews " +
                "WHERE id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, reviewRowMapper(), reviewId);
        if (reviews.size() != 1) {
            throw new ReviewNotFoundException("Отзыв с таким id не найден: " + reviewId);
        }
        return reviews.get(0);
    }

    @Override
    public List<Review> getAllReviews(int count) {
        return jdbcTemplate.query("SELECT * FROM reviews LIMIT ?", reviewRowMapper(), count);
    }

    @Override
    public List<Review> getReviewsByFilm(Integer filmId, Integer count) {
        final String sql = "SELECT * " +
                "FROM reviews " +
                "WHERE film_id = ? " +
                "LIMIT ?";
        List<Review> reviews = jdbcTemplate.query(sql, reviewRowMapper(), filmId, count);
        return reviews;
    }

    @Override
    public void addReviewLike(int reviewId, int userId) {
        jdbcTemplate.update("INSERT into review_likes(review_id, user_id) values(?, ?)", reviewId, userId);
    }

    @Override
    public void addReviewDislike(int reviewId, int userId) {
        jdbcTemplate.update("INSERT into review_dislikes(review_id, user_id) values(?, ?)", reviewId, userId);
    }

    @Override
    public void deleteReviewLike(int reviewId, int userId) {
        jdbcTemplate.update("DELETE from review_likes WHERE review_id = ? AND user_id = ?", reviewId, userId);
    }

    @Override
    public void deleteReviewDislike(int reviewId, int userId) {
        jdbcTemplate.update("DELETE from review_dislikes WHERE review_id = ? AND user_id = ?", reviewId, userId);
    }

    @Override
    public void loadUseful(List<Review> reviews) {
        String inSql = String.join(",", Collections.nCopies(reviews.size(), "?"));
        final Map<Integer, Review> reviewMap = reviews.stream().collect(Collectors.toMap(Review::getReviewId, identity()));
        final String sql = "SELECT r.id id, (COUNT(rl.user_id) - COUNT(rd.user_id)) useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON rl.review_id = r.id " +
                "LEFT JOIN review_dislikes rd ON rd.review_id = r.id " +
                "WHERE r.id in (" + inSql + ") " +
                "GROUP BY id";
        jdbcTemplate.query(sql, (rs) -> {
            final Review review = reviewMap.get(rs.getInt("id"));
            review.setUseful(rs.getInt("useful"));
        }, reviews.stream().map(Review::getReviewId).toArray());
    }

    private RowMapper<Review> reviewRowMapper() {
        return (rs, rowNum) -> new Review(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("film_id"),
                rs.getBoolean("is_positive"),
                rs.getString("content")
        );
    }
}
