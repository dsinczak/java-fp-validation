package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.concurrent.CompletableFuture;

public class FailFastForEachValidator<T> implements Validator<Iterable<T>> {

    private Validator<T> validator;

    FailFastForEachValidator(Validator<T> validator) {
        this.validator = validator;
    }

    @Override
    public CompletableFuture<ValidationResult> validate(Iterable<T> iterable) {
        return Validator.valid();
    }

}