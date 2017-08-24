package io.dropwizard.vavr.jersey;

import io.vavr.control.Either;
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
public class EitherMessageBodyWriter implements MessageBodyWriter<Either<?, ?>> {

    @Inject
    private javax.inject.Provider<MessageBodyWorkers> mbw;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize(Either<?, ?> entity, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return Either.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void writeTo(Either<?, ?> entity,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream)
            throws IOException {
        final Object entityValue;
        final int typeArgumentIndex;
        if (entity.isLeft()) {
            entityValue = entity.getLeft();
            typeArgumentIndex = 0;
        } else {
            entityValue = entity.get();
            typeArgumentIndex = 1;
        }
        final Class<?> entityClass = entityValue.getClass();

        final Type actualGenericTypeArgument;
        if (genericType instanceof ParameterizedType) {
            actualGenericTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[typeArgumentIndex];
        } else {
            actualGenericTypeArgument = entityClass;
        }

        final MessageBodyWriter writer = mbw.get().getMessageBodyWriter(entityClass,
                actualGenericTypeArgument, annotations, mediaType);
        writer.writeTo(entityValue, entityClass,
                actualGenericTypeArgument,
                annotations, mediaType, httpHeaders, entityStream);
    }

}
