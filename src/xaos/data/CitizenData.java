package xaos.data;

import xaos.actions.ActionPriorityManager;
import xaos.main.Game;
import xaos.tiles.entities.living.LivingEntity;
import xaos.tiles.entities.living.LivingEntityManagerItem;
import xaos.utils.Messages;
import xaos.utils.Names;
import xaos.utils.Utils;
import xaos.utils.UtilsIniHeaders;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

public class CitizenData implements Externalizable {

    // Se usa para la "animaci�n" cuando el aldeano come o duerme (por ejemplo)
    public static final int MAX_BLINK_ANIMATION_TURNS = 10;
    private static final long serialVersionUID = -5585963825804884015L;
    private String fullName;
    private int zoneID;
    private int happiness; // De 0 a 100
    private int happinessWorkCounter;
    private int happinessIdleCounter;
    private int happinessWorkCounterMax;
    private int happinessIdleCounterMax;
    private int boostCounter; // Boost en sus actividades debido a un BOSS_AROUND solider (por ejemplo)

    // Turnos (se usa para la "animaci�n" (cada X pasos se resetea a 0) (tambi�n al pillar un objeto, para que no todos los aldeanos tengan la misma animaci�n)
    private int blinkAnimationTurns = 0;

    // Hungry
    private int maxHungry; // Cada aldeano tiene un aguante distinto, aqu� lo guardamos
    private int hungry; // Si llega a 0 es que tiene hambre, si llega a -TIME_TURNS_1_DAY muere de hambre
    private int hungryEating; // Cuando tiene hambre se pone a 0 y luego a cada turno ir� sumando 1 hasta llegar a un tope, en ese momento ya habr� comido

    // Sleep
    private int maxSleep; // Cada aldeano tiene un aguante distinto, aqu� lo guardamos
    private int sleep; // Si llega a 0 deja todo y se pone a dormir
    private int sleepSleeping; // Cuando tiene sue�o se pone a 0 y luego a cada turno ir� sumando 1 hasta llegar a un tope (6h), en ese momento ya habr� dormido

    // Heal
    private int healHealing; // Cuando va a curarse se pone a 0, si est� en un hospital se ir� sumando 1 hasta llegar a un tope (1h), en ese momento se curar� 1PV

    // Carrying
    private CarryingData carryingData;

    // Group ID
    private int groupID = -1;

    // Jobs denied
    private ArrayList<Integer> jobsDenied;

    // Work/idle counters
    private int workCounterPCT;
    private int idleCounterPCT;

    public CitizenData() {
    }

    public CitizenData(LivingEntityManagerItem lemi) {
        StringBuilder sFullName = new StringBuilder();
        if (lemi.getNamePoolTag() != null) {
            sFullName.append(Names.getName(lemi.getNamePoolTag(), Game.getWorld().getCampaignID(), Game.getWorld().getMissionID()));

            if (lemi.getSurnamePoolTag() != null) {
                sFullName.append(" "); //$NON-NLS-1$

                // El nick s�lo aparece 1 de cada 100
                if (Utils.getRandomBetween(1, 100) == 1) {
                    sFullName.append("'"); //$NON-NLS-1$
                    sFullName.append(Names.getName("nick", Game.getWorld().getCampaignID(), Game.getWorld().getMissionID())); //$NON-NLS-1$
                    sFullName.append("' "); //$NON-NLS-1$
                }

                sFullName.append(Names.getName(lemi.getSurnamePoolTag(), Game.getWorld().getCampaignID(), Game.getWorld().getMissionID()));
            }

            setFullName(sFullName.toString());
        } else {
            setFullName(lemi.getName());
        }

        if (lemi.getType() == LivingEntity.TYPE_HERO) {
            setFullName(getFullName() + Messages.getString("CitizenData.3")); //$NON-NLS-1$
        }

        // Hungry / Sleep
        setMaxHungry(Utils.launchDice(lemi.getMaxHungryTurns()));
        setMaxSleep(Utils.launchDice(lemi.getMaxSleepTurns()));
        setHungry(getMaxHungry());
        setSleep(Utils.getRandomBetween(getMaxSleep() / 2, getMaxSleep() * 2)); // Al crearse randomizo un poco para que no vayan a dormir a la vez

        // Work/idle counters
        setWorkCounterPCT(Utils.launchDice(lemi.getWorkCounterPCT()));
        setIdleCounterPCT(Utils.launchDice(lemi.getIdleCounterPCT()));

        // Happiness
        setHappiness(35);
        setHappinessWorkCounter(calculateWorkCounter(getSleep()));
        setHappinessIdleCounter(calculateIdleCounter(getSleep()));
        setHappinessWorkCounterMax(getHappinessWorkCounter());
        setHappinessIdleCounterMax(getHappinessIdleCounter());

        setCarryingData(new CarryingData());
        setJobsDenied(null);
        setGroupID(-1);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public int getHappiness() {
        return happiness;
    }

    public void setHappiness(int happiness) {
        if (happiness > 100) {
            happiness = 100;
        } else if (happiness < 0) {
            happiness = 0;
        }
        this.happiness = happiness;
    }

    public int getHappinessWorkCounter() {
        return happinessWorkCounter;
    }

    public void setHappinessWorkCounter(int happiness) {
        if (happiness < 0) {
            this.happinessWorkCounter = 0;
        } else {
            this.happinessWorkCounter = happiness;
        }
    }

    public int getHappinessIdleCounter() {
        return happinessIdleCounter;
    }

    public void setHappinessIdleCounter(int happiness) {
        if (happiness < 0) {
            this.happinessIdleCounter = 0;
        } else {
            this.happinessIdleCounter = happiness;
        }
    }

    public int getHappinessWorkCounterMax() {
        return happinessWorkCounterMax;
    }

    public void setHappinessWorkCounterMax(int happiness) {
        if (happiness < 0) {
            this.happinessWorkCounterMax = 0;
        } else {
            this.happinessWorkCounterMax = happiness;
        }
    }

    public int getHappinessIdleCounterMax() {
        return happinessIdleCounterMax;
    }

    public void setHappinessIdleCounterMax(int happiness) {
        if (happiness < 0) {
            this.happinessIdleCounterMax = 0;
        } else {
            this.happinessIdleCounterMax = happiness;
        }
    }

    /**
     * @return the boostCounter
     */
    public int getBoostCounter() {
        return boostCounter;
    }

    /**
     * @param boostCounter the boostCounter to set
     */
    public void setBoostCounter(int boostCounter) {
        this.boostCounter = boostCounter;
    }

    /**
     * @return the blinkAnimationTurns
     */
    public int getBlinkAnimationTurns() {
        return blinkAnimationTurns;
    }

    /**
     * @param blinkAnimationTurns the blinkAnimationTurns to set
     */
    public void setBlinkAnimationTurns(int blinkAnimationTurns) {
        this.blinkAnimationTurns = blinkAnimationTurns;
    }

    public int getHungry() {
        return hungry;
    }

    public void setHungry(int hungry) {
        this.hungry = hungry;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public int getMaxHungry() {
        return maxHungry;
    }

    public void setMaxHungry(int hungry) {
        this.maxHungry = hungry;
    }

    public int getMaxSleep() {
        return maxSleep;
    }

    public void setMaxSleep(int sleep) {
        this.maxSleep = sleep;
    }

    public int getHungryEating() {
        return hungryEating;
    }

    public void setHungryEating(int hungryEating) {
        this.hungryEating = hungryEating;
    }

    public int getSleepSleeping() {
        return sleepSleeping;
    }

    public void setSleepSleeping(int sleepSleeping) {
        this.sleepSleeping = sleepSleeping;
    }

    public int getHealHealing() {
        return healHealing;
    }

    public void setHealHealing(int healHealing) {
        this.healHealing = healHealing;
    }

    /**
     * @return the carryingData
     */
    public CarryingData getCarryingData() {
        return carryingData;
    }

    /**
     * @param carryingData the carryingData to set
     */
    public void setCarryingData(CarryingData carryingData) {
        this.carryingData = carryingData;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public ArrayList<Integer> getJobsDenied() {
        return jobsDenied;
    }

    public void setJobsDenied(ArrayList<Integer> jobsDenied) {
        this.jobsDenied = jobsDenied;
    }

    public void addDeniedJob(String sJob) {
        addDeniedJob(UtilsIniHeaders.getIntIniHeader(sJob));
    }

    public void addDeniedJob(int iJob) {
        if (jobsDenied == null) {
            jobsDenied = new ArrayList<>(1);
            jobsDenied.add(iJob);
        } else {
            Integer iObj = iJob;
            if (!jobsDenied.contains(iObj)) {
                jobsDenied.add(iObj);
            }
        }
    }

    public void addDeniedJobs(ArrayList<Integer> jobsD) {
        if (jobsD == null) {
            removeAllDeniedJobs();
        } else {
            for (Integer integer : jobsD) {
                addDeniedJob(integer);
            }
        }
    }

    public void removeAllDeniedJobs() {
        jobsDenied = null;
    }

    public void addAllDeniedJobs() {
        ArrayList<String> alPriorities = ActionPriorityManager.getPrioritiesList();
        if (alPriorities != null) {
            for (String alPriority : alPriorities) {
                addDeniedJob(alPriority);
            }
        }
    }

    public void removeDeniedJob(String sJob) {
        removeDeniedJob(UtilsIniHeaders.getIntIniHeader(sJob));
    }

    public void removeDeniedJob(int iJob) {
        if (jobsDenied == null) {
            return;
        }

        Integer iObj = iJob;
        jobsDenied.remove(iObj);

        if (jobsDenied.isEmpty()) {
            jobsDenied = null;
        }
    }

    public boolean containsDeniedJob(String sJob) {
        return containsDeniedJob(UtilsIniHeaders.getIntIniHeader(sJob));
    }

    public boolean containsDeniedJob(int iJob) {
        if (jobsDenied == null) {
            return false;
        }

        Integer iObj = iJob;
        return jobsDenied.contains(iObj);
    }

    public int getWorkCounterPCT() {
        return workCounterPCT;
    }

    public void setWorkCounterPCT(int workCounterPCT) {
        this.workCounterPCT = workCounterPCT;
    }

    public int getIdleCounterPCT() {
        return idleCounterPCT;
    }

    public void setIdleCounterPCT(int idleCounterPCT) {
        this.idleCounterPCT = idleCounterPCT;
    }

    public int calculateWorkCounter(int iAwakeTurns) {
        int iReturn = (iAwakeTurns * workCounterPCT) / 100;
        if (iReturn < 100) {
            iReturn = 100;
        }

        return iReturn;
    }

    public int calculateIdleCounter(int iAwakeTurns) {
        int iReturn = (iAwakeTurns * idleCounterPCT) / 100;
        if (iReturn < 100) {
            iReturn = 100;
        }

        return iReturn;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        fullName = (String) in.readObject();
        zoneID = in.readInt();
        happiness = in.readInt();
        boostCounter = in.readInt();
        blinkAnimationTurns = in.readInt();
        maxHungry = in.readInt();
        hungry = in.readInt();
        hungryEating = in.readInt();
        maxSleep = in.readInt();
        sleep = in.readInt();
        sleepSleeping = in.readInt();
        healHealing = in.readInt();
        carryingData = (CarryingData) in.readObject();

        if (Game.SAVEGAME_LOADING_VERSION >= Game.SAVEGAME_V11) {
            ArrayList<String> alStrings = (ArrayList<String>) in.readObject();
            if (alStrings == null || alStrings.isEmpty()) {
                jobsDenied = null;
            } else {
                jobsDenied = new ArrayList<>(alStrings.size());
                for (String alString : alStrings) {
                    jobsDenied.add(Integer.valueOf(UtilsIniHeaders.getIntIniHeader(alString)));
                }
            }
        }

        if (Game.SAVEGAME_LOADING_VERSION >= Game.SAVEGAME_V11) {
            groupID = in.readInt();
        } else {
            groupID = -1; // Sin grupo
        }

        if (Game.SAVEGAME_LOADING_VERSION >= Game.SAVEGAME_V13) {
            workCounterPCT = in.readInt();
            idleCounterPCT = in.readInt();
        } else {
            workCounterPCT = 25;
            idleCounterPCT = 30;
        }

        if (Game.SAVEGAME_LOADING_VERSION >= Game.SAVEGAME_V13) {
            happinessWorkCounter = in.readInt();
            happinessIdleCounter = in.readInt();
            happinessWorkCounterMax = in.readInt();
            happinessIdleCounterMax = in.readInt();
        } else {
            happinessWorkCounter = calculateWorkCounter(maxSleep);
            happinessIdleCounter = calculateIdleCounter(maxSleep);
            happinessWorkCounterMax = happinessWorkCounter;
            happinessIdleCounterMax = happinessIdleCounter;
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(fullName);
        out.writeInt(zoneID);
        out.writeInt(happiness);
        out.writeInt(boostCounter);
        out.writeInt(blinkAnimationTurns);
        out.writeInt(maxHungry);
        out.writeInt(hungry);
        out.writeInt(hungryEating);
        out.writeInt(maxSleep);
        out.writeInt(sleep);
        out.writeInt(sleepSleeping);
        out.writeInt(healHealing);
        out.writeObject(carryingData);
        // Pasamos a Strings
        out.writeObject(UtilsIniHeaders.getArrayStrings(jobsDenied));

        out.writeInt(groupID);

        out.writeInt(workCounterPCT);
        out.writeInt(idleCounterPCT);

        out.writeInt(happinessWorkCounter);
        out.writeInt(happinessIdleCounter);
        out.writeInt(happinessWorkCounterMax);
        out.writeInt(happinessIdleCounterMax);
    }
}
