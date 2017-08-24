package io.dropwizard.vavr.jersey;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class LazyParamFeature implements Feature {
    @Override
    public boolean configure(final FeatureContext context) {
        context.register(new LazyParamBinder());
        return true;
    }
}
