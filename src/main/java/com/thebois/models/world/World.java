package com.thebois.models.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thebois.Pawntastic;
import com.thebois.listeners.events.ObstaclePlacedEvent;
import com.thebois.models.IFinder;
import com.thebois.models.Position;
import com.thebois.models.world.generation.ResourceGenerator;
import com.thebois.models.world.generation.TerrainGenerator;
import com.thebois.models.world.resources.IResource;
import com.thebois.models.world.structures.House;
import com.thebois.models.world.structures.IStructure;
import com.thebois.models.world.terrains.ITerrain;
import com.thebois.utils.MatrixUtils;

/**
 * World creates a matrix and keeps track of all the structures and resources in the game world.
 */
public class World implements IWorld, IFinder {

    private final ITerrain[][] terrainMatrix;
    private final IStructure[][] structureMatrix;
    private final IResource[][] resourceMatrix;
    private final ITile[][] canonicalMatrix;
    private final int worldSize;

    /**
     * Initiates the world with the given size.
     *
     * @param worldSize The amount of tiles in length for X and Y, e.g. worldSize x worldSize.
     * @param seed      The seed used to generate the world.
     */
    public World(final int worldSize, final int seed) {
        this.worldSize = worldSize;
        terrainMatrix = setUpTerrain(worldSize, seed);
        structureMatrix = setUpStructures();
        resourceMatrix = setUpResources(worldSize, seed);
        canonicalMatrix = new ITile[worldSize][worldSize];
        updateCanonicalMatrix();
    }

    private IStructure[][] setUpStructures() {
        final IStructure[][] newStructureMatrix = new IStructure[worldSize][worldSize];
        MatrixUtils.populateElements(newStructureMatrix, (x, y) -> null);
        return newStructureMatrix;
    }

    protected ITerrain[][] setUpTerrain(final int size, final int seed) {
        return new TerrainGenerator(worldSize, seed).generateTerrainMatrix();
    }

    protected IResource[][] setUpResources(final int size, final int seed) {
        return new ResourceGenerator(worldSize, seed).generateResourceMatrix();
    }

    /**
     * The canonical matrix is a mash-up of all the different layers of the world. It replaces less
     * important tiles like grass with a structure if it is located at the same coordinates.
     *
     * <p>
     * Warning: expensive method, do not call every frame!
     * </p>
     */
    private void updateCanonicalMatrix() {
        // Fill with terrain.
        MatrixUtils.forEachElement(terrainMatrix, tile -> {
            final Position position = tile.getPosition();
            final int posY = (int) position.getPosY();
            final int posX = (int) position.getPosX();
            canonicalMatrix[posY][posX] = terrainMatrix[posY][posX].deepClone();
        });
        // Replace terrain with any possible resource.
        MatrixUtils.forEachElement(resourceMatrix, maybeResource -> {
            if (maybeResource != null) {
                final Position position = maybeResource.getPosition();
                final int posY = (int) position.getPosY();
                final int posX = (int) position.getPosX();
                canonicalMatrix[posY][posX] = maybeResource.deepClone();
            }
        });
        // Replace terrain with any possible structure.
        MatrixUtils.forEachElement(structureMatrix, structure -> {
            if (structure != null) {
                final Position position = structure.getPosition();
                final int posY = (int) position.getPosY();
                final int posX = (int) position.getPosX();
                canonicalMatrix[posY][posX] = structure.deepClone();
            }
        });
    }

    /**
     * Creates a list of tiles that are not occupied by a structure or resource.
     *
     * @param count The amount of empty positions that needs to be found.
     *
     * @return List of empty positions.
     */
    public Iterable<Position> findEmptyPositions(final int count) {
        final List<Position> emptyPositions = new ArrayList<>();
        MatrixUtils.forEachElement(canonicalMatrix, tile -> {
            if (emptyPositions.size() >= count) return;
            if (isPositionEmpty(tile.getPosition())) {
                emptyPositions.add(tile.getPosition());
            }
        });
        return emptyPositions;
    }

    private boolean isPositionEmpty(final Position position) {
        return canonicalMatrix[(int) position.getPosY()][(int) position.getPosX()].getCost()
               < Float.MAX_VALUE;
    }

    /**
     * Locates an object in the world and returns it.
     *
     * @return Object.
     */
    public Object find() {
        return null;
    }

    /**
     * Returns the matrix of the world.
     *
     * @return ITile[][]
     */
    public Collection<ITerrain> getTerrainTiles() {
        final Collection<ITerrain> copy = new ArrayList<>();
        MatrixUtils.forEachElement(terrainMatrix, maybeTerrain -> {
            if (maybeTerrain != null) {
                copy.add(maybeTerrain.deepClone());
            }
        });
        return copy;
    }

    /**
     * Returns the structures in a Collection as the interface IStructure.
     *
     * @return The list to be returned.
     */
    public Collection<IStructure> getStructures() {
        final Collection<IStructure> copy = new ArrayList<>();
        MatrixUtils.forEachElement(structureMatrix, maybeStructure -> {
            if (maybeStructure != null) {
                copy.add(maybeStructure.deepClone());
            }
        });
        return copy;
    }

    /**
     * Returns the resources in a Collection as the interface IResource.
     *
     * @return The list to be returned.
     */
    public Collection<IResource> getResources() {
        final Collection<IResource> copy = new ArrayList<>();
        MatrixUtils.forEachElement(resourceMatrix, maybeResource -> {
            if (maybeResource != null) {
                copy.add(maybeResource.deepClone());
            }
        });
        return copy;
    }

    /**
     * Builds a structure at a given position if possible.
     *
     * @param position The position where the structure should be built.
     *
     * @return Whether the structure was built.
     */
    public boolean createStructure(final Position position) {
        return createStructure((int) position.getPosX(), (int) position.getPosY());
    }

    /**
     * Builds a structure at a given position if possible.
     *
     * @param posX The X coordinate where the structure should be built.
     * @param posY The Y coordinate where the structure should be built.
     *
     * @return Whether the structure was built.
     */
    public boolean createStructure(final int posX, final int posY) {
        final Position position = new Position(posX, posY);
        if (isPositionPlaceable(position)) {
            structureMatrix[posY][posX] = new House(position);

            updateCanonicalMatrix();
            postObstacleEvent(posX, posY);
            return true;
        }
        return false;
    }

    private boolean isPositionPlaceable(final Position position) {
        final int posIntX = (int) position.getPosX();
        final int posIntY = (int) position.getPosY();
        if (posIntY < 0 || posIntY >= structureMatrix.length) {
            return false;
        }
        if (posIntX < 0 || posIntX >= structureMatrix[posIntY].length) {
            return false;
        }
        return isPositionEmpty(position);
    }

    private void postObstacleEvent(final int posX, final int posY) {
        final ObstaclePlacedEvent obstacleEvent = new ObstaclePlacedEvent(posX, posY);
        Pawntastic.BUS.post(obstacleEvent);
    }

    @Override
    public Iterable<ITile> getNeighboursOf(final ITile tile) {
        final ArrayList<ITile> tiles = new ArrayList<>(8);
        final Position position = tile.getPosition();
        final int posY = (int) position.getPosY();
        final int posX = (int) position.getPosX();

        final int startY = Math.max(0, posY - 1);
        final int endY = Math.min(worldSize - 1, posY + 1);
        final int startX = Math.max(0, posX - 1);
        final int endX = Math.min(worldSize - 1, posX + 1);

        for (int neighbourY = startY; neighbourY <= endY; neighbourY++) {
            for (int neighbourX = startX; neighbourX <= endX; neighbourX++) {
                final ITile neighbour = canonicalMatrix[neighbourY][neighbourX];
                if (position.equals(neighbour.getPosition())) continue;
                if (isDiagonalTo(tile, neighbour)) continue;
                tiles.add(neighbour);
            }
        }

        return tiles;
    }

    @Override
    public ITile getTileAt(final Position position) {
        return getTileAt((int) position.getPosX(), (int) position.getPosY());
    }

    @Override
    public ITile getTileAt(final int posX, final int posY) {
        if (posX < 0 || posY < 0 || posX >= worldSize || posY >= worldSize) {
            throw new IndexOutOfBoundsException("Given position is outside of the world.");
        }
        return canonicalMatrix[posY][posX];
    }

    private boolean isDiagonalTo(final ITile tile, final ITile neighbour) {
        final int tileX = (int) tile.getPosition().getPosX();
        final int tileY = (int) tile.getPosition().getPosY();
        final int neighbourX = (int) neighbour.getPosition().getPosX();
        final int neighbourY = (int) neighbour.getPosition().getPosY();
        final int deltaX = Math.abs(tileX - neighbourX);
        final int deltaY = Math.abs(tileY - neighbourY);
        return deltaX == 1 && deltaY == 1;
    }

}
