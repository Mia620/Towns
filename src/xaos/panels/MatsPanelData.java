package xaos.panels;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xaos.Towns;
import xaos.main.Game;
import xaos.tiles.Tile;
import xaos.tiles.entities.items.ItemManager;
import xaos.utils.Log;
import xaos.utils.Messages;
import xaos.utils.Utils;
import xaos.utils.UtilsXML;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;

public class MatsPanelData {

    public static int numGroups;
    public static ArrayList<String> iconGroups;
    public static ArrayList<String> idGroups;
    public static ArrayList<String> nameGroups;
    public static ArrayList<ArrayList<Tile>> tileGroups;
    public static ArrayList<Integer> indexTileGroups;

    public static void loadGroups() {
        if (iconGroups != null) {
            return;
        }

        numGroups = 0;
        iconGroups = new ArrayList<>();
        idGroups = new ArrayList<>();
        nameGroups = new ArrayList<>();
        indexTileGroups = new ArrayList<>();
        tileGroups = new ArrayList<>();

        loadXMLGroups(Towns.getPropertiesString("DATA_FOLDER") + "matspanel.xml", true); //$NON-NLS-1$ //$NON-NLS-2$

        // Mods
        File fUserFolder = new File(Game.getUserFolder());
        if (!fUserFolder.exists() || !fUserFolder.isDirectory()) {
            return;
        }

        ArrayList<String> alMods = Game.getModsLoaded();
        if (!alMods.isEmpty()) {
            for (String alMod : alMods) {
                String sModActionsPath = fUserFolder.getAbsolutePath() + FileSystems.getDefault().getSeparator() + Game.MODS_FOLDER1 + FileSystems.getDefault().getSeparator() + alMod + FileSystems.getDefault().getSeparator() + Towns.getPropertiesString("DATA_FOLDER") + "matspanel.xml"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                File fIni = new File(sModActionsPath);
                if (fIni.exists()) {
                    loadXMLGroups(sModActionsPath, false);
                }
            }
        }
    }

    private static void loadXMLGroups(String sXMLPath, boolean bLoadingMain) {

        try {
            Document doc = UtilsXML.loadXMLFile(sXMLPath); //$NON-NLS-1$ //$NON-NLS-2$

            // Tenemos el documento XML parseado
            // Lo recorremos entero y vamos a�adiendo los grupos
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            Node node;
            String sAux, sIniHeader;
            for (int i = 0; i < nodeList.getLength(); i++) {
                node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    sIniHeader = node.getNodeName();
                    if (sIniHeader != null && sIniHeader.equalsIgnoreCase("DELETE")) {
                        // Miramos que ID quiere borrar
                        if (node.getAttributes() != null && node.getAttributes().getNamedItem("id") != null) {
                            String sIDToDelete = node.getAttributes().getNamedItem("id").getNodeValue();
                            if (sIDToDelete != null) {
                                int iIndex = idGroups.indexOf(sIDToDelete);
                                if (iIndex != -1) {
                                    numGroups--;
                                    iconGroups.remove(iIndex);
                                    idGroups.remove(iIndex);
                                    nameGroups.remove(iIndex);
                                    indexTileGroups.remove(iIndex);
                                    tileGroups.remove(iIndex);
                                }

                            }
                        }
                        continue;
                    }

                    // ID
                    sAux = UtilsXML.getChildValue(node.getChildNodes(), "id"); //$NON-NLS-1$
                    if (sAux == null || sAux.trim().isEmpty()) {
                        Log.log(Log.LEVEL_ERROR, Messages.getString("MatsPanelData.5"), "MatsPanelData"); //$NON-NLS-1$ //$NON-NLS-2$
                        Game.exit();
                    }

                    int indexExists = idGroups.indexOf(sAux);

                    // Mod cambiando valores de un item que ya existe?
                    boolean bModChangingValues = ((indexExists != -1) && !bLoadingMain);

                    if (bModChangingValues) {
                        idGroups.set(indexExists, sAux);
                    } else {
                        idGroups.add(sAux);
                    }

                    // Name
                    sAux = UtilsXML.getChildValue(node.getChildNodes(), "name"); //$NON-NLS-1$
                    if (bModChangingValues) {
                        if (sAux != null && !sAux.trim().isEmpty()) {
                            nameGroups.set(indexExists, sAux);
                        }
                    } else {
                        if (sAux == null || sAux.trim().isEmpty()) {
                            Log.log(Log.LEVEL_ERROR, Messages.getString("MatsPanelData.0"), "MatsPanelData"); //$NON-NLS-1$ //$NON-NLS-2$
                            Game.exit();
                        }
                        nameGroups.add(sAux);
                    }

                    // Icon
                    sAux = UtilsXML.getChildValue(node.getChildNodes(), "icon"); //$NON-NLS-1$
                    if (bModChangingValues) {
                        if (sAux != null && !sAux.trim().isEmpty()) {
                            iconGroups.set(indexExists, sAux);
                            // Index
                            indexTileGroups.set(indexExists, 0);
                        }
                    } else {
                        if (sAux == null || sAux.trim().isEmpty()) {
                            Log.log(Log.LEVEL_ERROR, Messages.getString("MatsPanelData.2"), "MatsPanelData"); //$NON-NLS-1$ //$NON-NLS-2$
                            Game.exit();
                        }
                        iconGroups.add(sAux);
                        // Index
                        indexTileGroups.add(0);
                    }

                    // Types
                    ArrayList<String> alTypes = Utils.getArray(UtilsXML.getChildValue(node.getChildNodes(), "types")); //$NON-NLS-1$
                    if (alTypes == null || alTypes.isEmpty()) {
                        if (!bModChangingValues) {
                            Log.log(Log.LEVEL_ERROR, Messages.getString("MatsPanelData.4"), "MatsPanelData"); //$NON-NLS-1$ //$NON-NLS-2$
                            Game.exit();
                        }
                    }

                    ArrayList<String> alItems = new ArrayList<>();
                    ArrayList<String> alItemNames = new ArrayList<>();
                    if (alTypes != null && !alTypes.isEmpty()) {
                        // Para cada type, buscamos los iconos y creamos un tile
                        for (String alType : alTypes) {
                            alItems.addAll(ItemManager.getItemsByType(alType));
                        }
                        if (alItems.isEmpty()) {
                            Log.log(Log.LEVEL_ERROR, Messages.getString("MatsPanelData.6"), "MatsPanelData"); //$NON-NLS-1$ //$NON-NLS-2$
                            Game.exit();
                        }
                        // Ordenamos alfab�ticamente
                        for (String alItem : alItems) {
                            alItemNames.add(ItemManager.getItem(alItem).getName());
                        }
                        for (int s1 = 0; s1 < (alItemNames.size() - 1); s1++) {
                            for (int s2 = (s1 + 1); s2 < alItemNames.size(); s2++) {
                                if (alItemNames.get(s1).compareTo(alItemNames.get(s2)) > 0) {
                                    sAux = alItemNames.get(s1);
                                    alItemNames.set(s1, alItemNames.get(s2));
                                    alItemNames.set(s2, sAux);

                                    sAux = alItems.get(s1);
                                    alItems.set(s1, alItems.get(s2));
                                    alItems.set(s2, sAux);
                                }
                            }
                        }
                    }

                    // Creamos los Tiles
                    ArrayList<Tile> alTiles = new ArrayList<>(alItems.size());
                    for (String alItem : alItems) {
                        alTiles.add(new Tile(alItem));
                    }
                    if (bModChangingValues) {
                        if (!alTiles.isEmpty()) {
                            tileGroups.set(indexExists, alTiles);
                        }
                    } else {
                        tileGroups.add(alTiles);
                    }

                    // Num groups
                    if (!bModChangingValues) {
                        numGroups++;
                    }
                }
            }
        } catch (Exception e) {
            Log.log(Log.LEVEL_ERROR, Messages.getString("MatsPanelData.8") + e + "]", "MatsPanelData"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            Game.exit();
        }

        if (numGroups == 0) {
            Log.log(Log.LEVEL_ERROR, Messages.getString("MatsPanelData.11"), "MatsPanelData"); //$NON-NLS-1$ //$NON-NLS-2$
            Game.exit();
        }
    }

    public static void clear() {
        numGroups = 0;
        iconGroups = null;
        idGroups = null;
        nameGroups = null;
        tileGroups = null;
        indexTileGroups = null;
    }
}
