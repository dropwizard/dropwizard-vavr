package io.dropwizard.vavr.jdbi;

import io.vavr.control.Option;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

public class OptionContainerFactory implements ContainerFactory<Option<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return Option.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<Option<?>> newContainerBuilderFor(Class<?> type) {
        return new OptionContainerBuilder();
    }

    private static class OptionContainerBuilder implements ContainerBuilder<Option<?>> {

        private Option<?> optional = Option.none();

        @Override
        public ContainerBuilder<Option<?>> add(Object it) {
            optional = Option.of(it);
            return this;
        }

        @Override
        public Option<?> build() {
            return optional;
        }
    }
}
