package io.dropwizard.vavr.jersey;

import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractor;

import jakarta.ws.rs.core.MultivaluedMap;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

class CollectionParameterExtractor<A, B extends Seq<String>> implements MultivaluedParameterExtractor<Seq<A>> {
    private final String name;
    private final Option<String> defaultValue;
    private final Function<String, A> fromString;
    private final Function<List<String>, B> collBuilder;

    CollectionParameterExtractor(final String name,
                                 final Option<String> defaultValue,
                                 final Function<String, A> fromString,
                                 final Function<List<String>, B> collBuilder) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.fromString = fromString;
        this.collBuilder = collBuilder;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDefaultValueString() {
        return this.defaultValue.getOrNull();
    }

    @Override
    public Seq<A> extract(final MultivaluedMap<String, String> parameters) {
        return this.collBuilder.apply(
            Option.of(parameters.get(this.name))
                .getOrElse(() -> defaultValue.map(Collections::singletonList).getOrElse(Collections.emptyList()))
        ).map(this.fromString);
    }
}
