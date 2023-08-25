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

import org.jfxcore.validation.UserInputNode;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.Node;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ControlValidationSupport {

    private record NodeInfo<T extends Node>(
            Class<T> nodeClass,
            Function<T, ? extends UserInputNode> inputNodeAttachment) {}

    private static final List<NodeInfo<?>> nodes = new ArrayList<>();

    private ControlValidationSupport() {}

    static {
        String[] map = new String[] {
            // currently unused
        };

        for (int i = 0; i < map.length; i += 2) {
            loadDefaultControlAttachment(map[i], map[i + 1]);
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadDefaultControlAttachment(String controlName, String attachmentName) {
        Class<Node> controlClass;

        try {
            controlClass = (Class<Node>)Class.forName(controlName);
        } catch (ClassNotFoundException ignored) {
            return;
        }

        try {
            Class<UserInputNode> attachmentClass = (Class<UserInputNode>)Class.forName(attachmentName);
            Constructor<UserInputNode> constructor = attachmentClass.getConstructor(controlClass);

            setInputNodeAttachment(controlClass, node -> {
                try {
                    return constructor.newInstance(node);
                } catch (ReflectiveOperationException e) {
                    Logger.error("Failed to initialize a new instance of " + attachmentName, e);
                    return null;
                }
            });
        } catch (NoSuchMethodException ex) {
            Logger.error(
                attachmentName + " requires a public constructor that accepts a "
                + controlClass.getName() + " + instance");
        } catch (Throwable ex) {
            Logger.error("Failed to initialize control attachment", ex);
        }
    }

    public static synchronized void setInputNodeAttachment(
            Class<Node> nodeClass, Function<Node, ? extends UserInputNode> attachment) {
        for (int i = 0; i < nodes.size(); ++i) {
            if (nodes.get(i).nodeClass() == nodeClass) {
                if (attachment != null) {
                    nodes.set(i, new NodeInfo<>(nodeClass, attachment));
                } else {
                    nodes.remove(i);
                }

                return;
            }
        }

        if (attachment != null) {
            nodes.add(new NodeInfo<>(nodeClass, attachment));
        }
    }

    public static <T extends Node> ReadOnlyBooleanProperty tryGetUserModifiedProperty(T node) {
        UserInputNode inputNode = tryGetInputNode(node);
        return inputNode != null ? inputNode.userModifiedProperty() : null;
    }

    public static <T extends Node> void trySetUserModifiedValue(T node, boolean value) {
        UserInputNode inputNode = tryGetInputNode(node);
        if (inputNode != null) {
            inputNode.setUserModified(value);
        }
    }

    @SuppressWarnings("unchecked")
    private static synchronized <T extends Node> UserInputNode tryGetInputNode(T node) {
        if (node instanceof UserInputNode inputNode) {
            return inputNode;
        }

        Class<?> nodeClass = node.getClass();
        NodeInfo<T> bestMatch = null;

        for (NodeInfo<?> nodeInfo : nodes) {
            if (nodeInfo.nodeClass().isAssignableFrom(nodeClass)) {
                if (bestMatch == null || bestMatch.nodeClass().isAssignableFrom(nodeInfo.nodeClass())) {
                    bestMatch = (NodeInfo<T>)nodeInfo;
                }
            }
        }

        return bestMatch != null ? bestMatch.inputNodeAttachment().apply(node) : null;
    }

}
