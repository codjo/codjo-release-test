package net.codjo.test.release.task.web;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class ComponentIdentifier {
    private String xpath;
    private String id;
    private Integer index;
    private String cssClass;


    public ComponentIdentifier() {
    }


    public ComponentIdentifier(String id, String xpath, String cssClass, Integer index) {
        this.xpath = xpath;
        this.id = id;
        this.index = index;
        this.cssClass = cssClass;
    }


    public String getXpath() {
        return xpath;
    }


    public void setXpath(String xpath) {
        this.xpath = xpath;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public Integer getIndex() {
        return index;
    }


    public void setIndex(Integer index) {
        this.index = index;
    }


    public String getCssClass() {
        return cssClass;
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


    public static Map<String, String> toArgumentMap(ComponentIdentifier identifier) {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("id", (identifier != null ? identifier.id : null));
        result.put("xpath", (identifier != null ? identifier.xpath : null));
        result.put("cssClass", (identifier != null ? identifier.cssClass : null));
        result.put("index",
                   ((identifier != null && identifier.index != null) ? String.valueOf(identifier.index) : null));
        return result;
    }
}
