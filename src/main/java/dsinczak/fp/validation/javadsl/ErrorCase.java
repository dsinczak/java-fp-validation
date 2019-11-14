package dsinczak.fp.validation.javadsl;

import java.util.function.Function;

/**
 * This interface is basically specific type of partial function.
 */
public interface ErrorCase {

    ValidationResult handle(Throwable t);

    boolean matches(Throwable t);

    static <T extends Throwable> ErrorCase $(Class<T> tc, Function<Throwable, Message> messageProvider) {
        return new ErrorCase(){

            @Override
            public ValidationResult handle(Throwable t) {
                return ValidationResult.failed(messageProvider.apply(t));
            }

            @Override
            public boolean matches(Throwable t) {
                return tc.isInstance(t);
            }
        };
    }

}