package com.thebois.views.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.thebois.models.world.structures.IStructure;
import com.thebois.views.TextureUtils;

/**
 * Responsible for displaying structures.
 */
public final class StructureView implements IView {

    private final float tileSize;
    private Iterable<IStructure> structures;
    private final Texture houseTexture;

    /**
     * Creates an instance of a Structure view.
     *
     * @param tileSize The size of a single world tile in screen space.
     */
    public StructureView(final float tileSize) {
        this.tileSize = tileSize;
        houseTexture = TextureUtils.createSquareTexture(tileSize);
    }

    @Override
    public void draw(final Batch batch, final float offsetX, final float offsetY) {
        for (final IStructure structure : structures) {
            // Generate color based on structure completeness
            batch.setColor(getHouseColor(structure));
            batch.draw(houseTexture,
                       offsetX + structure.getPosition().getPosX() * tileSize,
                       offsetY + structure.getPosition().getPosY() * tileSize,
                       tileSize,
                       tileSize);
        }
    }

    // Houses get different colors based on built status
    private Color getHouseColor(final IStructure structure) {
        final Color bluePrintColor = Color.valueOf("3B2916");
        final Color houseColor = Color.valueOf("1B52AB");

        return houseColor.lerp(bluePrintColor, structure.getBuiltRatio());
    }

    /**
     * Updates the structures that should be displayed in the game.
     *
     * @param updatedStructures The structures that are to be displayed.
     */
    public void update(final Iterable<IStructure> updatedStructures) {
        this.structures = updatedStructures;
    }

    @Override
    public void dispose() {
        houseTexture.dispose();
    }

}
