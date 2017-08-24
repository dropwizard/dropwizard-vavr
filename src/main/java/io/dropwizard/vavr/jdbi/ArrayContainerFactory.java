package io.dropwizard.vavr.jdbi;

import io.vavr.collection.Array;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.LinkedList;
import java.util.List;

public class ArrayContainerFactory implements ContainerFactory<Array<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return Array.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<Array<?>> newContainerBuilderFor(Class<?> type) {
        return new ArrayContainerBuilder();
    }

    private static class ArrayContainerBuilder implements ContainerBuilder<Array<?>> {
        private final List<Object> list = new LinkedList<>();

        @Override
        public ContainerBuilder<Array<?>> add(Object it) {
            list.add(it);
            return this;
        }

        @Override
        public Array<?> build() {
            return Array.ofAll(list);
        }
    }
}
