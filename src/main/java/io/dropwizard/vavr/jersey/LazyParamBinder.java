package io.dropwizard.vavr.jersey;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverterProvider;

final class LazyParamBinder extends AbstractBinder {
    @Override
    protected void configure() {
        // Param converter providers
        bind(LazyParamConverterProvider.class).to(ParamConverterProvider.class).in(Singleton.class);
    }
}
