package ru.yandex.practicum.filmorate.service.review;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public ReviewService(ReviewStorage reviewStorage, @Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Review createReview(Review review) {
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());
        return reviewStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        reviewStorage.getReview(review.getReviewId());
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(int reviewId) {
        if (reviewStorage.getReview(reviewId) == null) {
            throw new ReviewNotFoundException("Отзыв с id " + reviewId + " не найден");
        }
        reviewStorage.deleteReview(reviewId);
    }

    public Review getReview(int reviewId) {
        if (reviewStorage.getReview(reviewId) == null) {
            throw new ReviewNotFoundException("Отзыв с id " + reviewId + " не найден");
        }
        Review review = reviewStorage.getReview(reviewId);
        reviewStorage.loadUseful(Collections.singletonList(review));
        return review;
    }

    public List<Review> getReviewsByFilm(Integer filmId, Integer count) {
        int count1 = Objects.requireNonNullElse(count, 10);
        if (filmId == null) {
            List<Review> reviews = reviewStorage.getAllReviews(count1);
            reviewStorage.loadUseful(reviews);
            reviews.sort(Comparator.comparing(Review::getUseful).reversed());
            return reviews;
        }
        List<Review> reviews = reviewStorage.getReviewsByFilm(filmId, count1);
        reviewStorage.loadUseful(reviews);
        reviews.sort(Comparator.comparing(Review::getUseful).reversed());
        return reviews;
    }

    public void addReviewLike(int reviewId, int userId) {
        reviewStorage.addReviewLike(reviewId, userId);
    }

    public void addReviewDislike(int reviewId, int userId) {
        reviewStorage.addReviewDislike(reviewId, userId);
    }

    public void deleteReviewLike(int reviewId, int userId) {
        reviewStorage.deleteReviewLike(reviewId, userId);
    }

    public void deleteReviewDislike(int reviewId, int userId) {
        reviewStorage.deleteReviewDislike(reviewId, userId);
    }

}
