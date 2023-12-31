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

import impl.org.jfxcore.validation.DeferredMapProperty;
import impl.org.jfxcore.validation.MapChange;
import impl.org.jfxcore.validation.MapEmptyProperty;
import impl.org.jfxcore.validation.MapSizeProperty;
import impl.org.jfxcore.validation.MapValidationHelper;
import impl.org.jfxcore.validation.PropertyHelper;
import impl.org.jfxcore.validation.ValidationHelper;
import org.jfxcore.validation.ConstraintBase;
import org.jfxcore.validation.DiagnosticList;
import org.jfxcore.validation.ValidationListener;
import org.jfxcore.validation.ValidationState;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import java.lang.ref.WeakReference;

/**
 * Provides a base implementation for a constrained property that wraps an {@link ObservableMap}.
 * {@link Property#getBean()} and {@link Property#getName()} must be implemented by derived classes.
 *
 * @param <K> key type
 * @param <V> value type
 * @param <D> diagnostic type
 */
public abstract non-sealed class ConstrainedMapPropertyBase<K, V, D> extends ConstrainedMapProperty<K, V, D> {

    static {
        PropertyHelper.setMapAccessor(
            new PropertyHelper.Accessor<ObservableMap<Object, Object>>() {
                @Override
                public <D1> ValidationHelper<ObservableMap<Object, Object>, D1> getValidationHelper(
                        ReadOnlyConstrainedProperty<ObservableMap<Object, Object>, D1> property) {
                    return ((ConstrainedMapPropertyBase<Object, Object, D1>)property).validationHelper;
                }

                @Override
                public <D1> ObservableMap<Object, Object> readValue(
                        ReadOnlyConstrainedProperty<ObservableMap<Object, Object>, D1> property) {
                    return ((ConstrainedMapPropertyBase<Object, Object, D1>)property).readValue();
                }
            }
        );
    }

    private boolean valid = true;
    private ObservableMap<K, V> value;
    private ObservableValue<? extends ObservableMap<K, V>> observable;
    private InvalidationListener listener;
    private MapValidationHelper<K, V, D> validationHelper;
    private MapSizeProperty size0;
    private MapEmptyProperty empty0;

    private final DeferredMapProperty<K, V> constrainedValue;
    private final MapChangeListener<K, V> mapChangeListener = change -> {
        validationHelper.invalidated(change);
        invalidateProperties();
        invalidated();
        fireValueChangedEvent(change);
    };

    /**
     * The constructor of the {@code ConstrainedMapPropertyBase}.
     *
     * @param constraints the value constraints
     */
    @SafeVarargs
    protected ConstrainedMapPropertyBase(ConstraintBase<? super V, D>... constraints) {
        this(null, ValidationState.UNKNOWN, constraints);
    }

    /**
     * The constructor of the {@code ConstrainedMapPropertyBase}.
     *
     * @param initialValue the initial value of the wrapped value
     * @param constraints the value constraints
     */
    @SafeVarargs
    protected ConstrainedMapPropertyBase(
            ObservableMap<K, V> initialValue, ConstraintBase<? super V, D>... constraints) {
        this(initialValue, ValidationState.UNKNOWN, constraints);
    }

    /**
     * The constructor of the {@code ConstrainedMapPropertyBase}.
     *
     * @param initialValue the initial value of the wrapped value
     * @param initialValidationState the initial validation state
     * @param constraints the value constraints
     */
    @SafeVarargs
    protected ConstrainedMapPropertyBase(
            ObservableMap<K, V> initialValue,
            ValidationState initialValidationState,
            ConstraintBase<? super V, D>... constraints) {
        if (initialValue != null) {
            initialValue.addListener(mapChangeListener);
        }

        value = initialValue;

        constrainedValue = new DeferredMapProperty<>(initialValue) {
            @Override
            public Object getBean() {
                return ConstrainedMapPropertyBase.this;
            }

            @Override
            public String getName() {
                return "constrainedValue";
            }

            @Override
            protected MapChange<K, V> getMapChange() {
                return validationHelper.completeMapChange();
            }
        };

        validationHelper = new MapValidationHelper<>(this, constrainedValue, initialValidationState, constraints);

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
    public final ReadOnlyMapProperty<K, V> constrainedValueProperty() {
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
    public final ReadOnlyIntegerProperty sizeProperty() {
        if (size0 == null) {
            size0 = new MapSizeProperty(this);
        }
        return size0;
    }

    @Override
    public final ReadOnlyBooleanProperty emptyProperty() {
        if (empty0 == null) {
            empty0 = new MapEmptyProperty(this);
        }
        return empty0;
    }

    @Override
    public void addListener(ValidationListener<? super ObservableMap<K, V>, D> listener) {
        validationHelper.addListener(listener);
    }

    @Override
    public void removeListener(ValidationListener<? super ObservableMap<K, V>, D> listener) {
        validationHelper.removeListener(listener);
    }

    private void invalidateProperties() {
        if (size0 != null) {
            size0.fireValueChangedEvent();
        }
        if (empty0 != null) {
            empty0.fireValueChangedEvent();
        }
    }

    private void markInvalid(ObservableMap<K, V> oldValue) {
        if (valid) {
            if (oldValue != null) {
                oldValue.removeListener(mapChangeListener);
            }
            valid = false;
            validationHelper.invalidated(this);
            invalidateProperties();
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

    private ObservableMap<K, V> readValue() {
        return observable == null ? value : observable.getValue();
    }

    @Override
    public ObservableMap<K, V> get() {
        if (!valid) {
            value = observable == null ? value : observable.getValue();
            valid = true;
            if (value != null) {
                value.addListener(mapChangeListener);
            }
        }
        return value;
    }

    @Override
    public void set(ObservableMap<K, V> newValue) {
        if (isBound()) {
            throw PropertyHelper.cannotSetBoundProperty(this);
        }

        if (value != newValue) {
            final ObservableMap<K, V> oldValue = value;
            value = newValue;
            markInvalid(oldValue);
        }
    }

    @Override
    public boolean isBound() {
        return observable != null;
    }

    @Override
    public void bind(final ObservableValue<? extends ObservableMap<K, V>> source) {
        if (source == null) {
            throw PropertyHelper.cannotBindNull(this);
        }

        if (source != observable) {
            unbind();
            observable = source;
            if (listener == null) {
                listener = new Listener<>(this);
            }
            observable.addListener(listener);
            markInvalid(value);
        }
    }

    @Override
    public void unbind() {
        if (observable != null) {
            value = observable.getValue();
            observable.removeListener(listener);
            observable = null;
        }
    }

    @Override
    public String toString() {
        return PropertyHelper.toString(this, valid);
    }

    private static class Listener<K, V, D> implements InvalidationListener, WeakListener {
        private final WeakReference<ConstrainedMapPropertyBase<K, V, D>> wref;

        public Listener(ConstrainedMapPropertyBase<K, V, D> ref) {
            this.wref = new WeakReference<>(ref);
        }

        @Override
        public void invalidated(Observable observable) {
            ConstrainedMapPropertyBase<K, V, D> ref = wref.get();
            if (ref == null) {
                observable.removeListener(this);
            } else {
                ref.markInvalid(ref.value);
            }
        }

        @Override
        public boolean wasGarbageCollected() {
            return wref.get() == null;
        }
    }

}
