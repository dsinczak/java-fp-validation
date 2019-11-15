package dsinczak.fp.validation.javadsl.ne;

import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.stream.StreamSupport;

public class ForEachValidator<T> implements Validator<Iterable<T>> {

    private Validator<T> validator;

    ForEachValidator(Validator<T> validator) {
        this.validator = validator;
    }

    @Override
    public ValidationResult validate(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(t -> validator.apply(t))
                .reduce(ValidationResult.success(), (vr1, vr2) -> vr1.concat(vr2));
    }
}
