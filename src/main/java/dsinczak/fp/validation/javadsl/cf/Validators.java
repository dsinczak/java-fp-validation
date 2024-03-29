package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.ErrorCase;
import dsinczak.fp.validation.javadsl.Message;
import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.*;


public abstract class Validators {

    private Validators() {
    }

    ////////////////////////////
    //        MERGING         //
    ////////////////////////////

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

    ////////////////////////////
    //       ITERATION        //
    ////////////////////////////

    public static <A> Validator<Iterable<A>> forEach(Validator<A> validator) {
        return new ForEachValidator<>(validator);
    }

    public static <A> Validator<Iterable<A>> forEachFailFast(Validator<A> validator) {
        return new FailFastForEachValidator<>(validator);
    }

    ////////////////////////////
    //       EXTRACTION       //
    ////////////////////////////

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

    public static <A, B> Validator<A> forEach(Function<A, Iterable<B>> extractor, Validator<B> validator) {
        return a -> forEach(validator).apply(extractor.apply(a));
    }

    public static <A, B> Validator<A> forEachFailFast(Function<A, Iterable<B>> extractor, Validator<B> validator) {
        return a -> forEachFailFast(validator).apply(extractor.apply(a));
    }

    ////////////////////////////
    //         ERRORS         //
    ////////////////////////////

    public static <A> Validator<A> exceptionally(Validator<A> validator, Function<Throwable, Message> messageProvider) {
        return a -> validator.apply(a)
                .exceptionally(throwable -> ValidationResult.failed(messageProvider.apply(throwable)));
    }

    public static <A> Validator<A> exceptionally(Validator<A> validator, List<ErrorCase> cases) {
        return a -> validator.apply(a)
                .exceptionally(throwable -> ErrorCase.findOrRethrow(cases, throwable));
    }

    public static <A> Validator<A> exceptionally(Validator<A> validator, ErrorCase... cases) {
        return exceptionally(validator, Arrays.stream(cases).collect(Collectors.toList()));
    }

    /////////////////////////////////////
    //   NO EFFECT VALIDATOR LIFTING   //
    /////////////////////////////////////

    public static <A> Validator<A> lift(dsinczak.fp.validation.javadsl.ne.Validator<A> noEffectValidator) {
        return a-> {
            try {
                return completedFuture(noEffectValidator.validate(a));
            } catch (Throwable throwable) {
                return failedFuture(throwable);
            }
        };
    }

    public static <A> Validator<A> lift(dsinczak.fp.validation.javadsl.ne.Validator<A> noEffectValidator, Executor executor) {
        return a -> supplyAsync(() -> noEffectValidator.validate(a), executor);
    }
}
