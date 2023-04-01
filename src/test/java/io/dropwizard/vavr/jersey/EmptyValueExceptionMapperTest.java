package io.dropwizard.vavr.jersey;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.logging.common.BootstrapLogging;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class EmptyValueExceptionMapperTest extends AbstractJerseyTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Override
    protected Application configure() {
        return DropwizardResourceConfig.forTesting(new MetricRegistry())
                .register(EmptyValueExceptionMapper.class)
                .register(TestResource.class);
    }

    protected EmptyValueExceptionMapperTest() {
        super();
        forceSet(TestProperties.CONTAINER_PORT, "0");
    }

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
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