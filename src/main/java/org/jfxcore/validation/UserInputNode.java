/*
 * Copyright (c) 2023, JFXcore. All rights reserved.
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

package org.jfxcore.validation;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Represents a scene graph node that exposes the {@code userModified} property.
 */
public interface UserInputNode {

    /**
     * Gets the {@code userModified} property.
     *
     * @return the {@code userModified} property
     */
    ReadOnlyBooleanProperty userModifiedProperty();

    /**
     * Gets the value of the {@link #userModifiedProperty() userModified} property
     *
     * @return the value of the {@code userModified} property
     */
    boolean isUserModified();

    /**
     * Sets the value of the {@link #userModifiedProperty() userModified} property.
     *
     * @param value the new value
     */
    void setUserModified(boolean value);

}
