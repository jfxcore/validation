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
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * This class provides a convenient class to define read-only properties. It
 * creates two properties that are synchronized. One property is read-only
 * and can be passed to external users. The other property is read- and
 * writable and should be used internally only.
 *
 * @param <T> value type
 * @param <D> diagnostic type
 */
public class ReadOnlyConstrainedObjectWrapper<T, D> extends SimpleConstrainedObjectProperty<T, D> {
    
    private ReadOnlyPropertyImpl readOnlyProperty;

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedObjectWrapper}.
     * The specified constraints are immediately evaluated.
     *
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedObjectWrapper(Constraint<? super T, D>... constraints) {
        super(null, constraints);
    }

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedObjectWrapper}.
     * The specified constraints are immediately evaluated.
     *
     * @param initialValue the initial value of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedObjectWrapper(T initialValue, Constraint<? super T, D>... constraints) {
        super(initialValue, constraints);
    }

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedObjectWrapper}.
     * If the initial state is {@link ValidationState#UNKNOWN}, the constraints are immediately evaluated.
     * Otherwise, the constraints will be evaluated when the property value is changed.
     *
     * @param initialValue the initial value of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param initialValidationState the initial validation state of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedObjectWrapper(
            T initialValue,
            ValidationState initialValidationState,
            Constraint<? super T, D>... constraints) {
        super(initialValue, initialValidationState, constraints);
    }

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedObjectWrapper}.
     * The specified constraints are immediately evaluated.
     *
     * @param bean the bean of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param name the name of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedObjectWrapper(Object bean, String name, Constraint<? super T, D>... constraints) {
        super(bean, name, constraints);
    }

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedObjectWrapper}.
     * The specified constraints are immediately evaluated.
     *
     * @param bean the bean of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param name the name of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param initialValue the initial value of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedObjectWrapper(
            Object bean, String name, T initialValue, Constraint<? super T, D>... constraints) {
        super(bean, name, initialValue, constraints);
    }

    /**
     * Initializes a new instance of {@code ReadOnlyConstrainedObjectWrapper}.
     * If the initial state is {@link ValidationState#UNKNOWN}, the constraints are immediately evaluated.
     * Otherwise, the constraints will be evaluated when the property value is changed.
     *
     * @param bean the bean of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param name the name of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param initialValue the initial value of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param initialValidationState the initial validation state of this {@code ReadOnlyConstrainedObjectWrapper}
     * @param constraints the value constraints
     */
    @SafeVarargs
    public ReadOnlyConstrainedObjectWrapper(
            Object bean,
            String name,
            T initialValue,
            ValidationState initialValidationState,
            Constraint<? super T, D>... constraints) {
        super(bean, name, initialValue, initialValidationState, constraints);
    }

    /**
     * Returns the read-only property that is synchronized with this
     * {@code ReadOnlyConstrainedObjectWrapper}.
     *
     * @return the read-only property
     */
    public ReadOnlyConstrainedObjectProperty<T, D> getReadOnlyProperty() {
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

    final class ReadOnlyPropertyImpl extends ReadOnlyConstrainedObjectProperty<T, D> {
        @Override
        public void fireValueChangedEvent() {
            super.fireValueChangedEvent();
        }

        @Override
        public void addListener(ValidationListener<? super T, D> listener) {
            ReadOnlyConstrainedObjectWrapper.this.addListener(new ValidationListenerWrapper<>(this, listener));
        }

        @Override
        public void removeListener(ValidationListener<? super T, D> listener) {
            ReadOnlyConstrainedObjectWrapper.this.removeListener(new ValidationListenerWrapper<>(this, listener));
        }

        @Override
        public T get() {
            return ReadOnlyConstrainedObjectWrapper.this.get();
        }

        @Override
        public Object getBean() {
            return ReadOnlyConstrainedObjectWrapper.this.getBean();
        }

        @Override
        public String getName() {
            return ReadOnlyConstrainedObjectWrapper.this.getName();
        }

        @Override
        public ReadOnlyBooleanProperty validProperty() {
            return ReadOnlyConstrainedObjectWrapper.this.validProperty();
        }

        @Override
        public boolean isValid() {
            return ReadOnlyConstrainedObjectWrapper.this.isValid();
        }

        @Override
        public ReadOnlyBooleanProperty invalidProperty() {
            return ReadOnlyConstrainedObjectWrapper.this.invalidProperty();
        }

        @Override
        public boolean isInvalid() {
            return ReadOnlyConstrainedObjectWrapper.this.isInvalid();
        }

        @Override
        public ReadOnlyBooleanProperty userValidProperty() {
            return ReadOnlyConstrainedObjectWrapper.this.userValidProperty();
        }

        @Override
        public boolean isUserValid() {
            return ReadOnlyConstrainedObjectWrapper.this.isUserValid();
        }

        @Override
        public ReadOnlyBooleanProperty userInvalidProperty() {
            return ReadOnlyConstrainedObjectWrapper.this.userInvalidProperty();
        }

        @Override
        public boolean isUserInvalid() {
            return ReadOnlyConstrainedObjectWrapper.this.isUserInvalid();
        }

        @Override
        public ReadOnlyBooleanProperty validatingProperty() {
            return ReadOnlyConstrainedObjectWrapper.this.validatingProperty();
        }

        @Override
        public boolean isValidating() {
            return ReadOnlyConstrainedObjectWrapper.this.isValidating();
        }

        @Override
        public ReadOnlyDiagnosticListProperty<D> diagnosticsProperty() {
            return ReadOnlyConstrainedObjectWrapper.this.diagnosticsProperty();
        }

        @Override
        public DiagnosticList<D> getDiagnostics() {
            return ReadOnlyConstrainedObjectWrapper.this.getDiagnostics();
        }

        @Override
        public ReadOnlyObjectProperty<T> constrainedValueProperty() {
            return ReadOnlyConstrainedObjectWrapper.this.constrainedValueProperty();
        }

        @Override
        public T getConstrainedValue() {
            return ReadOnlyConstrainedObjectWrapper.this.getConstrainedValue();
        }
    }

}
