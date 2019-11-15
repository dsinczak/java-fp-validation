package dsinczak.fp.validation.javadsl.ne;

import dsinczak.fp.validation.javadsl.ValidationResult;

import java.util.Arrays;
import java.util.List;

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
    public ValidationResult validate(T t) {
        return validators.stream()
                .map(validator -> validator.apply(t))
                .reduce(ValidationResult.success(), (vr1,vr2)->vr1.concat(vr2));
    }
}
