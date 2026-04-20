package xaos.tiles.terrain.special;

import xaos.tiles.Tile;
import xaos.utils.Messages;

public class Lava {

    public final static Tile[] TERRAIN_LAVA = {
            new Tile("lava"), //$NON-NLS-1$
            new Tile("lavamin"), //$NON-NLS-1$
            new Tile("lavamax"), //$NON-NLS-1$
    };
    private static final long serialVersionUID = 8093200100906408032L;

    public Lava() {
        for (Tile tile : TERRAIN_LAVA) {
            tile.setTextureID(tile.getIniHeader(), "lava"); //$NON-NLS-1$

            // Updateamos los frames y tal de animaciones
            tile.setAnimationTiles(TERRAIN_LAVA[0].getAnimationTiles());
            tile.setAnimationFrameDelay(TERRAIN_LAVA[0].getAnimationFrameDelay());
            tile.setCurrentAnimationTile(TERRAIN_LAVA[0].getCurrentAnimationTile());
            tile.setCurrentFrameDelay(TERRAIN_LAVA[0].getCurrentFrameDelay());
        }
    }

    public static String getTileName() {
        return Messages.getString("Lava.0"); //$NON-NLS-1$
    }

    public Tile getLavaCursor(int iCount) {
        if (iCount > 4) {
            return TERRAIN_LAVA[2];
        } else if (iCount < 3) {
            return TERRAIN_LAVA[1];
        } else {
            return TERRAIN_LAVA[0];
        }
    }

    public void updateAnimation() {
        for (Tile tile : TERRAIN_LAVA) {
            tile.updateAnimation(false);
        }
    }
}
