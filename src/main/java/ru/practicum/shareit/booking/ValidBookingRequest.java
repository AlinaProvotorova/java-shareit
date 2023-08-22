package ru.practicum.shareit.booking;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE_USE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = BookingRequestValidator.class)
public @interface ValidBookingRequest {
    String message() default "Invalid booking request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
