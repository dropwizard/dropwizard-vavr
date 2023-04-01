package io.dropwizard.vavr.jersey;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * The default response when an empty {@link io.vavr.Value} is returned, is to respond with
 * a 404 (Not Found) response.
 */
public class EmptyValueExceptionMapper implements ExceptionMapper<EmptyValueException> {
    @Override
    public Response toResponse(EmptyValueException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
