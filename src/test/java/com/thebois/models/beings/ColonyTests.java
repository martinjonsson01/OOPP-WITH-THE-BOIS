package com.thebois.models.beings;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.thebois.models.IStructureFinder;
import com.thebois.models.Position;
import com.thebois.models.beings.pathfinding.IPathFinder;
import com.thebois.models.world.IWorld;
import com.thebois.models.world.terrains.Grass;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ColonyTests {

    @Test
    public void constructWithTilesCreatesOneBeingPerPosition() {
        // Arrange
        final int beingCount = 25;
        final List<Position> positions = new ArrayList<>(beingCount);
        for (int i = 0; i < beingCount; i++) {
            positions.add(new Position(0, 0));
        }
        final IWorld mockWorld = mock(IWorld.class);
        when(mockWorld.getTileAt(any())).thenReturn(new Grass(new Position()));

        final IPathFinder pathFinder = Mockito.mock(IPathFinder.class);

        final IStructureFinder structureFinder = Mockito.mock(IStructureFinder.class);

        // Act
        final Colony colony = new Colony(positions, pathFinder, structureFinder);

        // Assert
        assertThat(colony.getBeings().size()).isEqualTo(beingCount);
    }

    private Colony mockColony() {
        final List<Position> positions = new ArrayList<>();
        final IPathFinder pathFinder = Mockito.mock(IPathFinder.class);
        final IStructureFinder structureFinder = Mockito.mock(IStructureFinder.class);
        return new Colony(positions, pathFinder, structureFinder);
    }

    @Test
    public void ensureBeingsUpdatesWhenColonyUpdates() {
        // Arrange
        final IBeing being = Mockito.mock(IBeing.class);
        final Colony colony = mockColony();

        colony.addBeing(being);
        final float deltaTime = 0.1f;

        // Act
        colony.update(deltaTime);

        // Assert
        verify(being, times(1)).update(deltaTime);
    }

}
