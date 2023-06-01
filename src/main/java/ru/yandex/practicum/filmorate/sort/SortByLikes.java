package ru.yandex.practicum.filmorate.sort;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class SortByLikes implements Comparator<Film> {

    @Override
    public int compare(Film o1, Film o2) {
        if (o1.getLikes().size()>o2.getLikes().size()){
            return 1;
        }
        return 0;
    }
}
