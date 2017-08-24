package io.dropwizard.vavr.jdbi;

import io.vavr.collection.Queue;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.LinkedList;
import java.util.List;

public class QueueContainerFactory implements ContainerFactory<Queue<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return Queue.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<Queue<?>> newContainerBuilderFor(Class<?> type) {
        return new QueueContainerBuilder();
    }

    private static class QueueContainerBuilder implements ContainerBuilder<Queue<?>> {
        private final List<Object> list = new LinkedList<>();

        @Override
        public ContainerBuilder<Queue<?>> add(Object it) {
            list.add(it);
            return this;
        }

        @Override
        public Queue<?> build() {
            return Queue.ofAll(list);
        }
    }
}
