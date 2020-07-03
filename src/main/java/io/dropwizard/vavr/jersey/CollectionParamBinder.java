package io.dropwizard.vavr.jersey;

import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;

public class CollectionParamBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(CollectionValueFactoryProvider.CollectionQueryParamFactoryProvider.class)
            .to(ValueFactoryProvider.class)
            .in(Singleton.class);
        bind(CollectionValueFactoryProvider.CollectionFormParamFactoryProvider.class)
            .to(ValueFactoryProvider.class)
            .in(Singleton.class);
        bind(CollectionValueFactoryProvider.CollectionHeaderParamFactoryProvider.class)
            .to(ValueFactoryProvider.class)
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

    private static class QueryParamInjectionResolver extends ParamInjectionResolver<QueryParam> {
        public QueryParamInjectionResolver() {
            super(CollectionValueFactoryProvider.CollectionQueryParamFactoryProvider.class);
        }
    }

    private static class FormParamInjectionResolver extends ParamInjectionResolver<FormParam> {
        public FormParamInjectionResolver() {
            super(CollectionValueFactoryProvider.CollectionFormParamFactoryProvider.class);
        }
    }

    private static class HeaderParamInjectionResolver extends ParamInjectionResolver<HeaderParam> {
        public HeaderParamInjectionResolver() {
            super(CollectionValueFactoryProvider.CollectionHeaderParamFactoryProvider.class);
        }
    }
}
