package com.thebois.models.beings.actions;

import java.io.Serializable;

import com.thebois.models.beings.IActionPerformer;

/**
 * Represents a generator of actions.
 * <p>
 * Using this interface instead of storing actions directly means that they can be generated on the
 * fly depending on current conditions.
 * </p>
 *
 * @author Martin
 */
@FunctionalInterface
public interface IActionSource extends Serializable {

    /**
     * Generates an action.
     *
     * @param performer The entity who will perform the generated action.
     *
     * @return The generated action.
     */
    IAction generate(IActionPerformer performer);

}
