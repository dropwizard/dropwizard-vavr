package io.dropwizard.vavr.jersey;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class CollectionParamFeature implements Feature {
    @Override
    public boolean configure(final FeatureContext context) {
        context.register(new CollectionParamBinder());
        return true;
    }
}
