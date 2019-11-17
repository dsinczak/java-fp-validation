package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.ErrorCase;
import dsinczak.fp.validation.javadsl.Message;
import dsinczak.fp.validation.javadsl.Message.ParametrizedMessage;
import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static dsinczak.fp.validation.javadsl.ValidationResult.failed;
import static dsinczak.fp.validation.javadsl.ValidationResult.success;
import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Validator interface. Basically validator is just a function that takes type and returns validation result of value of
 * that type wrapped in completable future effect. What is the strength of this solution is composition. Where we can
 * compose complex object validation of simple more granular validations.
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
public interface Validator<T> extends Function<T, CompletableFuture<ValidationResult>> {

    CompletableFuture<ValidationResult> validate(T t);

    @Override
    default CompletableFuture<ValidationResult> apply(T t) {
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
        return a -> Validator.valid();
    }

    //////////////////////////////////////////////////////////
    //     Effect (CompletableFuture) factory methods       //
    //////////////////////////////////////////////////////////

    /**
     * @return SuccessfulValidation instance
     */
    public static CompletableFuture<ValidationResult> valid() {
        return completedFuture(success());
    }

    public static CompletableFuture<ValidationResult> invalid(Message message) {
        return completedFuture(failed(message));
    }

    public static CompletableFuture<ValidationResult> invalid(String message) {
        return completedFuture(failed(message));
    }

    public static CompletableFuture<ValidationResult> invalid(ParametrizedMessage.Code code) {
        return completedFuture(failed(code));
    }

    public static CompletableFuture<ValidationResult> invalid(ParametrizedMessage.Code code, Map<ParametrizedMessage.Parm, Object> parms) {
        return completedFuture(failed(code, parms));
    }

    public static CompletableFuture<ValidationResult> invalid(ParametrizedMessage.Code code, ParametrizedMessage.Parm p1, Object v1) {
        return completedFuture(failed(code, p1, v1));
    }

    public static CompletableFuture<ValidationResult> invalid(ParametrizedMessage.Code code, ParametrizedMessage.Parm p1, Object v1, ParametrizedMessage.Parm p2, Object v2) {
        return completedFuture(failed(code, p1, v1, p2, v2));
    }

    public static CompletableFuture<ValidationResult> invalid(ParametrizedMessage.Code code, ParametrizedMessage.Parm p1, Object v1, ParametrizedMessage.Parm p2, Object v2, ParametrizedMessage.Parm p3, Object v3) {
        return completedFuture(failed(code, p1, v1, p2, v2, p3, v3));
    }

    public static CompletableFuture<ValidationResult> invalid(ParametrizedMessage.Code code, ParametrizedMessage.Parm p1, Object v1, ParametrizedMessage.Parm p2, Object v2, ParametrizedMessage.Parm p3, Object v3, ParametrizedMessage.Parm p4, Object v4) {
        return completedFuture(failed(code, p1, v1, p2, v2, p3, v3, p4, v4));
    }

    public static CompletableFuture<ValidationResult> invalid(ParametrizedMessage.Code code, ParametrizedMessage.Parm p1, Object v1, ParametrizedMessage.Parm p2, Object v2, ParametrizedMessage.Parm p3, Object v3, ParametrizedMessage.Parm p4, Object v4, ParametrizedMessage.Parm p5, Object v5) {
        return completedFuture(failed(code, p1, v1, p2, v2, p3, v3, p4, v4, p5, v5));
    }

    public static CompletableFuture<ValidationResult> invalid(ParametrizedMessage.Code code, ParametrizedMessage.Parm p1, Object v1, ParametrizedMessage.Parm p2, Object v2, ParametrizedMessage.Parm p3, Object v3, ParametrizedMessage.Parm p4, Object v4, ParametrizedMessage.Parm p5, Object v5, ParametrizedMessage.Parm p6, Object v6) {
        return completedFuture(failed(code, p1, v1, p2, v2, p3, v3, p4, v4, p5, v5, p6, v6));
    }
}
