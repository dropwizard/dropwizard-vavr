package io.dropwizard.vavr.jdbi;

import io.vavr.collection.IndexedSeq;
import io.vavr.collection.Vector;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.LinkedList;
import java.util.List;

public class IndexedSeqContainerFactory implements ContainerFactory<IndexedSeq<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return IndexedSeq.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<IndexedSeq<?>> newContainerBuilderFor(Class<?> type) {
        return new IndexedSeqContainerBuilder();
    }

    private static class IndexedSeqContainerBuilder implements ContainerBuilder<IndexedSeq<?>> {
        private final List<Object> list = new LinkedList<>();

        @Override
        public ContainerBuilder<IndexedSeq<?>> add(Object it) {
            list.add(it);
            return this;
        }

        @Override
        public IndexedSeq<?> build() {
            return Vector.ofAll(list);
        }
    }
}
