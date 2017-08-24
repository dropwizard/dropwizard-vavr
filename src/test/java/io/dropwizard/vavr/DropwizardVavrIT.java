package io.dropwizard.vavr;

import com.google.common.io.ByteStreams;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DropwizardVavrIT {
    @ClassRule
    public static final DropwizardAppRule<Configuration> RULE = new DropwizardAppRule<>(VavrApplication.class);

    private Client client = null;

    @Before
    public void setUp() {
        client = new JerseyClientBuilder(RULE.getEnvironment()).build("test-" + UUID.randomUUID());
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void emptyOptionTextReturns404() {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/option-none", RULE.getLocalPort()))
                .request()
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void filledOptionTextReturns200() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/option-filled", RULE.getLocalPort()))
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
        Response response = client.target(
                String.format("http://localhost:%d/jersey/either-right", RULE.getLocalPort()))
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
        Response response = client.target(
                String.format("http://localhost:%d/jersey/either-left", RULE.getLocalPort()))
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
        Response response = client.target(
                String.format("http://localhost:%d/jersey/lazy", RULE.getLocalPort()))
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
        Response response = client.target(
                String.format("http://localhost:%d/jersey/option-path/", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void filledOptionPathParamReturns200() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/option-path/option", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("option");
    }

    @Test
    public void lazyMissingPathParamReturns404() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/lazy-path", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void lazyPathParamReturns200() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/lazy-path/lazy", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("lazy");
    }

    @Test
    public void emptyOptionQueryParamReturns404() {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/option-query", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void filledOptionQueryParamReturns200() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/option-query?param=option", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("option");
    }

    @Test
    public void lazyMissingQueryParamReturns204() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/lazy-query", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    public void lazyQueryParamReturns200() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/lazy-query?param=lazy", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.bufferEntity()).isTrue();
        assertThat(entityToString((InputStream) response.getEntity())).isEqualTo("lazy");
    }

    @Test
    public void emptyOptionHeaderParamReturns404() {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/option-header", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void filledOptionHeaderParamReturns200() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/option-header", RULE.getLocalPort()))
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
        Response response = client.target(
                String.format("http://localhost:%d/jersey/lazy-header", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    public void lazyHeaderParamReturns200() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/lazy-header", RULE.getLocalPort()))
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
        Response response = client.target(
                String.format("http://localhost:%d/jersey/option-cookie", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void filledOptionCookieParamReturns200() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/option-cookie", RULE.getLocalPort()))
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
        Response response = client.target(
                String.format("http://localhost:%d/jersey/lazy-cookie", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    public void lazyHeaderCookieReturns200() throws IOException {
        Response response = client.target(
                String.format("http://localhost:%d/jersey/lazy-cookie", RULE.getLocalPort()))
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
