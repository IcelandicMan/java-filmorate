package ru.yandex.practicum.filmorate.service.review;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
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
    private final FeedStorage feedStorage;

    public ReviewService(ReviewStorage reviewStorage, @Qualifier("filmDbStorage") FilmStorage filmStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage, FeedStorage feedStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
    }

    public Review createReview(Review review) {
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());
        Review review1 = reviewStorage.createReview(review);
        feedStorage.addFeed(new Feed(0, null, review1.getUserId(), "REVIEW", "ADD", review1.getReviewId()));
        return review1;
    }

    public Review updateReview(Review review) {
        reviewStorage.getReview(review.getReviewId());
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());
        Review review1 = reviewStorage.updateReview(review);
        feedStorage.addFeed(new Feed(0, null, review1.getUserId(), "REVIEW", "UPDATE", review1.getReviewId()));
        return review1;
    }

    public void deleteReview(int reviewId) {
        Review review = reviewStorage.getReview(reviewId);
        reviewStorage.deleteReview(reviewId);
        feedStorage.addFeed(new Feed(0, null, review.getUserId(), "REVIEW", "REMOVE", reviewId));

    }

    public Review getReview(int reviewId) {
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
