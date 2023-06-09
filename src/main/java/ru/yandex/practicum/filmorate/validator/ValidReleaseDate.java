package ru.yandex.practicum.filmorate.validator;

import javax.validation.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ru.yandex.practicum.filmorate.validator.ReleaseDateValidator.class)
@Documented
public @interface ValidReleaseDate {
    String message() default "Invalid release date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

