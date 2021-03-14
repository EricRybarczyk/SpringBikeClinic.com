package com.springbikeclinic.web.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BikeModelYearValidator implements ConstraintValidator<BikeModelYear, Integer> {

    @Override
    public void initialize(BikeModelYear constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value > 1900 && value <= LocalDate.now().getYear();
    }

}
