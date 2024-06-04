package com.bond.annotation;

import com.bond.annotation.validator.StartsWithCapitalValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartsWithCapitalValidator.class)
public @interface StartsWithCapital {
    String message() default "should start with a capital letter";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
