package com.thebois.models.world.terrains;

import com.thebois.models.world.ITile;
import com.thebois.abstractions.IDeepClonable;

/**
 * Represents a tile of terrain. E.g. grass, dirt etc.
 *
 * @author Jonathan
 */
public interface ITerrain extends ITile, IDeepClonable<ITerrain> {

    /**
     * Returns the specific type of terrain.
     *
     * @return The terrain type.
     */
    TerrainType getType();

}
