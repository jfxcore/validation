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

import impl.org.jfxcore.validation.PropertyHelper;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.value.WritableObjectValue;

/**
 * Defines a constrained property that wraps an object value.
 *
 * @param <T> value type
 * @param <D> diagnostic type
 */
public abstract sealed class ConstrainedObjectProperty<T, D>
        extends ReadOnlyConstrainedObjectProperty<T, D>
        implements ConstrainedProperty<T, D>, WritableObjectValue<T>
        permits ConstrainedObjectPropertyBase {

    /**
     * Creates a default {@code ConstrainedObjectProperty}.
     */
    protected ConstrainedObjectProperty() {
    }

    @Override
    public void setValue(T v) {
        set(v);
    }

    @Override
    public void bindBidirectional(Property<T> other) {
        Bindings.bindBidirectional(this, other);
    }

    @Override
    public void unbindBidirectional(Property<T> other) {
        Bindings.unbindBidirectional(this, other);
    }

    @Override
    public String toString() {
        return PropertyHelper.toString(this);
    }

}
