package io.dropwizard.vavr.jdbi;

import io.vavr.collection.Tree;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.LinkedList;
import java.util.List;

public class TreeContainerFactory implements ContainerFactory<Tree<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return Tree.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<Tree<?>> newContainerBuilderFor(Class<?> type) {
        return new TreeContainerBuilder();
    }

    private static class TreeContainerBuilder implements ContainerBuilder<Tree<?>> {
        private final List<Object> list = new LinkedList<>();

        @Override
        public ContainerBuilder<Tree<?>> add(Object it) {
            list.add(it);
            return this;
        }

        @Override
        public Tree<?> build() {
            return Tree.ofAll(list);
        }
    }
}
