package com.thebois.models.beings.actions;

import java.io.Serializable;

import com.thebois.models.beings.IActionPerformer;

/**
 * Makes the performer do nothing at all.
 * <p>
 * Can never be performed. It is an action given to a being when it asks for an action but there is
 * nothing more to do.
 * </p>
 *
 * @author Martin
 */
public class DoNothingAction implements IAction, Serializable {

    private boolean isDone = false;

    @Override
    public void perform(final IActionPerformer performer, final float deltaTime) {
        isDone = true;
    }

    @Override
    public boolean isCompleted(final IActionPerformer performer) {
        return isDone;
    }

    @Override
    public boolean canPerform(final IActionPerformer performer) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        return other instanceof DoNothingAction;
    }

}
