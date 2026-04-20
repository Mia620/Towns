package xaos.tasks;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

public final class TaskManagerItem implements Externalizable {

    private static final long serialVersionUID = -6261978696270872236L;

    private Task task;
    private ArrayList<Integer> listCitizens;

    public TaskManagerItem() {
    }

    public TaskManagerItem(Task task) {
        setTask(task);
        listCitizens = new ArrayList<>();
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public ArrayList<Integer> getListCitizens() {
        return listCitizens;
    }

    public void setListCitizens(ArrayList<Integer> listCitizens) {
        this.listCitizens = listCitizens;
    }

    public boolean containsCitizen(int citizenID) {
        return listCitizens.contains(citizenID);
    }

    public void addCitizen(int citizenID) {
        listCitizens.add(citizenID);
    }

    public void removeCitizen(Integer citizenID) {
        listCitizens.remove(citizenID);
    }

    public String toString() {
        return "Task\n" + task.toString() +
                "\n Cits: " +
                listCitizens.size() +
                "\n";
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        task = (Task) in.readObject();
        listCitizens = (ArrayList<Integer>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(task);
        out.writeObject(listCitizens);
    }
}
