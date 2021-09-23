package com.thebois.models.beings;

import java.util.Random;

import com.thebois.models.Position;
import com.thebois.models.beings.pathfinding.IPathFinder;

/**
 * Pawn.
 */
public class Pawn extends AbstractBeing {

    /* Temporary hard-coded world size. Should be removed when pathfinding is implemented. */
    private static final int WORLD_SIZE = 50;
    private final Random random;

    /**
     * Instantiates with an initial position and a destination to travel to.
     *
     * @param startPosition initial position.
     * @param destination   initial destination to travel to.
     * @param random        the generator of random numbers.
     * @param pathFinder    The generator of paths to positions in the world.
     */
    public Pawn(final Position startPosition,
                final Position destination,
                final Random random,
                final IPathFinder pathFinder) {
        super(startPosition, destination, pathFinder);
        this.random = random;
    }

    @Override
    public void update() {
        super.update();

        if (getDestination().isEmpty()) setRandomDestination();
    }

    private void setRandomDestination() {
        final int randomX = random.nextInt(WORLD_SIZE);
        final int randomY = random.nextInt(WORLD_SIZE);
        final Position destination = new Position(randomX, randomY);

        setPath(getPathFinder().path(getPosition(), destination));
    }

}
