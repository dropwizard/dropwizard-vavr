package io.dropwizard.vavr.jdbi;

import io.vavr.collection.Vector;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.LinkedList;
import java.util.List;

public class VectorContainerFactory implements ContainerFactory<Vector<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return Vector.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<Vector<?>> newContainerBuilderFor(Class<?> type) {
        return new VectorContainerBuilder();
    }

    private static class VectorContainerBuilder implements ContainerBuilder<Vector<?>> {
        private final List<Object> list = new LinkedList<>();

        @Override
        public ContainerBuilder<Vector<?>> add(Object it) {
            list.add(it);
            return this;
        }

        @Override
        public Vector<?> build() {
            return Vector.ofAll(list);
        }
    }
}
