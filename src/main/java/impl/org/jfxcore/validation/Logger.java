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

package impl.org.jfxcore.validation;

public final class Logger {

    private Logger() {}

    public static void info(String message) {
        getLogger().log(System.Logger.Level.INFO, message);
    }

    public static void error(String message) {
        getLogger().log(System.Logger.Level.ERROR, message);
    }

    public static void error(String message, Throwable throwable) {
        getLogger().log(System.Logger.Level.ERROR, message, throwable);
    }

    private static System.Logger getLogger() {
        if (loggerInstance == null) {
            loggerInstance = System.getLogger("jfxcore.validation");
        }

        return loggerInstance;
    }

    private static System.Logger loggerInstance;

}
