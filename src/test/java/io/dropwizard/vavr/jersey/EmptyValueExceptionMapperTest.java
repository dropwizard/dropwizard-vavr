package io.dropwizard.vavr.jersey;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.logging.BootstrapLogging;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class EmptyValueExceptionMapperTest extends JerseyTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Override
    protected Application configure() {
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return DropwizardResourceConfig.forTesting(new MetricRegistry())
                .register(EmptyValueExceptionMapper.class)
                .register(TestResource.class);
    }

    @Test
    public void emptyValueExceptionMapsToNotFound() throws Exception {
        final Response response = target("/").request().get();
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.hasEntity()).isFalse();
    }

    @Path("/")
    public static class TestResource {
        @GET
        public String throwException() {
            throw EmptyValueException.INSTANCE;
        }
    }
}