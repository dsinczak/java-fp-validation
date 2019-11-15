# java-fp-validation
A small validation library written in Java. Its main purpose is to provide the simple object validation mechanism (
each validator is simply a function) that allows for very easy composition and re-usability of the validators.

Additionally, the project is an attempt to show (and experiment with) functional programming approach (or being more 
critical: functional'ish).

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

````$java
        Validator<String> startsWithCapitalLetter = str ->
                Character.isUpperCase(str.charAt(0))
                        ? success()
                        : failed("Must start with capital letter");

        Validator<String> onlyLetters = str ->
                str.chars().allMatch(Character::isLetter)
                        ? success()
                        : failed("Only letters are allowed");

        Validator<String> nameValidator = startsWithCapitalLetter.merge(onlyLetters);
        Validator<String> nameFFValidator = startsWithCapitalLetter.mergeFailFast(onlyLetters);

        System.out.println(nameValidator.validate("c3PO"));
        System.out.println(nameFFValidator.validate("c3PO"));
````

execution:

```
FailedValidation{messages=[Must start with capital letter, Only letters are allowed]} 
FailedValidation{messages=[Must start with capital letter]} 
```

This is very simple name validator composed of 2 simpler string validators (i agree that, it can be done with single regexp
but I will stick to this example as it does not draw attention away from main topic).

What is noteworthy is the fact that we have 2 types of validator combinations here:
* all validations are started regardless of the previous result: ``merge.(...)``
* validations are carried out in turn and stopped in the event of the first failure: ``mergeFailFast(...)``


395/5000
Thanks to this approach, we have full control over the chain of validation calls. We can interrupt it as soon as possible 
and return to the caller with the result or perform all validations to return as many validation errors as possible. 
Additionally, such joining can be nested on many levels (nothing stands in the way of joining validators connected with 
another chain).
 
 
## Examples
All examples presented in paragraph can be found [here](/src/test/groovy/dsinczak/fp/validation/javadsl/example/ComplexDomainValidationExampleCaseSpec.groovy)

## Known Issues
