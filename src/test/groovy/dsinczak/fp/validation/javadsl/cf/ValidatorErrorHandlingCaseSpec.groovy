package dsinczak.fp.validation.javadsl.cf

import dsinczak.fp.validation.javadsl.Message
import dsinczak.fp.validation.javadsl.ValidationResult
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.CompletableFuture

import static dsinczak.fp.validation.javadsl.ErrorCase.$


class ValidatorErrorHandlingCaseSpec extends Specification {

    def 'should handle all exceptions and fallback to failed validation result'() {
        given:
            Validator<String> plain = { ignore -> CompletableFuture.failedFuture(new IllegalArgumentException("Does not compute")) }
            Validator<String> exceptionProof = Validators.exceptionally(plain, { t -> Message.of("This happened: " + t.message) })
        when:
            def result = exceptionProof.validate('#@$@%$@#%$#%#$').join()
        then:
            result == ValidationResult.failed("This happened: Does not compute")
    }

    @Unroll
    def 'should handle exceptions with error case matching'() {
        given:
            Validator<String> plain = { ignore -> CompletableFuture.failedFuture(exception) }
            Validator<String> exceptionProof = plain.exceptionally(
                    $(IllegalArgumentException.class, { t -> Message.of("This happened: " + t.message) }),
                    $(IllegalStateException.class, { t -> Message.of("Handled: " + t.message) }),
                    $(NullPointerException.class, { t -> Message.of("Who's there") })
            )
        when:
            def result = exceptionProof.validate('#@$@%$@#%$#%#$').join()
        then:
            result == expectedResult
        where:
            exception                                           | expectedResult
            new IllegalArgumentException("Does not compute")    | ValidationResult.failed("This happened: Does not compute")
            new IllegalStateException("Wrong state")            | ValidationResult.failed("Handled: Wrong state")
            new NullPointerException("Knock know")              | ValidationResult.failed("Who's there")

    }

}