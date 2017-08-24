package io.dropwizard.vavr.jdbi;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.LinkedList;
import java.util.List;

public class SetContainerFactory implements ContainerFactory<Set<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return Set.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<Set<?>> newContainerBuilderFor(Class<?> type) {
        return new SetContainerBuilder();
    }

    private static class SetContainerBuilder implements ContainerBuilder<Set<?>> {
        private final List<Object> list = new LinkedList<>();

        @Override
        public ContainerBuilder<Set<?>> add(Object it) {
            list.add(it);
            return this;
        }

        @Override
        public Set<?> build() {
            return HashSet.ofAll(list);
        }
    }
}
