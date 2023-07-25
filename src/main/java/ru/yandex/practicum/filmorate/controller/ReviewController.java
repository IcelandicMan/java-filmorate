package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("Запрос на создание отзыва");
        Review createdReview = reviewService.createReview(review);
        log.info("Отзыв создан: {}", createdReview);
        return createdReview;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Запрос на обновление отзыва: {}", review);
        Review updatedReview = reviewService.updateReview(review);
        log.info("Отзыв обновлен: {}", updatedReview);
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") int reviewId) {
        log.info("Запрос на удаление отзыва с id {}", reviewId);
        reviewService.deleteReview(reviewId);
        log.info("Отзыв с id " + reviewId + " удалён");
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable("id") int reviewId) {
        log.info("Запрос на получение отзыва с id {}", reviewId);
        Review review = reviewService.getReview(reviewId);
        log.info("Отзыв с id " + reviewId + " получен");
        return review;
    }

    @GetMapping
    public List<Review> getReviewsByFilm(@RequestParam(required = false) Integer filmId, @RequestParam(required = false) Integer count) {
        log.info("Запрос на получение списка отзывов по фильму с id {}", filmId);
        List<Review> reviewsByFilm = reviewService.getReviewsByFilm(filmId, count);
        log.info("Список получен");
        return reviewsByFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addReviewLike(@PathVariable("id") int reviewId, @PathVariable int userId) {
        log.info("Запрос на добавление лайка отзыву с id " + reviewId + " от пользователя с id " + userId);
        reviewService.addReviewLike(reviewId, userId);
        log.info("Лайк добавлен");
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addReviewDislike(@PathVariable("id") int reviewId, @PathVariable int userId) {
        log.info("Запрос на добавление дизлайка отзыву с id " + reviewId + " от пользователя с id " + userId);
        reviewService.addReviewDislike(reviewId, userId);
        log.info("Дизлайк добавлен");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteReviewLike(@PathVariable("id") int reviewId, @PathVariable int userId) {
        log.info("Запрос на удаление лайка отзыву с id " + reviewId + " от пользователя с id " + userId);
        reviewService.deleteReviewLike(reviewId, userId);
        log.info("Лайк удален");
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteReviewDislike(@PathVariable("id") int reviewId, @PathVariable int userId) {
        log.info("Запрос на удаление дизлайка отзыву с id " + reviewId + " от пользователя с id " + userId);
        reviewService.deleteReviewDislike(reviewId, userId);
        log.info("Дизлайк удален");
    }

}
