package dsinczak.fp.validation.javadsl;

import java.util.List;
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

    static ValidationResult findOrRethrow(List<ErrorCase> cases, Throwable throwable) {
        return cases.stream()
                .filter(ec -> ec.matches(throwable))
                .findFirst()
                .map(errorCase -> errorCase.handle(throwable))
                .orElseGet(() -> {
                    softenedException(throwable);
                    return null;
                });
    }

    private static <T extends Throwable> void softenedException(final T e) {
        uncheck(e);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void uncheck(Throwable throwable) throws T {
        throw (T) throwable;
    }
}