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

## Examples
All examples presented in paragraph can be found [here](/src/test/groovy/dsinczak/fp/validation/javadsl/example/ComplexDomainValidationExampleCaseSpec.groovy)

## Known Issues
