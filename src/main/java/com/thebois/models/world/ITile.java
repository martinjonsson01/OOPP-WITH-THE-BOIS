package com.thebois.models.world;

import com.thebois.models.Position;

/**
 * A section of the world representing a structure or resource.
 */
public interface ITile {

    /**
     * Gets the current location.
     *
     * @return The current location.
     */
    Position getPosition();

    /**
     * Gets the cost of moving across.
     *
     * @return The cost of moving across
     */
    float getCost();

}
