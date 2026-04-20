package xaos.campaign;

import java.util.ArrayList;


/**
 *
 * Aqu� se guardan los datos de una campa�a. ID, Nombre, misiones que la componen, ...
 *
 */
public class CampaignData {

    private String id;
    private String name;
    private boolean tutorial;

    private ArrayList<MissionData> missions;


    public CampaignData(String sID) {
        setId(sID);
        setMissions(new ArrayList<MissionData>());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTutorial() {
        return tutorial;
    }

    public void setTutorial(String sTutorial) {
        setTutorial(Boolean.parseBoolean(sTutorial));
    }

    public void setTutorial(boolean tutorial) {
        this.tutorial = tutorial;
    }

    public ArrayList<MissionData> getMissions() {
        return missions;
    }

    public void setMissions(ArrayList<MissionData> missions) {
        this.missions = missions;
    }
}
