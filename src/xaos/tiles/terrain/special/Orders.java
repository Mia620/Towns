package xaos.tiles.terrain.special;

import xaos.tiles.Tile;

public class Orders {

    public final static Tile[] TERRAIN_ORDERS = {
            new Tile("orders"), //$NON-NLS-1$
            new Tile("ordersBlock"), //$NON-NLS-1$
            new Tile("ordersMiniBlock"), //$NON-NLS-1$
    };
    private static final long serialVersionUID = -3349547564930871768L;

    public Orders() {
        for (Tile terrainOrder : TERRAIN_ORDERS) {
            terrainOrder.setTextureID(terrainOrder.getIniHeader(), "orders"); //$NON-NLS-1$
        }
    }

    public Tile getOrderTile(boolean bMined) {
        if (bMined) {
            return TERRAIN_ORDERS[0];
        }
        return TERRAIN_ORDERS[1];
    }

    public Tile getOrderTileMiniBlock() {
        return TERRAIN_ORDERS[2];
    }
}
