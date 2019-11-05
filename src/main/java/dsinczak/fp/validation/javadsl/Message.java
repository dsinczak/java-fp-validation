package dsinczak.fp.validation.javadsl;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Representation of validation message backed by:
 * <a href="https://4comprehension.com/effectively-sealed-classes-in-java/">Effectively sealed classes concept</a>
 * Two types of messages are implemented:
 * <ul>
 *     <li>{@link SimpleMessage} where message is represented by string value</li>
 *     <li>{@link ComplexMessage} which is type-safe representation of message represented by message code and message parameters</li>
 * </ul>
 */
public abstract class Message {

    private Message() {
    }

    public static final class ComplexMessage extends Message {

        /**
         * Type-safe message name (identifier).
         *
         * @param <E> this is just a 'try' to constraint implementators of this interface to enums.
         */
        public interface Code<E extends Enum<E>> { }

        /**
         * Type-safe message parameter name.
         *
         * @param <E> this is just a 'try' to constraint implementators of this interface to enums.
         */
        public interface Parm<E extends Enum<E>> { }

        private Code code;
        private Map<Parm, Object> parameters;

        private ComplexMessage(Code code, Map<Parm, Object> parameters) {
            Objects.requireNonNull(code, "Message code cannot be null");
            this.code = code;
            this.parameters = parameters;
        }

        public Code getCode() {
            return code;
        }

        public Map<Parm, Object> getParameters() {
            return Collections.unmodifiableMap(parameters);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ComplexMessage)) return false;
            ComplexMessage other = (ComplexMessage) o;

            return this.code == other.code
                    && this.parameters.entrySet().stream()
                    .allMatch(e -> e.getValue().equals(other.parameters.get(e.getKey())));
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, parameters);
        }

        @Override
        public String toString() {
            return "Message{" + code + ": [" +
                    // TODO make it lazy
                    parameters.entrySet().stream()
                            .map(e -> "(" + e.getKey() + ": " + e.getValue() + ")")
                            .collect(Collectors.joining(",")) +
                    "]}";
        }
    }

    public static final class SimpleMessage extends Message {

        private final String value;

        public SimpleMessage(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SimpleMessage)) return false;
            SimpleMessage that = (SimpleMessage) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Message{" +
                    "value='" + value + '\'' +
                    "} " + super.toString();
        }
    }

    public static Message of(ComplexMessage.Code code, Map<ComplexMessage.Parm, Object> parameters) {
        return new ComplexMessage(code, parameters != null ? Map.copyOf(parameters) : Map.of());
    }

    public static Message of(ComplexMessage.Code code) {
        return new ComplexMessage(code, Map.of());
    }

    public static Message of(String message) {
        return new SimpleMessage(message);
    }
}
