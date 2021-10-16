package com.thebois.models.world.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.thebois.models.Position;
import com.thebois.models.inventory.IInventory;
import com.thebois.models.inventory.Inventory;
import com.thebois.models.inventory.items.IItem;
import com.thebois.models.inventory.items.ItemType;

/**
 * Base structure for the game.
 */
abstract class AbstractStructure implements IStructure {

    private Position position;
    private StructureType structureType;
    private Map<ItemType, Integer> allNeededItems;
    private IInventory deliveredItems = new Inventory();

    /**
     * Creates a structure with a position and structure type.
     *
     * @param posX          Position in X-axis.
     * @param posY          Position in Y-axis.
     * @param structureType The type of structure to create.
     * @param allNeededItems The items needed to finalize construction.
     */
    AbstractStructure(
        final int posX,
        final int posY,
        final StructureType structureType,
        final Map<ItemType, Integer> allNeededItems) {
        this.position = new Position(posX, posY);
        this.structureType = structureType;
        this.allNeededItems = allNeededItems;
    }

    /**
     * Creates a structure with a position and type.
     *
     * @param position      The position the structure have.
     * @param structureType The type of structure to create.
     * @param allNeededItems The items needed to finalize construction.
     */
    AbstractStructure(
        final Position position,
        final StructureType structureType,
        final Map<ItemType, Integer> allNeededItems) {
        this((int) position.getPosX(), (int) position.getPosY(), structureType, allNeededItems);
    }

    @Override
    public Position getPosition() {
        return position.deepClone();
    }

    @Override
    public StructureType getType() {
        return structureType;
    }

    @Override
    public Collection<ItemType> getNeededItems() {
        final Collection<ItemType> neededItems = new ArrayList<>();
        for (final var entry : allNeededItems.entrySet()) {
            final int numberOf = entry.getValue() - deliveredItems.numberOf(entry.getKey());

            for (int i = 0; i < numberOf; i++) {
                neededItems.add(entry.getKey());
            }

        }
        return neededItems;
    }

    private int calculateSumOfAllNeededItems() {
        int sum = 0;
        for (final var entry : allNeededItems.entrySet()) {
            sum += entry.getValue();
        }
        return sum;
    }

    @Override
    public boolean tryDeliverItem(final IItem deliveredItem) {
        final Collection<ItemType> neededItems = getNeededItems();
        if (neededItems.contains(deliveredItem.getType())) {
            deliveredItems.add(deliveredItem);
            return true;
        }
        return false;
    }

    @Override
    public float getBuiltRatio() {
        final float totalNeeded = calculateSumOfAllNeededItems();
        final float totalDelivered = totalNeeded - getNeededItems().size();

        return totalDelivered / totalNeeded;
    }

    @Override
    public boolean isCompleted() {
        return Float.compare(getBuiltRatio(), 1f) >= 0;
    }

    @Override
    public Optional<IItem> tryDismantle(final ItemType retrieving) {
        if (deliveredItems.hasItem(retrieving)) {
            return Optional.of(deliveredItems.take(retrieving));
        }
        return Optional.empty();
    }

}
