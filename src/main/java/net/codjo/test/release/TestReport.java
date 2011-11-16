package net.codjo.test.release;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static org.apache.commons.lang.StringUtils.isEmpty;

public class TestReport {
    public static final String TAB = "\t";
    public static final int ONE_MEGA = 1024 * 1024;
    private static final String EMPTY_STRING = " ";
    private PrintStream printStreamLog;
    private Map<FieldEntry, String> fields;


    public TestReport(String path) throws FileNotFoundException {
        this(path, Arrays.asList("Test",
                                 "Total before",
                                 "Used before",
                                 "Free before",
                                 "Total after",
                                 "Used after",
                                 "Free after",
                                 "Time",
                                 "Tokio load time",
                                 "Tokio insert time"));
    }


    TestReport(String path, List<String> titles) throws FileNotFoundException {
        printStreamLog = new PrintStream(new FileOutputStream(path));
        fields = new TreeMap<FieldEntry, String>(new FieldEntryComparator());
        this.initReportHeader(titles);
    }


    private void initReportHeader(final List<String> titles) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i);
            buffer.append(title).append(TAB);
            fields.put(new FieldEntry(title, i), "");
        }
        printStreamLog.println(buffer.toString());
    }


    public void printExceptionInReport(final Throwable exception) {
        printStreamLog.println("###################################\n" + new Date()
                               + " Erreur !!!  :  ");
        exception.printStackTrace(printStreamLog);
        printStreamLog.println("###################################\n");
        printStreamLog.flush();
    }


    public void logMemoryBeforeTest(final double freeMemory, final double totalMemory) {
        double free = freeMemory / ONE_MEGA;
        double total = totalMemory / ONE_MEGA;
        setFieldValue("Total before", total);
        setFieldValue("Free before", free);
        setFieldValue("Used before", total - free);
    }


    public void logMemoryAfterTest(final double freeMemory, final double totalMemory) {
        double free = freeMemory / ONE_MEGA;
        double total = totalMemory / ONE_MEGA;
        setFieldValue("Total after", total);
        setFieldValue("Free after", free);
        setFieldValue("Used after", total - free);
    }


    public void logTime(final long time) {
        setFieldValue("Time", time);
    }


    public void logTestName(final String testName) {
        fields.put(getFieldName("Test"), testName);
    }


    public void flush() {
        StringBuilder buffer = new StringBuilder();
        for (FieldEntry columnName : fields.keySet()) {
            String value = fields.get(columnName);
            buffer.append((isEmpty(value) ? EMPTY_STRING : value));
            buffer.append(TAB);
        }
        printStreamLog.println(buffer.toString());
        printStreamLog.flush();
    }


    private FieldEntry getFieldName(String name) {
        if (name == null) {
            return null;
        }
        for (FieldEntry fieldName : fields.keySet()) {
            if (name.equals(fieldName.toString())) {
                return fieldName;
            }
        }
        return null;
    }


    public void setFieldValue(String fieldName, Object value) {
        FieldEntry fieldNameObject = getFieldName(fieldName);
        if (fieldNameObject == null) {
            throw new IllegalArgumentException("La colonne " + fieldName + " n'existe pas");
        }
        fields.put(fieldNameObject, String.valueOf(value));
    }


    public void close() {
        if (printStreamLog != null) {
            printStreamLog.close();
        }
    }


    private static class FieldEntry {
        private String fieldName;
        private int order;


        private FieldEntry(String fieldName, int order) {
            this.fieldName = fieldName;
            this.order = order;
        }


        @Override
        public String toString() {
            return fieldName;
        }


        @Override
        public int hashCode() {
            final int prime = 17;
            int code = this.order;
            code = code + prime * (this.fieldName == null ? 0 : this.fieldName.hashCode());
            return code;
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || (this.getClass() != obj.getClass())) {
                return false;
            }
            FieldEntry that = (FieldEntry)obj;
            boolean comp = (this.order == that.order);
            comp = comp && (fieldName == null ? that.fieldName == null : fieldName.equals(that.fieldName));
            return comp;
        }


        public int getOrder() {
            return order;
        }
    }

    private static class FieldEntryComparator implements Comparator<FieldEntry> {

        public int compare(FieldEntry fieldName1, FieldEntry fieldName2) {
            if (fieldName1 == null && fieldName2 == null) {
                return 0;
            }
            else if (fieldName1 == null) {
                return -1;
            }
            else if (fieldName2 == null) {
                return 1;
            }
            return (fieldName1.getOrder() - fieldName2.getOrder());
        }
    }
}
