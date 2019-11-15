package dsinczak.fp.validation.javadsl.ne;

import dsinczak.fp.validation.javadsl.ValidationResult;

public class FailFastForEachValidator<T> implements Validator<Iterable<T>> {

    private Validator<T> validator;

    FailFastForEachValidator(Validator<T> validator) {
        this.validator = validator;
    }

    @Override
    public ValidationResult validate(Iterable<T> iterable) {
        for (T t : iterable) {
            var validationResult = validator.validate(t);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }
        }
        return ValidationResult.success();
    }

}