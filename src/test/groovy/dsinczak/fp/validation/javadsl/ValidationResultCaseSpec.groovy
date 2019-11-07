package dsinczak.fp.validation.javadsl

import spock.lang.Specification

import static dsinczak.fp.validation.javadsl.ValidationResult.concatMany
import static dsinczak.fp.validation.javadsl.ValidationResult.success
import static dsinczak.fp.validation.javadsl.ValidationResult.failed
import dsinczak.fp.validation.javadsl.ValidationResult.FailedValidation

class ValidationResultCaseSpec extends Specification {

    def 'should equal successful validation results'() {
        expect:
            success() == success()
    }

    def 'should equal failed results ignoring messages order'() {
        when:
            def fail1 = new FailedValidation(List.of(
                    Message.of("Message1"),
                    Message.of("Message2"),
                    Message.of("Message3")
            ))
            def fail2 = new FailedValidation(List.of(
                    Message.of("Message2"),
                    Message.of("Message1"),
                    Message.of("Message3")
            ))
        then:
            fail1 == fail2
    }

    def 'should concat successful and failed validation into failure'() {
        given:
            def failedValidation = failed("bad message")
        when:
            def successConcatFail = success().concat(failedValidation)
            def failConcatSuccess = failedValidation.concat(success())
        then:
            successConcatFail == failedValidation
            failConcatSuccess == failedValidation
    }

    def 'should concat many results into one with summed up messages'() {
        given:
            def fail1 = failed("bad user")
            def fail2 = failed("bad dog")
            def fail3 = failed("bad grammar")
        when:
            def concatenated = concatMany(success(), fail1, fail2, fail3)
        then:
            concatenated == new FailedValidation(List.of(
                    Message.of("bad dog"),
                    Message.of("bad user"),
                    Message.of("bad grammar")
            ))
    }

}