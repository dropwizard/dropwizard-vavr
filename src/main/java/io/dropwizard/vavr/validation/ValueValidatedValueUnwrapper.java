package io.dropwizard.vavr.validation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import io.vavr.Value;
import org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper;

import java.lang.reflect.Type;

/**
 * A {@link ValidatedValueUnwrapper} for Vavr's {@link Value}.
 *
 * Extracts the value contained by the {@link Value} for validation, or produces {@code null}.
 */
public class ValueValidatedValueUnwrapper extends ValidatedValueUnwrapper<Value<?>> {

    private final TypeResolver resolver = new TypeResolver();

    @Override
    public Object handleValidatedValue(final Value<?> option) {
        return option.getOrElse(() -> null);
    }

    @Override
    public Type getValidatedValueType(final Type type) {
        final ResolvedType resolvedType = resolver.resolve(type);
        return resolvedType.typeParametersFor(Value.class).get(0).getErasedType();
    }
}
