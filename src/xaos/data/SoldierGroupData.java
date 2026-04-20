package xaos.data;

import xaos.main.World;
import xaos.utils.Messages;
import xaos.utils.Point3DShort;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

public class SoldierGroupData implements Externalizable {

    public final static int STATE_GUARD = 0;
    public final static int STATE_PATROL = 1;
    public final static int STATE_BOSS = 2;
    private static final long serialVersionUID = 5464174806753157978L;
    private int id;
    private String name;
    private ArrayList<Integer> livingIDs;
    private int state;
    private int zoneID;

    // Data
    private ArrayList<Point3DShort> patrolPoints;

    public SoldierGroupData() {
    }

    public SoldierGroupData(int id) {
        setId(id);
        setName(null);
        livingIDs = new ArrayList<>();
        setState(STATE_GUARD);
        setZoneID(0); // No zone
        patrolPoints = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.name = Messages.getString("SoldierGroupData.0") + (id + 1); //$NON-NLS-1$
        } else {
            this.name = name;
        }
    }

    public ArrayList<Integer> getLivingIDs() {
        return livingIDs;
    }

    public void setLivingIDs(ArrayList<Integer> livingIDs) {
        this.livingIDs = livingIDs;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;

        if (this.state != STATE_PATROL && getPatrolPoints() != null) {
            int iSize = getPatrolPoints().size();
            Point3DShort p3d;
            for (int i = 0; i < iSize; i++) {
                p3d = getPatrolPoints().remove(0);

                World.checkFlagPatrolPoint(p3d);
            }
        }
    }

    public int getZoneID() {
        return zoneID;
    }

    public void setZoneID(int zoneID) {
        this.zoneID = zoneID;
    }

    public boolean hasZone() {
        return zoneID != 0;
    }

    public ArrayList<Point3DShort> getPatrolPoints() {
        return patrolPoints;
    }

    public void setPatrolPoints(ArrayList<Point3DShort> patrolPoints) {
        this.patrolPoints = patrolPoints;
    }

    public void addPatrolPoint(Point3DShort p3d) {
        if (getPatrolPoints().size() == SoldierData.COUNTER_MAX_PATROL_POINTS) {
            getPatrolPoints().remove(0);
        }

        getPatrolPoints().add(Point3DShort.getPoolInstance(p3d.x, p3d.y, p3d.z));

        World.getCell(p3d).setFlagPatrol(true);
    }

    public void removePatrolPoint(Point3DShort p3d) {
        getPatrolPoints().remove(p3d);
        World.checkFlagPatrolPoint(p3d);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readInt();
        name = (String) in.readObject();
        livingIDs = (ArrayList<Integer>) in.readObject();
        state = in.readInt();
        zoneID = in.readInt();
        patrolPoints = (ArrayList<Point3DShort>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeObject(name);
        out.writeObject(livingIDs);
        out.writeInt(state);
        out.writeInt(zoneID);
        out.writeObject(patrolPoints);
    }
}
