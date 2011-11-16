/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.tokio;
import net.codjo.test.release.task.AgfTask;
import net.codjo.tokio.TokioLoaderException;
import net.codjo.tokio.XMLTokioLoader;
import net.codjo.tokio.model.Scenario;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.tools.ant.BuildException;
import org.xml.sax.SAXException;
/**
 */
public class Load extends AgfTask {
    private String id;
    private XMLTokioLoader loader;
    private String file;


    public void setFile(String file) {
        this.file = file;
    }


    public String getFile() {
        return file;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }


    public void setLoader(XMLTokioLoader loader) {
        this.loader = loader;
    }


    public XMLTokioLoader getLoader() {
        return loader;
    }


    @Override
    public void execute() {
        try {
            File tokioFile = getTokioFile();
            info("Chargement du fichier tokio " + tokioFile);
            loader = new XMLTokioLoader(tokioFile, getAntPropertiesMap(), true);
        }
        catch (IOException e) {
            throw new BuildException("Lecture du fichier tokio en erreur", e);
        }
        catch (SAXException e) {
            throw new BuildException("Fichier tokio invalide", e);
        }
        catch (ParserConfigurationException e) {
            throw new BuildException("Fichier tokio invalide", e);
        }
        catch (TokioLoaderException e) {
            throw new BuildException("Erreur lors du chargement du fichier tokio", e);
        }
    }


    protected File getTokioFile() {
        return toAbsoluteFile(getFile());
    }


    private Map<String, String> getAntPropertiesMap() {
        Map<String, String> propertiesMap = new HashMap<String, String>();
        Map propertiesHashTable = getProject().getProperties();

        for (Object key : propertiesHashTable.keySet()) {
            propertiesMap.put("${" + key + "}", (String)propertiesHashTable.get(key));
        }
        return propertiesMap;
    }


    Scenario getScenario(String name) {
        return getLoader().getScenario(name);
    }
}
