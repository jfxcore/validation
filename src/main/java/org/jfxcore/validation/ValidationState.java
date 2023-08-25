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

package org.jfxcore.validation;

import impl.org.jfxcore.validation.NodeValidationInfo;
import javafx.scene.Node;

/**
 * Represents the validation state of a {@link ConstrainedValue} and facilitates the
 * visualization of validation states in the scene graph using CSS.
 * <p>
 * An easy way for scene graph nodes to visualize the result of data validation is by providing
 * CSS styles for the different data validation states. The data validation framework supports
 * five validation pseudo-classes:
 *
 * <ol>
 *     <li><b>:validating</b> - Selects an element that is currently validating.
 *     <li><b>:invalid</b> - Selects an element that failed data validation.
 *     <li><b>:valid</b> - Selects an element that successfully completed data validation.
 *     <li><b>:user-invalid</b> - Selects an element that failed data validation after the user has interacted with it.
 *     <li><b>:user-valid</b> - Selects an element that successfully completed data validation after the user has
 *                              interacted with it.
 * </ol>
 *
 * Validation pseudo-classes are enabled by connecting a scene graph {@link Node} to a {@code ConstrainedValue}
 * by setting the {@link ValidationState#setSource(Node, ConstrainedValue) source} attached property:
 *
 * <pre>{@code
 * var textField = new TextField();
 * var firstName = new SimpleConstrainedStringProperty<String>(
 *     Constraints.notNullOrBlank(() -> "Value cannot be empty"),
 *     Constraints.matchesPattern("[^\\d\\W]*", v -> "Invalid value"));
 *
 * textField.textProperty().bindBidirectional(firstName);
 *
 * // The 'firstName' property will provide validation states for the 'textField' node:
 * ValidationState.setSource(textField, firstName);
 * }</pre>
 */
public enum ValidationState {

    /**
     * The value is currently validating, or the validation run was cancelled.
     */
    UNKNOWN,

    /**
     * The value is known to be valid.
     */
    VALID,

    /**
     * The value is known to be invalid.
     */
    INVALID;

    /**
     * Gets the {@link ConstrainedValue} that provides validation state for the specified {@link Node},
     * i.e. the source of the node's validation state.
     *
     * @param node the node
     * @return the {@code ConstrainedValue} that provides the validation state for the node
     */
    public static ConstrainedValue<?, ?> getSource(Node node) {
        if (node.hasProperties()) {
            NodeValidationInfo info = (NodeValidationInfo)node.getProperties().get(NodeValidationInfo.class);
            if (info == null) {
                node.getProperties().put(NodeValidationInfo.class, info = new NodeValidationInfo(node));
            }

            return info.getSource();
        }

        return null;
    }

    /**
     * Sets the {@link ConstrainedValue} that provides the validation state for the specified {@link Node},
     * i.e. the source of the node's validation state.
     *
     * @param node the node
     * @param source the {@code ConstrainedValue} that provides the validation state for the node
     */
    public static void setSource(Node node, ConstrainedValue<?, ?> source) {
        NodeValidationInfo info = (NodeValidationInfo)node.getProperties().get(NodeValidationInfo.class);
        if (info == null) {
            node.getProperties().put(NodeValidationInfo.class, info = new NodeValidationInfo(node));
        }

        info.setSource(source);
    }

}
