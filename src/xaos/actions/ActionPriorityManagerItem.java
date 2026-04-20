package xaos.actions;

import xaos.tiles.Tile;
import xaos.utils.Messages;

public class ActionPriorityManagerItem {

    private String id;
    private String name;
    private Tile icon;

    public ActionPriorityManagerItem(String sID) throws Exception {
        setId(sID);
    }

    public String getId() {
        return id;
    }

    public void setId(String sID) throws Exception {
        if (sID == null || sID.trim().isEmpty()) {
            throw new Exception(Messages.getString("ActionManagerItem.0")); //$NON-NLS-1$
        }

        this.id = sID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tile getIcon() {
        return icon;
    }

    public void setIcon(String sIcon) {
        if (sIcon != null) {
            this.icon = new Tile(sIcon);
        }
    }
}
