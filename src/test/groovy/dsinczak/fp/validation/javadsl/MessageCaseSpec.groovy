package dsinczak.fp.validation.javadsl

import spock.lang.Specification

import static dsinczak.fp.validation.javadsl.Message.ParametrizedMessage.*
import static dsinczak.fp.validation.javadsl.MessageCaseSpec.MessageCodes.USER_NOT_FOUND
import static dsinczak.fp.validation.javadsl.MessageCaseSpec.Parms.USER_NAME
import static dsinczak.fp.validation.javadsl.MessageCaseSpec.Parms.USER_SURNAME

class MessageCaseSpec extends Specification {

    enum MessageCodes implements Code<MessageCodes> {
        USER_NOT_FOUND
    }

    enum Parms implements Parm<Parms> {
        USER_NAME,
        USER_SURNAME,
    }

    def 'should equal two messages ignoring parameters order'() {
        when:
            def msg1 = Message.of(USER_NOT_FOUND, Map.of(USER_NAME, "Damian", USER_SURNAME, "Sinczak"))
            def msg2 = Message.of(USER_NOT_FOUND, Map.of(USER_SURNAME, "Sinczak", USER_NAME, "Damian"))
        then:
            msg1==msg2
    }

}
