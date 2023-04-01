package io.dropwizard.vavr.validation;

import io.vavr.Value;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;

/**
 * A {@link ValueExtractor} for Vavr's {@link Value}.
 * <p>
 * Extracts the value contained by the {@link Value} for validation, or produces {@code null}.
 */
@UnwrapByDefault
public class ValueValidatedValueExtractor implements ValueExtractor<Value<@ExtractedValue ?>> {
    @Override
    public void extractValues(Value<?> originalValue, ValueExtractor.ValueReceiver receiver) {
        receiver.value(null, originalValue.getOrNull());
    }
}
