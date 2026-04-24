package xaos;

import xaos.main.Game;
import xaos.property.Property;
import xaos.property.PropertyFile;
import xaos.utils.Log;
import xaos.utils.Messages;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Properties;

public final class Towns {

    // Properties in ini files
    public static Properties propertiesMain;
    public static Properties propertiesGraphics;

    public static void main(String[] args) {

        // Lanzamos la ventana principal
        try {
            new Game();
        } catch (Throwable t) {
            try {
                Writer writer = new StringWriter();
                PrintWriter pw = new PrintWriter(writer);
                t.printStackTrace(pw);
                pw.close();
                writer.close();

                Log.log(Log.LEVEL.ERROR, "Error Code [" + Game.iError + "]", "Towns"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                Log.log(Log.LEVEL.ERROR, writer.toString(), "Towns"); //$NON-NLS-1$
            } catch (Exception e) {
            }

            Game.exit();
        }
    }

    /**
     * Loads town.ini
     */
    private static void loadPropertiesMain() {
        // Cargamos el .ini
        propertiesMain = new Properties();

        String sFile = "towns.ini"; //$NON-NLS-1$
        try {
            propertiesMain.load(new FileInputStream(sFile));
            try {
                propertiesMain.load(new FileInputStream(Game.getUserFolder() + Game.getFileSeparator() + sFile));
            } catch (Exception e) {
            }
        } catch (FileNotFoundException e) {
            Log.log(Log.LEVEL.ERROR, Messages.getString("Towns.2") + sFile, "Towns"); //$NON-NLS-1$ //$NON-NLS-2$
            Game.exit();
        } catch (IOException e) {
            Log.log(Log.LEVEL.ERROR, Messages.getString("Towns.7"), "Towns"); //$NON-NLS-1$ //$NON-NLS-2$
            Log.log(Log.LEVEL.ERROR, e.toString(), "Towns"); //$NON-NLS-1$
            Game.exit();
        }
    }

    /**
     * Loads graphics.ini
     */
    private static void loadPropertiesGraphics() {
        // Cargamos el .ini
        propertiesGraphics = new Properties();

        String sFile = "graphics.ini"; //$NON-NLS-1$
        try {
            propertiesGraphics.load(new FileInputStream(sFile));

            // Mods
            File fUserFolder = new File(Game.getUserFolder());
            if (!fUserFolder.exists() || !fUserFolder.isDirectory()) {
                return;
            }

            ArrayList<String> alMods = Game.getModsLoaded();
            if (!alMods.isEmpty()) {
                for (String alMod : alMods) {
                    String sModGraphicsIniPath = fUserFolder.getAbsolutePath() + FileSystems.getDefault().getSeparator() + Game.MODS_FOLDER1 + FileSystems.getDefault().getSeparator() + alMod + FileSystems.getDefault().getSeparator() + "graphics.ini";
                    File fIni = new File(sModGraphicsIniPath);
                    if (fIni.exists()) {
                        propertiesGraphics.load(new FileInputStream(fIni));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.log(Log.LEVEL.ERROR, Messages.getString("Towns.2") + sFile, "Towns"); //$NON-NLS-1$ //$NON-NLS-2$
            Game.exit();
        } catch (IOException e) {
            Log.log(Log.LEVEL.ERROR, Messages.getString("Towns.7"), "Towns"); //$NON-NLS-1$ //$NON-NLS-2$
            Log.log(Log.LEVEL.ERROR, e.toString(), "Towns"); //$NON-NLS-1$
            Game.exit();
        }
    }

    public static <T> T getProperty(Property<T> property, T defaultValue) {
        final String rawValue = getPropertiesString(property.propertyFile(), property.key());
        if (rawValue != null) {
            final T value = property.propertyWrapper().wrap(rawValue);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    /**
     * Returns a property from main .ini converted to int with default value 0
     *
     * @param sProperty Property name
     * @return a property converted to int with default value 0
     */
    public static int getPropertiesInt(String sProperty) {
        return getPropertiesInt(PropertyFile.PROPERTY_FILE_MAIN, sProperty, 0);
    }

    /**
     * Returns a main property converted to int with default value if error
     *
     * @param sProperty     Propery name
     * @param iDefaultValue Default value in case of error
     * @return a property converted to int with default value if error
     */
    public static int getPropertiesInt(String sProperty, int iDefaultValue) {
        return getPropertiesInt(PropertyFile.PROPERTY_FILE_MAIN, sProperty, iDefaultValue);
    }

    /**
     * Returns a property converted to int with default value 0
     *
     * @param propertyFile
     * @param sProperty    Property name
     * @return a property converted to int with default value 0
     */
    public static int getPropertiesInt(PropertyFile propertyFile, String sProperty) {
        return getPropertiesInt(propertyFile, sProperty, 0);
    }

    /**
     * Returns a property converted to int with default value if error
     *
     * @param propertyFile
     * @param sProperty     Propery name
     * @param iDefaultValue Default value in case of error
     * @return a property converted to int with default value if error
     */
    public static int getPropertiesInt(PropertyFile propertyFile, String sProperty, int iDefaultValue) {
        String sValue = getPropertiesString(propertyFile, sProperty);

        try {
            if (sValue != null) {
                return Integer.parseInt(sValue);
            }
        } catch (NumberFormatException nfe) {
            Log.log(Log.LEVEL.ERROR, Messages.getString("Towns.10") + sProperty + Messages.getString("Towns.11") + sValue + Messages.getString("Towns.12") + iDefaultValue + "]", "Towns"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        }

        return iDefaultValue;
    }

    /**
     * Returns a property from main .ini
     *
     * @param sProperty Property name
     * @return a property from .ini, null if something fails
     */
    public static String getPropertiesString(String sProperty) {
        return getPropertiesString(PropertyFile.PROPERTY_FILE_MAIN, sProperty);
    }

    /**
     *
     * @param propertyFile
     * @param sProperty    Property name
     * @return a property from a .ini, null if something fails
     */
    public static String getPropertiesString(PropertyFile propertyFile, String sProperty) {
        if (propertyFile == PropertyFile.PROPERTY_FILE_MAIN && propertiesMain == null) {
            loadPropertiesMain();
        } else if (propertyFile == PropertyFile.PROPERTY_FILE_GRAPHICS && propertiesGraphics == null) {
            loadPropertiesGraphics();
        }

        if (sProperty == null || sProperty.isEmpty()) {
            Log.log(Log.LEVEL.ERROR, Messages.getString("Towns.15"), "Towns"); //$NON-NLS-1$ //$NON-NLS-2$
            Game.exit();
        }

        return switch (propertyFile) {
            case PROPERTY_FILE_MAIN -> propertiesMain.getProperty(sProperty);
            case PROPERTY_FILE_GRAPHICS -> propertiesGraphics.getProperty(sProperty);
        };
    }

    public static Properties getPropertiesGraphics() {
        if (propertiesGraphics == null) {
            loadPropertiesGraphics();
        }

        return propertiesGraphics;
    }

    public static void clearPropertiesGraphics() {
        if (propertiesGraphics != null) {
            propertiesGraphics.clear();
        }
        propertiesGraphics = null;
    }

    public native boolean SteamAPI_Init();
}
