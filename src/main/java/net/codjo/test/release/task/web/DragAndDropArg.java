package net.codjo.test.release.task.web;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class DragAndDropArg {
    private String xpath;
    private String id;
    private Integer index;
    private String cssClass;


    public DragAndDropArg() {
    }


    public DragAndDropArg(String id, String xpath, String cssClass, Integer index) {
        this.xpath = xpath;
        this.id = id;
        this.index = index;
        this.cssClass = cssClass;
    }


    public void setXpath(String xpath) {
        this.xpath = xpath;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setIndex(Integer index) {
        this.index = index;
    }


    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }


    public String getArgValue() {
        if (id != null) {
            return id;
        }
        if (xpath != null) {
            return xpath;
        }
        if (cssClass != null) {
            return cssClass;
        }
        return null;
    }


    public static Map<String, String> toArgumentMap(DragAndDropArg args) {
        final HashMap<String, String> result = new HashMap<String, String>();
        result.put("id", (args != null ? args.id : null));
        result.put("xpath", (args != null ? args.xpath : null));
        result.put("cssClass", (args != null ? args.cssClass : null));
        result.put("index", ((args != null && args.index != null) ? String.valueOf(args.index) : null));
        return result;
    }
}
