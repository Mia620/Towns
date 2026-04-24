package xaos.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Calendar;

public final class Log {
    private static FileOutputStream logFile;

    static {
        try {
            logFile = new FileOutputStream("error.log", true);
        } catch (FileNotFoundException e) {
            debug("Unable to open log file", e.toString());
        }
    }

    public enum LEVEL {
        DEBUG,
        ERROR
    }

    public static void log(LEVEL type, String message, String sClass) {
        StringBuffer sMessage = new StringBuffer();

        // Time block
        sMessage.append("["); //$NON-NLS-1$
        sMessage.append(DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
        sMessage.append("]"); //$NON-NLS-1$

        // Class block
        sMessage.append("["); //$NON-NLS-1$
        sMessage.append(sClass);
        sMessage.append("] "); //$NON-NLS-1$
        sMessage.append(message);

        switch (type) {
            case DEBUG:
                System.out.println(sMessage);
                break;
            case ERROR:
                System.err.println(sMessage);
                try {
                    logFile.write(("\r\n" + sMessage).getBytes()); //$NON-NLS-1$
                    logFile.close();
                } catch (Exception e) {
                    System.err.println(Messages.getString("Log.4")); //$NON-NLS-1$
                }
                break;
            default:
        }
    }

    public static void debug(String message, String sClass) {
        Log.log(LEVEL.DEBUG, message, sClass);
    }

    public static void error(String message, String sClass) {
        Log.log(LEVEL.ERROR, message, sClass);

    }
}
