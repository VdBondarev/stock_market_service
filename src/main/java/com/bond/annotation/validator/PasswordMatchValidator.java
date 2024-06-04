package com.bond.annotation.validator;

import com.bond.annotation.FieldMatch;
import java.lang.reflect.Field;
import java.util.Objects;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstField;
    private String secondField;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstField = constraintAnnotation.firstField();
        secondField = constraintAnnotation.secondField();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Field firstField = o.getClass().getDeclaredField(this.firstField);
            firstField.setAccessible(true);
            Object firstFieldValue = firstField.get(o);
            Field secondField = o.getClass().getDeclaredField(this.secondField);
            secondField.setAccessible(true);
            Object secondFieldValue = secondField.get(o);
            return Objects.equals(firstFieldValue, secondFieldValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error accessing fields", e);
        }
    }
}
