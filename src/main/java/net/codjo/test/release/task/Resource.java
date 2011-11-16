/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task;
/**
 * Description d'une ressource. Une ressource est ouverte au debut d'un release-test et
 * fermé à la fin (ou lors d'une erreur).
 *
 * @author $Author: crego $
 * @version $Revision: 1.6 $
 */
public interface Resource {
    public void open();


    public void close();
}
