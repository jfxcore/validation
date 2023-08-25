/*
 * Copyright (c) 2022, 2023, JFXcore. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  JFXcore designates this
 * particular file as subject to the "Classpath" exception as provided
 * in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jfxcore.validation.property;

import org.jfxcore.validation.ConstrainedValue;
import org.jfxcore.validation.Constraint;
import org.jfxcore.validation.DiagnosticList;
import org.jfxcore.validation.ValidationResult;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;

/**
 * Defines methods and properties common to all read-only constrained properties.
 *
 * @param <T> data type
 * @param <D> diagnostic type
 */
public sealed interface ReadOnlyConstrainedProperty<T, D>
        extends ReadOnlyProperty<T>, ConstrainedValue<T, D>
        permits ConstrainedProperty,
                ReadOnlyConstrainedBooleanProperty,
                ReadOnlyConstrainedDoubleProperty,
                ReadOnlyConstrainedFloatProperty,
                ReadOnlyConstrainedIntegerProperty,
                ReadOnlyConstrainedListProperty,
                ReadOnlyConstrainedLongProperty,
                ReadOnlyConstrainedMapProperty,
                ReadOnlyConstrainedObjectProperty,
                ReadOnlyConstrainedSetProperty,
                ReadOnlyConstrainedStringProperty {

    /**
     * Indicates whether the property value is currently known to be valid.
     * <p>
     * The property value is valid if all constraint validators have successfully completed.
     *
     * @return the {@code valid} property
     */
    ReadOnlyBooleanProperty validProperty();

    /**
     * Gets the value of the {@link #validProperty() valid} property.
     *
     * @return the value of the {@code valid} property
     */
    default boolean isValid() {
        return validProperty().get();
    }

    /**
     * Indicates whether the property value is currently known to be valid after the
     * user has significantly interacted with it.
     * <p>
     * The property value is valid if all constraint validators have successfully completed.
     *
     * @return the {@code userValid} property
     */
    ReadOnlyBooleanProperty userValidProperty();

    /**
     * Gets the value of the {@link #userValidProperty() userValid} property.
     *
     * @return the value of the {@code userValid} property
     */
    default boolean isUserValid() {
        return userValidProperty().get();
    }

    /**
     * Indicates whether the property value is currently known to be invalid.
     * <p>
     * The property value is invalid if at least one constraint has been violated, independently of
     * whether other constraint validators have already completed validation.
     *
     * @return the {@code invalid} property
     */
    ReadOnlyBooleanProperty invalidProperty();

    /**
     * Gets the value of the {@link #invalidProperty() invalid} property.
     *
     * @return the value of the {@code invalid} property
     */
    default boolean isInvalid() {
        return invalidProperty().get();
    }

    /**
     * Indicates whether the property value is currently known to be invalid after the
     * user has significantly interacted with it.
     * <p>
     * The property value is invalid if at least one constraint has been violated, independently of
     * whether other constraint validators have already completed validation.
     *
     * @return the {@code userInvalid} property
     */
    ReadOnlyBooleanProperty userInvalidProperty();

    /**
     * Gets the value of the {@link #userInvalidProperty() userInvalid} property.
     *
     * @return the value of the {@code userInvalid} property
     */
    default boolean isUserInvalid() {
        return userInvalidProperty().get();
    }

    /**
     * Indicates whether the property value is currently being validated.
     *
     * @return the {@code validating} property
     */
    ReadOnlyBooleanProperty validatingProperty();

    /**
     * Gets the value of the {@link #validatingProperty() validating} property.
     *
     * @return the value of the {@code validating} property
     */
    default boolean isValidating() {
        return validatingProperty().get();
    }

    /**
     * Contains a list of validation diagnostics.
     * <p>
     * {@link Constraint} validators may generate a diagnostic as part of the returned {@link ValidationResult}.
     * Diagnostics are application-specified data objects that can be used to provide contextual information
     * for the validated value.
     * <p>
     * All diagnostics that were generated by constraint validators during a validation run are surfaced
     * in this list. Since diagnostics are optional and can be generated regardless of whether the value
     * is valid or invalid, the presence or absence of diagnostics does not necessarily imply that the
     * validated value is either valid or invalid.
     * <p>
     * Diagnostics in this list are not retained across subsequent validation runs: when a constraint
     * is re-evaluated, the diagnostic that was generated in the previous validation run is removed.
     * This means that the diagnostic list will never contain multiple diagnostics from a single
     * constraint validator.
     * <p>
     * For ease of use, the returned diagnostics list provides two sublist views:
     * <ul>
     *     <li>{@link DiagnosticList#validSubList()}, which only includes diagnostics of constraint
     *         validators that successfully validated the value
     *     <li>{@link DiagnosticList#invalidSubList()}, which only includes diagnostics of constraint
     *         validators that failed to validate the value
     * </ul>
     *
     * @return the {@code diagnostics} property
     */
    ReadOnlyDiagnosticListProperty<D> diagnosticsProperty();

    /**
     * Gets the value of the {@link #diagnosticsProperty() diagnostics} property.
     *
     * @return the value of the {@code diagnostics} property
     */
    default DiagnosticList<D> getDiagnostics() {
        return diagnosticsProperty().get();
    }

    /**
     * Contains the last value that satisfies all constraints.
     *
     * @return the {@code constrainedValue} property
     */
    ReadOnlyProperty<T> constrainedValueProperty();

    /**
     * Gets the value of the {@link #constrainedValueProperty() constrainedValue} property.
     *
     * @return the value of the {@code constrainedValue} property
     */
    default T getConstrainedValue() {
        return constrainedValueProperty().getValue();
    }

}
