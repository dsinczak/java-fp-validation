package dsinczak.fp.validation.javadsl;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This interface is basically specific type of partial function.
 */
public interface ErrorCase {

    ValidationResult handle(Throwable t);

    boolean matches(Throwable t);

    static ErrorCase $(Predicate<Throwable> tp, Function<Throwable, Message> messageProvider) {
        return new ErrorCase() {

            @Override
            public ValidationResult handle(Throwable t) {
                return ValidationResult.failed(messageProvider.apply(t));
            }

            @Override
            public boolean matches(Throwable t) {
                return tp.test(t);
            }
        };
    }

    static ErrorCase $(Class<? extends Throwable> tc, Function<Throwable, Message> messageProvider) {
        return $(tc::isInstance, messageProvider);
    }

    static ErrorCase $(Function<Throwable, Message> messageProvider) {
        return $(t -> true, messageProvider);
    }

}