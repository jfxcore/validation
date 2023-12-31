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

import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import java.util.Map;

public class MapSizeProperty extends ReadOnlyIntegerPropertyBase {

    private final Map<?, ?> map;
    private int size;

    public MapSizeProperty(Map<?, ?> map) {
        this.map = map;
        this.size = map.size();
    }

    @Override
    public int get() {
        return size;
    }

    @Override
    public Object getBean() {
        return map;
    }

    @Override
    public String getName() {
        return "size";
    }

    @Override
    public void fireValueChangedEvent() {
        int newSize = map.size();
        if (size != newSize) {
            size = newSize;
            super.fireValueChangedEvent();
        }
    }

}
