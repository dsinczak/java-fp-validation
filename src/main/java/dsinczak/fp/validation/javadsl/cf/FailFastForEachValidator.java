package dsinczak.fp.validation.javadsl.cf;

import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class FailFastForEachValidator<T> implements Validator<Iterable<T>> {

    private Validator<T> validator;

    FailFastForEachValidator(Validator<T> validator) {
        this.validator = validator;
    }

    @Override
    public CompletableFuture<ValidationResult> validate(Iterable<T> iterable) {
        return validate(iterable.iterator());
    }

    private CompletableFuture<ValidationResult> validate(Iterator<T> iterator) {
        if(!iterator.hasNext()) {
            return Validator.valid();
        }

        var next = iterator.next();

        return validator.validate(next).thenCompose(nextResult -> {
            if(nextResult.isSuccess()) {
                return this.validate(iterator);
            } else {
                return completedFuture(nextResult);
            }
        });
    }

}