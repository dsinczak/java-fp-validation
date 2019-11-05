package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.allOf;

class MergedValidator<T> implements Validator<T> {

    private List<Validator<T>> validators;

    @SafeVarargs
    MergedValidator(Validator<T>... validators) {
        this.validators = Arrays.asList(validators);
    }

    @Override
    public CompletableFuture<ValidationResult> validate(T t) {
        var appliedValidators = validators.stream().map(validator -> validator.apply(t)).collect(Collectors.toList());
        return allOf(appliedValidators.toArray(CompletableFuture[]::new))
                .thenApply(ignore -> appliedValidators.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                )
                .thenApply(validationResults -> validationResults.stream()
                        .reduce(ValidationResult.success(), ValidationResult.concat)
                );
    }
}
