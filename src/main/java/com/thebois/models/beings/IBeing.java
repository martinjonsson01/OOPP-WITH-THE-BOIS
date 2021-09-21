package com.thebois.models.beings;

import com.thebois.models.IDeepClonable;
import com.thebois.models.Position;
import com.thebois.models.beings.roles.AbstractRole;

/**
 * An entity.
 */
public interface IBeing extends IDeepClonable<IBeing> {

    /**
     * Gets the current location.
     *
     * @return The current location
     */
    Position getPosition();

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
     */
    void update();

}
