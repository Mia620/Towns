package xaos.stockpiles;

import java.util.ArrayList;

public class StockpileTempData {

    private final ArrayList<String> alElements; // iniheader del item
    private final ArrayList<Boolean> alElementsStatus; // Status, true enabled, false disabled

    public StockpileTempData() {
        alElements = new ArrayList<>();
        alElementsStatus = new ArrayList<>();
    }

    public void addElement(String sElement, boolean bStatus) {
        alElements.add(sElement);
        alElementsStatus.add(bStatus);
    }

    public ArrayList<String> getAlElements() {
        return alElements;
    }

    public ArrayList<Boolean> getAlElementsStatus() {
        return alElementsStatus;
    }
}
