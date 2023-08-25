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

import impl.org.jfxcore.validation.DeferredBooleanProperty;
import impl.org.jfxcore.validation.PropertyHelper;
import impl.org.jfxcore.validation.ValidationHelper;
import org.jfxcore.validation.Constraint;
import org.jfxcore.validation.DiagnosticList;
import org.jfxcore.validation.ValidationListener;
import org.jfxcore.validation.ValidationState;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import java.lang.ref.WeakReference;

/**
 * Provides a base implementation for a constrained property that wraps a boolean value.
 * {@link Property#getBean()} and {@link Property#getName()} must be implemented by derived classes.
 *
 * @param <D> diagnostic type
 */
public abstract non-sealed class ConstrainedBooleanPropertyBase<D> extends ConstrainedBooleanProperty<D> {

    static {
        PropertyHelper.setBooleanAccessor(
            new PropertyHelper.Accessor<Boolean>() {
                @Override
                public <D1> ValidationHelper<Boolean, D1> getValidationHelper(
                        ReadOnlyConstrainedProperty<Boolean, D1> property) {
                    return ((ConstrainedBooleanPropertyBase<D1>)property).validationHelper;
                }

                @Override
                public <D1> Boolean readValue(
                        ReadOnlyConstrainedProperty<Boolean, D1> property) {
                    return ((ConstrainedBooleanPropertyBase<D1>)property).readValue();
                }
            }
        );
    }

    private final ValidationHelper<Boolean, D> validationHelper;
    private final DeferredBooleanProperty constrainedValue;
    private ObservableBooleanValue observable;
    private InvalidationListener listener;
    private boolean valid = true;
    private boolean value;

    /**
     * The constructor of the {@code ConstrainedIntegerPropertyBase}.
     *
     * @param constraints the value constraints
     */
    @SafeVarargs
    protected ConstrainedBooleanPropertyBase(Constraint<? super Boolean, D>... constraints) {
        this(false, ValidationState.UNKNOWN, constraints);
    }

    /**
     * The constructor of the {@code ConstrainedBooleanPropertyBase}.
     *
     * @param initialValue the initial value of the wrapped value
     * @param constraints the value constraints
     */
    @SafeVarargs
    protected ConstrainedBooleanPropertyBase(boolean initialValue, Constraint<? super Boolean, D>... constraints) {
        this(initialValue, ValidationState.UNKNOWN, constraints);
    }

    /**
     * The constructor of the {@code ConstrainedIntegerPropertyBase}.
     *
     * @param initialValue the initial value of the wrapped value
     * @param initialValidationState the initial validation state
     * @param constraints the value constraints
     */
    @SafeVarargs
    protected ConstrainedBooleanPropertyBase(
            boolean initialValue,
            ValidationState initialValidationState,
            Constraint<? super Boolean, D>... constraints) {
        value = initialValue;

        constrainedValue = new DeferredBooleanProperty(initialValue) {
            @Override public String getName() { return "constrainedValue"; }
            @Override public Object getBean() { return ConstrainedBooleanPropertyBase.this; }
        };

        validationHelper = new ValidationHelper<>(this, constrainedValue, initialValidationState, constraints);

        if (initialValidationState == ValidationState.UNKNOWN) {
            validationHelper.invalidated(this);
        }
    }

    @Override
    public final ReadOnlyBooleanProperty validProperty() {
        return validationHelper.validProperty();
    }

    @Override
    public final boolean isValid() {
        return validationHelper.isValid();
    }

    @Override
    public final ReadOnlyBooleanProperty invalidProperty() {
        return validationHelper.invalidProperty();
    }

    @Override
    public final boolean isInvalid() {
        return validationHelper.isInvalid();
    }

    @Override
    public ReadOnlyBooleanProperty userValidProperty() {
        return validationHelper.userValidProperty();
    }

    @Override
    public boolean isUserValid() {
        return validationHelper.isUserValid();
    }

    @Override
    public ReadOnlyBooleanProperty userInvalidProperty() {
        return validationHelper.userInvalidProperty();
    }

    @Override
    public boolean isUserInvalid() {
        return validationHelper.isUserInvalid();
    }

    @Override
    public final ReadOnlyBooleanProperty validatingProperty() {
        return validationHelper.validatingProperty();
    }

    @Override
    public final boolean isValidating() {
        return validationHelper.isValidating();
    }

    @Override
    public final ReadOnlyBooleanProperty constrainedValueProperty() {
        return constrainedValue;
    }

    @Override
    public final ReadOnlyDiagnosticListProperty<D> diagnosticsProperty() {
        return validationHelper.diagnosticsProperty();
    }

    @Override
    public final DiagnosticList<D> getDiagnostics() {
        return validationHelper.getDiagnostics();
    }

    @Override
    public final void addListener(ValidationListener<? super Boolean, D> listener) {
        validationHelper.addListener(listener);
    }

    @Override
    public final void removeListener(ValidationListener<? super Boolean, D> listener) {
        validationHelper.removeListener(listener);
    }

    private void markInvalid() {
        if (valid) {
            valid = false;
            validationHelper.invalidated(this);
            invalidated();
            fireValueChangedEvent();
        } else {
            validationHelper.invalidated(this);
        }
    }

    /**
     * The method {@code invalidated()} can be overridden to receive
     * invalidation notifications. This is the preferred option in
     * {@code Objects} defining the property, because it requires less memory.
     *
     * The default implementation is empty.
     */
    protected void invalidated() {
    }

    private boolean readValue() {
        return observable == null ? value : observable.get();
    }

    @Override
    public boolean get() {
        valid = true;
        return observable == null ? value : observable.get();
    }

    @Override
    public void set(boolean newValue) {
        if (isBound()) {
            throw PropertyHelper.cannotSetBoundProperty(this);
        }

        if (value != newValue) {
            value = newValue;
            markInvalid();
        }
    }

    @Override
    public boolean isBound() {
        return observable != null;
    }

    @Override
    public void bind(final ObservableValue<? extends Boolean> source) {
        if (source == null) {
            throw PropertyHelper.cannotBindNull(this);
        }

        final ObservableBooleanValue newObservable = (source instanceof ObservableBooleanValue) ?
                (ObservableBooleanValue) source : new ValueWrapper(source);

        if (!newObservable.equals(observable)) {
            unbind();
            observable = newObservable;
            if (listener == null) {
                listener = new Listener<>(this);
            }
            observable.addListener(listener);
            markInvalid();
        }
    }

    @Override
    public void unbind() {
        if (observable != null) {
            value = observable.get();
            observable.removeListener(listener);
            if (observable instanceof ValueWrapper) {
                ((ValueWrapper)observable).dispose();
            }
            observable = null;
        }
    }

    @Override
    public String toString() {
        return PropertyHelper.toString(this, valid);
    }

    private static class Listener<D> implements InvalidationListener, WeakListener {
        private final WeakReference<ConstrainedBooleanPropertyBase<D>> wref;

        public Listener(ConstrainedBooleanPropertyBase<D> ref) {
            this.wref = new WeakReference<>(ref);
        }

        @Override
        public void invalidated(Observable observable) {
            ConstrainedBooleanPropertyBase<D> ref = wref.get();
            if (ref == null) {
                observable.removeListener(this);
            } else {
                ref.markInvalid();
            }
        }

        @Override
        public boolean wasGarbageCollected() {
            return wref.get() == null;
        }
    }

    private static class ValueWrapper extends BooleanBinding {
        private final ObservableValue<? extends Boolean> observable;

        public ValueWrapper(ObservableValue<? extends Boolean> observable) {
            this.observable = observable;
            bind(observable);
        }

        @Override
        protected boolean computeValue() {
            final Boolean value = observable.getValue();
            return value != null && value;
        }

        @Override
        public void dispose() {
            unbind(observable);
        }
    }

}
