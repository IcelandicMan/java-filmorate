package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    @Override
    public void initialize(ValidReleaseDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        LocalDate minimumDate = LocalDate.of(1895, 12, 28);
        return value.isAfter(minimumDate);
    }
}
