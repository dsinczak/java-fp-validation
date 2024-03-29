package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dsinczak.fp.validation.javadsl.cf.CfCommon.sequence;
import static java.util.stream.Collectors.toList;

class MergedValidator<T> implements Validator<T> {

    private List<Validator<T>> validators;

    MergedValidator(List<Validator<T>> validators) {
        this.validators = validators;
    }

    @SafeVarargs
    MergedValidator(Validator<T>... validators) {
        this.validators = Arrays.asList(validators);
    }

    @Override
    public CompletableFuture<ValidationResult> validate(T t) {
        var appliedValidators = validators.stream()
                .map(validator -> validator.apply(t))
                .collect(toList());
        return sequence(appliedValidators)
                .thenApply(validationResults -> validationResults.stream()
                        .reduce(ValidationResult.success(), ValidationResult.concat)
                );
    }
}
