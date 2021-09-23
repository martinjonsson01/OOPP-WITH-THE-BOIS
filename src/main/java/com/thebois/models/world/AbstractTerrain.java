package com.thebois.models.world;

import com.thebois.models.Position;

/**
 * Abstract implementation of ITerrain.
 */
public abstract class AbstractTerrain implements ITerrain {

    private final Position position;

    protected AbstractTerrain(final Position position) {
        this.position = position;
    }

    protected AbstractTerrain(final float posX, final float posY) {
        this.position = new Position(posX, posY);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass().equals(obj.getClass())) {
            final Grass other = (Grass) obj;
            return getType().equals(other.getType())
                   && getPosition().getPosX() == other.getPosition().getPosX()
                   && getPosition().getPosY() == other.getPosition().getPosY();
        }
        return false;
    }

    @Override
    public Position getPosition() {
        return position.deepClone();
    }

    @Override
    public float getCost() {
        return 0;
    }

    @Override
    public String toString() {
        return "AbstractTerrain{" + "position=" + position + '}';
    }

}
