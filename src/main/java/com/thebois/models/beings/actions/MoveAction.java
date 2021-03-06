package com.thebois.models.beings.actions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import com.google.common.eventbus.Subscribe;

import com.thebois.listeners.IEventBusSource;
import com.thebois.listeners.events.ObstaclePlacedEvent;
import com.thebois.models.Position;
import com.thebois.models.beings.IActionPerformer;
import com.thebois.models.beings.pathfinding.IPathFinder;

/**
 * Moves the performer towards a specified goal.
 *
 * @author Martin
 */
class MoveAction implements IAction, Serializable {

    /**
     * The minimum distance the performer has to be from the start of the path for it not to be
     * recalculated.
     */
    private static final float MINIMUM_DISTANCE_TO_START = 2f;
    private final Position destination;
    private final IPathFinder pathFinder;
    private final IEventBusSource eventBusSource;
    private boolean canReachDestination = true;
    private LinkedList<Position> path = new LinkedList<>();

    /**
     * Instantiates with a destination to move towards.
     *
     * @param destination    The goal to move to.
     * @param pathFinder     A way of generating paths to positions.
     * @param eventBusSource The eventbus to listen to.
     */
    MoveAction(
        final Position destination,
        final IPathFinder pathFinder,
        final IEventBusSource eventBusSource) {
        this.destination = destination;
        this.pathFinder = pathFinder;
        this.eventBusSource = eventBusSource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MoveAction)) return false;
        final MoveAction moveTask = (MoveAction) o;
        return destination.equals(moveTask.destination);
    }

    @Override
    public String toString() {
        return "MoveAction{" + "destination=" + destination + '}';
    }

    @Override
    public void perform(final IActionPerformer performer, final float deltaTime) {
        if (isCompleted(performer)) return;

        final Position position = performer.getPosition();
        if (needsNewPath(position)) {
            calculatePathFrom(position);
            if (path.isEmpty()) {
                canReachDestination = false;
                return;
            }
        }

        if (position.equals(path.element())) {
            path.remove();
        }

        eventBusSource.getEventBus().register(this);
        performer.setDestination(path.element());
    }

    private boolean needsNewPath(final Position position) {
        return path.isEmpty() || !isNearStartOfPath(position);
    }

    private void calculatePathFrom(final Position start) {
        final Collection<Position> newPath = pathFinder.path(start, destination);
        setPath(newPath);
    }

    private boolean isNearStartOfPath(final Position current) {
        final Position start = path.getFirst();
        return current.distanceTo(start) < MINIMUM_DISTANCE_TO_START;
    }

    private void setPath(final Collection<Position> path) {
        this.path = new LinkedList<>();
        this.path.addAll(path);
    }

    @Override
    public boolean isCompleted(final IActionPerformer performer) {
        return performer.getPosition().equals(destination);
    }

    @Override
    public boolean canPerform(final IActionPerformer performer) {
        return canReachDestination;
    }

    /**
     * Listens to the ObstaclePlacedEvent in order to update pathfinding.
     *
     * @param event The published event.
     */
    @Subscribe
    public void onObstaclePlaced(final ObstaclePlacedEvent event) {
        if (path.contains(event.getPosition())) {
            recalculatePathAroundObstacle(event.getPosition());
        }
    }

    private void recalculatePathAroundObstacle(final Position obstaclePosition) {
        final int obstacleInPathIndex = path.indexOf(obstaclePosition);
        final int nodeBeforeObstacleIndex = Math.max(0, obstacleInPathIndex - 1);
        final Position pathNodeBeforeObstacle = path.get(nodeBeforeObstacleIndex);

        final Collection<Position> recalculatedPathSegment =
            pathFinder.path(pathNodeBeforeObstacle, destination);

        // If not possible to find another path to the destination.
        if (recalculatedPathSegment.isEmpty()) {
            canReachDestination = false;
            return;
        }

        final int originalSegmentEndIndex = Math.max(0, nodeBeforeObstacleIndex - 1);
        final Collection<Position> originalPathSegment = path.subList(0, originalSegmentEndIndex);

        final Collection<Position> recalculatedPath = new ArrayList<>(originalPathSegment);
        recalculatedPath.addAll(recalculatedPathSegment);
        setPath(recalculatedPath);
    }

    @Serial
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Registers every time on deserialization because it might be registered to an old instance
        // of the event bus.
        // (caused by saving/loading).
        in.defaultReadObject();
        // IMPORTANT: Has to subscribe after being read.
        eventBusSource.getEventBus().register(this);
    }

}
