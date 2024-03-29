package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.concurrent.CompletableFuture;

import static dsinczak.fp.validation.javadsl.cf.CfCommon.sequence;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class ForEachValidator<T> implements Validator<Iterable<T>> {

    private Validator<T> validator;

    ForEachValidator(Validator<T> validator) {
        this.validator = validator;
    }

    @Override
    public CompletableFuture<ValidationResult> validate(Iterable<T> iterable) {
        var appliedValidators = stream(iterable.spliterator(), false)
                .map(t -> validator.validate(t))
                .collect(toList());
        return sequence(appliedValidators)
                .thenApply(validationResults -> validationResults.stream()
                        .reduce(ValidationResult.success(), ValidationResult.concat)
                );
    }
}
