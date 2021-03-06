package com.thebois.models.world.structures;

import java.util.Objects;

import com.thebois.models.Position;
import com.thebois.models.inventory.IInventory;

/**
 * A factory that instantiates structures.
 *
 * @author Mathias
 */
public final class StructureFactory {

    private static IInventory inventory;

    private StructureFactory() {

    }

    public static void setInventory(final IInventory inventory) {
        StructureFactory.inventory = inventory;
    }

    /**
     * Creates an instance of a structure with given type at the given position.
     *
     * @param type     The type of structure to be created.
     * @param position The position of the structure.
     *
     * @return The created structure.
     */
    public static IStructure createStructure(
        final StructureType type, final Position position) {

        return switch (type) {
            case HOUSE -> new House(position);
            case STOCKPILE -> stockpile(position);
            case TOWN_HALL -> townHall(position);
        };
    }

    /**
     * Creates an instance of a structure with given type at the given position.
     *
     * @param type The type of structure to be created.
     * @param x    The X position of the structure.
     * @param y    The Y position of the structure.
     *
     * @return The created structure.
     */
    public static IStructure createStructure(final StructureType type, final int x, final int y) {
        return createStructure(type, new Position(x, y));
    }

    private static Stockpile stockpile(final Position position) {
        assertDependenciesNotNull();
        return new Stockpile(position, inventory);
    }

    private static TownHall townHall(final Position position) {
        assertDependenciesNotNull();
        return new TownHall(position, inventory);
    }

    private static void assertDependenciesNotNull() {
        Objects.requireNonNull(inventory, "Inventory is null. Call StructureFactory.setInventory.");
    }

}
