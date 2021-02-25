package com.springbikeclinic.web.security.validation;

import com.springbikeclinic.web.dto.CreateAccountDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// Source: https://www.baeldung.com/registration-with-spring-mvc-and-spring-security

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        CreateAccountDto createAccountDto = (CreateAccountDto) obj;
        boolean isValid = createAccountDto.getCreatePassword().equals(createAccountDto.getConfirmPassword());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode( "createPassword" ).addConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode( "confirmPassword" ).addConstraintViolation();
        }
        return isValid;
    }
}
