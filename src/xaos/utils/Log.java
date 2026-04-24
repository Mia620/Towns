package xaos.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Calendar;

public final class Log {

    public enum LEVEL {
        DEBUG,
        ERROR
    }

    public static void log(LEVEL type, String message, String sClass) {
        Calendar cal = Calendar.getInstance();
        DateFormat df = DateFormat.getDateTimeInstance();
        StringBuffer sMessage = new StringBuffer();
        if (df != null) {
            sMessage.append("["); //$NON-NLS-1$
            sMessage.append(df.format(cal.getTime()));
            sMessage.append("]"); //$NON-NLS-1$
        }
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
                File f = new File("error.log"); //$NON-NLS-1$
                try {
                    FileOutputStream fos = new FileOutputStream(f, true);
                    fos.write(("\r\n" + sMessage).getBytes()); //$NON-NLS-1$
                    fos.close();
                } catch (Exception e) {
                    System.err.println(Messages.getString("Log.4")); //$NON-NLS-1$
                }
                break;
            default:
        }
    }
}
