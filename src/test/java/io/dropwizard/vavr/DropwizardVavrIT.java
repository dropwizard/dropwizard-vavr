package io.dropwizard.vavr;

import com.google.common.io.ByteStreams;
import io.dropwizard.Configuration;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
public class DropwizardVavrIT {
    private static final DropwizardAppExtension<Configuration> dropwizard = new DropwizardAppExtension<>(VavrApplication.class);

    @Test
    public void emptyOptionTextReturns404() {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/option-none", dropwizard.getLocalPort()))
                .request()
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void filledOptionTextReturns200() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/option-filled", dropwizard.getLocalPort()))
                .request()
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("option");
    }

    @Test
    public void eitherRightTextReturnsRight() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/either-right", dropwizard.getLocalPort()))
                .request()
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("right");
    }

    @Test
    public void eitherLeftTextReturnsLeft() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/either-left", dropwizard.getLocalPort()))
                .request()
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("42");
    }

    @Test
    public void lazyTextReturns200() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/lazy", dropwizard.getLocalPort()))
                .request()
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("lazy");
    }

    @Test
    public void emptyOptionPathParamReturns404() {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/option-path/", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void filledOptionPathParamReturns200() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/option-path/option", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("option");
    }

    @Test
    public void lazyMissingPathParamReturns404() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/lazy-path", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void lazyPathParamReturns200() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/lazy-path/lazy", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("lazy");
    }

    @Test
    public void emptyOptionQueryParamReturns404() {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/option-query", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void filledOptionQueryParamReturns200() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/option-query?param=option", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("option");
    }

    @Test
    public void lazyMissingQueryParamReturns204() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/lazy-query", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    public void lazyQueryParamReturns200() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/lazy-query?param=lazy", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("lazy");
    }

    @Test
    public void emptyOptionHeaderParamReturns404() {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/option-header", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void filledOptionHeaderParamReturns200() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/option-header", dropwizard.getLocalPort()))
                .request()
                .header("X-Option", "option")
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("option");
    }

    @Test
    public void lazyMissingHeaderParamReturns204() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/lazy-header", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    public void lazyHeaderParamReturns200() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/lazy-header", dropwizard.getLocalPort()))
                .request()
                .header("X-Lazy", "lazy")
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("lazy");
    }

    @Test
    public void emptyOptionCookieParamReturns404() {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/option-cookie", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void filledOptionCookieParamReturns200() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/option-cookie", dropwizard.getLocalPort()))
                .request()
                .cookie("param", "option")
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("option");
    }

    @Test
    public void lazyMissingCookieParamReturns204() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/lazy-cookie", dropwizard.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    public void lazyHeaderCookieReturns200() throws IOException {
        Response response = dropwizard.client().target(
                String.format("http://localhost:%d/jersey/lazy-cookie", dropwizard.getLocalPort()))
                .request()
                .cookie("param", "lazy")
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("lazy");
    }

    private String entityToString(InputStream inputStream) throws IOException {
        final byte[] bytes = ByteStreams.toByteArray(inputStream);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
