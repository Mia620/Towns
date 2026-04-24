package xaos.tasks;

import xaos.actions.Action;
import xaos.actions.ActionManagerItem;
import xaos.actions.QueueItem;
import xaos.main.Game;
import xaos.main.World;
import xaos.tiles.entities.items.Item;
import xaos.tiles.entities.living.Citizen;
import xaos.tiles.entities.living.LivingEntity;
import xaos.utils.Point3DShort;

import java.util.ArrayList;

public class TaskUtil {
    private static boolean itemInUseCheckCoords(Task task, Item item) {
        switch (task.getType()) {
            case Task.TYPE.HAUL, Task.TYPE.MOVE_AND_LOCK, Task.TYPE.PUT_IN_CONTAINER:
                if (task.getPointIni().equals(item.getCoordinates())) {
                    return true;
                }
        }
        return false;
    }

    /**
     * Indica si el item pasado est� en uso. Vamos, que otro aldeano va a por �l
     *
     * @param item
     * @return true si el item pasado est� en uso
     */
    public static boolean itemInUse(Item item) {
        Task task;
        for (var id : World.getCitizenIDs()) {
            task = ((Citizen) World.getLivingEntityByID(id)).getCurrentTask();
            if (task != null) {
                // Aldeano con tarea, veamos si implica el uso del item pasado
                return itemInUseCheckCoords(task, item);
            }
        }
        for (var id : World.getSoldierIDs()) {
            task = ((Citizen) World.getLivingEntityByID(id)).getCurrentTask();
            if (task != null) {
                // Aldeano con tarea, veamos si implica el uso del item pasado
                return itemInUseCheckCoords(task, item);
            }
        }

        // Si llega aqu� es que ning�n aldeano tiene tarea con ese item, vamos a mirar que no est� pendiente en el taskManager
        ArrayList<TaskManagerItem> alTasks = Game.getWorld().getTaskManager().getTaskItems();
        for (TaskManagerItem taskManagerItem : alTasks) {
            task = taskManagerItem.getTask();
            return itemInUseCheckCoords(task, item);
        }

        alTasks = Game.getWorld().getTaskManager().getTaskItemsTemp();
        for (TaskManagerItem alTask : alTasks) {
            task = alTask.getTask();
            return itemInUseCheckCoords(task, item);
        }

        return false;
    }

    public static void cancelTask(ArrayList<Integer> alCitizens, Point3DShort point) {
        for (Integer alCitizen : alCitizens) {
            var citizen = (Citizen) World.getLivingEntityByID(alCitizen);
            if (citizen == null) {
                continue;
            }
            var action = citizen.getCurrentCustomAction();
            if (action != null && citizen.getCurrentTask() != null) {
                // Colas
                if ((action.getDestinationPoint() != null && action.getDestinationPoint().equals(point)) || (action.getTerrainPoint() != null && action.getTerrainPoint().equals(point))) {
                    citizen.getCurrentTask().setFinished(true);
                    Game.getWorld().getTaskManager().removeCitizen(citizen);
                    World.getCell(point).setFlagOrders(false);
                }
            }
        }
    }

    public static void checkCancelActions(ArrayList<HotPoint> hotPoints, ArrayList<Action> alActions, boolean checkCitizens) {
        ArrayList<Integer> alCitizens = World.getCitizenIDs();
        ArrayList<Integer> alSoldiers = World.getSoldierIDs();

        for (HotPoint hotpoint : hotPoints) {
            var point = hotpoint.getPoint();

            // Acciones en cola
            for (int x = (alActions.size() - 1); x >= 0; x--) {
                var action = alActions.get(x);
                var destPoint = action.getDestinationPoint();
                var terrainPoint = action.getTerrainPoint();

                boolean bRemover = false;
                // Colas
                if (destPoint != null && destPoint.equals(point)) {
                    bRemover = true;
                } else {
                    if (terrainPoint != null && terrainPoint.equals(point)) {
                        bRemover = true;
                    } else {
                        // Create item desde item
                        if (action.getEntityID() != -1) {
                            Item item = Item.getItemByID(action.getEntityID());
                            if (item != null && item.getCoordinates().equals(point)) {
                                bRemover = true;
                            } else {
                                // Livings
                                LivingEntity le = World.getLivingEntityByID(action.getEntityID());
                                if (le != null && le.getCoordinates().equals(point)) {
                                    bRemover = true;
                                }
                            }
                        }
                    }
                }

                if (bRemover) {
                    Action actionRemoved = alActions.remove(x);
                    Game.getWorld().getTaskManager().removeFromProductionPanelRegular(actionRemoved.getId());
                    World.getCell(point).setFlagOrders(false);
                }
            }

            if (checkCitizens) {
                // Acciones de aldeanos
                TaskUtil.cancelTask(alCitizens, point);
                // Acciones de soldiers... necesario?
                TaskUtil.cancelTask(alSoldiers, point);
            }
        }
    }

    public static void checkCancelTask(ArrayList<HotPoint> hotPoints, ArrayList<TaskManagerItem> alTasks) {
        ArrayList<HotPoint> alPoints;
        Point3DShort p3dCancel, p3dTask;
        Task task;
        for (TaskManagerItem alTask : alTasks) {
            task = alTask.getTask();
            switch (task.getType()) {
                case Task.TYPE.MINE, Task.TYPE.MINE_LADDER:
                    // Tarea mine/dig
                    // Miramos los puntos
                    alPoints = task.getHotPoints();
                    for (int p = 0; p < alPoints.size(); p++) {
                        if (!alPoints.get(p).isFinished()) {
                            p3dTask = alPoints.get(p).getPoint();

                            for (HotPoint hotPoint : hotPoints) {
                                p3dCancel = hotPoint.getPoint();

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

    public static String getQueueSItem(ActionManagerItem ami) {
        ArrayList<QueueItem> alQueue = ami.getQueue();
        String sItem = null;
        for (int i = alQueue.size() - 1; i >= 0; i--) {
            var type = alQueue.get(i).getType();
            sItem = switch (type) {
                case QueueItem.TYPE_CREATE_ITEM, QueueItem.TYPE_CREATE_ITEM_BY_TYPE ->
                        alQueue.get(i).getValue();
                default -> sItem;
            };
        }
        return sItem;
    }
}
