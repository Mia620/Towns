package xaos.tiles.terrain.special;

import xaos.tiles.Tile;
import xaos.utils.Messages;

public class Water {

    public final static Tile[] TERRAIN_WATER = {
            new Tile("water"), //$NON-NLS-1$
            new Tile("watermin"), //$NON-NLS-1$
            new Tile("watermax"), //$NON-NLS-1$
    };
    private static final long serialVersionUID = 2201137814233532108L;

    public Water() {
        for (Tile tile : TERRAIN_WATER) {
            tile.setTextureID(tile.getIniHeader(), "water"); //$NON-NLS-1$

            // Updateamos los frames y tal de animaciones
            tile.setAnimationTiles(TERRAIN_WATER[0].getAnimationTiles());
            tile.setAnimationFrameDelay(TERRAIN_WATER[0].getAnimationFrameDelay());
            tile.setCurrentAnimationTile(TERRAIN_WATER[0].getCurrentAnimationTile());
            tile.setCurrentFrameDelay(TERRAIN_WATER[0].getCurrentFrameDelay());
        }
    }

    public static String getTileName() {
        return Messages.getString("Water.0"); //$NON-NLS-1$
    }

    public Tile getWaterCursor(int iCount) {
        if (iCount > 4) {
            return TERRAIN_WATER[2];
        } else if (iCount < 3) {
            return TERRAIN_WATER[1];
        } else {
            return TERRAIN_WATER[0];
        }
    }

    public void updateAnimation() {
        for (Tile tile : TERRAIN_WATER) {
            tile.updateAnimation(false);
        }
    }
}
