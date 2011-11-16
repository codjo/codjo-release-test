package net.codjo.test.release.task.imports;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Variable {
    private String format;
    private String name;
    private String value;


    public void setFormat(String format) {
        this.format = format;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public String getFormat() {
        return format;
    }


    public String getName() {
        return name;
    }


    public String getValue() {
        return value;
    }


    public String formatValue() {
        Object val = getValue();
        if ("today".equals(val)) {
            val = new Date();
        }
        if (getFormat() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(getFormat());
            return formatter.format(val);
        }
        else {
            return val.toString();
        }
    }
}
