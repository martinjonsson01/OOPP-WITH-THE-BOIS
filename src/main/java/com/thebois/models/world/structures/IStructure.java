package com.thebois.models.world.structures;

import java.io.Serializable;

import com.thebois.abstractions.IDeepClonable;
import com.thebois.models.beings.IGiver;
import com.thebois.models.beings.IReceiver;
import com.thebois.models.world.ITile;

/**
 * Represents a Structure. E.g. a house.
 */
public interface IStructure
    extends ITile, IBuildable, IDeepClonable<IStructure>, Serializable, IGiver, IReceiver {

    /**
     * Returns the specific type of terrain.
     *
     * @return The Structure type.
     */
    StructureType getType();

}
