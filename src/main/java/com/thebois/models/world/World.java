package com.thebois.models.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.thebois.models.IFinder;
import com.thebois.models.Position;
import com.thebois.models.beings.Colony;
import com.thebois.models.beings.IBeingGroup;
import com.thebois.models.beings.roles.IRoleAllocator;
import com.thebois.models.world.structures.House;
import com.thebois.models.world.structures.IStructure;
import com.thebois.utils.MatrixUtils;

/**
 * World creates a matrix and keeps track of all the structures and resources in the game world.
 */
public class World implements IWorld, IFinder {

    private final ITerrain[][] terrainMatrix;
    private final Optional<IStructure>[][] structureMatrix;
    private final int worldSize;
    private Colony colony;

    /**
     * Initiates the world with the given size and the amount of pawns in the colony.
     *
     * @param worldSize The amount of tiles in length for X and Y, e.g. worldSize x worldSize.
     * @param pawnCount The amount of beings to initialize the players' BeingGroup with
     */
    public World(final int worldSize, final int pawnCount) {
        this(worldSize, null);
        this.colony = initColony(pawnCount);
    }

    /**
     * Initiates the world with the given size and colony.
     *
     * @param worldSize The amount of tiles in length for X and Y, e.g. worldSize x worldSize.
     * @param colony    The Colony that should be added to the world.
     */
    public World(final int worldSize, final Colony colony) {
        this.worldSize = worldSize;

        terrainMatrix = new ITerrain[worldSize][worldSize];
        for (int y = 0; y < worldSize; y++) {
            for (int x = 0; x < worldSize; x++) {
                terrainMatrix[y][x] = new Grass(x, y);
            }
        }
        // Structures
        // noinspection unchecked
        structureMatrix = new Optional[worldSize][worldSize];
        for (int y = 0; y < structureMatrix.length; y++) {
            for (int x = 0; x < structureMatrix[y].length; x++) {
                structureMatrix[y][x] = Optional.empty();
            }
        }
        this.colony = colony;
    }

    private Colony initColony(final int pawnCount) {
        return new Colony(findEmptyPositions(pawnCount), this);
    }

    private Iterable<Position> findEmptyPositions(final int count) {
        final List<Position> emptyPositions = new ArrayList<>();
        MatrixUtils.forEachElement(terrainMatrix, terrain -> {
            if (emptyPositions.size() >= count) return;
            if (terrain.getType().equals(TerrainType.GRASS)) {
                emptyPositions.add(terrain.getPosition());
            }
        });
        return emptyPositions;
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
    public ArrayList<ITerrain> getTerrainTiles() {
        final ArrayList<ITerrain> copy = new ArrayList<>();
        for (final ITerrain[] matrix : terrainMatrix) {
            for (final ITerrain iTerrain : matrix) {
                copy.add(iTerrain.deepClone());
            }
        }
        return copy;
    }

    /**
     * Returns the structures in a Collection as the interface IStructures.
     *
     * @return The list to be returned.
     */
    public Collection<IStructure> getStructures() {
        final Collection<IStructure> copy = new ArrayList<>();
        for (final Optional<IStructure>[] matrix : structureMatrix) {
            for (final Optional<IStructure> structure : matrix) {
                structure.ifPresent(iStructure -> copy.add(iStructure.deepClone()));
            }
        }
        return copy;
    }

    /**
     * Builds a structure at a given position if possible.
     *
     * @param position The position where the structure should be built.
     *
     * @return Whether or not the structure was built.
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
     * @return Whether or not the structure was built.
     */
    public boolean createStructure(final int posX, final int posY) {
        final Position position = new Position(posX, posY);
        if (isPositionPlaceable(position)) {
            structureMatrix[posY][posX] = Optional.of(new House(position));

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
        return structureMatrix[posIntY][posIntX].isEmpty();
    }

    /**
     * Returns the players' colony.
     *
     * @return the colony.
     */
    public IBeingGroup getColony() {
        return colony;
    }

    /**
     * Returns the role allocator for the players' colony.
     *
     * @return the role allocator.
     */
    public IRoleAllocator getRoleAllocator() {
        return colony;
    }

    /**
     * Updates the state of the world.
     */
    public void update() {
        colony.update();
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
                final ITile neighbour = terrainMatrix[neighbourY][neighbourX];
                if (tile.equals(neighbour)) continue;
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
        return terrainMatrix[posY][posX];
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
