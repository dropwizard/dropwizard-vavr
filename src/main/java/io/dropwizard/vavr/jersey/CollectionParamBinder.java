package io.dropwizard.vavr.jersey;

import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.internal.process.RequestProcessingContextReference;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;

public class CollectionParamBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(CollectionValueParamProvider.CollectionQueryParamProvider.class)
            .to(ValueParamProvider.class)
            .in(Singleton.class);
        bind(CollectionValueParamProvider.CollectionFormParamProvider.class)
            .to(ValueParamProvider.class)
            .in(Singleton.class);
        bind(CollectionValueParamProvider.CollectionHeaderParamProvider.class)
            .to(ValueParamProvider.class)
            .in(Singleton.class);

        bind(QueryParamInjectionResolver.class)
            .to(new TypeLiteral<InjectionResolver<QueryParam>>() {})
            .in(Singleton.class);
        bind(FormParamInjectionResolver.class)
            .to(new TypeLiteral<InjectionResolver<FormParam>>() {})
            .in(Singleton.class);
        bind(HeaderParamInjectionResolver.class)
            .to(new TypeLiteral<InjectionResolver<HeaderParam>>() {})
            .in(Singleton.class);
    }

    private static Provider<ContainerRequest> getContainerRequestProvider(final InjectionManager injectionManager) {
        return () -> {
            RequestProcessingContextReference reference = injectionManager.getInstance(RequestProcessingContextReference.class);
            return reference.get().request();
        };
    }

    private static class QueryParamInjectionResolver extends ParamInjectionResolver<QueryParam> {
        @Inject
        public QueryParamInjectionResolver(final Provider<MultivaluedParameterExtractorProvider> mpep, final InjectionManager injectionManager) {
            super(new CollectionValueParamProvider.CollectionQueryParamProvider(mpep, injectionManager), QueryParam.class, getContainerRequestProvider(injectionManager));
        }
    }

    private static class FormParamInjectionResolver extends ParamInjectionResolver<FormParam> {
        @Inject
        public FormParamInjectionResolver(final Provider<MultivaluedParameterExtractorProvider> mpep, final InjectionManager injectionManager) {
            super(new CollectionValueParamProvider.CollectionFormParamProvider(mpep, injectionManager), FormParam.class, getContainerRequestProvider(injectionManager));
        }
    }

    private static class HeaderParamInjectionResolver extends ParamInjectionResolver<HeaderParam> {
        @Inject
        public HeaderParamInjectionResolver(final Provider<MultivaluedParameterExtractorProvider> mpep, final InjectionManager injectionManager) {
            super(new CollectionValueParamProvider.CollectionHeaderParamProvider(mpep, injectionManager), HeaderParam.class, getContainerRequestProvider(injectionManager));
        }
    }
}
