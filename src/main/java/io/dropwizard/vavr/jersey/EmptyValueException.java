package io.dropwizard.vavr.jersey;

/**
 * An exception thrown when a resource endpoint attempts to write out a
 * {@link io.vavr.Value} that is empty.
 */
public class EmptyValueException extends RuntimeException {
    public static final EmptyValueException INSTANCE = new EmptyValueException();

    private EmptyValueException() {
    }
}
