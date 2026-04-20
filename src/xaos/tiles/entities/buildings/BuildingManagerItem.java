package xaos.tiles.entities.buildings;

import xaos.tiles.entities.items.ItemManager;
import xaos.tiles.entities.items.ItemManagerItem;
import xaos.tiles.entities.living.LivingEntityManager;
import xaos.tiles.entities.living.LivingEntityManagerItem;
import xaos.tiles.terrain.TerrainManager;
import xaos.utils.Log;
import xaos.utils.Messages;
import xaos.utils.Point3DShort;
import xaos.utils.UtilsIniHeaders;

import java.util.ArrayList;

public class BuildingManagerItem {

    private String iniHeader;
    private String name;
    private ArrayList<String> descriptions; // Descripciones
    private String type;
    private short width;
    private short height;
    private String groundData;
    private Point3DShort entranceBaseCoordinates;
    private boolean canBeBuiltUnderground;
    private ArrayList<Integer> mustBeBuiltOver;
    private boolean mineTerrain;
    private ArrayList<int[]> prerequisites;
    private ArrayList<int[]> prerequisitesFriendly;

    private boolean automatic;

    public BuildingManagerItem(String sIniHeader, String sName, short iWidth, short iHeight) {
        iniHeader = sIniHeader;
        name = sName;
        width = iWidth;
        height = iHeight;
        prerequisites = new ArrayList<>();
        prerequisitesFriendly = new ArrayList<>();
    }

    public String getIniHeader() {
        return iniHeader;
    }

    public void setIniHeader(String iniHeader) {
        this.iniHeader = iniHeader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(ArrayList<String> descriptions) {
        this.descriptions = descriptions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public short getWidth() {
        return width;
    }

    public void setWidth(short width) {
        this.width = width;
    }

    public short getHeight() {
        return height;
    }

    public void setHeight(short height) {
        this.height = height;
    }

    public String getGroundData() {
        return groundData;
    }

    public void setGroundData(String groundData) {
        if (groundData == null || groundData.isEmpty() || groundData.length() != (getWidth() * getHeight()) || groundData.indexOf(Building.GROUND_ENTRANCE) == -1) {
            if (groundData != null && !groundData.isEmpty()) {
                Log.log(Log.LEVEL_ERROR, Messages.getString("BuildingManagerItem.0") + getIniHeader() + Messages.getString("BuildingManagerItem.1"), getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // Entrance en la primera casilla
            this.groundData = Building.GROUND_ENTRANCE +
                    // Las dem�s 0 -> No transitable
                    String.valueOf(Building.GROUND_NON_TRANSITABLE).repeat(Math.max(0, (getHeight() * getWidth()) - 1));
        } else {
            this.groundData = groundData;
        }

        // Seteamos la entrance coordinates
        short iEntrance = (short) this.groundData.indexOf(Building.GROUND_ENTRANCE);
        short yEntrance = (short) (iEntrance / getWidth());
        short xEntrance = (short) (iEntrance - (yEntrance * getWidth()));
        setEntranceBaseCoordinates(Point3DShort.getPoolInstance(xEntrance, yEntrance, (short) 0));
    }

    public Point3DShort getEntranceBaseCoordinates() {
        return entranceBaseCoordinates;
    }

    public void setEntranceBaseCoordinates(Point3DShort entranceBaseCoordinates) {
        this.entranceBaseCoordinates = entranceBaseCoordinates;
    }

    public void setCanBeBuiltUnderground(boolean canBeBuiltUnderground) {
        this.canBeBuiltUnderground = canBeBuiltUnderground;
    }

    public void setCanBeBuiltUnderground(String sCanBeBuiltUnderground) {
        // Por defecto es true
        if (sCanBeBuiltUnderground == null || sCanBeBuiltUnderground.trim().isEmpty()) {
            setCanBeBuiltUnderground(true);
        } else {
            setCanBeBuiltUnderground(Boolean.parseBoolean(sCanBeBuiltUnderground));
        }
    }

    public boolean canBeBuiltUnderground() {
        return canBeBuiltUnderground;
    }

    public ArrayList<Integer> getMustBeBuiltOver() {
        return mustBeBuiltOver;
    }

    public void setMustBeBuiltOver(ArrayList<String> mustBeBuiltOver) {
        if (mustBeBuiltOver != null) {
            ArrayList<Integer> alMustBeBuiltOver = new ArrayList<>(mustBeBuiltOver.size());
            for (String s : mustBeBuiltOver) {
                alMustBeBuiltOver.add(TerrainManager.getItem(s).getTerrainID());
            }

            this.mustBeBuiltOver = alMustBeBuiltOver;
        }
    }

    public boolean isMineTerrain() {
        return mineTerrain;
    }

    public void setMineTerrain(boolean mineTerrain) {
        this.mineTerrain = mineTerrain;
    }

    public void setMineTerrain(String sMineTerrain) {
        setMineTerrain(Boolean.parseBoolean(sMineTerrain));
    }

    public ArrayList<int[]> getPrerequisites() {
        if (prerequisites == null) {
            return null;
        }

        // Devolvemos una copia, siempre
        ArrayList<int[]> alReturn = new ArrayList<>(prerequisites.size());
        for (int[] prerequisite : prerequisites) {
            int[] aAux = new int[prerequisite.length];
            System.arraycopy(prerequisite, 0, aAux, 0, aAux.length);
            alReturn.add(aAux);
        }

        return alReturn;
    }

    public void setPrerequisites(ArrayList<String> prerequisites) throws Exception {
        if (prerequisites == null) {
            this.prerequisites = null;
            return;
        }

        ItemManagerItem imi;
        String sHeader;
        this.prerequisites = new ArrayList<>();
        for (String sList : prerequisites) {
            int[] aInts = UtilsIniHeaders.getIntsArray(sList);
            if (aInts != null) {
                for (int aInt : aInts) {
                    sHeader = UtilsIniHeaders.getStringIniHeader(aInt);
                    imi = ItemManager.getItem(sHeader);
                    if (imi == null) {
                        throw new Exception(Messages.getString("BuildingManagerItem.2") + sHeader + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                this.prerequisites.add(aInts);
            }
        }

        if (this.prerequisites.isEmpty()) {
            this.prerequisites = null;
        }
    }

    public ArrayList<int[]> getPrerequisitesFriendly() {
        if (prerequisitesFriendly == null) {
            return null;
        }

        // Devolvemos una copia, siempre
        ArrayList<int[]> alReturn = new ArrayList<>(prerequisitesFriendly.size());
        for (int[] ints : prerequisitesFriendly) {
            int[] aAux = new int[ints.length];
            System.arraycopy(ints, 0, aAux, 0, aAux.length);
            alReturn.add(aAux);
        }

        return alReturn;
    }

    public void setPrerequisitesFriendly(ArrayList<String> prerequisitesFriendly) throws Exception {
        if (prerequisitesFriendly == null) {
            this.prerequisitesFriendly = null;
            return;
        }

        LivingEntityManagerItem lemi;
        String sHeader;
        this.prerequisitesFriendly = new ArrayList<>();
        for (String sList : prerequisitesFriendly) {
            int[] aInts = UtilsIniHeaders.getIntsArray(sList);
            if (aInts != null) {
                for (int aInt : aInts) {
                    sHeader = UtilsIniHeaders.getStringIniHeader(aInt);
                    lemi = LivingEntityManager.getItem(sHeader);
                    if (lemi == null) {
                        throw new Exception(Messages.getString("BuildingManagerItem.4") + sHeader + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                this.prerequisitesFriendly.add(aInts);
            }
        }

        if (this.prerequisitesFriendly.isEmpty()) {
            this.prerequisitesFriendly = null;
        }
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(String sAutomatic) {
        setAutomatic(Boolean.parseBoolean(sAutomatic));
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }
}
