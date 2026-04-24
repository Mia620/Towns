package xaos.tasks;

import org.lwjgl.input.Keyboard;
import xaos.actions.*;
import xaos.campaign.TutorialTrigger;
import xaos.data.HeroPrerequisite;
import xaos.data.SoldierGroupData;
import xaos.data.SoldierGroups;
import xaos.main.Game;
import xaos.main.World;
import xaos.panels.MainPanel;
import xaos.panels.MessagesPanel;
import xaos.stockpiles.Stockpile;
import xaos.tiles.Cell;
import xaos.tiles.Tile;
import xaos.tiles.entities.Entity;
import xaos.tiles.entities.buildings.Building;
import xaos.tiles.entities.buildings.BuildingManager;
import xaos.tiles.entities.buildings.BuildingManagerItem;
import xaos.tiles.entities.items.Item;
import xaos.tiles.entities.items.ItemManager;
import xaos.tiles.entities.items.ItemManagerItem;
import xaos.tiles.entities.living.Citizen;
import xaos.tiles.entities.living.LivingEntity;
import xaos.tiles.entities.living.LivingEntityManager;
import xaos.tiles.entities.living.LivingEntityManagerItem;
import xaos.tiles.entities.living.heroes.Hero;
import xaos.tiles.entities.living.heroes.HeroManager;
import xaos.tiles.terrain.TerrainManager;
import xaos.tiles.terrain.TerrainManagerItem;
import xaos.utils.*;
import xaos.zones.*;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static xaos.tasks.Task.TYPE.NO_TASK;

public final class Task implements Externalizable {

    // Tipos de tarea
    public enum TYPE {
        NO_TASK, // Sin tarea
        DIG, // Tarea de digar (mine abajo)
        MINE, // Tarea de minar
        CANCEL_ORDER, // Cancelar �rden (mine/dig/chop)
        MINE_LADDER, // Tarea de minar y poner escalera

        // Citizens
        WEAR, // Tarea de equipar aldeano
        WEAR_OFF, // Tarea de 'des'equipar aldeano
        CONVERT_TO_CIVILIAN, // Tarea de convertir un soldado en civil
        CONVERT_TO_SOLIDER, // Tarea de convertir un civil en soldado
        FIGHT, // Tarea de luchar, s�lo se asigna a soldados
        HEAL, // Tarea de curarse
        AUTOEQUIP, // Tarea de autoequiparse
        SOLIDER_SET_STATE, // Tarea de cambiar el estado de un soldado (guard, boss around, patrol)
        SOLIDER_ADD_PATROL_POINT, // Tarea de a�adir un punto de patrol a un soldado
        SOLIDER_REMOVE_PATROL_POINT, // Tarea de eliminar un punto de patrol a un soldado

        // Groups
        SOLIDER_ADD_PATROL_POINT_GROUP,
        SOLIDER_REMOVE_PATROL_POINT_GROUP,

        // Buildings
        BUILD, // Tarea de construir (edificios)
        DESTROY_BUILDING,
        TURN_OFF_NON_STOP,
        TURN_ON_NON_STOP,

        // Terrain
        TERRAIN_RAISE,
        TERRAIN_LOWER,
        TERRAIN_CHANGE,
        TERRAIN_ADD_FLUID,
        TERRAIN_REMOVE_FLUID,

        // Items
        CREATE_AND_PLACE, // Tarea de construir (items) y ponerlos en alg�n sitio
        REMOVE_BUILDING_TASK, // Tarea de eliminar el item que se est� construyendo de un edificio
        CREATE_IN_A_BUILDING, // Tarea de construir (items) en un edificio dado
        CREATE, // Tarea de construir (items) sin especificar edificio ni place
        DESTROY_ENTITY,
        CREATE_AND_PLACE_ROW, // Tarea de construir (items) y ponerlos en alg�n sitio. Se crea una fila de ellos
        LOCK,
        UNLOCK_OPEN,
        UNLOCK_CLOSE,

        // Stockpiles
        STOCKPILE, // Tarea de crear stockpile
        DELETE_STOCKPILE,

        // Zones
        CREATE_ZONE, // Tarea de crear zonas (hospital, comedor, carpenters, ...)
        DELETE_ZONE, // Tarea de eliminar zona
        EXPAND_ZONE, // Tarea de expandir zona
        CHANGE_OWNER, // Tarea de cambiar el propietario de la zona
        CHANGE_OWNER_GROUP, // Tarea de cambiar el grupo propietario de la zona

        // Haul / Move / put in containers
        HAUL, // Tarea de haul (son especiales, se crean "on the fly" y no se guardan en la lista de tareas, desaparecen cuando el aldeano las suelta
        MOVE_AND_LOCK, // Como el haul pero persistente (SI se guarda en la lista de tareas). Se usan solamente en las tareas de create, en el caso de que ya haya un item en el mundo y no haya que construir nada
        DROP, // Igual que la tarea de haul a excepci�n que el aldeano no tiene ir a recoger nada ("on the fly" tambi�n)
        PUT_IN_CONTAINER, // Igual que la tarea de haul a excepci�n que el aldeano no tiene ir a recoger nada ("on the fly" tambi�n)
        REMOVE_FROM_CONTAINER, // Igual que la tarea de haul a excepci�n que el aldeano no tiene ir a recoger nada ("on the fly" tambi�n)

        // Sleep / eat
        SLEEP,
        EAT,

        // Custom action
        CUSTOM_ACTION,
        QUEUE,
        QUEUE_AND_PLACE,
        QUEUE_AND_PLACE_ROW,
        QUEUE_AND_PLACE_AREA,

        // Containers
        //CONTAINER_ENABLE_ALL,
        //CONTAINER_DISABLE_ALL,
        //CONTAINER_ENABLE_ITEM,
        //CONTAINER_DISABLE_ITEM,

        // Caravan
        MOVE_TO_CARAVAN,

        // Food
        FOOD_NEEDED
    }

    // Estados de tarea
    public final static int STATE_CREATING_INIZONE = 1; // Para marcar el inicio de un area
    public final static int STATE_CREATING_ENDZONE = 2; // Para marcar el final de un area
    public final static int STATE_CREATING_SINGLEPOINT = 3; // Para marcar un punto en el mapa
    public final static int STATE_CREATED = 10; // Para indicar que la tarea ya est� creada
    private static final long serialVersionUID = -1621427522490649314L;
    public static int ID_INDEX = 0;
    private int id; // ID
    private TYPE task = NO_TASK; // Tipo de tarea (minar, construir, ...)
    private int state; // Estado actual en la creaci�n de la misma (ej: marcando punto inicial de la zona, ...)
    private Point3D pointIni; // Punto inicial de la zona (tambi�n se usa en el casi de tareas de un solo punto (ej: construir))
    private Point3D pointEnd; // Punto final de la zona
    private ArrayList<HotPoint> hotPoints;
    private int maxCitizens; // M�ximo de aldeanos que pueden realizar la tarea
    private String parameter; // Par�metro usado en ciertas tareas
    private String parameter2; // Par�metro usado en ciertas tareas
    private int face = Item.FACE_WEST; // Par�metro usado para rotar los items construidos

    private boolean finished = false; // Indica si la tarea est� finalizada para que el gestor de taeras la elimine cuando le pete

    private transient Tile tile; // Icono para mostrar al setear esa acci�n
    private transient int iconType; // Tipo de icono para saber que textura usar

    public Task() {
    }

    public Task(TYPE iTask) {
        setID(ID_INDEX);
        ID_INDEX++;
        setTask(iTask);
        setMaxCitizens(1); // Por defecto 1
    }

    /**
     * Devuelve el valor de happines segun la tarea pasada
     *
     * @param oTask
     * @return
     */
    public static int getHappiness(Task oTask) {
        if (oTask == null) {
            return 1;
        }

        return switch (oTask.getType()) {
            case NO_TASK, SLEEP, EAT -> 1;
            //case TASKS.DIG: // No hace falta, un aldeano nunca tendr� esta tarea
            case HAUL, PUT_IN_CONTAINER, MOVE_TO_CARAVAN, FOOD_NEEDED, CUSTOM_ACTION,
                 MINE, MINE_LADDER, FIGHT, BUILD, CREATE, CREATE_AND_PLACE, QUEUE,
                 QUEUE_AND_PLACE, MOVE_AND_LOCK -> -2;
            default -> 0;
        };
    }

    /**
     * Indica si la tarea se considera trabajo (se usa en el modificador de
     * happiness)
     *
     * @param oTask
     * @return
     */
    public static boolean isWorkingTask(Task oTask) {
        if (oTask == null) {
            return false;
        }

        return switch (oTask.getType()) {
            case NO_TASK, SLEEP, EAT -> false;
            default -> true;
        };
    }

    /**
     * Devuelve una lista de puntos adyacentes accesibles
     *
     * @param x
     * @param y
     * @param z
     * @param task
     * @return una lista de puntos adyacentes accesibles
     */
    public static ArrayList<Point3DShort> getAccesingPoints(int x, int y, int z, TYPE task) {
        ArrayList<Point3DShort> places = new ArrayList<>();
        if (x > 0) {
            if (y > 0) {
                places.add(Point3DShort.getPoolInstance(x - 1, y - 1, z));
            }
            places.add(Point3DShort.getPoolInstance(x - 1, y, z));
            if (y < (World.MAP_HEIGHT - 1)) {
                places.add(Point3DShort.getPoolInstance(x - 1, y + 1, z));
            }
        }
        if (x < (World.MAP_WIDTH - 1)) {
            if (y > 0) {
                places.add(Point3DShort.getPoolInstance(x + 1, y - 1, z));
            }
            places.add(Point3DShort.getPoolInstance(x + 1, y, z));
            if (y < (World.MAP_HEIGHT - 1)) {
                places.add(Point3DShort.getPoolInstance(x + 1, y + 1, z));
            }
        }
        if (y > 0) {
            places.add(Point3DShort.getPoolInstance(x, y - 1, z));
        }
        if (y < (World.MAP_HEIGHT - 1)) {
            places.add(Point3DShort.getPoolInstance(x, y + 1, z));
        }

        // Abajo
        if (z < (World.MAP_DEPTH - 1)) {
            // Minar desde abajo
            places.add(Point3DShort.getPoolInstance(x, y, z + 1));
        }

        // Celdas vecinas de arriba (s�lo si la central est� minada)
        if (z > 0 && World.getCell(x, y, z - 1).isMined()) {
            // Oeste
            if (x > 0) {
                if (y > 0) {
                    places.add(Point3DShort.getPoolInstance(x - 1, y - 1, z - 1));
                }
                places.add(Point3DShort.getPoolInstance(x - 1, y, z - 1));
                if (y < (World.MAP_HEIGHT - 1)) {
                    places.add(Point3DShort.getPoolInstance(x - 1, y + 1, z - 1));
                }
            }
            // Este
            if (x < (World.MAP_WIDTH - 1)) {
                if (y > 0) {
                    places.add(Point3DShort.getPoolInstance(x + 1, y - 1, z - 1));
                }
                places.add(Point3DShort.getPoolInstance(x + 1, y, z - 1));
                if (y < (World.MAP_HEIGHT - 1)) {
                    places.add(Point3DShort.getPoolInstance(x + 1, y + 1, z - 1));
                }
            }
            // Norte
            if (y > 0) {
                places.add(Point3DShort.getPoolInstance(x, y - 1, z - 1));
            }
            // Sur
            if (y < (World.MAP_HEIGHT - 1)) {
                places.add(Point3DShort.getPoolInstance(x, y + 1, z - 1));
            }
        }

        // Celdas vecinas de abajo (s�lo si la central est� minada)
        if (z < (World.MAP_DEPTH - 1) && World.getCell(x, y, z + 1).isMined()) {
            // Oeste
            if (x > 0) {
                if (y > 0) {
                    places.add(Point3DShort.getPoolInstance(x - 1, y - 1, z + 1));
                }
                places.add(Point3DShort.getPoolInstance(x - 1, y, z + 1));
                if (y < (World.MAP_HEIGHT - 1)) {
                    places.add(Point3DShort.getPoolInstance(x - 1, y + 1, z + 1));
                }
            }
            // Este
            if (x < (World.MAP_WIDTH - 1)) {
                if (y > 0) {
                    places.add(Point3DShort.getPoolInstance(x + 1, y - 1, z + 1));
                }
                places.add(Point3DShort.getPoolInstance(x + 1, y, z + 1));
                if (y < (World.MAP_HEIGHT - 1)) {
                    places.add(Point3DShort.getPoolInstance(x + 1, y + 1, z + 1));
                }
            }
            // Norte
            if (y > 0) {
                places.add(Point3DShort.getPoolInstance(x, y - 1, z + 1));
            }
            // Sur
            if (y < (World.MAP_HEIGHT - 1)) {
                places.add(Point3DShort.getPoolInstance(x, y + 1, z + 1));
            }
        }

        // Justo arriba (la pongo al final para que sea la �ltima opci�n de los aldeanos en caso de mine)
        //if (task != TASK_MINE && task != TASK_MINE_LADDER && task != TASK_DIG) {
        if (z > 0) {
            places.add(Point3DShort.getPoolInstance(x, y, z - 1));
        }
        //}

        return places;
    }

    /**
     * Devuelve una lista de puntos adyacentes accesibles que est�n en la zona
     * pasada
     *
     * @param p3d
     * @return una lista de puntos adyacentes accesibles que est�n en la zona
     * pasada
     * @para aszi
     * @para task
     */
    public static ArrayList<Point3DShort> getAccesingPointsMatchingASZI(Point3DShort p3d, int aszi, TYPE task) {
        return getAccesingPointsMatchingASZI(p3d.x, p3d.y, p3d.z, aszi, task);
    }

    public static ArrayList<Point3DShort> getAccesingPointsMatchingASZI(Point3D p3d, int aszi, TYPE task) {
        return getAccesingPointsMatchingASZI(p3d.x, p3d.y, p3d.z, aszi, task);
    }

    /**
     * Devuelve una lista de puntos adyacentes accesibles que est�n en la zona
     * pasada
     *
     * @param x
     * @param y
     * @param z
     * @return una lista de puntos adyacentes accesibles que est�n en la zona
     * pasada
     * @para aszi
     */
    public static ArrayList<Point3DShort> getAccesingPointsMatchingASZI(int x, int y, int z, int aszi, TYPE task) {
        ArrayList<Point3DShort> places = getAccesingPoints(x, y, z, task);
        ArrayList<Point3DShort> placesOK = new ArrayList<>();

        for (Point3DShort place : places) {
            if (World.getCell(place).getAstarZoneID() == aszi) {
                placesOK.add(place);
            }
        }

        return placesOK;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public TYPE getType() {
        return task;
    }

    public void setTask(TYPE nTask) {
        task = nTask;

        switch (task) {
            case TYPE.MINE, TYPE.MINE_LADDER, TYPE.DIG, TYPE.CANCEL_ORDER, TYPE.STOCKPILE, TYPE.CREATE_ZONE,
                 TYPE.EXPAND_ZONE, TYPE.CREATE_AND_PLACE_ROW, TYPE.QUEUE_AND_PLACE_ROW, TYPE.QUEUE_AND_PLACE_AREA,
                 TYPE.CUSTOM_ACTION:
                setState(STATE_CREATING_INIZONE);
                break;
            case TYPE.BUILD, TYPE.CREATE_AND_PLACE, TYPE.QUEUE_AND_PLACE:
                setState(STATE_CREATING_SINGLEPOINT);
                break;
            default:
                setState(STATE_CREATED);
                break;
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String toString() {
        switch (task) {
            case NO_TASK:
                return Messages.getString("Task.0"); //$NON-NLS-1$
            case DIG:
            case MINE:
                return Messages.getString("Task.3"); //$NON-NLS-1$
            case MINE_LADDER:
                return Messages.getString("Task.41"); //$NON-NLS-1$
            case CANCEL_ORDER:
                return Messages.getString("Task.29"); //$NON-NLS-1$
            case WEAR:
                return Messages.getString("Task.18"); //$NON-NLS-1$
            case AUTOEQUIP:
                return Messages.getString("Task.38"); //$NON-NLS-1$
            case WEAR_OFF:
                return Messages.getString("Task.24"); //$NON-NLS-1$
            case FIGHT:
                return Messages.getString("Task.25"); //$NON-NLS-1$
            case HEAL:
                return Messages.getString("Task.28"); //$NON-NLS-1$
            case BUILD:
                return Messages.getString("Task.4"); //$NON-NLS-1$
            case CREATE_AND_PLACE:
            case QUEUE_AND_PLACE:
            case CREATE_AND_PLACE_ROW:
            case QUEUE_AND_PLACE_ROW:
            case QUEUE_AND_PLACE_AREA:
                return Messages.getString("Task.5"); //$NON-NLS-1$
            case REMOVE_BUILDING_TASK:
                return Messages.getString("Task.20"); //$NON-NLS-1$
            case QUEUE:
                return Messages.getString("Task.1"); //$NON-NLS-1$
            case CREATE:
                return Messages.getString("Task.23"); //$NON-NLS-1$
            case CREATE_IN_A_BUILDING:
                return Messages.getString("Task.23"); //$NON-NLS-1$
            case STOCKPILE:
                return Messages.getString("Task.6"); //$NON-NLS-1$
            case CREATE_ZONE:
                return Messages.getString("Task.26"); //$NON-NLS-1$
            case DELETE_ZONE:
                return Messages.getString("Task.27"); //$NON-NLS-1$
            case EXPAND_ZONE:
                return Messages.getString("Task.35"); //$NON-NLS-1$
            case HAUL:
            case PUT_IN_CONTAINER:
                return Messages.getString("Task.7"); //$NON-NLS-1$
            case MOVE_AND_LOCK:
                return Messages.getString("Task.8"); //$NON-NLS-1$
            case DROP:
                return Messages.getString("Task.9"); //$NON-NLS-1$
            case SLEEP:
                return Messages.getString("Task.10"); //$NON-NLS-1$
            case EAT:
                return Messages.getString("Task.11"); //$NON-NLS-1$
            case MOVE_TO_CARAVAN:
                return Messages.getString("Task.39"); //$NON-NLS-1$
            case FOOD_NEEDED:
                return Messages.getString("Task.42"); //$NON-NLS-1$

            case REMOVE_FROM_CONTAINER:
                return Messages.getString("Task.43"); //$NON-NLS-1$
            case CUSTOM_ACTION:
                ActionManagerItem ami = ActionManager.getItem(getParameter());
                if (ami != null && ami.getName() != null) {
                    return ami.getName();
                } else {
                    if (getParameter() != null && getParameter().contains(",")) { //$NON-NLS-1$
                        StringTokenizer tokenizer = new StringTokenizer(getParameter(), ","); //$NON-NLS-1$
                        String sToken;
                        ArrayList<String> alStrings = new ArrayList<>();
                        while (tokenizer.hasMoreTokens()) {
                            sToken = tokenizer.nextToken().trim();
                            ami = ActionManager.getItem(sToken);
                            if (ami != null && !alStrings.contains(ami.getName())) {
                                alStrings.add(ami.getName());
                            }
                        }
                        StringBuilder sBuffer = new StringBuilder();
                        if (!alStrings.isEmpty()) {
                            for (int i = 0; i < alStrings.size(); i++) {
                                if (i > 0) {
                                    sBuffer.append(", "); //$NON-NLS-1$
                                }
                                sBuffer.append(alStrings.get(i));
                            }
                        }
                        if (!sBuffer.isEmpty()) {
                            return sBuffer.toString();
                        } else {
                            return Messages.getString("Task.12"); //$NON-NLS-1$
                        }
                    } else {
                        return Messages.getString("Task.12"); //$NON-NLS-1$
                    }
                }
            default:
                return Messages.getString("Task.12"); //$NON-NLS-1$
        }
    }

    public String toStringState() {
        return switch (state) {
            case STATE_CREATING_INIZONE -> Messages.getString("Task.13"); //$NON-NLS-1$
            case STATE_CREATING_ENDZONE -> Messages.getString("Task.14"); //$NON-NLS-1$
            case STATE_CREATING_SINGLEPOINT -> Messages.getString("Task.15"); //$NON-NLS-1$
            default -> Messages.getString("Task.16"); //$NON-NLS-1$
        };
    }

    public Point3D getPointIni() {
        return pointIni;
    }

    public void setPointIni(Point3D pointIni) {
        this.pointIni = pointIni;
    }

    public void setPointIni(Point3DShort pointIni) {
        if (pointIni == null) {
            this.pointIni = null;
        } else {
            this.pointIni = pointIni.toPoint3D();
        }
    }

    public Point3D getPointEnd() {
        return pointEnd;
    }

    public void setPointEnd(Point3D pointEnd) {
        this.pointEnd = pointEnd;
    }

    public void setPointEnd(Point3DShort pointEnd) {
        this.pointEnd = pointEnd.toPoint3D();
    }

    public void setPoint(Point3D point) {
        boolean bCheckShift = false;
        TYPE iOldTask = getType(); // Esto es para que el dig/mine con el shift no se jorobe
        if (state == STATE_CREATING_INIZONE) {
            setPointIni(point);
            setState(STATE_CREATING_ENDZONE);
        } else if (state == STATE_CREATING_ENDZONE) {
            if (MainPanel.tDMouseON) {
                setPointEnd(new Point3D(point.x, point.y, getPointIni().z));
            } else {
                setPointIni(new Point3D(getPointIni().x, getPointIni().y, point.z));
                setPointEnd(point);
            }

            // Buscamos los puntos donde ir a hacer la tarea
            setZoneHotPoints();
            setState(STATE_CREATED);

            // Controlamos que no haya acabado
            // Podr�a ser en el caso de marcar una zona no accesible para una tarea
            if (isFinished()) {
                Game.deleteCurrentTask();
            } else {
                Game.taskCreated();
            }
            bCheckShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        } else if (state == STATE_CREATING_SINGLEPOINT) {
//			if (MainPanel.tDMouseON) {
            //int iZ3D = MainPanel.getMaxZ3DMouse (point.x, point.y, point.z);
            //setPointIni (MainPanel.get3DMouse (point.x, point.y, point.z));
//			} else {
//				setPointIni (point);
//			}
            setPointIni(point);

            // Buscamos los puntos donde ir a hacer la tarea
            setZoneHotPoints();
            setState(STATE_CREATED);

            // Controlamos que no haya acabado
            // Podr�a ser en el caso de marcar una zona no accesible para una tarea
            if (isFinished()) {
                Game.deleteCurrentTask();
            } else {
                Game.taskCreated();
            }
            bCheckShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        }

        // Miramos si hay que crear otra tarea igual
        if (bCheckShift) {
            Game.createTask(iOldTask);
            Game.getCurrentTask().setParameter(getParameter());
            Game.getCurrentTask().setParameter2(getParameter2());
            Game.getCurrentTask().setTile(getTile(), getIconType());
        }
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;

        switch (task) {
            case TYPE.QUEUE_AND_PLACE, TYPE.QUEUE_AND_PLACE_ROW, TYPE.QUEUE_AND_PLACE_AREA:
                // Tarea queue and place, metemos en el parameter el item a crear (para el dibujado mientras lo coloca) y la queueID en el parameter2
                ActionManagerItem ami = ActionManager.getItem(getParameter());
                if (ami == null) {
                    Log.log(Log.LEVEL.ERROR, Messages.getString("Task.34") + getParameter() + "]", getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$
                    Game.deleteCurrentTask();
                    return;
                }
                ArrayList<QueueItem> alQueue = ami.getQueue();
                String sItem = null;
                for (int i = alQueue.size() - 1; i >= 0; i--) {
                    if (alQueue.get(i).getType() == QueueItem.TYPE_CREATE_ITEM || alQueue.get(i).getType() == QueueItem.TYPE_CREATE_ITEM_BY_TYPE) {
                        sItem = alQueue.get(i).getValue();
                        break;
                    }
                }

                if (sItem != null) {
                    setParameter2(sItem);
                } else {
                    Log.log(Log.LEVEL.ERROR, Messages.getString("Task.36") + getParameter() + "]", getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$
                    Game.deleteCurrentTask();
                }
                break;
            default:
                break;
        }
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    private void checkCancelTask(ArrayList<TaskManagerItem> alTasks) {
        ArrayList<HotPoint> alPoints;
        Point3DShort p3dCancel, p3dTask;
        Task task;
        for (TaskManagerItem alTask : alTasks) {
            task = alTask.getTask();
            switch (task.getType()) {
                case TYPE.MINE, TYPE.MINE_LADDER:
                    // Tarea mine/dig
                    // Miramos los puntos
                    alPoints = task.getHotPoints();
                    for (int p = 0; p < alPoints.size(); p++) {
                        if (!alPoints.get(p).isFinished()) {
                            p3dTask = alPoints.get(p).getHotPoint();

                            for (int t = 0; t < getHotPoints().size(); t++) {
                                p3dCancel = getHotPoints().get(t).getHotPoint();

                                if (p3dCancel.equals(p3dTask)) {
                                    // Punto encontrado, lo marcamos como finished
                                    Game.getWorld().getTaskManager().setHotPointFinished(task, p);
                                    // Quitamos el flag de tarea de la celda
                                    World.getCell(p3dCancel).setFlagOrders(false);
                                    // El aldeano ya mirar� si el hp est� acabado y se quitar� de la tarea
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void checkCancelActions(ArrayList<Action> alActions, boolean checkCitizens) {
        Action action;
        ArrayList<Integer> alCitizens = World.getCitizenIDs();
        ArrayList<Integer> alSoldiers = World.getSoldierIDs();
        Citizen citizen;
        Point3DShort p3dCancel;
        for (int t = 0; t < getHotPoints().size(); t++) {
            p3dCancel = getHotPoints().get(t).getHotPoint();

            // Acciones en cola
            for (int x = (alActions.size() - 1); x >= 0; x--) {
                action = alActions.get(x);

                boolean bRemover = false;
                // Colas
                if (action.getDestinationPoint() != null && action.getDestinationPoint().equals(p3dCancel)) {
                    bRemover = true;
                } else {
                    if (action.getTerrainPoint() != null && action.getTerrainPoint().equals(p3dCancel)) {
                        bRemover = true;
                    } else {
                        // Create item desde item
                        if (action.getEntityID() != -1) {
                            Item item = Item.getItemByID(action.getEntityID());
                            if (item != null && item.getCoordinates().equals(p3dCancel)) {
                                bRemover = true;
                            } else {
                                // Livings
                                LivingEntity le = World.getLivingEntityByID(action.getEntityID());
                                if (le != null && le.getCoordinates().equals(p3dCancel)) {
                                    bRemover = true;
                                }
                            }
                        }
                    }
                }

                if (bRemover) {
                    Action actionRemoved = alActions.remove(x);
                    Game.getWorld().getTaskManager().removeFromProductionPanelRegular(actionRemoved.getId());
                    World.getCell(p3dCancel).setFlagOrders(false);
                }
            }

            if (checkCitizens) {
                // Acciones de aldeanos
                for (Integer alCitizen : alCitizens) {
                    citizen = (Citizen) World.getLivingEntityByID(alCitizen);
                    if (citizen == null) {
                        continue;
                    }
                    action = citizen.getCurrentCustomAction();
                    if (action != null && citizen.getCurrentTask() != null) {
                        // Colas
                        if ((action.getDestinationPoint() != null && action.getDestinationPoint().equals(p3dCancel)) || (action.getTerrainPoint() != null && action.getTerrainPoint().equals(p3dCancel))) {
                            citizen.getCurrentTask().setFinished(true);
                            Game.getWorld().getTaskManager().removeCitizen(citizen);
                            World.getCell(p3dCancel).setFlagOrders(false);
                        }
                    }
                }
                // Acciones de soldiers... necesario?
                for (Integer alSoldier : alSoldiers) {
                    citizen = (Citizen) World.getLivingEntityByID(alSoldier);
                    if (citizen == null) {
                        continue;
                    }
                    action = citizen.getCurrentCustomAction();
                    if (action != null && citizen.getCurrentTask() != null) {
                        // Colas
                        if ((action.getDestinationPoint() != null && action.getDestinationPoint().equals(p3dCancel)) || (action.getTerrainPoint() != null && action.getTerrainPoint().equals(p3dCancel))) {
                            citizen.getCurrentTask().setFinished(true);
                            Game.getWorld().getTaskManager().removeCitizen(citizen);
                            World.getCell(p3dCancel).setFlagOrders(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Crea los hotpoints, tambi�n setea el maxCitizens para la tarea segun
     * estos
     */
    public void setZoneHotPoints() {
        // Buscamos en el mapa
        Cell[][][] cells = World.getCells();

        short x0 = (short) getPointIni().x;
        short y0 = (short) getPointIni().y;
        short x1, y1;
        boolean bXSwapped = false;
        boolean bYSwapped = false;
        if (getPointEnd() != null) {
            x1 = (short) getPointEnd().x;
            y1 = (short) getPointEnd().y;

            if (x0 > x1) {
                short aux = x0;
                x0 = x1;
                x1 = aux;
                bXSwapped = true;
            }
            if (y0 > y1) {
                short aux = y0;
                y0 = y1;
                y1 = aux;
                bYSwapped = true;
            }
        } else {
            // No es una zona, punto inicial y final los dejamos igual
            x1 = x0;
            y1 = y0;
        }
        short z = (short) getPointIni().z;

        var taskType = this.getType();

        if (taskType == TYPE.MINE || taskType == TYPE.DIG || taskType == TYPE.MINE_LADDER || taskType == TYPE.CUSTOM_ACTION || taskType == TYPE.CANCEL_ORDER) {
            ArrayList<ActionManagerItem> alAmis = new ArrayList<>(); // Con arrays por si se usa lo de 2 (o m�s) acciones en 1 mismo bot�n
            ArrayList<String> alParameters = new ArrayList<>(); // Con arrays por si se usa lo de 2 (o m�s) acciones en 1 mismo bot�n

            if (taskType == TYPE.CUSTOM_ACTION) {
                String sParameter = getParameter();
                if (sParameter.contains(",")) { //$NON-NLS-1$
                    StringTokenizer tokenizer = new StringTokenizer(sParameter, ","); //$NON-NLS-1$
                    String sToken;
                    while (tokenizer.hasMoreTokens()) {
                        sToken = tokenizer.nextToken().trim();
                        alAmis.add(ActionManager.getItem(sToken));
                        alParameters.add(sToken);
                    }
                } else {
                    alAmis.add(ActionManager.getItem(sParameter));
                    alParameters.add(sParameter);
                }
            }

            // Recorremos todas las celdas de la �rden (o �rdenes)
            for (short x = x0; x <= x1; x++) {
                for (short y = y0; y <= y1; y++) {
                    var cell = cells[x][y][getPointIni().z];

                    if (taskType == TYPE.MINE || taskType == TYPE.MINE_LADDER || taskType == TYPE.DIG) {
                        // MINE: Por cada celda minable a�adimos su coordenada a los hotpoints, y alloweds adyacentes a las places
                        // DIG: Se crea tarea de mine de la celda de abajo
                        if (taskType == TYPE.DIG) {
                            if (getPointIni().z < (World.MAP_DEPTH - 2)) {
                                cell = cells[x][y][getPointIni().z + 1];
                            } else {
                                continue;
                            }
                        } else if (taskType == TYPE.MINE || taskType == TYPE.MINE_LADDER) {
                            if (getPointIni().z >= (World.MAP_DEPTH - 1)) {
                                continue;
                            }
                        }

                        if (!cell.getTerrain().hasFluids() && (!cell.isMined()) || !cell.isDiscovered()) {
                            Point3DShort p3d = Point3DShort.getPoolInstance(x, y, cell.getCoordinates().z);

                            if (!existTaskPoint(taskType, p3d)) {
                                // Miramos desde donde se puede acceder
                                ArrayList<Point3DShort> places = getAccesingPoints(x, y, cell.getCoordinates().z, taskType);

                                addHotPoint(new HotPoint(p3d, places));
                            }
                        }
                    } else if (taskType == TYPE.CUSTOM_ACTION) {
                        // CUSTOM: Depende del tipo
                        ActionManagerItem ami;
                        String sParameter;

                        for (int a = 0; a < alAmis.size(); a++) {
                            ami = alAmis.get(a);
                            sParameter = alParameters.get(a);
                            Entity entity = cell.getEntity();
                            if (entity instanceof Item && ItemManager.getItem(entity.getIniHeader()).getActions().contains(sParameter)) {
                                Action action = new Action(ami.getId());
                                action.setEntityID(entity.getID());
                                action.setQueue(ami.getQueue());
                                action.setQueueData(new QueueData());
                                action.setFace(MainPanel.itemBuildFace);
                                Game.getWorld().getTaskManager().addCustomAction(action, true);
                            } else {
                                // Living?
                                boolean bLiving = false;
                                ArrayList<LivingEntity> alLivings = cell.getLivings();

                                if (alLivings != null) {
                                    for (LivingEntity le : alLivings) {
                                        if (LivingEntityManager.getItem(le.getIniHeader()).getActions().contains(sParameter)) {
                                            Action action = new Action(ami.getId());
                                            action.setEntityID(le.getID());
                                            action.setQueue(ami.getQueue());
                                            action.setQueueData(new QueueData());
                                            Game.getWorld().getTaskManager().addCustomAction(action, true);
                                            bLiving = true;
                                        }
                                    }
                                }

                                if (!bLiving) {
                                    // Acci�n de terrain?
                                    if (cell.isMined() && cell.getCoordinates().z < (World.MAP_DEPTH - 1)) {
                                        Cell cellUnder = World.getCell(x, y, getPointIni().z + 1);
                                        TerrainManagerItem tmi = TerrainManager.getItemByID(cellUnder.getTerrain().getTerrainID());

                                        if (tmi.getActions().contains(sParameter)) {
                                            // Celda posible para acci�n de terrain
                                            boolean bCasillaOcupada = cell.getTerrain().hasFluids() || cell.isFlagOrders();
                                            if (!bCasillaOcupada) {
                                                if (cell.hasItem()) {
                                                    Item item = (Item) cell.getEntity();
                                                    if (item != null && item.isOperative()) {
                                                        ItemManagerItem imi = ItemManager.getItem(item.getIniHeader());

                                                        if (imi.isWall()) {
                                                            bCasillaOcupada = true;
                                                        }
                                                    }
                                                }
                                                if (!bCasillaOcupada) {
                                                    if (cell.hasBuilding()) {
                                                        Building building = Building.getBuilding(cell.getCoordinates());

                                                        if (building != null) {
                                                            bCasillaOcupada = true;
                                                        }
                                                    }

                                                    if (!bCasillaOcupada) {
                                                        Point3DShort p3d = Point3DShort.getPoolInstance(x, y, z);
                                                        Action action = new Action(ami.getId());
                                                        action.setTerrainPoint(p3d);
                                                        action.setQueue(ami.getQueue());
                                                        action.setQueueData(new QueueData());
                                                        Game.getWorld().getTaskManager().addCustomAction(action, true);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    } else if (taskType == TYPE.CANCEL_ORDER) {
                        if (cell.isFlagOrders()) {
                            Point3DShort p3d = Point3DShort.getPoolInstance(x, y, z);
                            addHotPoint(new HotPoint(p3d, p3d));
                        }
                    }
                }
            }

            if (taskType == TYPE.CANCEL_ORDER) {
                // Miramos si existen tareas dig/mine/custom en el manager
                // En ese caso miramos si alguno de sus puntos se corresponde con alguno de la lista de puntos a cancelar
                checkCancelTask(Game.getWorld().getTaskManager().getTaskItems());
                checkCancelTask(Game.getWorld().getTaskManager().getTaskItemsTemp());

                // Ahora las custom actions
                checkCancelActions(Game.getWorld().getTaskManager().getCustomActions(), true);
                checkCancelActions(Game.getWorld().getTaskManager().getCustomActionsTemp(), false);
                checkCancelActions(Game.getWorld().getTaskManager().getCustomActionsWait(), false);

                setMaxCitizens(0);
                setFinished(true);
            } else {
                // Seteamos el flag de casilla con "�rdenes" en cada celda (se usa en el pintado)
                for (int x = 0; x < getHotPoints().size(); x++) {
                    World.getCell(getHotPoints().get(x).getHotPoint()).setFlagOrders(true);
                }

                setMaxCitizens(getHotPoints().size());
            }

        } else if (taskType == TYPE.MOVE_TO_CARAVAN) {
            addHotPoint(new HotPoint(getPointIni().toPoint3DShort(), getPointEnd().toPoint3DShort()));
        } else if (taskType == TYPE.FOOD_NEEDED) {
            addHotPoint(new HotPoint(getPointIni().toPoint3DShort(), getPointIni().toPoint3DShort()));
        } else if (taskType == TYPE.BUILD) {
            // BUILD
            //cell = cells[x0][y0][z];

            // BUILD: Miramos si la celda es accesible en todas las casillas
            // Excepto las que no forman parte del edificio ya que ahora no tienen porque ser rectangulares
            BuildingManagerItem item = BuildingManager.getItem(getParameter());
            Building building = Building.createBuilding(item);

            if (building == null) {
                Log.log(Log.LEVEL.ERROR, Messages.getString("Task.17") + getParameter() + "]", getClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                boolean bAvailableForBuilding = true;

                break1:
                for (short x = x0; x < (x0 + item.getWidth()); x++) {
                    for (short y = y0; y < (y0 + item.getHeight()); y++) {
                        char groundDataChar = item.getGroundData().charAt((y - y0) * item.getWidth() + (x - x0));
                        if (groundDataChar == Building.GROUND_NON_BUILDING) {
                            continue;
                        }
                        if (!Building.isCellAvailableForBuilding(item, x, y, z)) {
                            bAvailableForBuilding = false;
                            break break1;
                        }
                    }
                }

                if (bAvailableForBuilding) {
                    // Metemos las celdas del edificio transitables y la entrance como places para construirse
                    Point3DShort p3d = null;
                    break2:
                    for (short x = x0; x < (x0 + item.getWidth()); x++) {
                        for (short y = y0; y < (y0 + item.getHeight()); y++) {
                            char groundDataChar = item.getGroundData().charAt((y - y0) * item.getWidth() + (x - x0));
                            if (groundDataChar == Building.GROUND_ENTRANCE) {
                                p3d = Point3DShort.getPoolInstance(x, y, z);
                                break break2;
                            }
                        }
                    }

                    if (p3d == null) {
                        // No deber�a pasar nunca
                        Log.log(Log.LEVEL.ERROR, Messages.getString("Task.31") + building.getIniHeader() + "]", getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    addHotPoint(new HotPoint(p3d, p3d));

                    setMaxCitizens(1);

                    // Tarea de construcci�n, metemos el building donde toca y los aldeanos ya lo construir�n y pondr�n operativo
                    building.setCoordinates(x0, y0, z);

                    // Cargamos los prerequisitos
                    BuildingManagerItem bmi = BuildingManager.getItem(getParameter());
                    building.setPrerequisites(bmi.getPrerequisites());
                    building.setPrerequisitesLiving(bmi.getPrerequisitesFriendly());

                    // Si es un edificio autom�tico le metemos en cola el primer item/living que pueda construir y le ponemos non-stop
                    if (bmi.isAutomatic()) {
                        boolean bSpawn = (bmi.getType() != null && bmi.getType().equalsIgnoreCase(Building.TYPE_SPAWN));

                        String itemName = null;
                        if (bSpawn) {
                            // SPAWNER
                            ArrayList<LivingEntityManagerItem> alItems = LivingEntityManager.getItemsByBuilding(bmi.getIniHeader());
                            if (!alItems.isEmpty()) {
                                itemName = alItems.get(0).getIniHeader();
                            }
                        } else {
                            // ITEMS
                            if (bmi.isMineTerrain()) {
                                // Pillamos un terreno al azar de los que tiene debajo, que tenga drop y pillamos su drop como primer item
                                ArrayList<String> alDrops = new ArrayList<>();
                                if (z < (World.MAP_DEPTH - 1)) {
                                    TerrainManagerItem tmi;
                                    for (int x = x0; x < (x0 + item.getWidth()); x++) {
                                        for (int y = y0; y < (y0 + item.getHeight()); y++) {
                                            tmi = TerrainManager.getItemByID(World.getCell(x, y, z + 1).getTerrain().getTerrainID());
                                            if (tmi.getDrop() != null) {
                                                alDrops.add(tmi.getDrop());
                                            }
                                        }
                                    }
                                }

                                if (alDrops.isEmpty()) {
                                    // No deber�a pasar
                                    Log.log(Log.LEVEL.ERROR, Messages.getString("Task.33"), getClass().toString()); //$NON-NLS-1$
                                } else {
                                    itemName = alDrops.get(Utils.getRandomBetween(0, alDrops.size() - 1));
                                }
                            } else {
                                ArrayList<ItemManagerItem> alItems = ItemManager.getItemsByBuilding(bmi.getIniHeader());
                                if (!alItems.isEmpty()) {
                                    itemName = alItems.get(0).getIniHeader();
                                }
                            }
                        }

                        // Tenemos item/living
                        if (itemName != null) {
                            building.setLastItem(itemName);
                            building.setNonStop(true);
                        }
                    }

                    // Lo a�adimos
                    World.getCells()[x0][y0][z].setEntity(building);
                    World.getBuildings().add(building);

                    // Activamos el flag de building a todas las casillas que formen parte del edificio
                    // Quitamos tambi�n el flag de stockpile y/o zona (si lo hubiera)
                    for (short x = x0; x < (x0 + item.getWidth()); x++) {
                        for (short y = y0; y < (y0 + item.getHeight()); y++) {
                            char groundDataChar = item.getGroundData().charAt((y - y0) * item.getWidth() + (x - x0));
                            if (groundDataChar == Building.GROUND_NON_BUILDING) {
                                continue;
                            }
                            World.getCells()[x][y][z].setBuildingCoordinates(Point3DShort.getPoolInstance(x0, y0, z));
                            Stockpile.deleteStockpilePoint(x, y, z);
                            Zone.deleteZonePoint(x, y, z);
                        }
                    }
                }
            }
        } else if (taskType == TYPE.STOCKPILE) {
            // STOCKPILE: Marcamos las celdas que toca como stockpile
            boolean bStockpileOK = false;
            Stockpile stockpile = new Stockpile(getParameter());

            for (short x = x0; x <= x1; x++) {
                for (short y = y0; y <= y1; y++) {
                    if (Stockpile.isCellAvailableForStockpile(x, y, (short) getPointIni().z)) {
                        stockpile.addPoint(Point3DShort.getPoolInstance(x, y, getPointIni().z));
                        bStockpileOK = true;
                    }
                }
            }

            if (bStockpileOK) {
                if (stockpile.getPoints() != null && !stockpile.getPoints().isEmpty()) {
                    // Deshabilitamos items si hace falta
                    if (Game.isDisabledItemsON()) {
                        stockpile.disableAll();
                    }

                    // A�adimos la stockpile al mundo
                    Game.getWorld().addStockPile(stockpile);

                    // Tutorial flow
                    Game.updateTutorialFlow(TutorialTrigger.TYPE_INT_PILE, UtilsIniHeaders.getIntIniHeader(getParameter()), null);
                }
            }

            // No hay que hacer nada m�s, el Manager de tareas ya se encargar� de crear/asignar tareas de haul
        } else if (taskType == TYPE.CREATE_ZONE) {
            // ZONE: Marcamos las celdas que toca como zone del tipo pasado
            Zone zone;
            ZoneManagerItem zmi = ZoneManager.getItem(getParameter());

            if (zmi != null) {

                if (zmi.getType() == ZoneManagerItem.TYPE_PERSONAL) {
                    zone = new ZonePersonal(getParameter());
                } else if (zmi.getType() == ZoneManagerItem.TYPE_HERO_ROOM) {
                    zone = new ZoneHeroRoom(getParameter());
                } else if (zmi.getType() == ZoneManagerItem.TYPE_BARRACKS) {
                    zone = new ZoneBarracks(getParameter());
                } else {
                    zone = new Zone(getParameter());
                }

                boolean bZoneOK = Zone.areCellsAvailableForZone(zmi, x0, y0, x1, y1, getPointIni().z, null);

                if (bZoneOK) {
                    // Metemos los puntos
                    for (short x = x0; x <= x1; x++) {
                        for (short y = y0; y <= y1; y++) {
                            if (!World.getCell(x, y, getPointIni().z).hasZone()) {
                                zone.getPoints().add(Point3DShort.getPoolInstance(x, y, getPointIni().z));
                            }
                        }
                    }

                    if (zone.getPoints() != null && !zone.getPoints().isEmpty()) {
                        // Zona personal, se la asignamos a alguien
                        if (zmi.getType() == ZoneManagerItem.TYPE_PERSONAL) {
                            Citizen cit;
                            boolean bAsignada = false;
                            for (int i = 0; i < World.getCitizenIDs().size(); i++) {
                                cit = (Citizen) World.getLivingEntityByID(World.getCitizenIDs().get(i));
                                if (!cit.getCitizenData().hasZone()) {
                                    // Aldeano sin zona personal, se la metemos
                                    cit.getCitizenData().setZoneID(zone.getID());
                                    ((ZonePersonal) zone).setOwnerID(cit.getID());
                                    bAsignada = true;
                                    break;
                                }
                            }
                            if (!bAsignada) {
                                // Soldiers
                                for (int i = 0; i < World.getSoldierIDs().size(); i++) {
                                    cit = (Citizen) World.getLivingEntityByID(World.getSoldierIDs().get(i));
                                    if (!cit.getCitizenData().hasZone()) {
                                        // Aldeano sin zona personal, se la metemos
                                        cit.getCitizenData().setZoneID(zone.getID());
                                        ((ZonePersonal) zone).setOwnerID(cit.getID());
                                        break;
                                    }
                                }
                            }
                        } else if (zmi.getType() == ZoneManagerItem.TYPE_HERO_ROOM) {
                            Hero hero;
                            for (int i = 0; i < World.getHeroIDs().size(); i++) {
                                hero = (Hero) World.getLivingEntityByID(World.getHeroIDs().get(i));
                                if (!hero.getCitizenData().hasZone()) {
                                    // H�roe sin zona personal, se la metemos (si es que le molan las free rooms)
                                    HeroPrerequisite hPrerequisite = HeroPrerequisite.getHeroPrerequisite(HeroManager.getStayPrerequisites(LivingEntityManager.getItem(hero.getIniHeader()).getHeroStayPrerequisite()), HeroPrerequisite.ID_FREE_ROOM);
                                    if (hPrerequisite != null && hPrerequisite.isValueBoolean()) {
                                        // H�roe necesita free room para stay
                                        hero.getCitizenData().setZoneID(zone.getID());
                                        ((ZoneHeroRoom) zone).setOwnerID(hero.getID());
                                        break;
                                    }
                                }
                            }
                        } else if (zmi.getType() == ZoneManagerItem.TYPE_BARRACKS) {
                            SoldierGroupData sgd;
                            for (int i = 0; i < SoldierGroups.MAX_GROUPS; i++) {
                                sgd = Game.getWorld().getSoldierGroups().getGroup(i);
                                if (!sgd.hasZone()) {
                                    // Grupo sin zona, se la metemos
                                    sgd.setZoneID(zone.getID());
                                    ((ZoneBarracks) zone).setGroupID(i);
                                    break;
                                }
                            }
                        }

                        // A�adimos la zone al mundo
                        Game.getWorld().addZone(zone, false);

                        // Tutorial flow
                        Game.updateTutorialFlow(TutorialTrigger.TYPE_INT_ZONE, UtilsIniHeaders.getIntIniHeader(zone.getIniHeader()), null);
                    }
                }
            }

            // No hay que hacer nada m�s
        } else if (taskType == TYPE.EXPAND_ZONE) {
            // EXPAND ZONE
            Zone zone = Zone.getZone(Integer.parseInt(getParameter()));

            if (zone != null) {
                ZoneManagerItem zmi = ZoneManager.getItem(zone.getIniHeader());
                boolean bZoneOK = Zone.areCellsAvailableForZone(zmi, x0, y0, x1, y1, getPointIni().z, zone);

                if (bZoneOK) {
                    // Metemos los puntos
                    Point3DShort p3dZonePoint;
                    Cell cellZone;
                    for (short x = x0; x <= x1; x++) {
                        for (short y = y0; y <= y1; y++) {
                            p3dZonePoint = Point3DShort.getPoolInstance(x, y, getPointIni().z);
                            cellZone = World.getCell(p3dZonePoint);
                            if (cellZone.getAstarZoneID() != -1 && !zone.getPoints().contains(p3dZonePoint)) {
                                zone.getPoints().add(p3dZonePoint);
                            }
                        }
                    }

                    if (zone.getPoints() != null && !zone.getPoints().isEmpty()) {
                        Game.getWorld().addZone(zone, true);
                    }
                }
            } else {
                Log.log(Log.LEVEL.ERROR, Messages.getString("Task.37"), getClass().toString()); //$NON-NLS-1$
            }

            // No hay que hacer nada m�s
        } else if (taskType == TYPE.CREATE_AND_PLACE || taskType == TYPE.QUEUE_AND_PLACE) {
            // CREATE: Miramos si en la casilla indicada se puede meter el item y, si no existe item, que tengamos un edificio
            //cell = cells[x0][y0][z];

            ItemManagerItem imi;
            if (taskType == TYPE.QUEUE_AND_PLACE) {
                imi = ItemManager.getItem(getParameter2());
            } else {
                imi = ItemManager.getItem(getParameter());
            }
            if (imi == null) {
                Log.log(Log.LEVEL.ERROR, Messages.getString("Task.30") + getParameter() + "]", getClass().toString()); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                // Miramos si se puede poner el item en todas las casillas (s�lo 1, he borrado el width/height)
                boolean bAvailableForBuilding = Item.isCellAvailableForItem(imi, x0, y0, z, true, true);
                if (bAvailableForBuilding) {
                    if (imi.canBeBuiltOnHoles()) {
                        // Miramos si hay alg�n ASZID distinto de -1
                        boolean bAllUnavailable = true;
                        foriteming:
                        for (short itemX = (short) (x0 - 1); itemX <= (x0 + 1); itemX++) {
                            for (short itemY = (short) (y0 - 1); itemY <= (y0 + 1); itemY++) {
                                for (short itemZ = (short) (z - 1); itemZ <= (z + 1); itemZ++) {
                                    if (Utils.isInsideMap(itemX, itemY, itemZ)) {
                                        if (World.getCell(itemX, itemY, itemZ).getAstarZoneID() != -1) {
                                            bAllUnavailable = false;
                                            break foriteming;
                                        }
                                    }
                                }
                            }
                        }
                        if (bAllUnavailable) {
                            // Arriba
                            if (z > 0) {
                                if (World.getCell(x0, y0, z - 1).getAstarZoneID() != -1) {
                                    bAllUnavailable = false;
                                }
                            }

                            if (bAllUnavailable) {
                                // Abajo
                                if (z < (World.MAP_DEPTH - 1)) {
                                    if (World.getCell(x0, y0, z + 1).getAstarZoneID() != -1) {
                                        bAllUnavailable = false;
                                    }
                                }
                            }
                        }
                        if (bAllUnavailable) {
                            bAvailableForBuilding = false;
                        }
                    }
                }

                boolean bItemEnElMundo = false;
                if (bAvailableForBuilding) {
                    // Miramos si en el mundo hay algun item NO-LOCKED de estos
                    int numItems = Item.getNumItems(UtilsIniHeaders.getIntIniHeader(imi.getIniHeader()), false, Game.getWorld().getRestrictHaulEquippingLevel());
                    if (numItems > 0) {
                        // Hay items, miramos si alguno no est� locked
                        //Integer[] aItems = World.getItems ().keySet ().toArray (new Integer [0]);
                        ArrayList<Integer> aItems = Item.getMapItems().get(imi.getNumericalIniHeader());
                        if (aItems != null) {
                            Item itemAux;
                            for (Integer aItem : aItems) {
                                itemAux = Item.getItemByID(aItem, true);
                                if (itemAux != null && itemAux.getNumericIniHeader() == imi.getNumericalIniHeader()) {
                                    // Item del tipo deseado, miramos si no est� locked y en la misma zona A* que el destino
                                    if (!itemAux.isLocked()) {
                                        // Item NO locked, miramos que no haya otro aldeano que lo vaya a usar
                                        if (!itemInUse(itemAux)) {
                                            int iItemASZID = World.getCell(itemAux.getCoordinates()).getAstarZoneID();
                                            int iItemASZIDDestination = World.getCell(x0, y0, z).getAstarZoneID();
                                            if (iItemASZID != -1 && (iItemASZID == iItemASZIDDestination || iItemASZIDDestination == -1)) {
                                                // Caso especial, ladders, estos van en una casilla digada, entonces el A*ZI ser� distinto
                                                // Hay que mirar si las casillas adyacentes (desde donde se colocar�) son accesibles
                                                if (ItemManager.getItem(itemAux.getIniHeader()).canBeBuiltOnHoles()) {
                                                    ArrayList<Point3DShort> alPoints = getAccesingPointsMatchingASZI(x0, y0, z, iItemASZID, taskType);
                                                    if (!alPoints.isEmpty()) {
                                                        // De guais, tenemos casillas accesibles en la misma zona que el item
                                                        // Creamos una tarea de MOVE (como las haul pero persistentes)
                                                        bItemEnElMundo = true;
                                                        Task task = new Task(TYPE.MOVE_AND_LOCK);
                                                        task.setPointIni(itemAux.getCoordinates().toPoint3D());

                                                        task.setPointEnd(new Point3D(x0, y0, z));

                                                        // Seteamos el flag de casilla con "�rdenes" en cada celda (se usa en el pintado)
                                                        World.getCell(x0, y0, z).setFlagOrders(true);

                                                        task.setParameter(itemAux.getIniHeader());
                                                        task.setFace(MainPanel.itemBuildFace);
                                                        HotPoint hp = new HotPoint(itemAux.getCoordinates(), alPoints);
                                                        task.addHotPoint(hp);
                                                        Game.getWorld().getTaskManager().addTask(task);
                                                        break;
                                                    }
                                                } else {
                                                    // Item encontrado !!! Creamos una tarea de MOVE (como las haul pero persistentes)
                                                    bItemEnElMundo = true;
                                                    Task task = new Task(TYPE.MOVE_AND_LOCK);
                                                    task.setPointIni(itemAux.getCoordinates().toPoint3D());
                                                    task.setPointEnd(new Point3D(x0, y0, z));

                                                    // Seteamos el flag de casilla con "�rdenes" en cada celda (se usa en el pintado)
                                                    World.getCell(x0, y0, z).setFlagOrders(true);

                                                    task.setParameter(itemAux.getIniHeader());
                                                    task.setFace(MainPanel.itemBuildFace);
                                                    HotPoint hp = new HotPoint(itemAux.getCoordinates(), itemAux.getCoordinates());
                                                    task.addHotPoint(hp);
                                                    Game.getWorld().getTaskManager().addTask(task);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Si llega aqu� es que no hemos encontrado item en el mundo, tendremos que construirlo
                if (!bItemEnElMundo) {
                    if (bAvailableForBuilding) {
                        // Aqu� distinguimos entre tarea QUEUE o tarea normal de toda la vida
                        if (getType() == TYPE.QUEUE_AND_PLACE) {
                            // QUEUE
                            Action action = new Action(getParameter());
                            action.setDestinationPoint(Point3DShort.getPoolInstance(x0, y0, z));
                            action.setQueue(ActionManager.getItem(getParameter()).getQueue());
                            action.setQueueData(new QueueData());
                            action.setFace(MainPanel.itemBuildFace);
                            Game.getWorld().getTaskManager().addCustomAction(action, true, false);
                        } else {
                            // Miramos si tenemos el edificio, el m�s cercano (y con la cola de items m�s peque�a) a donde se deja el item
                            ArrayList<Building> buildings = World.getBuildings();
                            Building building;
                            int iBuildingCercano = -1;
                            int iBuildingCercanoSinCola = -1;
                            Point3DShort p3dBuildingAux = null;
                            int iDistance = Utils.MAX_DISTANCE;
                            int iDistanceSinCola = Utils.MAX_DISTANCE;

                            // Buscamos la cola de items m�s peque�a
                            int iMinCola = 1000;
                            for (Building value : buildings) {
                                building = value;
                                if (building.isOperative() && building.getIniHeader().equals(imi.getBuilding())) {
                                    if (building.getItemQueue().size() < iMinCola) {
                                        iMinCola = building.getItemQueue().size();
                                    }
                                }
                            }

                            // Buscamos el edicifio m�s cercano que tenga la cola m�nima obtenida arriba
                            for (int i = 0; i < buildings.size(); i++) {
                                building = buildings.get(i);
                                if (building.isOperative() && building.getIniHeader().equals(imi.getBuilding())) {
                                    if (building.getItemQueue().size() != iMinCola) {
                                        continue;
                                    }

                                    // Buscamos la entrada del edificio
                                    BuildingManagerItem bmi = BuildingManager.getItem(building.getIniHeader());
                                    p3dBuildingAux = bmi.getEntranceBaseCoordinates().merge(building.getCoordinates());

                                    // Caso especial, puentes y ladders (o items que se construyen desde una casilla anterior a destino)
                                    // Miramos accesingpoints ya que se construye desde casillas adyacentes
                                    if (ItemManager.getItem(imi.getIniHeader()).canBeBuiltOnHoles()) {
                                        int iBuildingASZID = World.getCell(p3dBuildingAux).getAstarZoneID();
                                        ArrayList<Point3DShort> alPoints = getAccesingPointsMatchingASZI(x0, y0, z, iBuildingASZID, getType());
                                        if (!alPoints.isEmpty()) {
                                            // Edificio de guais y en la zona del item, miramos la distancia
                                            int iDistanceAux = Utils.getDistance(x0, y0, z, p3dBuildingAux);
                                            if (iDistanceAux < iDistance) {
                                                iDistance = iDistanceAux;
                                                iBuildingCercano = i;
                                            }
                                            if (!building.hasItemsInQueue()) {
                                                if (iDistanceAux < iDistanceSinCola) {
                                                    iDistanceSinCola = iDistanceAux;
                                                    iBuildingCercanoSinCola = i;
                                                }
                                            }
                                        }
                                    } else {
                                        if (World.getCell(p3dBuildingAux).getAstarZoneID() == World.getCell(x0, y0, z).getAstarZoneID()) {
                                            // Edificio de guais y en la zona, miramos la distancia
                                            int iDistanceAux = Utils.getDistance(x0, y0, z, p3dBuildingAux);
                                            if (iDistanceAux < iDistance) {
                                                iDistance = iDistanceAux;
                                                iBuildingCercano = i;
                                            }
                                            if (!building.hasItemsInQueue()) {
                                                if (iDistanceAux < iDistanceSinCola) {
                                                    iDistanceSinCola = iDistanceAux;
                                                    iBuildingCercanoSinCola = i;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (iBuildingCercanoSinCola != -1) {
                                iBuildingCercano = iBuildingCercanoSinCola;
                            }

                            if (iBuildingCercano != -1) {
                                // Tenemos el edificio m�s cercano, lo pillamos y le a�adimos el item en su cola
                                Item item = Item.createItem(imi);
                                item.setCoordinates(x0, y0, z); // Destino del item cuando se construya

                                // Seteamos el flag de casilla con "�rdenes" en cada celda (se usa en el pintado)
                                World.getCell(x0, y0, z).setFlagOrders(true);

                                // Cargamos los prerequisitos
                                item.setPrerequisites(ItemManager.getItem(getParameter()).getPrerequisites());

                                // Lo a�adimos al edificio
                                buildings.get(iBuildingCercano).addItem(item);
                            } else {
                                if (imi.getBuilding() != null) {
                                    MessagesPanel.addMessage(MessagesPanel.TYPE_ANNOUNCEMENT, Messages.getString("Task.21") + imi.getName() + Messages.getString("Task.22") + BuildingManager.getItem(imi.getBuilding()).getName() + "]", ColorGL.ORANGE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                } else {
                                    MessagesPanel.addMessage(MessagesPanel.TYPE_ANNOUNCEMENT, Messages.getString("Task.19") + imi.getName() + Messages.getString("Task.32"), ColorGL.ORANGE); //$NON-NLS-1$ //$NON-NLS-2$
                                }
                            }
                        }
                    }
                }

            }
        } else if (getType() == TYPE.CREATE_AND_PLACE_ROW) {
            // Por cada punto de la fila (row) crearemos una tarea de create_and_place
            // Miramos si la fila es horizontal o vertical
            boolean bHorizontal = (x1 - x0) >= (y1 - y0);

            boolean bToggle3D = false;
            if (MainPanel.tDMouseON) {
                MainPanel.toggle3DMouse();
                bToggle3D = true;
            }

            if (bHorizontal) {
                for (int i = x0; i <= x1; i++) {
                    // Creamos tarea de create_and_place
                    Task taskTmp = new Task(TYPE.CREATE_AND_PLACE);
                    taskTmp.setParameter(getParameter());
                    taskTmp.setPoint(new Point3D(i, (bYSwapped) ? y1 : y0, z));
                }
            } else {
                for (int i = y0; i <= y1; i++) {
                    // Creamos tarea de create_and_place
                    Task taskTmp = new Task(TYPE.CREATE_AND_PLACE);
                    taskTmp.setParameter(getParameter());
                    taskTmp.setPoint(new Point3D((bXSwapped) ? x1 : x0, i, z));
                }
            }

            if (bToggle3D) {
                MainPanel.toggle3DMouse();
            }
        } else if (getType() == TYPE.QUEUE_AND_PLACE_ROW) {
            // Por cada punto de la fila (row) crearemos una tarea de queue_and_place
            // Miramos si la fila es horizontal o vertical
            boolean bHorizontal = (x1 - x0) >= (y1 - y0);

            boolean bToggle3D = false;
            if (MainPanel.tDMouseON) {
                MainPanel.toggle3DMouse();
                bToggle3D = true;
            }

            if (bHorizontal) {
                for (int i = x0; i <= x1; i++) {
                    // Creamos tarea de queue_and_place
                    Task taskTmp = new Task(TYPE.QUEUE_AND_PLACE);
                    taskTmp.setParameter(getParameter());
                    taskTmp.setPoint(new Point3D(i, (bYSwapped) ? y1 : y0, z));
                }
            } else {
                for (int i = y0; i <= y1; i++) {
                    // Creamos tarea de create_and_place
                    Task taskTmp = new Task(TYPE.QUEUE_AND_PLACE);
                    taskTmp.setParameter(getParameter());
                    taskTmp.setPoint(new Point3D((bXSwapped) ? x1 : x0, i, z));
                }
            }

            if (bToggle3D) {
                MainPanel.toggle3DMouse();
            }
        } else if (getType() == TYPE.QUEUE_AND_PLACE_AREA) {
            // Por cada punto de la fila (row) y columna (col) crearemos una tarea de queue_and_place

            boolean bToggle3D = false;
            if (MainPanel.tDMouseON) {
                MainPanel.toggle3DMouse();
                bToggle3D = true;
            }

            for (int i = x0; i <= x1; i++) {
                for (int j = y0; j <= y1; j++) {
                    // Creamos tarea de queue_and_place
                    Task taskTmp = new Task(TYPE.QUEUE_AND_PLACE);
                    taskTmp.setParameter(getParameter());
                    taskTmp.setPoint(new Point3D(i, j, z));
                }
            }

            if (bToggle3D) {
                MainPanel.toggle3DMouse();
            }
        }

        // Si no hay hotpoints es que la tarea ya est� terminada
        if (getHotPoints().isEmpty()) {
            // Tarea finalizada
            setFinished(true);
        }
    }

    private void addHotPoint(HotPoint hotPoint) {
        if (hotPoints == null) {
            hotPoints = new ArrayList<>();
        }
        hotPoints.add(hotPoint);
    }

    public int getMaxCitizens() {
        return maxCitizens;
    }

    /**
     * Setea el m�ximo de aldeanos para la tarea. Excepciones: Si es tarea de
     * construcci�n s�lo permite 1 aldeano
     *
     * @param maxCitizens M�ximo de aldeanos
     */
    public void setMaxCitizens(int maxCitizens) {
        if (maxCitizens > 1 && getType() == TYPE.BUILD) {
            this.maxCitizens = 1;
        } else {
            this.maxCitizens = maxCitizens;
        }
    }

    /**
     * Indica si el item pasado est� en uso. Vamos, que otro aldeano va a por �l
     *
     * @param item
     * @return true si el item pasado est� en uso
     */
    private boolean itemInUse(Item item) {
        Task task;
        for (int i = 0; i < World.getCitizenIDs().size(); i++) {
            task = ((Citizen) World.getLivingEntityByID(World.getCitizenIDs().get(i))).getCurrentTask();
            if (task != null) {
                // Aldeano con tarea, veamos si implica el uso del item pasado
                if (task.getType() == TYPE.HAUL || task.getType() == TYPE.MOVE_AND_LOCK || task.getType() == TYPE.PUT_IN_CONTAINER) {
                    if (task.getPointIni().equals(item.getCoordinates())) {
                        return true;
                    }
                }
            }
        }
        for (int i = 0; i < World.getSoldierIDs().size(); i++) {
            task = ((Citizen) World.getLivingEntityByID(World.getSoldierIDs().get(i))).getCurrentTask();
            if (task != null) {
                // Aldeano con tarea, veamos si implica el uso del item pasado
                if (task.getType() == TYPE.HAUL || task.getType() == TYPE.MOVE_AND_LOCK || task.getType() == TYPE.PUT_IN_CONTAINER) {
                    if (task.getPointIni().equals(item.getCoordinates())) {
                        return true;
                    }
                }
            }
        }

        // Si llega aqu� es que ning�n aldeano tiene tarea con ese item, vamos a mirar que no est� pendiente en el taskManager
        ArrayList<TaskManagerItem> alTasks = Game.getWorld().getTaskManager().getTaskItems();
        for (TaskManagerItem taskManagerItem : alTasks) {
            task = taskManagerItem.getTask();
            if (task.getType() == TYPE.HAUL || task.getType() == TYPE.MOVE_AND_LOCK || task.getType() == TYPE.PUT_IN_CONTAINER) {
                if (task.getPointIni().equals(item.getCoordinates())) {
                    return true;
                }
            }
        }

        alTasks = Game.getWorld().getTaskManager().getTaskItemsTemp();
        for (TaskManagerItem alTask : alTasks) {
            task = alTask.getTask();
            if (task.getType() == TYPE.HAUL || task.getType() == TYPE.MOVE_AND_LOCK || task.getType() == TYPE.PUT_IN_CONTAINER) {
                if (task.getPointIni().equals(item.getCoordinates())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Indica si, para un punto de una tarea ya existe otra tarea con el mismo
     * punto y el mismo NO est� finished Se usa para DIG/CHOP/MINE/CUSTOM
     * solamente
     *
     * @param task
     * @param p3d
     * @return true si para un punto de una tarea ya existe otra tarea con el
     * mismo punto
     */
    private boolean existTaskPoint(TYPE task, Point3DShort p3d) {
        ArrayList<TaskManagerItem> alTasks = Game.getWorld().getTaskManager().getTaskItems();
        ArrayList<HotPoint> alHPs;
        for (TaskManagerItem alTask : alTasks) {
            if (alTask.getTask().getType() == task) {
                alHPs = alTask.getTask().getHotPoints();
                for (HotPoint alHP : alHPs) {
                    if (alHP.getHotPoint().equals(p3d) && !alHP.isFinished()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setTile(Tile tile, int iconType) {
        this.tile = tile;
        this.iconType = iconType;
    }

    public Tile getTile() {
        return tile;
    }

    public int getIconType() {
        return iconType;
    }

    /**
     * Devuelve el punto indicado ir para realizar la tarea
     *
     * @return el punto indicado ir para realizar la tarea
     */
    public HotPoint getHotPoint(int hotPointIndex) {
        return getHotPoints().get(hotPointIndex);
    }

    public ArrayList<HotPoint> getHotPoints() {
        if (hotPoints == null) {
            hotPoints = new ArrayList<>();
        }
        return hotPoints;
    }

    public void setHotPoints(ArrayList<HotPoint> hotPoints) {
        this.hotPoints = hotPoints;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readInt();
        task = (TYPE) in.readObject();
        state = in.readInt();
        pointIni = (Point3D) in.readObject();
        pointEnd = (Point3D) in.readObject();
        hotPoints = (ArrayList<HotPoint>) in.readObject();
        maxCitizens = in.readInt();
        parameter = (String) in.readObject();
        parameter2 = (String) in.readObject();
        finished = in.readBoolean();

        if (Game.SAVEGAME_LOADING_VERSION >= Game.SAVEGAME_V14) {
            face = in.readInt();
        } else {
            face = Item.FACE_WEST;
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeObject(task);
        out.writeInt(state);
        out.writeObject(pointIni);
        out.writeObject(pointEnd);
        out.writeObject(hotPoints);
        out.writeInt(maxCitizens);
        out.writeObject(parameter);
        out.writeObject(parameter2);
        out.writeBoolean(finished);
        out.writeInt(face);
    }
}
