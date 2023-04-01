package io.dropwizard.vavr.jersey;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.logging.common.BootstrapLogging;
import io.vavr.Lazy;
import io.vavr.control.Option;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueMessageBodyWriterTest extends AbstractJerseyTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Override
    protected Application configure() {
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return DropwizardResourceConfig.forTesting(new MetricRegistry())
                .register(ValueMessageBodyWriter.class)
                .register(EmptyValueExceptionMapper.class)
                .register(TestResource.class);
    }

    @Test
    public void presentOptionsReturnTheirValue() throws Exception {
        assertThat(target("option-some").request().get(String.class)).isEqualTo("Foobar");
    }

    @Test
    public void presentOptionsReturnTheirValueWithResponse() throws Exception {
        assertThat(target("response-some").request().get(String.class)).isEqualTo("Foobar");
    }

    @Test
    public void absentOptionsReturnNotFound() throws Exception {
        final Response response = target("option-none").request().get();
        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void absentOptionsReturnNotFoundWithResponse() throws Exception {
        final Response response = target("response-none").request().get();
        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void optionNullReturnsEmpty() throws Exception {
        final Response response = target("option-null").request().get();
        assertThat(response.getStatus()).isEqualTo(204);
        assertThat(response.hasEntity()).isFalse();
    }

    @Test
    public void lazyReturnTheirValue() throws Exception {
        assertThat(target("lazy").request().get(String.class)).isEqualTo("Foobar");
    }

    @Test
    public void lazyNullReturnsEmpty() throws Exception {
        final Response response = target("lazy-null").request().get();
        assertThat(response.getStatus()).isEqualTo(204);
        assertThat(response.hasEntity()).isFalse();
    }

    @Test
    public void lazyReturnTheirValueWithResponse() throws Exception {
        assertThat(target("response-lazy").request().get(String.class)).isEqualTo("Foobar");
    }

    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public static class TestResource {
        @GET
        @Path("option-some")
        public Option<String> some() {
            return Option.of("Foobar");
        }

        @GET
        @Path("option-none")
        public Option<String> none() {
            return Option.none();
        }

        @GET
        @Path("option-null")
        public Option<String> optionNull() {
            return null;
        }

        @Path("response-some")
        @GET
        public Response responseSome() {
            return Response.ok(Option.of("Foobar")).build();
        }

        @Path("response-none")
        @GET
        public Response responseNone() {
            return Response.ok(Option.none()).build();
        }

        @GET
        @Path("lazy")
        public Lazy<String> lazy() {
            return Lazy.of(() -> "Foobar");
        }

        @GET
        @Path("lazy-null")
        public Lazy<String> lazyNull() {
            return null;
        }

        @Path("response-lazy")
        @GET
        public Response responseLazy() {
            return Response.ok(Lazy.of(() -> "Foobar")).build();
        }
    }

}