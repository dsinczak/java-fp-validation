package dsinczak.fp.validation.javadsl.ne

import dsinczak.fp.validation.javadsl.ValidationResult
import spock.lang.Specification


class ValidatorMergingCaseSpec extends Specification {

    def 'should merge validators and run all validations'() {
        given:
            def userValidator = Validators.merge(nameValidator, surnameValidator, ageValidator)
        when:
            def result = userValidator.validate(new User(name:null, surname: null, age:200))
        then:
            result == ValidationResult.failed(
                    "Name is a must and must start with capital letter",
                    "Surname is a must and must start with capital letter",
                    "Only turtles lives that long"
            )
    }

    def 'should merge validators and return merged result'() {
        given:
            def userValidator = Validators.merge(nameValidator, surnameValidator, ageValidator)
        when:
            def result = userValidator.validate(new User(name:null, surname: "Kowalski", age:200))
        then:
            result == ValidationResult.failed(
                    "Name is a must and must start with capital letter",
                    "Only turtles lives that long"
            )
    }

    def 'should merge validators with fail fast and run first validation only'() {
        given:
            def userValidator = Validators.mergeFailFast(nameValidator, surnameValidator, ageValidator)
        when:
            def result = userValidator.validate(new User(name:null, surname: null, age:200))
        then:
            result == ValidationResult.failed(
                    "Name is a must and must start with capital letter"
            )
    }

    Validator<User> nameValidator = { User u ->
        if((u.name == null || u.name.isBlank()) || u.name.charAt(0).isLowerCase())
            return ValidationResult.failed("Name is a must and must start with capital letter")
        else
            return ValidationResult.success()
    }

   Validator<User> surnameValidator = { User u ->
        if((u.surname == null || u.surname.isBlank()) || u.surname.charAt(0).isLowerCase())
            return ValidationResult.failed("Surname is a must and must start with capital letter")
        else
            return ValidationResult.success()
    }

    Validator<User> ageValidator = { User u ->
        if(u.age != null) {
            if (u.age < 0)
                return ValidationResult.failed("Age must be between")
            else if(u.age > 150)
                return ValidationResult.failed("Only turtles lives that long")
        }
        ValidationResult.success()
    }

    static class User {
        String name
        String surname
        Integer age
    }

}