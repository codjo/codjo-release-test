package net.codjo.test.release.task.web.finder;
import java.util.ArrayList;
import java.util.List;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;

public class ComponentFinder<T> {
    List<IComponentFinder> finders = new ArrayList<IComponentFinder>();


    public ComponentFinder(String text, String id, String xpath) {
        checkParameters(text, id, xpath);
        finders.add(new IdComponentFinder(id));
        finders.add(new XpathComponentFinder(xpath));
        finders.add(new TextComponentFinder(text));
    }


    ComponentFinder() {
    }


    public T find(WebContext context, ResultHandler resultHandler) {
        for (IComponentFinder finder : finders) {
            final T htmlElement = (T)finder.find(context, resultHandler);
            if (htmlElement != null) {
                return htmlElement;
            }
        }
        return null;
    }


    void checkParameters(String text, String id, String xpath) {
        final String parametersAsString = String.valueOf(text) + String.valueOf(id) + String.valueOf(xpath);
        if ("nullnullnull".equals(parametersAsString)) {
            throw new WebException("Le champ 'text', 'id' ou 'xpath' doit être spécifié");
        }

        String fieldList = "";
        if (text != null && id != null) {
            fieldList = "'text' et 'id'";
        }
        if (text != null && xpath != null) {
            fieldList = "'text' et 'xpath'";
        }
        if (id != null && xpath != null) {
            fieldList = "'id' et 'xpath'";
        }
        if (text != null && id != null && xpath != null) {
            fieldList = "'text', 'id' et 'xpath'";
        }
        if (!"".equals(fieldList)) {
            throw new WebException("Les champs " + fieldList + " ne doivent pas être utilisés en même temps");
        }
    }
}