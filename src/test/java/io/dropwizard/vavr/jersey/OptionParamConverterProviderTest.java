package io.dropwizard.vavr.jersey;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.logging.BootstrapLogging;
import io.vavr.control.Option;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.Test;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionParamConverterProviderTest extends AbstractJerseyTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Override
    protected Application configure() {
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return DropwizardResourceConfig.forTesting(new MetricRegistry())
                .register(OptionParamFeature.class)
                .register(TestResource.class);
    }

    @Test
    public void headerSome() throws Exception {
        assertThat(target("/header").request().header("param", "Foobar").get(String.class)).isEqualTo("Foobar");
    }

    @Test
    public void headerNone() throws Exception {
        assertThat(target("/header").request().get().getStatus()).isEqualTo(404);
    }

    @Test
    public void cookieSome() throws Exception {
        assertThat(target("/cookie").request().cookie("param", "Foobar").get(String.class)).isEqualTo("Foobar");
    }

    @Test
    public void cookieNone() throws Exception {
        assertThat(target("/cookie").request().get().getStatus()).isEqualTo(404);
    }

    @Test
    public void pathSome() throws Exception {
        assertThat(target("/path/Foobar").request().get(String.class)).isEqualTo("Foobar");
    }

    @Test
    public void pathNone() throws Exception {
        // Standard path not found
        assertThat(target("/path").request().get().getStatus()).isEqualTo(404);
    }

    @Test
    public void querySome() throws Exception {
        assertThat(target("/query").queryParam("param", "Foobar").request().get(String.class)).isEqualTo("Foobar");
    }

    @Test
    public void queryNone() throws Exception {
        assertThat(target("/query").request().get().getStatus()).isEqualTo(404);
    }

    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public static class TestResource {
        @GET
        @Path("header")
        public String header(@HeaderParam("param") Option<String> param) {
            return param.getOrElseThrow(NotFoundException::new);
        }

        @GET
        @Path("cookie")
        public String cookie(@CookieParam("param") Option<String> param) {
            return param.getOrElseThrow(NotFoundException::new);
        }

        @GET
        @Path("path/{param}")
        public String path(@PathParam("param") Option<String> param) {
            return param.getOrElseThrow(NotFoundException::new);
        }

        @GET
        @Path("query")
        public String query(@QueryParam("param") Option<String> param) {
            return param.getOrElseThrow(NotFoundException::new);
        }
    }

}