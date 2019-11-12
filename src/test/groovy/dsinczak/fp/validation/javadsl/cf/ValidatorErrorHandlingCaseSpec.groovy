package dsinczak.fp.validation.javadsl.cf

import dsinczak.fp.validation.javadsl.Message
import dsinczak.fp.validation.javadsl.ValidationResult
import spock.lang.Specification

import java.util.concurrent.CompletableFuture


class ValidatorErrorHandlingCaseSpec extends Specification {

    def 'should handle all exceptions and fallback to failed validation result'() {
        given:
            Validator<String> plain = {ignore -> CompletableFuture.failedFuture(new IllegalArgumentException("Does not compute"))}
            Validator<String> exceptionProof = Validators.exceptionally(plain, {t-> Message.of("This happened: " + t.message)})
        when:
            def result = exceptionProof.validate('#@$@%$@#%$#%#$').join()
        then:
            result == ValidationResult.failed("This happened: Does not compute")
    }

}