package io.dropwizard.vavr.jersey;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.ParamConverterProvider;

final class OptionParamBinder extends AbstractBinder {
    @Override
    protected void configure() {
        // Param converter providers
        bind(OptionParamConverterProvider.class).to(ParamConverterProvider.class).in(Singleton.class);
    }
}
