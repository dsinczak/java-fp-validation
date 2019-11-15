package dsinczak.fp.validation.javadsl;

import dsinczak.fp.validation.javadsl.Message.ComplexMessage;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

/**
 * Validation result represents the outcome of whole validation process as single value.
 * This implementation is abstract data type and uses 2 concepts:
 * <li>Effectively sealed class - example:
 *  <a href="https://4comprehension.com/effectively-sealed-classes-in-java/">Effectively sealed classes in java</a>
 * </li>
 * <li>Monoid - algebraic structure with a single associative binary operation and an identity element. Where:
 *  <ul>
 *     <li>identity element: {@link SuccessfulValidation}</li>
 *     <li>binary operation: {@link ValidationResult#concat(ValidationResult, ValidationResult)}</li>
 *  </ul>
 * </li>
 */
public abstract class ValidationResult {

    private ValidationResult() { }

    /**
     * Validation success
     */
    public static final class SuccessfulValidation extends ValidationResult {
        private static final SuccessfulValidation INSTANCE = new SuccessfulValidation();

        private SuccessfulValidation() { }

        public static SuccessfulValidation instance() { return INSTANCE; }

        @Override
        public String toString() { return "SuccessfulValidation"; }

        // equals
        // hashCode
        // We can rely on default as SuccessfulValidation is singleton
    }

    /**
     * Validation failure along with validation failure information ({@link Message})
     */
    public static final class FailedValidation extends ValidationResult {

        // Because mutable collections are used we need to
        // be careful about its leaking outside of validation result
        List<Message> messages;

        public FailedValidation(Message messages) {
            this(List.of(messages));
        }

        FailedValidation(List<Message> messages) {
            this.messages = messages;
        }

        public List<Message> getMessages() { return unmodifiableList(messages); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FailedValidation)) return false;
            FailedValidation that = (FailedValidation) o;
            return this.messages.size()==that.messages.size()
                    && this.messages.containsAll(that.messages);
        }

        @Override
        public int hashCode() {
            return Objects.hash(messages);
        }

        @Override
        public String toString() {
            return "FailedValidation{" +
                    "messages=" + messages +
                    "} ";
        }
    }

    /**
     * Check if validation result represents success.
     *
     * @return <code>true</code> when instance is successful validation result
     */
    public boolean isSuccess() { return this instanceof SuccessfulValidation; }

    /**
     * Sum this validation result with argument one
     */
    public ValidationResult concat(ValidationResult b) { return concat(this, b); }

    public static BinaryOperator<ValidationResult> concat = (a, b) -> concat(a,b);

    public static ValidationResult concatMany(ValidationResult ... results) {
        return Arrays.stream(results).reduce(success(), concat);
    }

    public static ValidationResult concat(ValidationResult a, ValidationResult b) {
        if (a instanceof SuccessfulValidation) {
            if (b instanceof SuccessfulValidation) {
                // op(a success, b success) -> success
                return success();
            } else {
                // op(a success, b failed) -> b
                return b;
            }
        } else {
            if (b instanceof SuccessfulValidation) {
                // op(a failed, b success) -> a
                return a;
            } else {
                // op(a failed, b failed )-> a + b
                var aMessages = ((FailedValidation)a).messages;
                var bMessages = ((FailedValidation)b).messages;
                // Defensive copy
                var messages = new ArrayList<Message>(aMessages.size()+bMessages.size());
                messages.addAll(aMessages);
                messages.addAll(bMessages);
                return new FailedValidation(messages);
            }
        }
    }


    ///////////////////////////////////////
    //           Factory methods         //
    ///////////////////////////////////////

    /**
     * @return  SuccessfulValidation instance
     */
    public static ValidationResult success() { return SuccessfulValidation.instance(); }

    public static ValidationResult failed(Message message) { return new FailedValidation(message); }

    public static ValidationResult failed(String message) { return new FailedValidation(Message.of(message)); }

    public static ValidationResult failed(String ... messages) { return new FailedValidation(Arrays.stream(messages).map(Message::of).collect(Collectors.toList())); }

    public static ValidationResult failed(ComplexMessage.Code code) { return new FailedValidation(Message.of(code)); }

    public static ValidationResult failed(ComplexMessage.Code code, Map<ComplexMessage.Parm, Object> parms) { return new FailedValidation(Message.of(code, parms)); }

    public static ValidationResult failed(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1) {
        return new FailedValidation(Message.of(code, Map.of(p1, v1)));
    }

    public static ValidationResult failed(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1, ComplexMessage.Parm p2, Object v2) {
        return new FailedValidation(Message.of(code, Map.of(p1, v1, p2, v2)));
    }

    public static ValidationResult failed(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1, ComplexMessage.Parm p2, Object v2, ComplexMessage.Parm p3, Object v3) {
        return new FailedValidation(Message.of(code, Map.of(p1, v1, p2, v2, p3, v3)));
    }

    public static ValidationResult failed(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1, ComplexMessage.Parm p2, Object v2, ComplexMessage.Parm p3, Object v3, ComplexMessage.Parm p4, Object v4) {
        return new FailedValidation(Message.of(code, Map.of(p1, v1, p2, v2, p3, v3, p4, v4)));
    }

    public static ValidationResult failed(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1, ComplexMessage.Parm p2, Object v2, ComplexMessage.Parm p3, Object v3, ComplexMessage.Parm p4, Object v4, ComplexMessage.Parm p5, Object v5) {
        return new FailedValidation(Message.of(code, Map.of(p1, v1, p2, v2, p3, v3, p4, v4, p5, v5)));
    }

    public static ValidationResult failed(ComplexMessage.Code code, ComplexMessage.Parm p1, Object v1, ComplexMessage.Parm p2, Object v2, ComplexMessage.Parm p3, Object v3, ComplexMessage.Parm p4, Object v4, ComplexMessage.Parm p5, Object v5, ComplexMessage.Parm p6, Object v6) {
        return new FailedValidation(Message.of(code, Map.of(p1, v1, p2, v2, p3, v3, p4, v4, p5, v5, p6, v6)));
    }

    // i could go up to e.g. 10 but i see no point for now

}
