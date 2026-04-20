package xaos.utils;

import xaos.property.Property;
import xaos.property.PropertyFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Florian Frankenberger
 */
public class PropertiesWriter {

    private final PropertyFile file;
    private final Map<EntryKey, String> map = new LinkedHashMap<>();

    public PropertiesWriter(PropertyFile file) {
        this.file = file;
    }

    public <T> void setProperty(Property<T> property, T value) {
        if (property.propertyFile() != file) {
            throw new IllegalArgumentException("property " + property.key() + " is not part of " + file);
        }
        map.put(new PropertiesEntryKey(property.key()), property.propertyWrapper().unwrap(value));
    }

    public void setProperty(String key, String value) {
        map.put(new PropertiesEntryKey(key), value);
    }

    public void addSection(String sectionHeading) {
        map.put(new SectionEntryKey(sectionHeading), null);
    }

    public void store(File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("ISO8859-1")))) {
            int counter = 0;
            for (Entry<EntryKey, String> entry : map.entrySet()) {
                EntryKey entryKey = entry.getKey();
                if (entryKey instanceof SectionEntryKey sectionEntryKey) {
                    if (counter > 0) {
                        pw.append("\n");
                    }
                    pw.append("# ").append(sectionEntryKey.sectionHeading()).append("\n");
                } else if (entryKey instanceof PropertiesEntryKey propertiesEntryKey) {
                    pw.append(propertiesEntryKey.key()).append(" = ").append(entry.getValue()).append("\n");
                }
                counter++;
            }
        }
    }

    private interface EntryKey {

    }

    private record PropertiesEntryKey(String key) implements EntryKey {
    }

    private record SectionEntryKey(String sectionHeading) implements EntryKey {
    }


}
