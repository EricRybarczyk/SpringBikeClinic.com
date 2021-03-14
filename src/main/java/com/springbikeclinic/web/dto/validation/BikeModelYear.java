package com.springbikeclinic.web.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = BikeModelYearValidator.class)
@Documented
public @interface BikeModelYear {
    String message() default "Invalid bike model year";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
