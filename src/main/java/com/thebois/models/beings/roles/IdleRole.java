package com.thebois.models.beings.roles;

import java.util.Collection;
import java.util.List;

import com.thebois.models.beings.IActionPerformer;
import com.thebois.models.beings.actions.ActionFactory;
import com.thebois.models.beings.actions.IAction;
import com.thebois.models.beings.actions.IActionSource;
import com.thebois.models.world.ITile;
import com.thebois.models.world.IWorld;

/**
 * The builder constructs buildings.
 *
 * @author Martin
 */
class IdleRole extends AbstractRole {

    /**
     * The radius at which random positions to move to are picked, in tiles.
     */
    private static final int ROAM_RADIUS = 5;
    private final IWorld world;

    /**
     * Instantiates with a world to randomly move around in.
     *
     * @param world The world to move around in.
     */
    IdleRole(final IWorld world) {
        this.world = world;
    }

    @Override
    public RoleType getType() {
        return RoleType.IDLE;
    }

    @Override
    protected Collection<IActionSource> getTaskGenerators() {
        return List.of(this::getRandomMove);
    }

    private IAction getRandomMove(final IActionPerformer performer) {
        final ITile randomVacantTile =
            world.getRandomVacantSpotInRadiusOf(performer.getPosition(), ROAM_RADIUS);
        return ActionFactory.createMoveTo(randomVacantTile.getPosition());
    }

}
