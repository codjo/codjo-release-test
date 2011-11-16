/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.user;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Classe utilitaire permettant de charger les requête pour la balise 'client'.<p>Le format de fichier lu
 * est le suivant :<pre> <list> <send>... 1er requete envoye (ou liste de requete)...</send>
 *  <send>... 2eme requete (ou liste)...</send> </list> </pre></p>
 */
class QueryLoader {
    private static final String START_TAG = "<send>";
    private static final String END_TAG = "</send>";


    public String[] load(File file) throws IOException {
        String content = FileUtil.loadContent(file);

        List<String> requestList = new ArrayList<String>();
        extractRequest(content, requestList);

        return requestList.toArray(new String[requestList.size()]);
    }


    private void extractRequest(String content, List<String> requestList) {
        int startIndex = content.indexOf(START_TAG);
        if (startIndex == -1) {
            return;
        }

        int endIndex = content.indexOf(END_TAG);
        String request = content.substring(startIndex + START_TAG.length(), endIndex);
        requestList.add(request);

        if (content.length() > endIndex + END_TAG.length()) {
            extractRequest(content.substring(endIndex + END_TAG.length()), requestList);
        }
    }
}
