/*
 * Copyright (c) 2022, JFXcore. All rights reserved.
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

package impl.org.jfxcore.validation;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.jfxcore.validation.DiagnosticList;
import org.jfxcore.validation.property.ReadOnlyDiagnosticListProperty;

public final class DiagnosticListPropertyImpl<E> extends ReadOnlyDiagnosticListProperty<E> {

    private final Object bean;
    private final DiagnosticList<E> value;
    private SizeProperty size0;
    private EmptyProperty empty0;

    public DiagnosticListPropertyImpl(Object bean, DiagnosticList<E> initialValue) {
        this.bean = bean;
        this.value = initialValue;

        initialValue.addListener((ListChangeListener<? super E>)change -> {
            if (size0 != null) {
                size0.fireValueChangedEvent();
            }
            if (empty0 != null) {
                empty0.fireValueChangedEvent();
            }

            fireValueChangedEvent(change);
        });
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return "diagnostics";
    }

    @Override
    public boolean isValid(int index) {
        return get().isValid(index);
    }

    @Override
    public ObservableList<E> validSubList() {
        return get().validSubList();
    }

    @Override
    public ObservableList<E> invalidSubList() {
        return get().invalidSubList();
    }

    @Override
    public ReadOnlyIntegerProperty sizeProperty() {
        if (size0 == null) {
            size0 = new SizeProperty();
        }

        return size0;
    }

    private class SizeProperty extends ReadOnlyIntegerPropertyBase {
        @Override
        public int get() {
            return size();
        }

        @Override
        public Object getBean() {
            return DiagnosticListPropertyImpl.this;
        }

        @Override
        public String getName() {
            return "size";
        }

        protected void fireValueChangedEvent() {
            super.fireValueChangedEvent();
        }
    }

    @Override
    public ReadOnlyBooleanProperty emptyProperty() {
        if (empty0 == null) {
            empty0 = new EmptyProperty();
        }

        return empty0;
    }

    private class EmptyProperty extends ReadOnlyBooleanPropertyBase {
        @Override
        public boolean get() {
            return isEmpty();
        }

        @Override
        public Object getBean() {
            return DiagnosticListPropertyImpl.this;
        }

        @Override
        public String getName() {
            return "empty";
        }

        protected void fireValueChangedEvent() {
            super.fireValueChangedEvent();
        }
    }

    @Override
    public DiagnosticList<E> get() {
        return value;
    }

    @Override
    public DiagnosticList<E> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ReadOnlyListProperty [" + "bean: " + getBean() + ", " + "name: " + getName() + ", " + "value: " + get() + "]";
    }

}
