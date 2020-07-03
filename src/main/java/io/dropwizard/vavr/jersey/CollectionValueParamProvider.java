package io.dropwizard.vavr.jersey;

import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ParamException;
import org.glassfish.jersey.server.internal.inject.AbstractValueParamProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractor;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.model.Parameter;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Function;

abstract class CollectionValueParamProvider extends AbstractValueParamProvider {
    private final InjectionManager injectionManager;

    protected CollectionValueParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep,
                                           final InjectionManager injectionManager,
                                           final Parameter.Source... compatibleSources) {
        super(mpep, compatibleSources);
        this.injectionManager = injectionManager;
    }

    @Override
    protected Function<ContainerRequest, ?> createValueProvider(final Parameter parameter) {
        final Option<String> name = Option.of(parameter.getSourceName());
        final Option<String> defaultValue = Option.of(parameter.getDefaultValue());

        return name
            .filter(n -> !n.isEmpty())
            .flatMap(n ->
                findParamConverter(parameter.getType(), parameter.getAnnotations())
                    .map(conv -> (Function<String, Object>) conv::fromString)
                    .flatMap(conv -> buildExtractor(parameter.getRawType(), n, defaultValue, conv))
                    .map(extractor -> buildProvider(extractor, !parameter.isEncoded()))
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

    protected abstract Function<ContainerRequest, ?> buildProvider(final MultivaluedParameterExtractor<?> extractor, final boolean decode);

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
                    return List.ofAll(Providers.getProviders(this.injectionManager, ParamConverterProvider.class)).flatMap(provider ->
                        Option.of((ParamConverter<Object>) provider.getConverter(ctp.rawClass(), ctp.type(), annotations))
                    ).headOption();
                }
            });
    }

    static class CollectionQueryParamProvider extends CollectionValueParamProvider {
        @Inject
        public CollectionQueryParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep, final InjectionManager injectionManager) {
            super(mpep, injectionManager, Parameter.Source.QUERY);
        }

        @Override
        protected Function<ContainerRequest, ?> buildProvider(final MultivaluedParameterExtractor<?> extractor, boolean decode) {
            return containerRequest -> Try.of(() -> {
                final MultivaluedMap<String, String> parameters = containerRequest.getUriInfo().getQueryParameters(decode);
                return extractor.extract(parameters);
            }).getOrElseThrow(e -> new ParamException.QueryParamException(e.getCause(), extractor.getName(), extractor.getDefaultValueString()));
        }
    }

    static class CollectionFormParamProvider extends CollectionValueParamProvider {
        @Inject
        public CollectionFormParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep, final InjectionManager injectionManager) {
            super(mpep, injectionManager, Parameter.Source.FORM);
        }

        @Override
        protected Function<ContainerRequest, ?> buildProvider(final MultivaluedParameterExtractor<?> extractor, boolean decode) {
            return containerRequest ->
                Try.of(() -> {
                    containerRequest.bufferEntity();
                    final Form form = containerRequest.readEntity(Form.class);
                    return extractor.extract(form.asMap());
                }).getOrElseThrow(e -> new ParamException.FormParamException(e.getCause(), extractor.getName(), extractor.getDefaultValueString()));
        }
    }

    static class CollectionHeaderParamProvider extends CollectionValueParamProvider {
        @Inject
        public CollectionHeaderParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep, final InjectionManager injectionManager) {
            super(mpep, injectionManager, Parameter.Source.HEADER);
        }

        @Override
        protected Function<ContainerRequest, ?> buildProvider(final MultivaluedParameterExtractor<?> extractor, boolean decode) {
            return containerRequest -> Try
                .of(() -> extractor.extract(containerRequest.getHeaders()))
                .getOrElseThrow(e -> new ParamException.HeaderParamException(e.getCause(), extractor.getName(), extractor.getDefaultValueString()));
        }
    }
}
