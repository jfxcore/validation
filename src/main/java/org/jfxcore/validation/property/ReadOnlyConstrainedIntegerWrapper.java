/*
 * Copyright (c) 2011, 2015, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022, JFXcore. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.jfxcore.validation.property;

import impl.org.jfxcore.validation.ValidationListenerWrapper;
import org.jfxcore.validation.Constraint;
import org.jfxcore.validation.DiagnosticList;
import org.jfxcore.validation.ValidationListener;
import org.jfxcore.validation.ValidationState;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;

/**
 * This class provides a convenient class to define read-only properties. It
 * creates two properties that are synchronized. One property is read-only
 * and can be passed to external users. The other property is read- and
 * writable and should be used internally only.
 *
 * @param <D> diagnostic type
 */
public class ReadOnlyConstrainedIntegerWrapper<D> extends SimpleConstrainedIntegerProperty<D> {
    
    private ReadOnlyPropertyImpl readOnlyProperty;

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedIntegerWrapper}.
     * The specified constraints are immediately evaluated.
     *
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedIntegerWrapper(Constraint<? super Number, D>... constraints) {
        super(0, constraints);
    }

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedIntegerWrapper}.
     * The specified constraints are immediately evaluated.
     *
     * @param initialValue the initial value of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedIntegerWrapper(
            int initialValue, Constraint<? super Number, D>... constraints) {
        super(initialValue, constraints);
    }

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedIntegerWrapper}.
     * If the initial state is {@link ValidationState#UNKNOWN}, the constraints are immediately evaluated.
     * Otherwise, the constraints will be evaluated when the property value is changed.
     *
     * @param initialValue the initial value of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param initialValidationState the initial validation state of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedIntegerWrapper(
            int initialValue,
            ValidationState initialValidationState,
            Constraint<? super Number, D>... constraints) {
        super(initialValue, initialValidationState, constraints);
    }

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedIntegerWrapper}.
     * The specified constraints are immediately evaluated.
     *
     * @param bean the bean of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param name the name of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedIntegerWrapper(
            Object bean, String name, Constraint<? super Number, D>... constraints) {
        super(bean, name, constraints);
    }

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedIntegerWrapper}.
     * The specified constraints are immediately evaluated.
     *
     * @param bean the bean of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param name the name of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param initialValue the initial value of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedIntegerWrapper(
            Object bean, String name, int initialValue, Constraint<? super Number, D>... constraints) {
        super(bean, name, initialValue, constraints);
    }

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedIntegerWrapper}.
     * If the initial state is {@link ValidationState#UNKNOWN}, the constraints are immediately evaluated.
     * Otherwise, the constraints will be evaluated when the property value is changed.
     *
     * @param bean the bean of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param name the name of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param initialValue the initial value of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param initialValidationState the initial validation state of this {@code ReadOnlyConstrainedIntegerWrapper}
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedIntegerWrapper(
            Object bean,
            String name,
            int initialValue,
            ValidationState initialValidationState,
            Constraint<? super Number, D>... constraints) {
        super(bean, name, initialValue, initialValidationState, constraints);
    }

    /**
     * Returns the read-only property that is synchronized with this
     * {@code ReadOnlyConstrainedIntegerWrapper}.
     *
     * @return the read-only property
     */
    public ReadOnlyConstrainedIntegerProperty<D> getReadOnlyProperty() {
        if (readOnlyProperty == null) {
            readOnlyProperty = new ReadOnlyPropertyImpl();
        }
        return readOnlyProperty;
    }

    @Override
    protected void fireValueChangedEvent() {
        super.fireValueChangedEvent();
        
        if (readOnlyProperty != null) {
            readOnlyProperty.fireValueChangedEvent();
        }
    }

    final class ReadOnlyPropertyImpl extends ReadOnlyConstrainedIntegerProperty<D> {
        @Override
        public void fireValueChangedEvent() {
            super.fireValueChangedEvent();
        }

        @Override
        public void addListener(ValidationListener<? super Number, D> listener) {
            ReadOnlyConstrainedIntegerWrapper.this.addListener(new ValidationListenerWrapper<>(this, listener));
        }

        @Override
        public void removeListener(ValidationListener<? super Number, D> listener) {
            ReadOnlyConstrainedIntegerWrapper.this.removeListener(new ValidationListenerWrapper<>(this, listener));
        }

        @Override
        public int get() {
            return ReadOnlyConstrainedIntegerWrapper.this.get();
        }

        @Override
        public Object getBean() {
            return ReadOnlyConstrainedIntegerWrapper.this.getBean();
        }

        @Override
        public String getName() {
            return ReadOnlyConstrainedIntegerWrapper.this.getName();
        }

        @Override
        public ReadOnlyBooleanProperty validProperty() {
            return ReadOnlyConstrainedIntegerWrapper.this.validProperty();
        }

        @Override
        public boolean isValid() {
            return ReadOnlyConstrainedIntegerWrapper.this.isValid();
        }

        @Override
        public ReadOnlyBooleanProperty invalidProperty() {
            return ReadOnlyConstrainedIntegerWrapper.this.invalidProperty();
        }

        @Override
        public boolean isInvalid() {
            return ReadOnlyConstrainedIntegerWrapper.this.isInvalid();
        }

        @Override
        public ReadOnlyBooleanProperty userValidProperty() {
            return ReadOnlyConstrainedIntegerWrapper.this.userValidProperty();
        }

        @Override
        public boolean isUserValid() {
            return ReadOnlyConstrainedIntegerWrapper.this.isUserValid();
        }

        @Override
        public ReadOnlyBooleanProperty userInvalidProperty() {
            return ReadOnlyConstrainedIntegerWrapper.this.userInvalidProperty();
        }

        @Override
        public boolean isUserInvalid() {
            return ReadOnlyConstrainedIntegerWrapper.this.isUserInvalid();
        }

        @Override
        public ReadOnlyBooleanProperty validatingProperty() {
            return ReadOnlyConstrainedIntegerWrapper.this.validatingProperty();
        }

        @Override
        public boolean isValidating() {
            return ReadOnlyConstrainedIntegerWrapper.this.isValidating();
        }

        @Override
        public ReadOnlyDiagnosticListProperty<D> diagnosticsProperty() {
            return ReadOnlyConstrainedIntegerWrapper.this.diagnosticsProperty();
        }

        @Override
        public DiagnosticList<D> getDiagnostics() {
            return ReadOnlyConstrainedIntegerWrapper.this.getDiagnostics();
        }

        @Override
        public ReadOnlyIntegerProperty constrainedValueProperty() {
            return ReadOnlyConstrainedIntegerWrapper.this.constrainedValueProperty();
        }

        @Override
        public Integer getConstrainedValue() {
            return ReadOnlyConstrainedIntegerWrapper.this.getConstrainedValue();
        }
    }

}
