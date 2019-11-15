package dsinczak.fp.validation.javadsl.ne;

import dsinczak.fp.validation.javadsl.ErrorCase;
import dsinczak.fp.validation.javadsl.Message;
import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.function.Function;

/**
 * Validator interface. Basically validator is just a function that takes type and returns validation result of value of
 * that type. What is the strength of this solution is composition. Where we can compose complex object validation of
 * simple more granular validations.
 * Validator uses concept of Monoid:
 * <ul>
 *    <li>identity element: {@link Validator#neutral()}</li>
 *    <li>binary operation: {@link Validator#merge(Validator)}</li>
 * </ul>
 * but extends this concept with other operations useful in process of validation (like fail fast and exceptions handling)
 *
 * @param <T> validated type
 */
@FunctionalInterface
public interface Validator<T> extends Function<T, ValidationResult> {

    ValidationResult validate(T t);

    @Override
    default ValidationResult apply(T t) {
        return validate(t);
    }

    default Validator<T> merge(Validator<T> another) {
        return Validators.merge(this, another);
    }

    default Validator<T> mergeFailFast(Validator<T> another) {
        return Validators.mergeFailFast(this, another);
    }

    default Validator<T> exceptionally(Function<Throwable, Message> messageProvider) {
        return Validators.exceptionally(this, messageProvider);
    }

    default Validator<T> exceptionally(ErrorCase... cases) {
        return Validators.exceptionally(this, cases);
    }

    public static <A> Validator<A> neutral() {
        return a -> ValidationResult.success();
    }

}
