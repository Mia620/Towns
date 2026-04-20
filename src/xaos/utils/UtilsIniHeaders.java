package xaos.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class UtilsIniHeaders {

    private static final long serialVersionUID = 8132377885335964294L;

    private static final HashMap<String, Integer> hmIniHeaders = new HashMap<>();
    private static final ArrayList<String> alStringIniHeaders = new ArrayList<>();
    private static int MAX_INI_HEADER = 0;

    public static int getIntIniHeader(String iniHeader) {
        Integer intReturn = hmIniHeaders.get(iniHeader);
        if (intReturn == null) {
            hmIniHeaders.put(iniHeader, MAX_INI_HEADER);
            alStringIniHeaders.add(iniHeader);

            intReturn = MAX_INI_HEADER;
            MAX_INI_HEADER++;
        }

        return intReturn;
    }

    public static String getStringIniHeader(int iniHeader) {
        return alStringIniHeaders.get(iniHeader);
    }

    public static boolean contains(int[] aiInts, int iValue) {
        if (aiInts == null) {
            return false;
        }

        for (int aiInt : aiInts) {
            if (aiInt == iValue) {
                return true;
            }
        }

        return false;
    }

    public static boolean contains(String[] aStrings, int iValue) {
        if (aStrings == null) {
            return false;
        }

        for (String aString : aStrings) {
            if (getIntIniHeader(aString) == iValue) {
                return true;
            }
        }

        return false;
    }

    public static int[] getIntsArray(ArrayList<String> alStrings) {
        if (alStrings == null) {
            return null;
        }

        int[] aiInts = new int[alStrings.size()];
        for (int i = 0; i < aiInts.length; i++) {
            aiInts[i] = getIntIniHeader(alStrings.get(i));
        }

        return aiInts;
    }

    public static int[] getIntsArray(String[] aStrings) {
        if (aStrings == null) {
            return null;
        }

        int[] aiInts = new int[aStrings.length];
        for (int i = 0; i < aiInts.length; i++) {
            aiInts[i] = getIntIniHeader(aStrings[i]);
        }

        return aiInts;
    }

    public static int[] getIntsArray(String sString) {
        if (sString == null) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(sString, ",");
        if (!tokenizer.hasMoreTokens()) {
            return null;
        }

        ArrayList<Integer> alInts = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            alInts.add(getIntIniHeader(tokenizer.nextToken()));
        }

        int[] aiInts = new int[alInts.size()];
        for (int i = 0; i < aiInts.length; i++) {
            aiInts[i] = alInts.get(i);
        }
        return aiInts;
    }

    public static ArrayList<int[]> getArrayIntsArray(ArrayList<String[]> alArray) {
        if (alArray == null) {
            return null;
        }

        ArrayList<int[]> alReturn = new ArrayList<>(alArray.size());
        for (String[] strings : alArray) {
            int[] aints = new int[strings.length];
            for (int j = 0; j < strings.length; j++) {
                aints[j] = getIntIniHeader(strings[j]);
            }
            alReturn.add(aints);
        }

        return alReturn;
    }

    public static ArrayList<String[]> getArrayStringsArray(ArrayList<int[]> alArray) {
        if (alArray == null) {
            return null;
        }

        ArrayList<String[]> alReturn = new ArrayList<>(alArray.size());
        for (int[] ints : alArray) {
            String[] aints = new String[ints.length];
            for (int j = 0; j < ints.length; j++) {
                aints[j] = getStringIniHeader(ints[j]);
            }
            alReturn.add(aints);
        }

        return alReturn;
    }

    public static ArrayList<String> getArrayStrings(ArrayList<Integer> alArray) {
        if (alArray == null) {
            return null;
        }

        ArrayList<String> alReturn = new ArrayList<>(alArray.size());
        for (Integer integer : alArray) {
            alReturn.add(getStringIniHeader(integer));
        }

        return alReturn;
    }

    /**
     * Indica si el ID pasado est� en la lista, y devuelve la posici�n. -1 en
     * caso de no encontrarlo
     *
     * @return
     */
    public static int contains(ArrayList<int[]> alList, int iValue) {
        if (alList == null || alList.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < alList.size(); i++) {
            if (contains(alList.get(i), iValue)) {
                return i;
            }
        }

        return -1;
    }
}
