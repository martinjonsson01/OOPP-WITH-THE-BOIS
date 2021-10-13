package com.thebois.models.world;

import com.thebois.models.world.resources.IResource;
import com.thebois.models.world.terrains.Grass;
import com.thebois.models.world.terrains.ITerrain;

public class MockWorld extends World {

    /**
     * Initiates the world with the given size, filled with grass and no resources.
     *
     * @param worldSize The amount of tiles in length for X and Y, e.g. worldSize x worldSize.
     */
    public MockWorld(final int worldSize) {
        super(worldSize, 0);
    }

    @Override
    protected IResource[][] setUpResources(final int worldSize, final int seed) {
        final IResource[][] resourceMatrix = new IResource[worldSize][worldSize];
        for (int y = 0; y < worldSize; y++) {
            for (int x = 0; x < worldSize; x++) {
                resourceMatrix[y][x] = null;
            }
        }
        return resourceMatrix;
    }

    @Override
    protected ITerrain[][] setUpTerrain(final int worldSize, final int seed) {
        final ITerrain[][] terrainMatrix = new ITerrain[worldSize][worldSize];
        for (int y = 0; y < worldSize; y++) {
            for (int x = 0; x < worldSize; x++) {
                terrainMatrix[y][x] = new Grass(x, y);
            }
        }
        return terrainMatrix;
    }

}
