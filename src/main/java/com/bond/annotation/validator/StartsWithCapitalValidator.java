package com.bond.annotation.validator;

import com.bond.annotation.StartsWithCapital;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartsWithCapitalValidator implements ConstraintValidator<StartsWithCapital, String> {
    private static final int FIRST_SYMBOL_POSITION = 0;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || Character.isUpperCase(s.charAt(FIRST_SYMBOL_POSITION));
    }
}
