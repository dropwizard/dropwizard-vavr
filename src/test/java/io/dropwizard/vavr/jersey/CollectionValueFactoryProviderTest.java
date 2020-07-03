package io.dropwizard.vavr.jersey;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.logging.BootstrapLogging;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import io.vavr.jackson.datatype.VavrModule;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionValueFactoryProviderTest extends JerseyTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Override
    protected Application configure() {
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return DropwizardResourceConfig.forTesting(new MetricRegistry())
            .register(new JacksonMessageBodyProvider(new ObjectMapper().registerModule(new VavrModule())))
            .register(CollectionParamFeature.class)
            .register(TestResource.class);
    }

    @Test
    public void headerElements() {
        assertThat(
            target("/header")
                .request()
                .header("paramV", "Foobar V")
                .header("paramV", "Baz V")
                .header("paramL", "Foobar L")
                .header("paramL", "Baz L")
                .header("paramA", "Foobar A")
                .header("paramA", "Baz A")
                .get(new GenericType<java.util.List<String>>() {})
        ).containsExactly(
            "Foobar V",
            "Baz V",
            "Foobar L",
            "Baz L",
            "Foobar A",
            "Baz A"
        );
    }

    @Test
    public void headersEmpty() {
        assertThat(
            target("/header")
                .request()
                .get(new GenericType<java.util.List<String>>() {})
        ).isEmpty();
    }

    @Test
    public void queryElements() {
        assertThat(
            target("/query")
                .queryParam("paramV", "Foobar V")
                .queryParam("paramV", "Baz V")
                .queryParam("paramL", "Foobar L")
                .queryParam("paramL", "Baz L")
                .queryParam("paramA", "Foobar A")
                .queryParam("paramA", "Baz A")
                .request()
                .get(new GenericType<java.util.List<String>>() {})
        ).containsExactly(
            "Foobar V",
            "Baz V",
            "Foobar L",
            "Baz L",
            "Foobar A",
            "Baz A"
        );
    }

    @Test
    public void queryEmpty() {
        assertThat(
            target("/query")
                .request()
                .get(new GenericType<java.util.List<String>>() {})
        ).isEmpty();
    }

    @Test
    public void formElements() {
        final Entity<Form> entity = Entity.form(
            new Form()
                .param("paramV", "Foobar V")
                .param("paramV", "Baz V")
                .param("paramL", "Foobar L")
                .param("paramL", "Baz L")
                .param("paramA", "Foobar A")
                .param("paramA", "Baz A")
        );
        assertThat(
            target("/form")
                .request()
                .post(entity, new GenericType<java.util.List<String>>() {})
        ).containsExactly(
            "Foobar V",
            "Baz V",
            "Foobar L",
            "Baz L",
            "Foobar A",
            "Baz A"
        );
    }

    @Test
    public void formEmpty() {
        final Entity<Form> entity = Entity.form(new Form());
        assertThat(
            target("/form")
                .request()
                .post(entity, new GenericType<java.util.List<String>>() {})
        ).isEmpty();
    }

    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public static class TestResource {
        @GET
        @Path("header")
        public Seq<String> header(@HeaderParam("paramV") final Vector<String> paramV,
                                  @HeaderParam("paramL") final List<String> paramL,
                                  @HeaderParam("paramA") final Array<String> paramA) {
            return Vector.ofAll(paramV).appendAll(paramL).appendAll(paramA);
        }

        @GET
        @Path("query")
        public Seq<String> query(@QueryParam("paramV") final Vector<String> paramV,
                                 @QueryParam("paramL") final List<String> paramL,
                                 @QueryParam("paramA") final Array<String> paramA) {
            return Vector.ofAll(paramV).appendAll(paramL).appendAll(paramA);
        }

        @POST
        @Path("form")
        public Seq<String> form(@FormParam("paramV") final Vector<String> paramV,
                                @FormParam("paramL") final List<String> paramL,
                                @FormParam("paramA") final Array<String> paramA) {
            return Vector.ofAll(paramV).appendAll(paramL).appendAll(paramA);
        }
    }
}
