package io.dropwizard.vavr.jdbi;

import io.vavr.collection.List;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.LinkedList;

public class ListContainerFactory implements ContainerFactory<List<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<List<?>> newContainerBuilderFor(Class<?> type) {
        return new ListContainerBuilder();
    }

    private static class ListContainerBuilder implements ContainerBuilder<List<?>> {
        private final java.util.List<Object> list = new LinkedList<>();

        @Override
        public ContainerBuilder<List<?>> add(Object it) {
            list.add(it);
            return this;
        }

        @Override
        public List<?> build() {
            return List.ofAll(list);
        }
    }
}
