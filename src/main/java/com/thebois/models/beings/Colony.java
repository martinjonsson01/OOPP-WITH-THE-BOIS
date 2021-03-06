package com.thebois.models.beings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.eventbus.Subscribe;

import com.thebois.abstractions.IPositionFinder;
import com.thebois.listeners.IEventBusSource;
import com.thebois.listeners.events.StructureCompletedEvent;
import com.thebois.models.Position;
import com.thebois.models.beings.roles.AbstractRole;
import com.thebois.models.beings.roles.IRoleAllocator;
import com.thebois.models.beings.roles.RoleFactory;
import com.thebois.models.beings.roles.RoleType;
import com.thebois.models.inventory.Inventory;
import com.thebois.models.world.structures.StructureType;

/**
 * A Colony is a collection of Pawns that can be controlled by the player.
 *
 * @author Martin
 */
public class Colony extends AbstractBeingGroup implements IRoleAllocator {

    private static final float PAWN_INVENTORY_MAX_CAPACITY = 50f;
    private static final int NUMBER_OF_PAWNS_SPAWNED_BY_HOUSE = 1;
    private static final int NUMBER_OF_PAWNS_SPAWNED_BY_TOWN_HALL = 5;
    private static final int PAWN_SPAWN_RADIUS = 2;
    private final IPositionFinder positionFinder;
    private final IEventBusSource eventBusSource;

    /**
     * Creates a colony and fills it with pawns in the provided open positions.
     *
     * @param eventBusSource A way of getting an event bus to listen for events on.
     * @param positionFinder Used to find positions in the world.
     */
    public Colony(final IPositionFinder positionFinder, final IEventBusSource eventBusSource) {
        super(eventBusSource);
        this.eventBusSource = eventBusSource;
        this.positionFinder = positionFinder;
        eventBusSource.getEventBus().register(this);
    }

    @Override
    public boolean tryIncreaseAllocation(final RoleType roleType) {
        final Optional<IBeing> idleBeing = findIdleBeings().stream().findAny();
        if (idleBeing.isPresent()) {
            final AbstractRole role = RoleFactory.fromType(roleType);
            idleBeing.get().setRole(role);
            return true;
        }

        return false;
    }

    @Override
    public boolean tryIncreaseAllocation(final RoleType roleType, final int amount) {
        if (!canIncreaseAllocation(amount)) return false;
        for (int i = 0; i < amount; i++) {
            tryIncreaseAllocation(roleType);
        }
        return true;
    }

    @Override
    public boolean tryDecreaseAllocation(final RoleType roleType) {
        final AbstractRole role = RoleFactory.fromType(roleType);
        final Optional<IBeing> beingWithRole = findBeingsWithRole(role).stream().findFirst();
        if (beingWithRole.isPresent()) {
            beingWithRole.get().setRole(RoleFactory.idle());
            return true;
        }
        return false;
    }

    @Override
    public boolean tryDecreaseAllocation(final RoleType roleType, final int amount) {
        if (!canDecreaseAllocation(roleType, amount)) return false;
        for (int i = 0; i < amount; i++) {
            tryDecreaseAllocation(roleType);
        }
        return true;
    }

    @Override
    public int countBeingsWithRole(final RoleType roleType) {
        final AbstractRole role = RoleFactory.fromType(roleType);
        return (int) getBeings().stream().filter(being -> role.equals(being.getRole())).count();
    }

    @Override
    public boolean canDecreaseAllocation(final RoleType roleType) {
        return canDecreaseAllocation(roleType, 1);
    }

    @Override
    public boolean canDecreaseAllocation(final RoleType roleType, final int amount) {
        final AbstractRole role = RoleFactory.fromType(roleType);
        return findBeingsWithRole(role).size() >= amount;
    }

    @Override
    public boolean canIncreaseAllocation() {
        return canIncreaseAllocation(1);
    }

    @Override
    public boolean canIncreaseAllocation(final int amount) {
        return findIdleBeings().size() >= amount;
    }

    private Collection<IBeing> findIdleBeings() {
        final Stream<IBeing> idleBeings = getBeings().stream().filter(this::isIdle);
        return idleBeings.collect(Collectors.toList());
    }

    private boolean isIdle(final IBeing being) {
        return being.getRole().getType().equals(RoleType.IDLE);
    }

    private Collection<IBeing> findBeingsWithRole(final AbstractRole role) {
        return getBeings().stream()
                          .filter(being -> role.equals(being.getRole()))
                          .collect(Collectors.toList());
    }

    /**
     * Listens to the spawnPawnsEvent in order to know when to add pawns to the colony.
     *
     * @param event The published event.
     */
    @Subscribe
    public void onStructureCompletedEvent(final StructureCompletedEvent event) {
        final int numberOfPawnsToSpawn = getNumberOfPawnsToSpawn(event.getStructureType());
        final Iterable<Position> vacantPositions =
            positionFinder.tryGetEmptyPositionsNextTo(event.getPosition(),
                                                      numberOfPawnsToSpawn,
                                                      PAWN_SPAWN_RADIUS);
        for (final Position vacantPosition : vacantPositions) {
            addBeing(new Being(vacantPosition,
                               RoleFactory.idle(),
                               RoleFactory.fisher(),
                               eventBusSource,
                               new Inventory(PAWN_INVENTORY_MAX_CAPACITY)));
        }
    }

    private int getNumberOfPawnsToSpawn(final StructureType structureType) {
        switch (structureType) {
            case HOUSE -> {
                return NUMBER_OF_PAWNS_SPAWNED_BY_HOUSE;
            }
            case TOWN_HALL -> {
                return NUMBER_OF_PAWNS_SPAWNED_BY_TOWN_HALL;
            }
            default -> {
                return 0;
            }
        }
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
