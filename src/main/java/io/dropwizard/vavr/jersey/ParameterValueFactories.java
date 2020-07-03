package io.dropwizard.vavr.jersey;

import io.vavr.control.Try;
import org.glassfish.jersey.server.ParamException;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractor;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;

abstract class ParameterValueFactories {
    static class QueryParamValueFactory extends AbstractContainerRequestValueFactory<Object> {
        private final MultivaluedParameterExtractor<?> extractor;
        private final boolean decode;

        public QueryParamValueFactory(final MultivaluedParameterExtractor<?> extractor, boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }

        @Override
        public Object provide() {
            return Try.of(() -> {
                final MultivaluedMap<String, String> parameters = getContainerRequest().getUriInfo().getQueryParameters(decode);
                return extractor.extract(parameters);
            }).getOrElseThrow(e -> new ParamException.QueryParamException(e.getCause(), extractor.getName(), extractor.getDefaultValueString()));
        }
    }

    static class FormParamValueFactory extends AbstractContainerRequestValueFactory<Object> {
        private final MultivaluedParameterExtractor<?> extractor;

        public FormParamValueFactory(final MultivaluedParameterExtractor<?> extractor) {
            this.extractor = extractor;
        }

        @Override
        public Object provide() {
            return Try.of(() -> {
                getContainerRequest().bufferEntity();
                final Form form = getContainerRequest().readEntity(Form.class);
                return extractor.extract(form.asMap());
            }).getOrElseThrow(e -> new ParamException.FormParamException(e.getCause(), extractor.getName(), extractor.getDefaultValueString()));
        }
    }

    static class HeaderParamValueFactory extends AbstractContainerRequestValueFactory<Object> {
        private final MultivaluedParameterExtractor<?> extractor;

        public HeaderParamValueFactory(MultivaluedParameterExtractor<?> extractor) {
            this.extractor = extractor;
        }

        @Override
        public Object provide() {
            return Try
                .of(() -> extractor.extract(getContainerRequest().getHeaders()))
                .getOrElseThrow(e -> new ParamException.HeaderParamException(e.getCause(), extractor.getName(), extractor.getDefaultValueString()));
        }
    }

    private ParameterValueFactories() {
        throw new AssertionError();
    }
}
