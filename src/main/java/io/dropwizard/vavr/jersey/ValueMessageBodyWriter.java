package io.dropwizard.vavr.jersey;

import io.vavr.Value;
import org.glassfish.jersey.message.MessageBodyWorkers;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.WILDCARD)
@Priority(Integer.MAX_VALUE)
public class ValueMessageBodyWriter implements MessageBodyWriter<Value<?>> {

    @Inject
    private javax.inject.Provider<MessageBodyWorkers> mbw;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize(Value<?> entity, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return Value.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void writeTo(Value<?> entity,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream)
            throws IOException {
        final Object entityValue = entity.getOrElseThrow(() -> EmptyValueException.INSTANCE);
        final Class<?> entityClass = entityValue.getClass();

        final Type actualGenericTypeArgument;
        if (genericType instanceof ParameterizedType) {
            actualGenericTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        } else {
            actualGenericTypeArgument = entityClass;
        }

        final MessageBodyWriter writer = mbw.get().getMessageBodyWriter(entityClass, actualGenericTypeArgument, annotations, mediaType);
        writer.writeTo(entityValue, entityClass, actualGenericTypeArgument, annotations, mediaType, httpHeaders, entityStream);
    }

}
