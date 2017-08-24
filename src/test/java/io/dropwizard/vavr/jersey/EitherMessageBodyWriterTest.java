package io.dropwizard.vavr.jersey;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.logging.BootstrapLogging;
import io.vavr.control.Either;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class EitherMessageBodyWriterTest extends JerseyTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Override
    protected Application configure() {
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return DropwizardResourceConfig.forTesting(new MetricRegistry())
                .register(EitherMessageBodyWriter.class)
                .register(EmptyValueExceptionMapper.class)
                .register(TestResource.class);
    }

    @Test
    public void plainEitherLeft() throws Exception {
        assertThat(target("left").request().get(String.class)).isEqualTo("Foobar");
    }

    @Test
    public void plainEitherRight() throws Exception {
        assertThat(target("right").request().get(Integer.class)).isEqualTo(42);
    }

    @Test
    public void plainEitherNull() throws Exception {
        final Response response = target("null").request().get();
        assertThat(response.getStatus()).isEqualTo(204);
        assertThat(response.hasEntity()).isFalse();
    }

    @Test
    public void responseEitherLeft() throws Exception {
        assertThat(target("response-left").request().get(String.class)).isEqualTo("Foobar");
    }

    @Test
    public void responseEitherRight() throws Exception {
        assertThat(target("response-right").request().get(Integer.class)).isEqualTo(42);
    }

    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public static class TestResource {
        @GET
        @Path("left")
        public Either<String, Integer> returnLeft() {
            return Either.left("Foobar");
        }

        @GET
        @Path("right")
        public Either<String, Integer> returnRight() {
            return Either.right(42);
        }

        @GET
        @Path("null")
        public Either<String, Integer> returnNull() {
            return null;
        }

        @Path("response-left")
        @GET
        public Response returnResponseLeft() {
            return Response.ok(Either.left("Foobar")).build();
        }

        @Path("response-right")
        @GET
        public Response returnResponseRight() {
            return Response.ok(Either.right(42)).build();
        }
    }

}