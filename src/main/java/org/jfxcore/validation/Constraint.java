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

package org.jfxcore.validation;

import org.jfxcore.validation.function.CancellableValidationFunction0;
import org.jfxcore.validation.function.ValidationFunction0;
import org.jfxcore.validation.property.ConstrainedListProperty;
import org.jfxcore.validation.property.ConstrainedMapProperty;
import org.jfxcore.validation.property.ConstrainedProperty;
import org.jfxcore.validation.property.ConstrainedSetProperty;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Defines a data constraint.
 * <p>
 * When used with {@link ConstrainedListProperty}, {@link ConstrainedSetProperty} or {@link ConstrainedMapProperty},
 * this type of constraint applies to each element individually, and not to the collection instance itself.
 * <p>
 * Use {@link ListConstraint}, {@link SetConstraint} or {@link MapConstraint} to create a constraint that applies
 * to the collection instance instead of its individual elements.
 * <p>
 * For a selection of predefined constraints, see {@link Constraints}.
 *
 * @param <T> data type
 * @param <D> diagnostic type
 */
public non-sealed interface Constraint<T, D> extends ConstraintBase<T, D> {

    /**
     * Validates the specified value.
     * <p>
     * This method can be implemented to support synchronous or asynchronous validation:
     * <ul>
     *     <li>For synchronous validation, the method must return a completed future that can be obtained
     *         by calling {@link CompletableFuture#completedFuture}.
     *     <li>For asynchronous validation, the method must return a non-completed future.
     *         It is up to the implementation to decide how the future computes its value.
     *         For example, the implementation may choose to run the computation on a background thread.
     *         <p>
     *         Note: if the returned future completes on a background thread, an appropriate
     *         {@link Constraint#getCompletionExecutor() completion executor} must be specified
     *         that can safely yield the {@code ValidationResult} to the validation system.
     * </ul>
     * <p>
     * At any point, the data validation system may choose to cancel a non-completed future by
     * invoking {@link CompletableFuture#cancel(boolean)}.
     * In practice, this happens when a {@link ConstrainedProperty} value is changed before the
     * previous validation run has completed.
     * Cancelling a {@code CompletableFuture} immediately transitions the future into the
     * {@link CompletableFuture#isCancelled() cancelled} state, independent of whether the
     * validation function is still running.
     * <p>
     * Applications are encouraged to implement cancellation support to stop the execution
     * of a validation run after the data validation system has cancelled the future (see
     * {@link Constraints#validateCancellableAsync(CancellableValidationFunction0, Executor) validateCancellableAsync}
     * and {@link Constraints#validateInterruptibleAsync(ValidationFunction0, Executor) validateInterruptibleAsync}).
     *
     * @see Constraints#validateCancellableAsync(CancellableValidationFunction0, Executor)
     * @see Constraints#validateInterruptibleAsync(ValidationFunction0, Executor)
     *
     * @param value the value to be validated
     * @return a future that produces a {@code ValidationResult}
     */
    CompletableFuture<ValidationResult<D>> validate(T value);

}
