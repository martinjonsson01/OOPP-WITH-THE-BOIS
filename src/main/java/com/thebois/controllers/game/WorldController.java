package com.thebois.controllers.game;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.thebois.ColonyManagement;
import com.thebois.controllers.IController;
import com.thebois.models.world.World;
import com.thebois.views.IProjector;
import com.thebois.views.debug.BeingPathDebugView;
import com.thebois.views.debug.FrameCounterView;
import com.thebois.views.game.GameView;
import com.thebois.views.game.IView;

/**
 * Container class for controllers that manage the world.
 */
public class WorldController implements IController<GameView> {

    private final Collection<IController<IView>> controllers;
    private final Collection<InputProcessor> inputProcessors;
    private final GameView gameView;

    /**
     * Instantiate with all controllers and views used for the world.
     *
     * @param world     The world that the controllers manage.
     * @param projector Projector used for converting screen coordinates to world coordinates.
     * @param tileSize  The tile size represented on the screen.
     * @param font      The font used for game widgets.
     */
    public WorldController(
        final World world,
        final IProjector projector,
        final float tileSize,
        final BitmapFont font) {
        final StructureController structureController = new StructureController(world,
                                                                                projector,
                                                                                tileSize);
        final TerrainController terrainController = new TerrainController(world, tileSize);
        final ColonyController colonyController = new ColonyController(world.getColony(), tileSize);

        controllers = List.of(terrainController, structureController, colonyController);
        inputProcessors = List.of(structureController);

        gameView = createGameView(world, tileSize, font);
        structureController.setGameWidget(gameView);
    }

    private GameView createGameView(
        final World world, final float tileSize, final BitmapFont font) {
        final List<IView> views =
            controllers.stream().map(IController::getView).collect(Collectors.toList());
        if (ColonyManagement.DEBUG) {
            views.addAll(createDebugViews(world, tileSize, font));
        }
        return new GameView(views, tileSize);
    }

    private List<IView> createDebugViews(
        final World world, final float tileSize, final BitmapFont font) {
        final BeingPathDebugView beingPathDebugView = new BeingPathDebugView(world.getColony(),
                                                                             tileSize);
        final FrameCounterView frameCounterView = new FrameCounterView(font);

        return List.of(frameCounterView, beingPathDebugView);
    }

    @Override
    public GameView getView() {
        return gameView;
    }

    @Override
    public void update() {
        for (final IController<IView> controller : controllers) {
            controller.update();
        }
    }

    public Iterable<InputProcessor> getInputProcessors() {
        return inputProcessors;
    }

}