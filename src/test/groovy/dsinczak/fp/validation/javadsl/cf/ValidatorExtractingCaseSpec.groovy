package dsinczak.fp.validation.javadsl.cf

import dsinczak.fp.validation.javadsl.Message
import dsinczak.fp.validation.javadsl.ValidationResult
import spock.lang.Specification

class ValidatorExtractingCaseSpec extends Specification {

    def 'should use merge extracting validators and return proper validation result'() {
        given:
            def cmd = new CreateCustomerCommand(
                    customer: new Customer(
                            name: "C3PO",
                            address: new Address(
                                    street: "Death Star #4"
                            )
                    )
            )
        when:
            def result = commandValidator.validate(cmd).join()
        then:
            result == ValidationResult.failed("Name: C3PO is not a proper name", "String: Death Star #4 is not valid street")
    }

    def 'should skip null field with ifExists extractor'() {
        given:
            def cmd = new CreateCustomerCommand(
                    customer: new Customer(
                            name: null,
                            address: new Address(
                                    street: "Death Star #4"
                            )
                    )
            )
        when:
            def result = commandValidator.validate(cmd).join()
        then:
            result == ValidationResult.failed("String: Death Star #4 is not valid street")
    }

    def 'should'() {
        given:
            def cmd = new CreateCustomerCommand(
                    customer: new Customer(
                            name: "C3PO",
                            address: null
                    )
            )
        when:
            def result = commandValidator.validate(cmd).join()
        then:
            result == ValidationResult.failed("Name: C3PO is not a proper name", "Address is required")
    }

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

    def 'should iterate through list and apply validator returning first failed validation'() {
        given:
            def names = List.of("Damian", "Kinga", "R2D2", "c3Po")
        when:
            def result = namesValidatorFailFast.validate(names).join()
        then:
            result == ValidationResult.failed("Name: R2D2 is not a proper name")
    }

    class CreateCustomerCommand {
        Customer customer
    }

    class Address {
        String street
    }

    class Customer {
        String name
        Address address
    }

    Validator<String> nameValidator = { String name ->
        if (name != null
                && !name.isBlank()
                && !name.charAt(0).isLowerCase()
                && name.chars().allMatch({ l -> Character.isLetter(l) }))
            return Validator.valid()
        else
            return Validator.invalid("Name: $name is not a proper name")
    }

    Validator<String> streetValidator = { String street ->
        if (street != null
                && !street.isBlank()
                && !street.charAt(0).isLowerCase()
                && street.chars().allMatch({ l -> Character.isLetter(l) || Character.isDigit(l) }))
            return Validator.valid()
        else
            return Validator.invalid("String: $street is not valid street")
    }

    Validator<Iterable<String>> namesValidator = Validators.forEach(nameValidator)
    Validator<Iterable<String>> namesValidatorFailFast = Validators.forEachFailFast(nameValidator)
    Validator<Address> addressValidator = Validators.extract({Address a->a.street}, streetValidator)
    Validator<Customer> customerValidator = Validators.merge(
            Validators.ifExists({Customer c-> c.name}, nameValidator),
            Validators.ifExistsOrElse({Customer c-> c.address}, addressValidator, Message.of("Address is required"))
    )
    Validator<CreateCustomerCommand> commandValidator = Validators.extract({CreateCustomerCommand c->c.customer}, customerValidator)

}