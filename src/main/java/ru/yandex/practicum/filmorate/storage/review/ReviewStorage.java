package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int reviewId);

    Review getReview(int reviewId);

    List<Review> getAllReviews(int count);

    List<Review> getReviewsByFilm(Integer filmId, Integer count);

    void addReviewLike(int reviewId, int userId);

    void addReviewDislike(int reviewId, int userId);

    void deleteReviewLike(int reviewId, int userId);

    void deleteReviewDislike(int reviewId, int userId);

    void loadUseful(List<Review> reviews);

}
