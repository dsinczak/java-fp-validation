package dsinczak.fp.validation.javadsl.cf

import dsinczak.fp.validation.javadsl.ValidationResult
import spock.lang.Ignore
import spock.lang.Specification


class ValidatorExtractingCaseSpec extends Specification {

    def 'should iterate through list and apply validator returning success'() {
        given:
            def names = List.of("Damian", "Kinga", "Oliwier")
        when:
            def result = namesValidator.validate(names).join()
        then:
            result == ValidationResult.success()
    }

    def 'should iterate through list and apply validator returning all failed validations'() {
        given:
            def names = List.of("Damian", "Kinga", "R2D2", "c3Po")
        when:
            def result = namesValidator.validate(names).join()
        then:
            result == ValidationResult.failed("Name: R2D2 is not a proper name", "Name: c3Po is not a proper name")
    }

    @Ignore("Implement FailFastForEachValidator")
    def 'should iterate through list and apply validator returning first failed validation'() {
        given:
            def names = List.of("Damian", "Kinga", "R2D2", "c3Po")
        when:
            def result = namesValidatorFailFast.validate(names).join()
        then:
            result == ValidationResult.failed("Name: R2D2 is not a proper name")
    }

    Validator<String> nameValidator = { String name ->
        if (name != null
                && !name.isBlank()
                && !name.charAt(0).isLowerCase()
                && name.chars().allMatch({l -> Character.isLetter(l)}))
            return Validator.valid()
        else
            return Validator.invalid("Name: $name is not a proper name")
    }

    def namesValidator = Validator.forEach(nameValidator)
    def namesValidatorFailFast = Validator.forEachFailFast(nameValidator)


}