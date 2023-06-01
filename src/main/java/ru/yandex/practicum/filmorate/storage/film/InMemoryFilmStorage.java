package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private int idCounter = 0;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Film createFilm(Film film) {
        log.info("Создание фильма: {}", film);
        try {
            isValidData(film);
            film.setId(++idCounter);
            films.put(film.getId(), film);
        } catch (MethodArgumentNotValidException ignored) {
        }
        log.info("Фильм под id {} создан: {}", film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        log.info("Обновление фильма: {} ", updatedFilm);
        try {
            isValidData(updatedFilm);
            isValidFilmId(updatedFilm.getId());
        } catch (MethodArgumentNotValidException ignored) {
        }
        long id = updatedFilm.getId();
        Film film = films.get(id);
        if (updatedFilm.getName() != null) {
            film.setName(updatedFilm.getName());
        }
        if (updatedFilm.getDescription() != null) {
            film.setDescription(updatedFilm.getDescription());
        }
        if (updatedFilm.getReleaseDate() != null) {
            film.setReleaseDate(updatedFilm.getReleaseDate());
        }
        if (updatedFilm.getDuration() > 0) {
            film.setDuration(updatedFilm.getDuration());
        }
        log.info("Фильм под ID {} обновлен: {} ", id, film);
        return film;
    }

    @Override
    public void deleteFilm(long id) {
        log.info("Удаление фильма с id {}", id);
        isValidFilmId(id);
        films.remove(id);
        log.info("Фильм с id {} удален", id);
    }

    public Film getFilm(long id) {
        log.info("Получение Фильма с id {}", id);
        isValidFilmId(id);
        Film film = films.get(id);
        log.info("Фильма с id {} предоставлен: {}", id, film);
        return film;
    }

    public List<Film> getFilms() {
        log.info("Получение списка всех фильмов");
        List<Film> filmsList = new ArrayList<>(films.values());
        log.info("Список всех фильмов сформирован");
        return filmsList;
    }

    public void isValidFilmId(long id) {
        if (!films.containsKey(id)) {
            log.warn("Фильм под id {} не найден", id);
            throw new FilmNotFoundException(String.format("Фильм c id %s не найден", id));
        }
    }

    public void isValidData(Film film) throws MethodArgumentNotValidException {
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            String errorMessage = String.format("Фильм под id %d не прошел валидацию по дате %s", film.getId(), film.getReleaseDate());

            FieldError fieldError = new FieldError("film", "releaseDate", film.getReleaseDate(), false, null, null, errorMessage);
            BindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(film, "film");
            bindingResult.addError(fieldError);
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }
}
