package com.thebois.models.world.structures;

import java.util.EnumMap;
import java.util.Map;

import com.thebois.models.Position;
import com.thebois.models.beings.IGiver;
import com.thebois.models.beings.IReceiver;
import com.thebois.models.inventory.IInventory;
import com.thebois.models.inventory.items.IItem;
import com.thebois.models.inventory.items.ItemType;

/**
 * A structure used for storing items.
 */
class Stockpile extends AbstractStructure implements IReceiver, IGiver {

    private final IInventory inventory;

    Stockpile(final Position position, final IInventory inventory) {
        super(position, StructureType.STOCKPILE, getBuildMaterials());
        this.inventory = inventory;
    }

    private static Map<ItemType, Integer> getBuildMaterials() {
        final Map<ItemType, Integer> neededItems = new EnumMap<>(ItemType.class);

        final int numberOfItems = 4;

        neededItems.put(ItemType.LOG, numberOfItems);
        neededItems.put(ItemType.ROCK, numberOfItems);

        return neededItems;
    }

    @Override
    public IStructure deepClone() {
        return new Stockpile(getPosition(), inventory);
    }

    @Override
    public float getCost() {
        return Float.MAX_VALUE;
    }

    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean tryGiveItem(final IItem item) {
        return inventory.tryAdd(item);
    }

    @Override
    public boolean canFitItem(final ItemType itemType) {
        return inventory.canFitItem(itemType);
    }

    @Override
    public IItem takeItem(final ItemType itemType) {
        return inventory.take(itemType);
    }

    @Override
    public boolean hasItem(final ItemType itemType) {
        return inventory.hasItem(itemType);
    }

}
