package io.dropwizard.vavr.validation;

import io.vavr.control.Option;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueValidatedValueUnwrapperTest {
    static class Example {

        @Min(3)
        @UnwrapValidatedValue
        Option<Integer> three = Option.none();

        @NotNull
        @UnwrapValidatedValue
        Option<Integer> notNull = Option.of(123);

        @NotBlank
        @UnwrapValidatedValue
        Option<String> notBlank = Option.of("Foobar");
    }

    private final Validator validator = Validation
            .byProvider(HibernateValidator.class)
            .configure()
            .addValidatedValueHandler(new ValueValidatedValueUnwrapper())
            .buildValidatorFactory()
            .getValidator();

    @Test
    public void failsWhenFailingConstraint() {
        Example example = new Example();
        example.three = Option.of(2);
        Set<ConstraintViolation<Example>> violations = validator.validate(example);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void succeedsWhenConstraintsMet() {
        Example example = new Example();
        example.three = Option.of(10);
        Set<ConstraintViolation<Example>> violations = validator.validate(example);
        assertThat(violations).isEmpty();
    }

    @Test
    public void notNullFailsWhenAbsent() {
        Example example = new Example();
        example.notNull = Option.none();
        Set<ConstraintViolation<Example>> violations = validator.validate(example);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void notBlankFailsWhenPresentButBlank() {
        Example example = new Example();
        example.notBlank = Option.of("\t");
        Set<ConstraintViolation<Example>> violations = validator.validate(example);
        assertThat(violations).hasSize(1);
    }
}