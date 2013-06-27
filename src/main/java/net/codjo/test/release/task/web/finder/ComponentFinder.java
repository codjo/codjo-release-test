package net.codjo.test.release.task.web.finder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;

public class ComponentFinder<T> {
    private List<IComponentFinder> finders = new ArrayList<IComponentFinder>();


    ComponentFinder() {
    }


    public ComponentFinder(String text, String id, String xpath) {
        Map<String, String> parameters = initMap(text, id, xpath);
        checkParameters(parameters);
        initFinders(parameters);
    }


    public ComponentFinder(Map<String, String> parameters) {
        checkParameters(parameters);
        initFinders(parameters);
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
        checkParameters(initMap(text, id, xpath));
    }


    private void checkParameters(Map<String, String> parameters) {
        final Set<String> keys = parameters.keySet();
        List<String> notNullParameters = new ArrayList<String>();
        for (String key : keys) {
            if (parameters.get(key) != null
                && !"index".equals(key)) {//TODO improve, because index should be linked to xpath or className
                notNullParameters.add(key);
            }
        }

        final int nbNotNullParameters = notNullParameters.size();
        if (nbNotNullParameters > 1) {
            throwWebException("Les champs ", notNullParameters, " et ", " ne doivent pas être utilisés en même temps");
        }

        if (notNullParameters.isEmpty()) {
            keys.remove("index");
            throwWebException("Le champ ", keys, " ou ", " doit être spécifié");
        }
    }


    private void initFinders(Map<String, String> parameters) {
        Integer index = null;
        if (parameters.containsKey("index")) {
            final String indexAsString = parameters.get("index");
            if (indexAsString != null) {
                index = Integer.valueOf(indexAsString);
            }
        }
        if (parameters.containsKey("id")) {
            final String id = parameters.get("id");
            if (id != null) {
                finders.add(new IdComponentFinder(id));
            }
        }
        if (parameters.containsKey("xpath")) {
            final String xpath = parameters.get("xpath");
            if (xpath != null) {
                finders.add(new XpathComponentFinder(xpath, index));
            }
        }
        if (parameters.containsKey("cssClass")) {
            final String cssClassName = parameters.get("cssClass");
            if (cssClassName != null) {
                finders.add(new XpathComponentFinder("//*[contains(@class, '" + cssClassName + "')]", index));
            }
        }
        if (parameters.containsKey("text")) {
            final String text = parameters.get("text");
            if (text != null) {
                finders.add(new TextComponentFinder(text));
            }
        }
    }


    private void throwWebException(String prefix, Collection<String> notNullParameters,
                                   String lastCommaReplacement, String postFix) {
        StringBuilder builder = new StringBuilder(prefix);
        builder.append(stringify(notNullParameters, lastCommaReplacement));
        builder.append(postFix);
        throw new WebException(builder.toString());
    }


    private String stringify(Collection<String> notNullParameters, String lastCommaReplacement) {
        StringBuilder builder = new StringBuilder();
        final List<String> parameters = sortCollection(notNullParameters);
        final int nbNotNullParameters1 = parameters.size();
        final Iterator<String> iterator = parameters.iterator();
        int cpt = 0;
        while (iterator.hasNext()) {
            String next = iterator.next();
            builder.append("'").append(next).append("'");
            if (cpt == (nbNotNullParameters1 - 2)) {
                builder.append(lastCommaReplacement);
            }
            else if (cpt != nbNotNullParameters1 - 1) {
                builder.append(", ");
            }
            cpt++;
        }
        return builder.toString();
    }


    private List<String> sortCollection(Collection<String> notNullParameters) {
        final List<String> parameters = new ArrayList<String>(notNullParameters);
        Collections.sort(parameters, new Comparator<String>() {
            public int compare(String o1, String o2) {
                if (o1 != null) {
                    return o1.compareToIgnoreCase(o2);
                }
                return 0;
            }
        });
        return parameters;
    }


    private Map<String, String> initMap(String text, String id, String xpath) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("text", text);
        parameters.put("id", id);
        parameters.put("xpath", xpath);
        return parameters;
    }
}