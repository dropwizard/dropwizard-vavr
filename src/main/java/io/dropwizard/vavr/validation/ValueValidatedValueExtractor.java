package io.dropwizard.vavr.validation;

import io.vavr.Value;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;

import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;

/**
 * A {@link ValueExtractor} for Vavr's {@link Value}.
 * <p>
 * Extracts the value contained by the {@link Value} for validation, or produces {@code null}.
 */
@UnwrapByDefault
public class ValueValidatedValueExtractor implements ValueExtractor<Value<@ExtractedValue ?>> {
    public static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new ValueValidatedValueExtractor());

    private ValueValidatedValueExtractor() {
    }

    @Override
    public void extractValues(Value<?> originalValue, ValueExtractor.ValueReceiver receiver) {
        receiver.value(null, originalValue.getOrNull());
    }
}
