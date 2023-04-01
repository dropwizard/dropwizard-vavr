package io.dropwizard.vavr.jersey;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

public class CollectionParamFeature implements Feature {
    @Override
    public boolean configure(final FeatureContext context) {
        context.register(new CollectionParamBinder());
        return true;
    }
}
