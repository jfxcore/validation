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
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;

/**
 * Represents a {@link ReadOnlyConstrainedProperty} that wraps a double value.
 *
 * @param <D> diagnostic type
 */
public abstract sealed class ReadOnlyConstrainedDoubleProperty<D>
        extends ReadOnlyDoublePropertyBase
        implements ReadOnlyConstrainedProperty<Number, D>
        permits ConstrainedDoubleProperty, ReadOnlyConstrainedDoubleWrapper.ReadOnlyPropertyImpl {

    /**
     * Creates a default {@code ReadOnlyConstrainedDoubleProperty}.
     */
    protected ReadOnlyConstrainedDoubleProperty() {}

    @Override
    public abstract ReadOnlyDoubleProperty constrainedValueProperty();

    @Override
    public Double getConstrainedValue() {
        return constrainedValueProperty().getValue();
    }

    @Override
    public String toString() {
        return PropertyHelper.toString(this);
    }

}
