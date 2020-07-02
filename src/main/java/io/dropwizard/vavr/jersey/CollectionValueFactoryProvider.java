package io.dropwizard.vavr.jersey;

import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractor;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.model.Parameter;

import javax.inject.Inject;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Function;

abstract class CollectionValueFactoryProvider extends AbstractValueFactoryProvider {
    protected CollectionValueFactoryProvider(final MultivaluedParameterExtractorProvider mpep,
                                             final ServiceLocator locator,
                                             final Parameter.Source... compatibleSources) {
        super(mpep, locator, compatibleSources);
    }

    @Override
    protected Factory<?> createValueFactory(final Parameter parameter) {
        final Option<String> name = Option.of(parameter.getSourceName());
        final Option<String> defaultValue = Option.of(parameter.getDefaultValue());

        return name
            .filter(n -> !n.isEmpty())
            .flatMap(n ->
                findParamConverter(parameter.getType(), parameter.getAnnotations())
                    .map(conv -> (Function<String, Object>) conv::fromString)
                    .flatMap(conv -> buildExtractor(parameter.getRawType(), n, defaultValue, conv))
                    .map(extractor -> buildFactory(extractor, !parameter.isEncoded()))
            )
            .getOrNull();
    }

    private Option<MultivaluedParameterExtractor<?>> buildExtractor(final Class<?> rawClass,
                                                                    final String name,
                                                                    final Option<String> defaultValue,
                                                                    final Function<String, Object> conv) {
        if (rawClass.equals(Vector.class)) {
            return Option.of(new CollectionParameterExtractor<>(name, defaultValue, conv, Vector::ofAll));
        } else if (rawClass.equals(List.class)) {
            return Option.of(new CollectionParameterExtractor<>(name, defaultValue, conv, List::ofAll));
        } else if (rawClass.equals(Array.class)) {
            return Option.of(new CollectionParameterExtractor<>(name, defaultValue, conv, Array::ofAll));
        } else {
            return Option.none();
        }
    }

    protected abstract AbstractContainerRequestValueFactory<?> buildFactory(final MultivaluedParameterExtractor<?> extractor, final boolean decode);

    @SuppressWarnings("unchecked")
    private Option<ParamConverter<Object>> findParamConverter(final Type type, final Annotation[] annotations) {
        return List.ofAll(ReflectionHelper.getTypeArgumentAndClass(type))
            .headOption()
            .flatMap(ctp -> {
                if (ctp.rawClass().equals(String.class)) {
                    return Option.<ParamConverter<Object>>some(new ParamConverter<Object>() {
                        @Override
                        public Object fromString(final String value) {
                            return value;
                        }

                        @Override
                        public String toString(final Object value) {
                            return value.toString();
                        }
                    });
                } else {
                    return List.ofAll(Providers.getProviders(getLocator(), ParamConverterProvider.class)).flatMap(provider ->
                        Option.of((ParamConverter<Object>) provider.getConverter(ctp.rawClass(), ctp.type(), annotations))
                    ).headOption();
                }
            });
    }

    static class CollectionQueryParamFactoryProvider extends CollectionValueFactoryProvider {
        @Inject
        public CollectionQueryParamFactoryProvider(final MultivaluedParameterExtractorProvider mpep, final ServiceLocator locator) {
            super(mpep, locator, Parameter.Source.QUERY);
        }

        @Override
        protected AbstractContainerRequestValueFactory<?> buildFactory(final MultivaluedParameterExtractor<?> extractor, boolean decode) {
            return new ParameterValueFactories.QueryParamValueFactory(extractor, decode);
        }
    }

    static class CollectionFormParamFactoryProvider extends CollectionValueFactoryProvider {
        @Inject
        public CollectionFormParamFactoryProvider(final MultivaluedParameterExtractorProvider mpep, final ServiceLocator locator) {
            super(mpep, locator, Parameter.Source.FORM);
        }

        @Override
        protected AbstractContainerRequestValueFactory<?> buildFactory(final MultivaluedParameterExtractor<?> extractor, boolean decode) {
            return new ParameterValueFactories.FormParamValueFactory(extractor);
        }
    }

    static class CollectionHeaderParamFactoryProvider extends CollectionValueFactoryProvider {
        @Inject
        public CollectionHeaderParamFactoryProvider(final MultivaluedParameterExtractorProvider mpep, final ServiceLocator locator) {
            super(mpep, locator, Parameter.Source.HEADER);
        }

        @Override
        protected AbstractContainerRequestValueFactory<?> buildFactory(final MultivaluedParameterExtractor<?> extractor, boolean decode) {
            return new ParameterValueFactories.HeaderParamValueFactory(extractor);
        }
    }
}
