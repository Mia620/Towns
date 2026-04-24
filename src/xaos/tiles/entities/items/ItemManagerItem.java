package xaos.tiles.entities.items;

import xaos.data.HateData;
import xaos.effects.EffectManager;
import xaos.tiles.entities.items.military.MilitaryItem;
import xaos.utils.Log;
import xaos.utils.Messages;
import xaos.utils.Utils;
import xaos.utils.UtilsIniHeaders;

import java.awt.*;
import java.util.ArrayList;

public class ItemManagerItem {

    public final static String CHARGES_INFINITE = "INFINITE"; //$NON-NLS-1$

    public final static String LIGHT_NONE = "NONE"; //$NON-NLS-1$
    public final static String LIGHT_FULL = "FULL"; //$NON-NLS-1$
    public final static String LIGHT_HALF = "HALF"; //$NON-NLS-1$

    public final static int LIGHT_I_NONE = 0;
    public final static int LIGHT_I_FULL = 1;
    public final static int LIGHT_I_HALF = 2;

    public static int MAX_LIGHT_RADIUS = 0;

    private String iniHeader; // Iniheader
    private int numericalIniHeader; // Iniheader
    private int level; // Nivel del item
    private String name; // Nombre
    private ArrayList<String> descriptions; // Descripciones
    private String type; // Tipo (se usa en las stockpiles)
    private String building; // Edificio donde se puede construir
    private ArrayList<String> prerequisites; // Prerequisitos para construirlo (2 maderas, 1 piedra, ...)
    private int buildingTime; // Tiempo que tarda en construirse (en el caso de que no haya prerequisitos)
    private int floorWalkSpeed;

    // Habitat
    private ArrayList<Integer> habitat; // Lista de terrenos donde puede crecer (o aparecer), se usa en los child (ver m�s abajo). (Ej: grass, sand, ...)
    private ArrayList<String> habitatString; // Lista de terrenos donde puede crecer (o aparecer), se usa en los child (ver m�s abajo). (Ej: grass, sand, ...)
    private String habitatGroup;
    private int habitatHeightMin;
    private int habitatHeightMax;

    // Age
    private String maxAge; // Edad m�xima del item
    private String maxAgeItem; // Item que suelta al morir (Ej: los arbustos al morir se convierten en �rbol)
    private String maxAgeTerrain; // When the item dies it will generate a real terrain
    private boolean maxAgeNeedsWater;
    private int maxAgeNeedsWaterRadius;
    private ArrayList<String> maxAgeNeedsItems;
    private int maxAgeNeedsItemsRadius;

    // Health points
    private String hp;

    // Spawn
    private String spawn; // Objeto o living hijo (ej: arbusto)
    private int spawnMaxItems; // M�ximo de hijos alrrededor. S�lo aplica con items. (ej: Si ya tiene 3 arbustos alrededor no "suelta" otro)
    private String spawnTurns; // Dice que se lanza cada turno para ver si toca soltar un hijo

    // Wall
    private boolean wall; // Indica si es un muro (vamos, que no se puede pasar por esa casilla)

    // Wall connectors
    private boolean wallConnector; // Indica si es un conector con muros (ej: puertas, ventanas)

    // Doors
    private boolean door; // Indica si es una puerta (para sacar el men� de open/close/lock)

    // Food
    private boolean canBeEaten; // Indica si se puede comer
    private int foodValue;
    private int foodFillPCT;
    private int foodEatTime;
    private ArrayList<String> foodEffects;

    // Happiness value
    private int happiness; // Modificador de happiness si el objeto est� en LOS de los aldeanos

    // Sleep (camas)
    private boolean canBeUsedToSleep; // Indica si se puede dormir ah�

    // Sit (sillas)
    private boolean canBeUsedToSit; // Indica si se puede sentar ah�

    // Zone mergers (bridges, stairs, ...)
    private boolean zoneMergerUp;

    // canBeBuilt
    private boolean canBeBuiltOnFloor;
    private boolean canBeBuiltOnHoles;

    private boolean canBeUnlocked;

    // Glue
    private boolean glue;

    // Base
    private boolean base;

    // Blocky
    private boolean blocky;

    // Text
    private boolean text;

    // Lights
    private int lightRadius;
    private int lightRed;
    private int lightGreen;
    private int lightBlue;
    private boolean translucent;

    // Locked
    private boolean locked;

    // Value (price)
    private int value;

    // AlwaysOperative
    private boolean alwaysOperative; // Indica si cuando se construye ya est� operativo o tienen que ponerse en alg�n sitio para que lo est�

    // Container
    private boolean container;
    private int containerSize;

    // Stackable
    private boolean stackable;
    private int stackableSize;

    // Speed up para los "benches". Acctiones <move> en las actions
    private int speedUpPCT;

    // Military
    private int location; // Cabeza, torso, ....
    private String attackModifier; // Modificador de ataque
    private String attackSpeedModifier; // Modificador de turnos entre ataque y ataque
    private String defenseModifier; // Modificador de defensa
    private String healthModifier; // Modificador de vida
    private String damageModifier;// Modificador de da�o
    private String LOSModifier;// Modificador de LOS
    private String movePCTModifier;// Modificador de movePCT
    private String walkSpeedModifier; // Modificador de turnos entre paso y paso
    private boolean ranged;
    private String rangedAmmo;
    private boolean rangedOneShoot;
    private String verb; // Verbo que se usa al atacar (ej: Espada->corta, si no lleva armas->Pega, ....)
    private String verbInfinitive; // Verbo que se usa al atacar (ej: Espada->corta, si no lleva armas->Pega, ....)

    // Wear effects
    private ArrayList<String> wearEffects;

    // Tags
    private ArrayList<String> tags;

    // Custom actions
    private ArrayList<String> actions;

    // Zones
    private ArrayList<String> zones;

    // Traps
    private boolean trap;
    private ArrayList<String> trapEffects;
    private String trapCooldown;
    private String trapOnIcon;
    private HateData trapTargets;

    // Block fluids
    private boolean blockFluids;

    // Fluids elevator
    private boolean fluidsElevator;

    // Allow fluids
    private boolean allowFluids;

    // Bury
    private boolean bury;
    private boolean buryLocked;
    private ArrayList<String> buryItem;
    private ArrayList<Integer> buryItemPCT;
    private boolean buryDestroyItem;
    private ArrayList<String> buryLivings;
    private ArrayList<Integer> buryLivingsPCT;

    // Rotate
    private boolean canBeRotated;

    // Build actions
    private String buildAction;

    public ItemManagerItem(String sIniHeader, String sType) {
        setIniHeader(sIniHeader);
        type = sType;
        prerequisites = new ArrayList<>();
    }

    public String getIniHeader() {
        return iniHeader;
    }

    public void setIniHeader(String iniHeader) {
        this.iniHeader = iniHeader;
        setNumericalIniHeader(UtilsIniHeaders.getIntIniHeader(iniHeader));
    }

    public int getNumericalIniHeader() {
        return numericalIniHeader;
    }

    public void setNumericalIniHeader(int numericalIniHeader) {
        this.numericalIniHeader = numericalIniHeader;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setLevel(String sLevel) {
        setLevel(Utils.getInteger(sLevel, 0));
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

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public ArrayList<String> getPrerequisites() {
        // Devolvemos una copia, siempre
        ArrayList<String> alReturn = new ArrayList<>(prerequisites.size());
        alReturn.addAll(prerequisites);

        return alReturn;
    }

    public void setPrerequisites(ArrayList<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void addPrerequisite(String prerequisite) {
        if (prerequisites == null) {
            prerequisites = new ArrayList<>();
        }

        prerequisites.add(prerequisite);
    }

    public int getBuildingTime() {
        return buildingTime;
    }

    public void setBuildingTime(int buildingTime) {
        this.buildingTime = buildingTime;
    }

    public void setBuildingTime(String sBuildingTime) {
        setBuildingTime(Utils.launchDice(sBuildingTime));
    }

    public ArrayList<Integer> getHabitat() {
        return habitat;
    }

    public void setHabitat(ArrayList<Integer> habitat) {
        this.habitat = new ArrayList<>();
        for (Integer integer : habitat) {
            if (!this.habitat.contains(integer)) {
                //getHabitat ().add (new Integer (TerrainManager.getItem (habitat.get (i)).getTerrainID ()));
                getHabitat().add(integer);
            }
        }
    }

    public ArrayList<String> getHabitatAsString() {
        return habitatString;
    }

    public void setHabitatAsString(ArrayList<String> habitat) {
        this.habitatString = habitat;
    }

    public void addHabitats(ArrayList<Integer> habitats) {
        if (this.habitat == null) {
            setHabitat(habitats);
        } else {
            if (habitats != null) {
                for (Integer integer : habitats) {
                    if (!this.habitat.contains(integer)) {
                        //getHabitat ().add (new Integer (TerrainManager.getItem (habitats.get (i)).getTerrainID ()));
                        getHabitat().add(integer);
                    }
                }
            }
        }
    }

    public String getHabitatGroup() {
        return habitatGroup;
    }

    public void setHabitatGroup(String habitatGroup) {
        this.habitatGroup = habitatGroup;
    }

    public int getHabitatHeightMin() {
        return habitatHeightMin;
    }

    public void setHabitatHeightMin(String sHabitatHeightMin) {
        setHabitatHeightMin(Utils.getInteger(sHabitatHeightMin, -1));
    }

    public void setHabitatHeightMin(int habitatHeightMin) {
        this.habitatHeightMin = habitatHeightMin;
    }

    public int getHabitatHeightMax() {
        return habitatHeightMax;
    }

    public void setHabitatHeightMax(int habitatHeightMax) {
        this.habitatHeightMax = habitatHeightMax;
    }

    public void setHabitatHeightMax(String sHabitatHeightMax) {
        setHabitatHeightMax(Utils.getInteger(sHabitatHeightMax, -1));
    }

    public String getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(String sMaxAge) {
        this.maxAge = sMaxAge;
    }

    public String getMaxAgeItem() {
        return maxAgeItem;
    }

    public void setMaxAgeItem(String maxAgeItem) {
        this.maxAgeItem = maxAgeItem;
    }

    public String getMaxAgeTerrain() {
        return maxAgeTerrain;
    }

    public void setMaxAgeTerrain(String maxAgeTerrain) {
        this.maxAgeTerrain = maxAgeTerrain;
    }

    public boolean isMaxAgeNeedsWater() {
        return maxAgeNeedsWater;
    }

    public void setMaxAgeNeedsWater(boolean maxAgeNeedsWater) {
        this.maxAgeNeedsWater = maxAgeNeedsWater;
    }

    public void setMaxAgeNeedsWater(String sMaxAgeNeedsWater) {
        setMaxAgeNeedsWater(Boolean.parseBoolean(sMaxAgeNeedsWater));
    }

    public int getMaxAgeNeedsWaterRadius() {
        return maxAgeNeedsWaterRadius;
    }

    public void setMaxAgeNeedsWaterRadius(int maxAgeNeedsWaterRadius) {
        this.maxAgeNeedsWaterRadius = maxAgeNeedsWaterRadius;
    }

    public void setMaxAgeNeedsWaterRadius(String sMaxAgeNeedsWaterRadius) {
        if (sMaxAgeNeedsWaterRadius == null || sMaxAgeNeedsWaterRadius.isEmpty()) {
            setMaxAgeNeedsWaterRadius(0);
        } else {
            try {
                setMaxAgeNeedsWaterRadius(Integer.parseInt(sMaxAgeNeedsWaterRadius));
            } catch (NumberFormatException nfe) {
                Log.log(Log.LEVEL.ERROR, Messages.getString("ItemManagerItem.12") + sMaxAgeNeedsWaterRadius + "]", getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$
                setMaxAgeNeedsWaterRadius(1);
            }
        }

        if (getMaxAgeNeedsWaterRadius() < 0) {
            setMaxAgeNeedsWaterRadius(0);
        }
    }

    public ArrayList<String> getMaxAgeNeedsItems() {
        return maxAgeNeedsItems;
    }

    public void setMaxAgeNeedsItems(ArrayList<String> alItems) {
        this.maxAgeNeedsItems = alItems;
    }

    public void setMaxAgeNeedsItems(String sMaxAgeNeedsItems) {
        setMaxAgeNeedsItems(Utils.getArray(sMaxAgeNeedsItems));
    }

    public int getMaxAgeNeedsItemsRadius() {
        return maxAgeNeedsItemsRadius;
    }

    public void setMaxAgeNeedsItemsRadius(int maxAgeNeedsItemsRadius) {
        this.maxAgeNeedsItemsRadius = maxAgeNeedsItemsRadius;
    }

    public void setMaxAgeNeedsItemsRadius(String sMaxAgeNeedsItemsRadius) {
        if (sMaxAgeNeedsItemsRadius == null || sMaxAgeNeedsItemsRadius.isEmpty()) {
            setMaxAgeNeedsItemsRadius(0);
        } else {
            try {
                setMaxAgeNeedsItemsRadius(Integer.parseInt(sMaxAgeNeedsItemsRadius));
            } catch (NumberFormatException nfe) {
                Log.log(Log.LEVEL.ERROR, Messages.getString("ItemManagerItem.18") + " [" + sMaxAgeNeedsItemsRadius + "]", getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                setMaxAgeNeedsItemsRadius(1);
            }
        }

        if (getMaxAgeNeedsItemsRadius() < 0) {
            setMaxAgeNeedsItemsRadius(0);
        }
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getSpawn() {
        return spawn;
    }

    public void setSpawn(String spawn) {
        this.spawn = spawn;
    }

    public int getSpawnMaxItems() {
        return spawnMaxItems;
    }

    public void setSpawnMaxItems(int spawnMaxItems) {
        this.spawnMaxItems = spawnMaxItems;
    }

    public void setSpawnMaxItems(String sSpawnMax) {
        setSpawnMaxItems(Utils.getInteger(sSpawnMax, 0));
    }

    public String getSpawnTurns() {
        return spawnTurns;
    }

    public void setSpawnTurns(String sSpawnTurns) {
        this.spawnTurns = sSpawnTurns;
    }

    public boolean isWall() {
        return wall;
    }

    public void setWall(String sWall) {
        setWall(Boolean.parseBoolean(sWall));
    }

    public void setWall(boolean wall) {
        this.wall = wall;
    }

    public boolean isWallConnector() {
        return wallConnector;
    }

    public void setWallConnector(boolean wallConnector) {
        this.wallConnector = wallConnector;
    }

    public void setWallConnector(String sWallConnector) {
        setWallConnector(Boolean.parseBoolean(sWallConnector));
    }

    public boolean isDoor() {
        return door;
    }

    public void setDoor(boolean door) {
        this.door = door;
    }

    public void setDoor(String sDoor) {
        setDoor(Boolean.parseBoolean(sDoor));
    }

    public void setCanBeEaten(String sCanBeEaten) {
        setCanBeEaten(Boolean.parseBoolean(sCanBeEaten));
    }

    public void setCanBeEaten(boolean canBeEaten) {
        this.canBeEaten = canBeEaten;
    }

    public boolean canBeEaten() {
        return canBeEaten;
    }

    public int getHappiness() {
        return happiness;
    }

    public void setHappiness(String sHappiness) {
        if (sHappiness == null || sHappiness.trim().isEmpty()) {
            setHappiness(0);
        } else {
            try {
                setHappiness(Integer.parseInt(sHappiness));
            } catch (NumberFormatException nfe) {
                Log.log(Log.LEVEL.ERROR, Messages.getString("ItemManagerItem.0") + sHappiness + "]", getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$
                setHappiness(0);
            }
        }
    }

    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    public void setCanBeUsedToSleep(String sCanBeUsedToSleep) {
        setCanBeUsedToSleep(Boolean.parseBoolean(sCanBeUsedToSleep));
    }

    public void setCanBeUsedToSleep(boolean canBeUsedToSleep) {
        this.canBeUsedToSleep = canBeUsedToSleep;
    }

    public boolean canBeUsedToSleep() {
        return canBeUsedToSleep;
    }

    public void setCanBeUsedToSit(String sCanBeUsedToSit) {
        setCanBeUsedToSit(Boolean.parseBoolean(sCanBeUsedToSit));
    }

    public void setCanBeUsedToSit(boolean canBeUsedToSit) {
        this.canBeUsedToSit = canBeUsedToSit;
    }

    public boolean canBeUsedToSit() {
        return canBeUsedToSit;
    }

    public boolean isZoneMergerUp() {
        return zoneMergerUp;
    }

    public void setZoneMergerUp(String sZoneMergerUp) {
        setZoneMergerUp(Boolean.parseBoolean(sZoneMergerUp));
    }

    public void setZoneMergerUp(boolean zoneMergerUp) {
        this.zoneMergerUp = zoneMergerUp;
    }

    public boolean canBeBuiltOnFloor() {
        return canBeBuiltOnFloor;
    }

    public void setCanBeBuiltOnFloor(boolean canBeBuiltOnFloor) {
        this.canBeBuiltOnFloor = canBeBuiltOnFloor;
    }

    public void setCanBeBuiltOnFloor(String sCanBeBuiltOnFloor) {
        // En el caso de null NO usamos el parse de Boolean ya que por defecto pondremos true
        if (sCanBeBuiltOnFloor == null) {
            setCanBeBuiltOnFloor(true);
        } else {
            setCanBeBuiltOnFloor(!sCanBeBuiltOnFloor.equalsIgnoreCase("FALSE")); //$NON-NLS-1$
        }
    }

    public boolean canBeBuiltOnHoles() {
        return canBeBuiltOnHoles;
    }

    public void setCanBeBuiltOnHoles(boolean canBeBuiltOnHoles) {
        this.canBeBuiltOnHoles = canBeBuiltOnHoles;
    }

    public void setCanBeBuiltOnHoles(String sCanBeBuiltOnHoles) {
        setCanBeBuiltOnHoles(Boolean.parseBoolean(sCanBeBuiltOnHoles));
    }

    public boolean canBeUnlocked() {
        return canBeUnlocked;
    }

    public void setCanBeUnlocked(boolean canBeUnlocked) {
        this.canBeUnlocked = canBeUnlocked;
    }

    public void setCanBeUnlocked(String sCanBeUnlocked) {
        if (sCanBeUnlocked == null || sCanBeUnlocked.trim().isEmpty()) {
            setCanBeUnlocked(true);
        } else {
            setCanBeUnlocked(Boolean.parseBoolean(sCanBeUnlocked));
        }
    }

    public boolean isGlue() {
        return glue;
    }

    public void setGlue(boolean glue) {
        this.glue = glue;
    }

    public void setGlue(String sGlue) {
        setGlue(Boolean.parseBoolean(sGlue));
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }

    public void setBase(String sBase) {
        setBase(Boolean.parseBoolean(sBase));
    }

    public boolean isBlocky() {
        return blocky;
    }

    public void setBlocky(boolean blocky) {
        this.blocky = blocky;
    }

    public void setBlocky(String sBlocky) {
        setBlocky(Boolean.parseBoolean(sBlocky));
    }

    public boolean isText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public void setText(String sText) {
        setText(Boolean.parseBoolean(sText));
    }

    public int getLightRadius() {
        return lightRadius;
    }

    public void setLightRadius(int lightRadius) {
        this.lightRadius = lightRadius;
        if (this.lightRadius > MAX_LIGHT_RADIUS) {
            MAX_LIGHT_RADIUS = this.lightRadius;
        }
    }

    public void setLightRadius(String sLightRadius) throws Exception {
        if (sLightRadius == null || sLightRadius.trim().isEmpty()) {
            setLightRadius(0);
        } else {
            try {
                setLightRadius(Integer.parseInt(sLightRadius));
                if (getLightRadius() < 0) {
                    setLightRadius(0);
                }
            } catch (Exception e) {
                throw new Exception(Messages.getString("ItemManagerItem.14") + " [" + sLightRadius + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
    }

    public int getLightIntValue(String sLight) throws Exception {
        if (sLight == null || sLight.trim().isEmpty()) {
            return LIGHT_I_NONE;
        } else {
            if (sLight.trim().equalsIgnoreCase(LIGHT_FULL)) {
                return LIGHT_I_FULL;
            } else if (sLight.trim().equalsIgnoreCase(LIGHT_HALF)) {
                return LIGHT_I_HALF;
            } else if (sLight.trim().equalsIgnoreCase(LIGHT_NONE)) {
                return LIGHT_I_NONE;
            } else {
                throw new Exception(Messages.getString("ItemManagerItem.15") + " [" + sLight + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
    }

    public int getLightRed() {
        return lightRed;
    }

    public void setLightRed(int lightRed) {
        this.lightRed = lightRed;
    }

    public void setLightRed(String sLightRed) throws Exception {
        setLightRed(getLightIntValue(sLightRed));
    }

    public int getLightGreen() {
        return lightGreen;
    }

    public void setLightGreen(int lightGreen) {
        this.lightGreen = lightGreen;
    }

    public void setLightGreen(String sLightGreen) throws Exception {
        setLightGreen(getLightIntValue(sLightGreen));
    }

    public int getLightBlue() {
        return lightBlue;
    }

    public void setLightBlue(int lightBlue) {
        this.lightBlue = lightBlue;
    }

    public void setLightBlue(String sLightBlue) throws Exception {
        setLightBlue(getLightIntValue(sLightBlue));
    }

    public boolean isTranslucent() {
        return translucent;
    }

    public void setTranslucent(boolean translucent) {
        this.translucent = translucent;
    }

    public void setTranslucent(String sTranslucent) {
        setTranslucent(Boolean.parseBoolean(sTranslucent));
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setLocked(String sLocked) {
        setLocked(Boolean.parseBoolean(sLocked));
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setValue(String sValue) throws Exception {
        if (sValue != null && !sValue.isEmpty()) {
            setValue(Integer.parseInt(sValue));
        } else {
            setValue(0);
        }
    }

    public boolean isAlwaysOperative() {
        return alwaysOperative;
    }

    public void setAlwaysOperative(boolean alwaysOperative) {
        this.alwaysOperative = alwaysOperative;
    }

    public void setAlwaysOperative(String sAlwaysOperative) {
        // En el caso de null NO usamos el parse de Boolean ya que por defecto pondremos true
        if (sAlwaysOperative == null) {
            setAlwaysOperative(true);
        } else {
            setAlwaysOperative(Boolean.parseBoolean(sAlwaysOperative));
        }
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public void setLocation(String location) {
        if (location == null || location.isEmpty()) {
            this.location = 0;
            return;
        }

        if (location.equalsIgnoreCase("HEAD")) { //$NON-NLS-1$
            this.location = MilitaryItem.LOCATION_HEAD;
        } else if (location.equalsIgnoreCase("BODY")) { //$NON-NLS-1$
            this.location = MilitaryItem.LOCATION_BODY;
        } else if (location.equalsIgnoreCase("LEGS")) { //$NON-NLS-1$
            this.location = MilitaryItem.LOCATION_LEGS;
        } else if (location.equalsIgnoreCase("FEET")) { //$NON-NLS-1$
            this.location = MilitaryItem.LOCATION_FEET;
        } else if (location.equalsIgnoreCase("WEAPON")) { //$NON-NLS-1$
            this.location = MilitaryItem.LOCATION_WEAPON;
        } else {
            this.location = 0;
        }
    }

    public String getAttackModifier() {
        return attackModifier;
    }

    public void setAttackModifier(String attackModifier) {
        this.attackModifier = attackModifier;
    }

    public String getAttackSpeedModifier() {
        return attackSpeedModifier;
    }

    public void setAttackSpeedModifier(String attackSpeedModifier) {
        this.attackSpeedModifier = attackSpeedModifier;
    }

    public String getWalkSpeedModifier() {
        return walkSpeedModifier;
    }

    public void setWalkSpeedModifier(String walkSpeedModifier) {
        this.walkSpeedModifier = walkSpeedModifier;
    }

    public String getDefenseModifier() {
        return defenseModifier;
    }

    public void setDefenseModifier(String defenseModifier) {
        this.defenseModifier = defenseModifier;
    }

    public String getHealthModifier() {
        return healthModifier;
    }

    public void setHealthModifier(String healthModifier) {
        this.healthModifier = healthModifier;
    }

    public String getDamageModifier() {
        return damageModifier;
    }

    public void setDamageModifier(String damageModifier) {
        this.damageModifier = damageModifier;
    }

    public String getLOSModifier() {
        return LOSModifier;
    }

    public void setLOSModifier(String lOSModifier) {
        LOSModifier = lOSModifier;
    }

    public String getMovePCTModifier() {
        return movePCTModifier;
    }

    public void setMovePCTModifier(String movePCTModifier) {
        this.movePCTModifier = movePCTModifier;
    }

    public boolean isRanged() {
        return ranged;
    }

    public void setRanged(boolean ranged) {
        this.ranged = ranged;
    }

    public void setRanged(String sRanged) {
        setRanged(Boolean.parseBoolean(sRanged));
    }

    public String getRangedAmmo() {
        return rangedAmmo;
    }

    public void setRangedAmmo(String rangedAmmo) throws Exception {
        if (ranged) {
            if (rangedAmmo == null || rangedAmmo.trim().isEmpty()) {
                throw new Exception(Messages.getString("ItemManagerItem.2") + getIniHeader() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        this.rangedAmmo = rangedAmmo;
    }

    public boolean isRangedOneShoot() {
        return rangedOneShoot;
    }

    public void setRangedOneShoot(boolean rangedOneShoot) {
        this.rangedOneShoot = rangedOneShoot;
    }

    public void setRangedOneShoot(String sRangedOneShoot) {
        setRangedOneShoot(Boolean.parseBoolean(sRangedOneShoot));
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getVerbInfinitive() {
        return verbInfinitive;
    }

    public void setVerbInfinitive(String verbInfinitive) {
        this.verbInfinitive = verbInfinitive;
    }

    public ArrayList<String> getWearEffects() {
        return wearEffects;
    }

    public void setWearEffects(ArrayList<String> wearEffects) {
        this.wearEffects = wearEffects;
    }

    public void setWearEffects(String sWearEffects) {
        setWearEffects(Utils.getArray(sWearEffects));
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setTags(String sTags) {
        setTags(Utils.getArray(sTags));
    }

    public ArrayList<String> getActions() {
        return actions;
    }

    public void setActions(ArrayList<String> actions) {
        this.actions = actions;
    }

    public boolean hasActions() {
        return actions != null && !actions.isEmpty();
    }

    public ArrayList<String> getZones() {
        return zones;
    }

    public void setZones(ArrayList<String> zones) {
        this.zones = zones;
    }

    public boolean isTrap() {
        return trap;
    }

    public void setTrap(boolean trap) {
        this.trap = trap;
    }

    public void setTrap(String sTrap) {
        setTrap(Boolean.parseBoolean(sTrap));
    }

    public ArrayList<String> getTrapEffects() {
        return trapEffects;
    }

    public void setTrapEffects(ArrayList<String> trapEffects) throws Exception {
        this.trapEffects = trapEffects;

        if (isTrap() && (trapEffects == null || trapEffects.isEmpty())) {
            throw new Exception(Messages.getString("ItemManagerItem.6")); //$NON-NLS-1$
        }
    }

    public void setTrapEffects(String sTrapEffects) throws Exception {
        setTrapEffects(Utils.getArray(sTrapEffects));
    }

    public String getTrapCooldown() {
        return trapCooldown;
    }

    public void setTrapCooldown(String trapCooldown) {
        this.trapCooldown = trapCooldown;
    }

    public String getTrapOnIcon() {
        return trapOnIcon;
    }

    public void setTrapOnIcon(String trapOnIcon) {
        this.trapOnIcon = trapOnIcon;
    }

    public HateData getTrapTargets() {
        return trapTargets;
    }

    public void setTrapTargets(HateData trapTargets) {
        this.trapTargets = trapTargets;
    }

    public void setTrapTargets(String sTrapTargets) throws Exception {
        if (isTrap()) {
            if (sTrapTargets != null && !sTrapTargets.isEmpty()) {
                setTrapTargets(new HateData(sTrapTargets));
            } else {
                throw new Exception(Messages.getString("ItemManagerItem.9")); //$NON-NLS-1$
            }
        }
    }

    public boolean isBlockFluids() {
        return blockFluids;
    }

    public void setBlockFluids(boolean blockFluids) {
        this.blockFluids = blockFluids;
    }

    public void setBlockFluids(String sBlockFluids) {
        setBlockFluids(Boolean.parseBoolean(sBlockFluids));
    }

    public boolean isFluidsElevator() {
        return fluidsElevator;
    }

    public void setFluidsElevator(boolean bFluidsElevator) {
        this.fluidsElevator = bFluidsElevator;
    }

    public void setFluidsElevator(String sFluidsElevator) {
        setFluidsElevator(Boolean.parseBoolean(sFluidsElevator));
    }

    public boolean isAllowFluids() {
        return allowFluids;
    }

    public void setAllowFluids(boolean allowFluids) {
        this.allowFluids = allowFluids;
    }

    public void setAllowFluids(String sAllowFluids) {
        setAllowFluids(Boolean.parseBoolean(sAllowFluids));
    }

    public boolean isBury() {
        return bury;
    }

    public void setBury(boolean bury) {
        this.bury = bury;
    }

    public void setBury(String sBury) {
        setBury(Boolean.parseBoolean(sBury));
    }

    public boolean isBuryLocked() {
        return buryLocked;
    }

    public void setBuryLocked(boolean buryLocked) {
        this.buryLocked = buryLocked;
    }

    public void setBuryLocked(String sBuryLocked) {
        setBuryLocked(Boolean.parseBoolean(sBuryLocked));
    }

    public ArrayList<String> getBuryItem() {
        return buryItem;
    }

    public void setBuryItem(ArrayList<String> buryItem) {
        this.buryItem = buryItem;
    }

    public void setBuryItem(String buryItem) {
        setBuryItem(Utils.getArray(buryItem));
    }

    public ArrayList<Integer> getBuryItemPCT() {
        return buryItemPCT;
    }

    public void setBuryItemPCT(ArrayList<Integer> buryItemPCT) throws Exception {
        this.buryItemPCT = buryItemPCT;

        if (buryItem != null) {
            if (buryItemPCT == null) {
                if (buryItem.size() != 1) {
                    throw new Exception(Messages.getString("ItemManagerItem.17")); //$NON-NLS-1$
                } else {
                    this.buryItemPCT = new ArrayList<>(1);
                    this.buryItemPCT.add(100);
                }
            } else {
                if (buryItem.size() != buryItemPCT.size()) {
                    throw new Exception(Messages.getString("ItemManagerItem.17")); //$NON-NLS-1$
                }
            }
        } else {
            this.buryItemPCT = null;
        }
    }

    public void setBuryItemPCT(String sBuryItemPCT) throws Exception {
        setBuryItemPCT(Utils.getArrayIntegers(sBuryItemPCT));
    }

    public boolean isBuryDestroyItem() {
        return buryDestroyItem;
    }

    public void setBuryDestroyItem(boolean buryDestroyItem) {
        this.buryDestroyItem = buryDestroyItem;
    }

    public void setBuryDestroyItem(String sDestroyItem) {
        setBuryDestroyItem(Boolean.parseBoolean(sDestroyItem));
    }

    public ArrayList<String> getBuryLivings() {
        return buryLivings;
    }

    public void setBuryLivings(ArrayList<String> buryLivings) {
        this.buryLivings = buryLivings;
    }

    public void setBuryLivings(String sBuryLivings) {
        setBuryLivings(Utils.getArray(sBuryLivings));
    }

    public ArrayList<Integer> getBuryLivingsPCT() {
        return buryLivingsPCT;
    }

    public void setBuryLivingsPCT(ArrayList<Integer> buryLivingsPCT) throws Exception {
        this.buryLivingsPCT = buryLivingsPCT;

        if (buryLivings != null) {
            if (buryLivingsPCT == null || buryLivingsPCT.size() != buryLivings.size()) {
                throw new Exception(Messages.getString("ItemManagerItem.19")); //$NON-NLS-1$
            }
        } else {
            this.buryLivingsPCT = null;
        }
    }

    public void setBuryLivingsPCT(String sBuryLivingsPCT) throws Exception {
        setBuryLivingsPCT(Utils.getArrayIntegers(sBuryLivingsPCT));
    }

    public boolean isCanBeRotated() {
        return canBeRotated;
    }

    public void setCanBeRotated(boolean canBeRotated) {
        this.canBeRotated = canBeRotated;
    }

    public void setCanBeRotated(String sCanBeRotated) {
        setCanBeRotated(Boolean.parseBoolean(sCanBeRotated));
    }

    public String getBuildAction() {
        return buildAction;
    }

    public void setBuildAction(String buildAction) {
        this.buildAction = buildAction;
    }

    public boolean isContainer() {
        return container;
    }

    public void setContainer(boolean container) {
        this.container = container;
    }

    public void setContainer(String sContainer) {
        setContainer(Boolean.parseBoolean(sContainer));
    }

    public int getContainerSize() {
        return containerSize;
    }

    public void setContainerSize(int containerSize) {
        this.containerSize = containerSize;
    }

    public void setContainerSize(String sContainerSize) {
        if (sContainerSize == null || sContainerSize.trim().isEmpty()) {
            if (isContainer()) {
                setContainerSize(1);
            } else {
                setContainerSize(0);
            }
        } else {
            try {
                setContainerSize(Integer.parseInt(sContainerSize));
                if (isContainer() && getContainerSize() < 1) {
                    setContainerSize(1);
                }
            } catch (NumberFormatException nfe) {
                if (isContainer()) {
                    setContainerSize(1);
                } else {
                    setContainerSize(0);
                }
            }
        }
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public void setStackable(String sStackable) {
        setStackable(Boolean.parseBoolean(sStackable));
    }

    public int getStackableSize() {
        return stackableSize;
    }

    public void setStackableSize(int stackableSize) {
        this.stackableSize = stackableSize;
    }

    public void setStackableSize(String sStackableSize) {
        if (sStackableSize == null || sStackableSize.trim().isEmpty()) {
            if (isStackable()) {
                setStackableSize(1);
            } else {
                setStackableSize(0);
            }
        } else {
            try {
                setStackableSize(Integer.parseInt(sStackableSize));
                if (isStackable() && getStackableSize() < 1) {
                    setStackableSize(1);
                }
            } catch (NumberFormatException nfe) {
                if (isStackable()) {
                    setStackableSize(1);
                } else {
                    setStackableSize(0);
                }
            }
        }
    }

    public int getSpeedUpPCT() {
        return speedUpPCT;
    }

    public void setSpeedUpPCT(int speedUpPCT) {
        this.speedUpPCT = speedUpPCT;
    }

    public void setSpeedUpPCT(String sSpeedUpPCT) {
        if (sSpeedUpPCT == null || sSpeedUpPCT.trim().isEmpty()) {
            setSpeedUpPCT(100);
        } else {
            try {
                setSpeedUpPCT(Integer.parseInt(sSpeedUpPCT));
                if (getSpeedUpPCT() <= 0) {
                    setSpeedUpPCT(1);
                }
            } catch (NumberFormatException nfe) {
                Log.log(Log.LEVEL.ERROR, Messages.getString("ItemManagerItem.1") + sSpeedUpPCT + Messages.getString("ItemManagerItem.3"), getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$
                setSpeedUpPCT(100);
            }
        }
    }

    /**
     * @return the floorWwalkSpeed
     */
    public int getFloorWalkSpeed() {
        return floorWalkSpeed;
    }

    /**
     * @param floorWalkSpeed the floorWalkSpeed to set
     */
    public void setFloorWalkSpeed(int floorWalkSpeed) {
        if (floorWalkSpeed < 1 || floorWalkSpeed > 100) {
            this.floorWalkSpeed = 100;
        } else {
            this.floorWalkSpeed = floorWalkSpeed;
        }
    }

    /**
     * @param sFloorWalkSpeed the floorWalkSpeed to set (dice)
     */
    public void setFloorWalkSpeed(String sFloorWalkSpeed) {
        setFloorWalkSpeed(Utils.launchDice(sFloorWalkSpeed));
    }

    public boolean isMilitaryItem() {
        return location != 0;
    }

    public int getFoodValue() {
        return foodValue;
    }

    public void setFoodValue(int foodValue) {
        this.foodValue = foodValue;
    }

    public void setFoodValue(String sFoodValue) {
        setFoodValue(Utils.launchDice(sFoodValue));
    }

    public int getFoodFillPCT() {
        return foodFillPCT;
    }

    public void setFoodFillPCT(int foodFillPCT) {
        this.foodFillPCT = foodFillPCT;
    }

    public void setFoodFillPCT(String sFoodFillPCT) {
        setFoodFillPCT(Utils.launchDice(sFoodFillPCT));
    }

    public int getFoodEatTime() {
        return foodEatTime;
    }

    public void setFoodEatTime(int foodEatTime) {
        this.foodEatTime = foodEatTime;
    }

    public void setFoodEatTime(String sFoodEatTime) {
        setFoodEatTime(Utils.launchDice(sFoodEatTime));
    }

    public ArrayList<String> getFoodEffects() {
        return foodEffects;
    }

    public void setFoodEffects(ArrayList<String> foodEffects) throws Exception {
        if (foodEffects != null) {
            // Comprobamos que existan
            for (String foodEffect : foodEffects) {
                if (EffectManager.getItem(foodEffect) == null) {
                    throw new Exception(Messages.getString("ItemManagerItem.11") + foodEffect + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
        this.foodEffects = foodEffects;
    }

    public void setFoodEffects(String sFoodEffects) throws Exception {
        setFoodEffects(Utils.getArray(sFoodEffects));
    }

    public String getEatString() {
        String buffer = Messages.getString("ItemManagerItem.5") + //$NON-NLS-1$
                getFoodFillPCT() +
                "% " + //$NON-NLS-1$
                Messages.getString("ItemManagerItem.8") + //$NON-NLS-1$
                getFoodEatTime();
        return buffer.trim();
    }

    public String getMilitaryString() {
        StringBuilder buffer = new StringBuilder();
        Point minMax = Utils.getDiceMinMax(getAttackModifier());
        if (minMax.x != minMax.y || minMax.x != 0) {
            buffer.append(Messages.getString("ItemManagerItem.4")).append(minMax.x); //$NON-NLS-1$
            if (minMax.x != minMax.y) {
                buffer.append("/").append(minMax.y); //$NON-NLS-1$
            }
            buffer.append(" "); //$NON-NLS-1$
        }
        minMax = Utils.getDiceMinMax(getDefenseModifier());
        if (minMax.x != minMax.y || minMax.x != 0) {
            buffer.append(Messages.getString("ItemManagerItem.7")).append(minMax.x); //$NON-NLS-1$
            if (minMax.x != minMax.y) {
                buffer.append("/").append(minMax.y); //$NON-NLS-1$
            }
            buffer.append(" "); //$NON-NLS-1$
        }
        minMax = Utils.getDiceMinMax(getDamageModifier());
        if (minMax.x != minMax.y || minMax.x != 0) {
            buffer.append(Messages.getString("ItemManagerItem.10")).append(minMax.x); //$NON-NLS-1$
            if (minMax.x != minMax.y) {
                buffer.append("/").append(minMax.y); //$NON-NLS-1$
            }
            buffer.append(" "); //$NON-NLS-1$
        }
        minMax = Utils.getDiceMinMax(getHealthModifier());
        if (minMax.x != minMax.y || minMax.x != 0) {
            buffer.append(Messages.getString("ItemManagerItem.13")).append(minMax.x); //$NON-NLS-1$
            if (minMax.x != minMax.y) {
                buffer.append("/").append(minMax.y); //$NON-NLS-1$
            }
            buffer.append(" "); //$NON-NLS-1$
        }
        minMax = Utils.getDiceMinMax(getLOSModifier());
        if (minMax.x != minMax.y || minMax.x != 0) {
            buffer.append(Messages.getString("ItemManagerItem.16")).append(minMax.x); //$NON-NLS-1$
            if (minMax.x != minMax.y) {
                buffer.append("/").append(minMax.y); //$NON-NLS-1$
            }
            buffer.append(" "); //$NON-NLS-1$
        }

        return buffer.toString().trim();
    }
}
