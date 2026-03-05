# JFXcore.validation

JFXcore.validation is a data validation framework that enables applications to validate user input, and to visualize
the validation state via CSS. Data validation ensures correctness and consistency of data in a JavaFX application by
specifying data constraints: a value that satisfies all constraints is *valid* and can be used for further processing;
a value that violates a constraint is *invalid*.

Many applications decouple the validation and visualization aspects:

1. *Validation* is implemented in a controller, view model, or business logic class
2. *Visualization* is implemented in the JavaFX scene graph

The data validation framework also supports two modes of validation:

1. *Blocking* (synchronous) data validation that runs on the JavaFX application thread
   and is useful for simple validation logic
2. *Non-blocking* (asynchronous) data validation that runs on an application-specified
   background thread, which allows applications to maintain a responsive user interface for
   long-running validation operations

## Constrained properties

The basic primitive of data validation is the `ConstrainedValue` interface, which represents a value with a
constrained range of validity. This concept is extended to JavaFX properties with the `ConstrainedProperty`
interface. The data validation framework comes with a full set of property implementations that extend the
standard JavaFX property specification:

| Standard property | Constrained property | Default implementation |
|---|---|---|
| `BooleanProperty` | `ConstrainedBooleanProperty` | `SimpleConstrainedBooleanProperty` |
| `IntegerProperty` | `ConstrainedIntegerProperty` | `SimpleConstrainedIntegerProperty` |
| `LongProperty` | `ConstrainedLongProperty` | `SimpleConstrainedLongProperty` |
| `FloatProperty` | `ConstrainedFloatProperty` | `SimpleConstrainedFloatProperty` |
| `DoubleProperty` | `ConstrainedDoubleProperty` | `SimpleConstrainedDoubleProperty` |
| `StringProperty` | `ConstrainedStringProperty` | `SimpleConstrainedStringProperty` |
| `ObjectProperty` | `ConstrainedObjectProperty` | `SimpleConstrainedObjectProperty` |
| `ListProperty` | `ConstrainedListProperty` | `SimpleConstrainedListProperty` |
| `SetProperty` | `ConstrainedSetProperty` | `SimpleConstrainedSetProperty` |
| `MapProperty` | `ConstrainedMapProperty` | `SimpleConstrainedMapProperty` |

Any value that is entered by users of an application can be considered tainted, and should be validated before
it is used in business logic. Constrained properties make it easier to work with potentially tainted values by
introducing `ReadOnlyConstrainedProperty.constrainedValueProperty()`. The value of this property always corresponds
to the last value that was successfully validated, and can therefore be considered to be untainted.

## Diagnostics

All `ConstrainedValue` implementations are parameterized with a diagnostic type. When diagnostics are not used,
`Void` can be used as a generic type placeholder. Diagnostics are application-specified objects that can be attached
to the `ValidationResult` that is produced by `Constraint` validators, and can be retrieved after a validation run
by calling `ConstrainedValue.getDiagnostics()`. `ConstrainedProperty` also adds `diagnosticsProperty()`.

In simple cases, the diagnostic type could be a `String` value that contains a message when a value fails validation.

The data validation framework only differentiates between *valid* and *invalid* values; it does not include
higher-level concepts like *errors* and *warnings*, nor classifications like *severity* or *priority*.

Applications are free to choose their own diagnostic semantics by defining custom diagnostic types.
Consider the following example:

```java
   enum Severity { ERROR, WARNING }

   record DiagnosticInfo(Severity severity, String message) {}

   class PersonController {
       private final ConstrainedStringProperty<DiagnosticInfo> name =
               new SimpleConstrainedStringProperty<>(
                   Constraints.notNullOrEmpty(
                       () -> new DiagnosticInfo(Severity.ERROR, "Name cannot be empty")));

       public final ConstrainedStringProperty<DiagnosticInfo> nameProperty() {
           return name;
       }

       private final ConstrainedIntegerProperty<DiagnosticInfo> age =
               new SimpleConstrainedIntegerProperty<>(
                   Constraints.validate(number -> {
                       if (number.intValue() < 0)
                           return ValidationResult.invalid(
                               new DiagnosticInfo(Severity.ERROR, number + " out of range"));

                       if (number.intValue() > 130)
                           return ValidationResult.valid(
                               new DiagnosticInfo(Severity.WARNING, number + " is suspicious, check again"));

                       return ValidationResult.valid();
                   }));

       public final ConstrainedIntegerProperty<DiagnosticInfo> ageProperty() {
           return age;
       }
   }
```

## Constraints

The value range of a property can be constrained by one or more `Constraint`.
Constraints are automatically evaluated by the data validation system whenever the property value or
one of its dependencies has changed.

Implementations of the `Constraint` interface must provide three methods:

| Method | Description |
|----|---|
| `validate(T): CompletableFuture<ValidationResult<D>>` | Contains the validation logic and returns a `ValidationResult` for each validated value. |
| `getDependencies(): Observable[]` | Returns the observable dependencies of the constraint. |
| `getCompletionExecutor(): Executor` | For asynchronous constraint implementations, returns the `Executor` that is used to yield the result to the validation system; for synchronous constraint implementations, returns `null`. |

For ease of use, the `Constraints` class contains several predefined constraint factories that cover a wide
variety of use cases. In the following example, the general-purpose `Constraints.validate(ValidationFunction0)`
factory is used to create an EAN barcode number constraint:

```java
   class ProductController {
       private final ConstrainedStringProperty<String> ean =
               new SimpleConstrainedStringProperty<>(
                   Constraints.validate(value -> {
                       ValidationResult<String> result;

                       boolean checksumValid = IntStream.range(0, value.length())
                           .map(i -> Character.digit(value.charAt(value.length() - i - 1), 10) * (i % 2 == 0 ? 3 : 1))
                           .sum() % 10 == 0;

                       if (value.length() != 8 && value.length() != 13) {
                           result = ValidationResult.invalid("Value must contain 8 or 13 digits");
                       } else if (!checksumValid) {
                           result = ValidationResult.invalid("Value is not a valid EAN number");
                       } else {
                           result = ValidationResult.valid();
                       }

                       return result;
                   }));

       public final ConstrainedStringProperty<String> eanProperty() {
           return ean;
       }
   }
```

## Constraint dependencies

If a `Constraint` implementation uses other fields and property values as inputs to its validation logic, it is often
useful to register these values as constraint dependencies by returning them from `Constraint.getDependencies()`.
The data validation system will automatically re-evaluate a constraint when any of its registered dependencies
has changed.

Many of the constraint factories in the `Constraints` class that take an `ObservableValue` as an argument also
register the argument as a constraint dependency. Some examples include:

- `Constraints.greaterThan(ObservableIntegerValue)`
- `Constraints.between(ObservableIntegerValue, ObservableIntegerValue)`
- `Constraints.matchesPattern(ObservableStringValue)`
- ...

The four general-purpose constraint factories are overloaded to accept up to eight dependencies
(shown here are the overloads for a single dependency):

- `Constraints.validate(ValidationFunction1, ObservableValue)`
- `Constraints.validateAsync(ValidationFunction1, ObservableValue, Executor)`
- `Constraints.validateCancellableAsync(CancellableValidationFunction1, ObservableValue, Executor)`
- `Constraints.validateInterruptibleAsync(ValidationFunction1, ObservableValue, Executor)`

The current values of the dependencies are passed into each invocation of the validation function:

```java
   var dependency1 = new SimpleIntegerProperty();
   var dependency2 = new SimpleStringProperty();

   Constraint<String, Void> constraint = Constraints.validateAsync(
       (String value, Number dep1, String dep2) -> {
           // Validate the value and return a ValidationResult
           return new ValidationResult(...);
       },
       dependency1,
       dependency2,
       ForkJoinPool.commonPool());
```

## Collection constraints

When a `Constraint` is applied to `ConstrainedListProperty`, `ConstrainedSetProperty` or `ConstrainedMapProperty`,
the constraint is not evaluated for the collection instance itself, but for each of its containing elements.

If a constraint should be evaluated for the collection instance instead of its elements, it must be an
implementation of `ListConstraint`, `SetConstraint` or `MapConstraint`.

A `Constraint` instance can be converted into a collection constraint instance by calling
`Constraints.forList(Constraint)`, `Constraints.forSet(Constraint)`, or `Constraints.forMap(Constraint)`.

In the following example, the `notNull` constraint is applied to all list elements, as well as to the list instance:

```java
   ConstrainedListProperty<String, Void> list = new SimpleConstrainedListProperty<>(
           Constraints.notNull(),                      // applies to elements
           Constraints.forList(Constraints.notNull())  // applies to list instance
       );
```

## Asynchronous data validation

The `Constraints` class comes with three types of factory methods to create asynchronous constraints.
The difference between the three factory types is how cancellation is implemented:

- `Constraints.validateAsync(ValidationFunction0, Executor)`<br>
  This is the simplest asynchronous constraint, since it does not support cancellation.
  When the data validation system cancels a future that was produced by this constraint,
  the future does not transition into the `cancelled`
  state before the validation function has run to completion or throws an exception.
- `Constraints.validateCancellableAsync(CancellableValidationFunction1, ObservableValue, Executor)`<br>
  This constraint implements a cooperative cancellation strategy.
  The validation function receives a token that it can use to periodically check whether
  cancellation was requested, and if that is the case, stop validating and return from the
  validation function as soon as possible.<br>
  Cooperative cancellation is particularly useful for computationally intensive validation
  functions that run in a loop and can therefore check the token repeatedly.
- `Constraints.validateInterruptibleAsync(ValidationFunction1, ObservableValue, Executor)`<br>
  This constraint implements cancellation by thread interruption, and is useful for
  IO-bound validation functions that wait on interruptible APIs.<br>
  When the data validation system cancels a future that was produced by this factory,
  the thread that is executing the validation function is interrupted.

It is recommended to use the built-in factories instead of implementing the `Constraint`
interface directly for asynchronous constraints.

### Thread safety considerations

The data validation system is not inherently thread-safe and can therefore not generally be accessed from
multiple concurrent threads. The asynchronous constraint factories in the `Constraints` class assume that the
`ConstrainedProperty` instances to which they apply are only accessed on the JavaFX application thread.

The values of dependencies are read *before* the validation function is executed by the user-specified `Executor`.
This ensures that no concurrent reads or writes can happen when dependency values are accessed on a background
thread within the validation function.

However, this guarantee does not cover the *internal state* of dependency values.
If a dependency value is a mutable object, then the application must manually synchronize
access to its shared state to prevent concurrent modifications or memory ordering effects.

In general, it is recommended to use deeply immutable objects to prevent race conditions.

## Visualization

Applications often need to visualize the validation state of data in the user interface.
This can be easily achieved by binding the `ValidationState` of a `ConstrainedValue`
to a UI control that serves as the representation of that value:

```java
   class ViewModel {
       private final ConstrainedStringProperty<String> name =
               new SimpleConstrainedStringProperty<>(Constraints.notNullOrEmpty());

       public final ConstrainedStringProperty<String> nameProperty() {
           return name;
       }
   }

   class View extends Pane {
       View(ViewModel viewModel) {
           var textField = new TextField();
           getChildren().add(textField);
           textField.textProperty().bindBidirectional(viewModel.nameProperty());

           // The 'name' property will provide validation states for the 'textField' node:
           ValidationState.setSource(textField, viewModel.nameProperty());
       }
   }
```
In this example, the validation state of the `name` property is applied to the
`TextField` to which the property is bound.

### CSS validation pseudo-classes

The data validation framework adds five CSS pseudo-classes that can be used to style UI controls:

| Pseudo-class | Description | Corresponding property |
|---|---|---|
| `:validating` | Selects an element that is currently validating | `ConstrainedProperty.validatingProperty()` |
| `:invalid` | Selects an element that failed data validation | `ConstrainedProperty.invalidProperty()` |
| `:valid` | Selects an element that successfully completed data validation | `ConstrainedProperty.validProperty()` |
| `:user-invalid` | Selects an element that failed data validation after the user has interacted with it, for example by typing or clicking | `ConstrainedProperty.userInvalidProperty()` |
| `:user-valid` | Selects an element that successfully completed data validation after the user has interacted with it, for example by typing or clicking | `ConstrainedProperty.userValidProperty()` |

# Releases
The latest release is available on [Maven Central](https://central.sonatype.com/artifact/org.jfxcore/validation/0.1.0).

## Maven
```xml
<dependency>
    <groupId>org.jfxcore</groupId>
    <artifactId>validation</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Gradle
```kotlin
dependencies {
    implementation("org.jfxcore:validation:0.1.0")
}
```
