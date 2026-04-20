package xaos.tiles.terrain.special;

import xaos.panels.MainPanel;
import xaos.tiles.Tile;

public class MouseCursorBAD {

    public final static Tile[] TERRAIN_CURSORS = {
            new Tile("mouseCursorBAD"), //$NON-NLS-1$
            new Tile("mouseCursorBADBlock"), //$NON-NLS-1$
            new Tile("mouseCursorBADMiniBlock"), //$NON-NLS-1$
    };
    private static final long serialVersionUID = -8677065729568918523L;

    public MouseCursorBAD() {
        for (Tile terrainCursor : TERRAIN_CURSORS) {
            terrainCursor.setTextureID(terrainCursor.getIniHeader(), "mouseCursorBAD"); //$NON-NLS-1$
        }
    }

    public Tile getMouseCursor(boolean bMined) {
        if (bMined) {
            return TERRAIN_CURSORS[0];
        }
        if (MainPanel.bMiniBlocksON) {
            return TERRAIN_CURSORS[2];
        } else {
            return TERRAIN_CURSORS[1];
        }
    }

    public Tile getMouseCursorMiniBlock() {
        return TERRAIN_CURSORS[2];
    }
}
