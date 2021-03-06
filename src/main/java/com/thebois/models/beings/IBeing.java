package com.thebois.models.beings;

import java.io.Serializable;

import com.thebois.abstractions.IPositionable;
import com.thebois.models.Position;
import com.thebois.models.beings.roles.AbstractRole;

/**
 * An entity.
 *
 * @author Jacob
 * @author Martin
 */
public interface IBeing extends Serializable, IPositionable {

    /**
     * Gets the role.
     *
     * @return The role
     */
    AbstractRole getRole();

    /**
     * Sets the role.
     *
     * @param role The role
     */
    void setRole(AbstractRole role);

    /**
     * Updates the objects internal state.
     *
     * @param deltaTime The amount of time that has passed since the last update, in seconds.
     */
    void update(float deltaTime);

    /**
     * Gets the current destination.
     *
     * @return The destination location.
     */
    Position getDestination();

    /**
     * Gets the health ratio, where 0 is dead and 1 is full health.
     *
     * @return The current ratio a number between 1 and 0.
     */
    float getHealthRatio();

}
