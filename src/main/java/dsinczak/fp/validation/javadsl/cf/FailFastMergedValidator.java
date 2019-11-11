package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.completedFuture;

class FailFastMergedValidator<T> implements Validator<T> {

    private List<Validator<T>> validators;

    @SafeVarargs
    FailFastMergedValidator(Validator<T>... validators) {
        this.validators = Arrays.asList(validators);
    }

    FailFastMergedValidator(List<Validator<T>> validators) {
        this.validators = validators;
    }

    @Override
    public CompletableFuture<ValidationResult> validate(T t) {
        if (validators.isEmpty()) {
            return Validator.valid();
        }

        var validator = validators.get(0);
        return validator.validate(t).thenCompose(validationCircuitBreaker(t));
    }

    private Function<ValidationResult, CompletableFuture<ValidationResult>> validationCircuitBreaker(T t) {
        return validationResult -> {
            if (validationResult.isSuccess()) {
                return new FailFastMergedValidator<>(
                        validators.size() == 1
                                ? List.of()
                                : validators.subList(1, validators.size())
                ).validate(t);
            } else {
                return completedFuture(validationResult);
            }
        };
    }
}
