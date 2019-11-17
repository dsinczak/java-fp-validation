# java-fp-validation
A small validation library written in Java. Its main purpose is to provide the simple object validation mechanism (
each validator is simply a function) that allows for very easy composition and re-usability of the validators.

Additionally, the project is an attempt to show (and experiment with) functional programming approach (or being more 
critical: functional'ish).

Main drivers:
* Validator as a function
* Get the most of declarative and functional programming (but without exaggeration)
* Structure of validators should be easily composed
* Easy declarative exception handling 
* Easy to test
* Easy to use with [DDD](https://en.wikipedia.org/wiki/Domain-driven_design) aggregates. Aggregates protects business
logic invariants in many ways, one of them is incoming requests validation (specification is one of the patterns used).
This library is in some ways reification of specification pattern. Allowing easy validation of commands send to aggregate.

## Overview
The mechanism that I created has already been used by me in several commercial projects (sometimes in more, sometimes 
in less extensive form) and so far it worked quite well so I decided to create a publicly available version. 
In addition, I set a goal for a small experiment: can we simulate higher order types in Java. The library in its 
current shape has 2 flavors:
* **synchronous** - available in the package: ```dsinczak.fp.validation.javadsl.ne```
* **asynchronous** (based on *CompletableFuture*) - available in the package: ```dsinczak.fp.validation.javadsl.cf```

In the next steps, I would like to create a more generic implementation, allowing the API user to decide on what effect 
he will base his implementation (sync, *CompletableFuture*, *vavr.Future*, *vavr.Try* or even *Optional* if only he 
would decide)

## Usage

Validator definition is basically a function that takes argument of certain type and returns ValidationResult instance:
````java
Validator<String> startsWithCapitalLetter = str ->
        Character.isUpperCase(str.charAt(0))
                ? success()
                : failed("Name must start with capital letter");

Validator<String> onlyLetters = str ->
        str.chars().allMatch(Character::isLetter)
                ? success()
                : failed("Only letters are allowed in name");
````

### Composition
The composition allows you to build complex validation chains from smaller components. This makes the code easy to 
maintain and test. In addition, re-usability is an additional advantage.

````java
Validator<String> nameValidator = startsWithCapitalLetter.merge(onlyLetters);
Validator<String> nameFFValidator = startsWithCapitalLetter.mergeFailFast(onlyLetters);

println(nameValidator.validate("c3PO"));
println(nameFFValidator.validate("c3PO"));
````
execution:
```
FailedValidation{messages=[Name must start with capital letter, Only letters are allowed in name]} 
FailedValidation{messages=[Name must start with capital letter]} 
```

This is very simple name validator composed of 2 simpler string validators (i agree that, it can be done with single regexp
but I will stick to this example as it does not draw attention away from main topic).

What is noteworthy is the fact that we have 2 types of validator combinations here:
* all validations are started regardless of the previous result: ``merge.(...)``
* validations are carried out in turn and stopped in the event of the first failure: ``mergeFailFast(...)``

Thanks to this approach, we have full control over the chain of validation calls. We can interrupt it as soon as possible 
and return to the caller with the result or perform all validations to return as many validation errors as possible. 
Additionally, such joining can be nested on many levels (nothing stands in the way of joining validators connected with 
another chain).

### Extraction
Now, for the purpose of another example lets introduce domain class:
````java
class User {
    String name;
    Integer age;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
````
and new validators:
````java
Validator<Integer> ageValidator = age ->
        age > 0 ? age < 150 ? success() : failed("Nobody lives that long")
                : failed("Age must be greater than 0");

Validator<User> userValidator = Validators.merge(
        Validators.ifExists(u->u.name, nameValidator),
        Validators.ifExistsOrElse(u->u.age, ageValidator, Message.of("Age is a must"))
);
````
execution:
````java
println(userValidator.validate(new User("Damian", 34)));
// SuccessfulValidation

println(userValidator.validate(new User("c3PO", 180)));
// FailedValidation{messages=[Name must start with capital letter, Only letters are allowed in name, Nobody lives that long]} 

println(userValidator.validate(new User("Damian", null)));
// FailedValidation{messages=[Age is a must]} 

println(userValidator.validate(new User(null, 34)));
// SuccessfulValidation
````

In this example we used (as in previous one) ``merge(...)`` combinator. Also there are two new ones ``ifExists(...)`` 
``ifExistsOrElse(...)`` where:
* **ifExists** - as the first argument it accepts function that extract the name field from the user object, and as 
the second, validator of name type. The resulting validator will only be called if the name field is not null, 
otherwise validation will not occur.
* **ifExistsOrElse** - unlike ifExists, it will trigger validation when the field is not null or return 
a validation error with the message defined as the third argument when field is null

### Collections
We can also validate collections of objects combining single object validator:
```java
 var names = List.of("Damian", "Kinga", "Oliwier", "r2D2", "c3PO");

Validator<Iterable<String>> namesValidator = Validators.forEach(nameValidator);
Validator<Iterable<String>> namesFailFastValidator = Validators.forEachFailFast(nameValidator);

println(namesValidator.validate(names));
println(namesFailFastValidator.validate(names));
```
execution:
```java
FailedValidation{messages=[Name must start with capital letter, Only letters are allowed in name, Name must start with capital letter, Only letters are allowed in name]} 
FailedValidation{messages=[Name must start with capital letter, Only letters are allowed in name]} 
```
Again, we have to flavors:
* **forEach** - that validates all objects in collection 
* **forEachFailFast** - stops validation after first failure

### Exception handling
Exception handling allows recovery from error and return of properly formatted validation message.
````java
Validator<User> throwingValidator1 = u -> {
     if (u.name.equals("bad state"))
         throw new IllegalStateException("Something went wrong with state");
     else
         return ValidationResult.success();
};

Validator<User> throwingValidator2 = u -> {
     if (u.name.equals("bad argument"))
         throw new IllegalArgumentException("These are not the druids you are looking for");
     else if (u.name.equals("null is emptiness"))
         throw new NullPointerException("Darkness is everywhere");
     else
         return ValidationResult.success();
};

Validator<User> mergedThrowing = Validators.merge(throwingValidator1, throwingValidator2)
     .exceptionally(
        $(IllegalStateException.class, t->Message.of("State problem: "+t.getMessage())),
        $(IllegalArgumentException.class, t->Message.of("Argue with this: "+t.getMessage())),
        $(t->Message.of("Something unexpected happened: " + t.getMessage()))
     );

println(mergedThrowing.validate(new User("bad state", 34)));
println(mergedThrowing.validate(new User("bad argument", 34)));
println(mergedThrowing.validate(new User("null is emptiness", 34)));
````
execution:
```
FailedValidation{messages=[State problem: Something went wrong with state]} 
FailedValidation{messages=[Argue with this: These are not the druids you are looking for]} 
FailedValidation{messages=[Something unexpected happened: Darkness is everywhere]} 
```
Error handling is very simple and resembles (intentionally) switch ... case construction. We provide the exception 
class and functions that provides the validation message, the rest of plumbing the library does for us.

### Type-safe parametrized validation message
Providing explicit text in a validation message is in many cases insufficient (e.g. internationalization). For this 
purpose, the message should carry with it the identifier of the validation message and the parameters that can be 
used for parametrization.

First we need to define enumerations for message id and parameters:
````java
enum MessageId implements ParametrizedMessage.Code<MessageId> {
  USER_NAME_IS_INVALID,
  USER_AGE_IS_INVALID,
  USER_VALIDATION_FAILED
}

enum Parameters implements  ParametrizedMessage.Parm<Parameters> {
  NAME,
  AGE,
  USER
}
````
Thanks to this, we can now define a message without having to write plain text:
````java
Message.of(USER_NAME_IS_INVALID, Map.of(NAME, "Damian"));
Message.of(USER_AGE_IS_INVALID, Map.of(AGE, 190));
````
The above messages can be rendered in any language. For example, using the internationalization properties file:
```
USER_NAME_IS_INVALID = User name {} is invalid.
USER_AGE_IS_INVALID = User age {} is invalid. Myst be between 1 and 150
```
We can pass even more complex objects as parameters:
```java
Message.of(USER_VALIDATION_FAILED, Map.of(USER, user));
```
and leave the decision on how to render the message to mechanisms independent of validation.

### Async API (based on CompletableFuture)
TODO Describe

## Examples
As real world examples speak more than academic considerations so lets build small sample domain and validate it with
some rules. 
All examples presented in paragraph can be found [here](/src/test/groovy/dsinczak/fp/validation/javadsl/example/ComplexDomainValidationExampleCaseSpec.groovy)

## Known Issues
* Async API thread control - currently it is not possible to control the pool on which the validators are combined, 
the field extraction and error handling. It is necessary to extend the API because standard context switching 
rules are quite complicated in the case of CompletableFuture and can often lead to the common pool.
* Grouping validators - so we can group validation results in more meaning sets (e.g. all name related validations 
are grouped with each other end distinguishable from other validation results).
