package com.thebois.models.beings.actions;

import java.io.Serializable;
import java.util.Objects;

import com.thebois.models.Position;
import com.thebois.models.beings.IActionPerformer;
import com.thebois.models.inventory.IStoreable;

/**
 * Action give item from performer to receiver.
 */
public class GiveItemAction extends AbstractTimeAction implements Serializable {

    private static final float TIME_REQUIRED_TO_GIVE_ITEM = 1f;
    private static final float MINIMUM_GIVE_DISTANCE = 2f;
    private final IStoreable storeable;
    private final Position storablePosition;

    /**
     * Instantiate with a receiver to give item to.
     *
     * @param storeable        Where the items should be stored.
     * @param storablePosition Where the storable is in the world.
     */
    public GiveItemAction(
        final IStoreable storeable, final Position storablePosition) {
        super(TIME_REQUIRED_TO_GIVE_ITEM);
        this.storeable = storeable;

        this.storablePosition = storablePosition;
    }

    /*
        @Override
        public boolean isCompleted(final IActionPerformer performer) {
            return performer.isEmpty();
        }
    */

    @Override
    protected void onPerformCompleted(final IActionPerformer performer) {
        if (!performer.isEmpty()) {
            storeable.tryAdd(performer.takeNextItem());
        }
    }

    @Override
    public boolean canPerform(final IActionPerformer performer) {
        return performer.getPosition().distanceTo(storablePosition) < MINIMUM_GIVE_DISTANCE;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final GiveItemAction that = (GiveItemAction) other;
        return Objects.equals(storeable, that.storeable) && Objects.equals(storablePosition,
                                                                           that.storablePosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeable, storablePosition);
    }

}
