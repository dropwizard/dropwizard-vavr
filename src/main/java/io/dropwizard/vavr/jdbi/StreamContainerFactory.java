package io.dropwizard.vavr.jdbi;

import io.vavr.collection.Stream;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.LinkedList;
import java.util.List;

public class StreamContainerFactory implements ContainerFactory<Stream<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return Stream.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<Stream<?>> newContainerBuilderFor(Class<?> type) {
        return new StreamContainerBuilder();
    }

    private static class StreamContainerBuilder implements ContainerBuilder<Stream<?>> {
        private final List<Object> list = new LinkedList<>();

        @Override
        public ContainerBuilder<Stream<?>> add(Object it) {
            list.add(it);
            return this;
        }

        @Override
        public Stream<?> build() {
            return Stream.ofAll(list);
        }
    }
}
