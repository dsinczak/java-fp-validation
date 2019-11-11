package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.Message;
import dsinczak.fp.validation.javadsl.Message.ComplexMessage;
import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static dsinczak.fp.validation.javadsl.ValidationResult.failed;
import static dsinczak.fp.validation.javadsl.ValidationResult.success;
import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Validator interface.
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

    public static CompletableFuture<ValidationResult> invalid(ComplexMessage.Code code) {
        return completedFuture(failed(code));
    }

    public static CompletableFuture<ValidationResult> invalid(ComplexMessage.Code code, Map<ComplexMessage.Parm, Object> parms) {
        return completedFuture(failed(code, parms));
    }

    public static CompletableFuture<ValidationResult> invalid(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1) {
        return completedFuture(failed(code, p1, v1));
    }

    public static CompletableFuture<ValidationResult> invalid(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1, ComplexMessage.Parm p2, Object v2) {
        return completedFuture(failed(code, p1, v1, p2, v2));
    }

    public static CompletableFuture<ValidationResult> invalid(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1, ComplexMessage.Parm p2, Object v2, ComplexMessage.Parm p3, Object v3) {
        return completedFuture(failed(code, p1, v1, p2, v2, p3, v3));
    }

    public static CompletableFuture<ValidationResult> invalid(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1, ComplexMessage.Parm p2, Object v2, ComplexMessage.Parm p3, Object v3, ComplexMessage.Parm p4, Object v4) {
        return completedFuture(failed(code, p1, v1, p2, v2, p3, v3, p4, v4));
    }

    public static CompletableFuture<ValidationResult> invalid(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1, ComplexMessage.Parm p2, Object v2, ComplexMessage.Parm p3, Object v3, ComplexMessage.Parm p4, Object v4, ComplexMessage.Parm p5, Object v5) {
        return completedFuture(failed(code, p1, v1, p2, v2, p3, v3, p4, v4, p5, v5));
    }

    public static CompletableFuture<ValidationResult> invalid(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1, ComplexMessage.Parm p2, Object v2, ComplexMessage.Parm p3, Object v3, ComplexMessage.Parm p4, Object v4, ComplexMessage.Parm p5, Object v5, ComplexMessage.Parm p6, Object v6) {
        return completedFuture(failed(code, p1, v1, p2, v2, p3, v3, p4, v4, p5, v5, p6, v6));
    }
}
