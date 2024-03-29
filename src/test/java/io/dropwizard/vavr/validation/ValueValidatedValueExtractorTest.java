package io.dropwizard.vavr.validation;

import io.vavr.control.Option;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueValidatedValueExtractorTest {
    static class Example {

        @Min(3)
        Option<Integer> three = Option.none();

        @NotNull
        Option<Integer> notNull = Option.of(123);

        @NotBlank
        Option<String> notBlank = Option.of("Foobar");
    }

    private final Validator validator = Validation
            .byProvider(HibernateValidator.class)
            .configure()
            .addValueExtractor(new ValueValidatedValueExtractor())
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