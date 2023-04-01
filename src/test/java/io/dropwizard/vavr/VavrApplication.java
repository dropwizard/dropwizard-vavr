package io.dropwizard.vavr;

import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.vavr.Lazy;
import io.vavr.control.Either;
import io.vavr.control.Option;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

public class VavrApplication extends Application<Configuration> {
    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(new VavrBundle(true));
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(VavrJerseyResource.class);
    }

    @Path("/jersey")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public static class VavrJerseyResource {
        @GET
        @Path("/option-none")
        public Option<String> optionNoneResult() {
            return Option.none();
        }

        @GET
        @Path("/option-filled")
        public Option<String> optionFilledResult() {
            return Option.of("option");
        }

        @GET
        @Path("/lazy")
        public Lazy<String> lazyResult() {
            return Lazy.of(() -> "lazy");
        }

        @GET
        @Path("/either-right")
        public Either<Integer, String> eitherRightResult() {
            return Either.right("right");
        }

        @GET
        @Path("/either-left")
        public Either<Integer, String> eitherLeftResult() {
            return Either.left(42);
        }

        @GET
        @Path("/option-path/{param}")
        public String optionPathParam(@PathParam("param") Option<String> option) {
            return option.getOrElseThrow(NotFoundException::new);
        }

        @GET
        @Path("/lazy-path/{param}")
        public String lazyPathParam(@PathParam("param") Lazy<String> lazy) {
            return lazy.getOrElseThrow(NotFoundException::new);
        }

        @GET
        @Path("/option-query")
        public String optionQueryParam(@QueryParam("param") Option<String> option) {
            return option.getOrElseThrow(NotFoundException::new);
        }

        @GET
        @Path("/lazy-query")
        public String lazyQueryParam(@QueryParam("param") Lazy<String> lazy) {
            return lazy.getOrElseThrow(NotFoundException::new);
        }

        @GET
        @Path("/option-header")
        public String optionHeaderParam(@HeaderParam("X-Option") Option<String> option) {
            return option.getOrElseThrow(NotFoundException::new);
        }

        @GET
        @Path("/lazy-header")
        public String lazyHeaderParam(@HeaderParam("X-Lazy") Lazy<String> lazy) {
            return lazy.getOrElseThrow(NotFoundException::new);
        }

        @GET
        @Path("/option-cookie")
        public String optionCookieParam(@CookieParam("param") Option<String> option) {
            return option.getOrElseThrow(NotFoundException::new);
        }

        @GET
        @Path("/lazy-cookie")
        public String lazyCookieParam(@CookieParam("param") Lazy<String> lazy) {
            return lazy.getOrElseThrow(NotFoundException::new);
        }
    }
}
