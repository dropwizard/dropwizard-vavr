package io.dropwizard.vavr;

import io.dropwizard.core.Configuration;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.util.Resources;
import io.dropwizard.vavr.jersey.CollectionParamFeature;
import io.dropwizard.vavr.jersey.EitherMessageBodyWriter;
import io.dropwizard.vavr.jersey.EmptyValueExceptionMapper;
import io.dropwizard.vavr.jersey.LazyParamFeature;
import io.dropwizard.vavr.jersey.OptionParamFeature;
import io.dropwizard.vavr.jersey.ValueMessageBodyWriter;
import io.dropwizard.vavr.validation.ValueValidatedValueExtractor;
import io.vavr.jackson.datatype.VavrModule;
import org.hibernate.validator.HibernateValidatorConfiguration;

import jakarta.validation.ValidatorFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;

public class VavrBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private final VavrModule.Settings settings;
    private final boolean registerMessageBodyWriters;

    /**
     * Add Vavr support (Jackson, Jersey) to Dropwizard.
     */
    public VavrBundle() {
        this(false);
    }


    /**
     * Add Vavr support (Jackson, Jersey) to Dropwizard.
     *
     * @param registerMessageBodyWriters Register {@link EitherMessageBodyWriter} and {@link ValueMessageBodyWriter}
     *                                   in {@link com.fasterxml.jackson.databind.ObjectMapper}.
     *                                   <em>NOTE:</em> This will make it impossible to serialize or deserialize classes
     *                                   such as {@link io.vavr.control.Either}, {@link io.vavr.control.Option}, and
     *                                   {@link io.vavr.Lazy} with Jackson. Use with care!
     */
    public VavrBundle(boolean registerMessageBodyWriters) {
        this(new VavrModule.Settings(), registerMessageBodyWriters);
    }


    /**
     * Add Vavr support (Jackson, Jersey) to Dropwizard.
     *
     * @param settings                   Settings for Jackson {@link VavrModule}
     * @param registerMessageBodyWriters Register {@link EitherMessageBodyWriter} and {@link ValueMessageBodyWriter}
     *                                   in {@link com.fasterxml.jackson.databind.ObjectMapper}.
     *                                   <em>NOTE:</em> This will make it impossible to serialize or deserialize classes
     *                                   such as {@link io.vavr.control.Either}, {@link io.vavr.control.Option}, and
     *                                   {@link io.vavr.Lazy} with Jackson. Use with care!
     */
    public VavrBundle(VavrModule.Settings settings, boolean registerMessageBodyWriters) {
        this.settings = settings;
        this.registerMessageBodyWriters = registerMessageBodyWriters;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.getObjectMapper().registerModule(new VavrModule(settings));
        bootstrap.setValidatorFactory(newValidatorFactory());
    }

    @Override
    public void run(T configuration, Environment environment) {
        environment.jersey().register(EmptyValueExceptionMapper.class);
        environment.jersey().register(LazyParamFeature.class);
        environment.jersey().register(OptionParamFeature.class);
        environment.jersey().register(CollectionParamFeature.class);

        // These MessageBodyWriters will shadow JacksonMessageBodyProvider and thus make it impossible
        // to serialize or deserialize Either<Left, Right> or classes based on Value<T>, such as Option<T>
        // and Lazy<T>, with Jackson.
        // FIXME: This can be removed with JAX-RS 2.1/Jersey 2.26, see https://github.com/jersey/jersey/issues/3473
        if (registerMessageBodyWriters) {
            environment.jersey().register(EitherMessageBodyWriter.class);
            environment.jersey().register(ValueMessageBodyWriter.class);
        }
    }

    /**
     * Creates a new {@link ValidatorFactory} based on {@link #newValidatorConfiguration()}
     */
    protected ValidatorFactory newValidatorFactory() {
        return newValidatorConfiguration().buildValidatorFactory();
    }

    /**
     * Creates a new {@link HibernateValidatorConfiguration} with all the custom {@link
     * javax.validation.valueextraction.ValueExtractor} registered.
     */
    protected HibernateValidatorConfiguration newValidatorConfiguration() {
        final InputStream vavrConstraints;
        try {
            final URL resource = Resources.getResource("META-INF/constraints-vavr.xml");
            vavrConstraints = resource.openStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return Validators.newConfiguration()
                .addValueExtractor(new ValueValidatedValueExtractor())
                .addMapping(vavrConstraints);
    }
}
