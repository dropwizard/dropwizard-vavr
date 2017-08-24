package io.dropwizard.vavr.jdbi;

import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.LinkedList;
import java.util.List;

public class SeqContainerFactory implements ContainerFactory<Seq<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return Seq.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<Seq<?>> newContainerBuilderFor(Class<?> type) {
        return new SeqContainerBuilder();
    }

    private static class SeqContainerBuilder implements ContainerBuilder<Seq<?>> {
        private final List<Object> list = new LinkedList<>();

        @Override
        public ContainerBuilder<Seq<?>> add(Object it) {
            list.add(it);
            return this;
        }

        @Override
        public Seq<?> build() {
            return Vector.ofAll(list);
        }
    }
}
