package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.Message;
import dsinczak.fp.validation.javadsl.Message.ComplexMessage;
import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        return merge(this, another);
    }

    default Validator<T> mergeFailFast(Validator<T> another) {
        return mergeFailFast(this, another);
    }

    @SafeVarargs
    public static <S> Validator<S> merge(Validator<S>... validators) {
        return new MergedValidator<>(validators);
    }

    @SafeVarargs
    public static <S> Validator<S> mergeFailFast(Validator<S>... validators) {
        return new FailFastMergedValidator<>(validators);
    }

    public static <S> Validator<S> merge(List<Validator<S>> validators) {
        return new MergedValidator<>(validators);
    }

    public static <S> Validator<S> mergeFailFast(List<Validator<S>> validators) {
        return new FailFastMergedValidator<>(validators);
    }

    public static <A, B> Validator<A> extract(Function<A, B> extractor, Validator<B> validator) {
        return a -> validator.apply(extractor.apply(a));
    }

    public static <A, B> Validator<A> ifExists(Function<A, B> extractor, Validator<B> validator) {
        return a -> Optional.ofNullable(extractor.apply(a))
                .map(validator)
                .orElseGet(Validator::valid);
    }

    public static <A, B> Validator<A> ifExistsOrElse(Function<A, B> extractor, Validator<B> validator, Message orElseMessage) {
        return a -> Optional.ofNullable(extractor.apply(a))
                .map(validator)
                .orElseGet(() -> Validator.invalid(orElseMessage));
    }

    public static <A> Validator<Iterable<A>> forEach(Validator<A> validator) {
        return new ForEachValidator<>(validator);
    }

    public static <A> Validator<Iterable<A>> forEachFailFast(Validator<A> validator) {
        return new FailFastForEachValidator<>(validator);
    }

    public static <A, B> Validator<A> forEach(Function<A, Iterable<B>> extractor, Validator<B> validator) {
        return a -> forEach(validator).apply(extractor.apply(a));
    }

    public static <A, B> Validator<A> forEachFailFast(Function<A, Iterable<B>> extractor, Validator<B> validator) {
        return a -> forEachFailFast(validator).apply(extractor.apply(a));
    }

    public static <A> Validator<A> neutral() {
        return a -> valid();
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
