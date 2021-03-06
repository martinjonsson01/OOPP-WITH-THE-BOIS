package com.thebois.views;

import com.badlogic.gdx.math.Vector2;

/**
 * Projector used to convert space and world coordinates.
 *
 * @author Mathias
 */
public interface IProjector {

    /**
     * Unprojects screen coordinates to world coordinates.
     *
     * @param screenX X coordinate of a position in screen space.
     * @param screenY Y coordinate of a position in screen space.
     *
     * @return Returns a Vector2 in the world coordinates.
     */
    Vector2 unproject(int screenX, int screenY);

}
