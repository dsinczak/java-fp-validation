package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.concurrent.CompletableFuture.allOf;

public class ForEachValidator<T> implements Validator<Iterable<T>> {

    private Validator<T> validator;

    ForEachValidator(Validator<T> validator) {
        this.validator = validator;
    }

    @Override
    public CompletableFuture<ValidationResult> validate(Iterable<T> iterable) {
        var appliedValidators = StreamSupport.stream(iterable.spliterator(), false)
                .map(t -> validator.validate(t))
                .collect(Collectors.toList());
        return allOf(appliedValidators.toArray(CompletableFuture[]::new))
                .thenApply(ignore -> appliedValidators.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                ).thenApply(validationResults -> validationResults.stream()
                        .reduce(ValidationResult.success(), ValidationResult.concat)
                );
    }
}
