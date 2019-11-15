package dsinczak.fp.validation.javadsl.ne;

import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.Arrays;
import java.util.List;

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
    public ValidationResult validate(T t) {
        for(Validator<T> validator : validators) {
            var validationResult = validator.validate(t);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }
        }
        return ValidationResult.success();
    }

}
