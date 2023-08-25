/**
 * Validation framework for JavaFX applications.
 */
module jfxcore.validation {

    requires javafx.base;
    requires javafx.graphics;
    requires static javafx.controls;

    exports org.jfxcore.validation;
    exports org.jfxcore.validation.function;
    exports org.jfxcore.validation.property;

}
