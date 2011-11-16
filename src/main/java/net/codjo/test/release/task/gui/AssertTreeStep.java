/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import static net.codjo.test.release.task.gui.TreeStepUtils.getAssertConverter;
import static net.codjo.test.release.task.gui.TreeUtils.convertIntoTreePath;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant de vérifier le contenu d'une {@link javax.swing.JTree}.
 */
public class AssertTreeStep extends AbstractAssertStep {
    private String name;
    private String path;
    private int row = -1;
    private boolean exists = true;
    private String mode = DISPLAY_MODE;


    public boolean isExists() {
        return exists;
    }


    public void setExists(boolean exists) {
        this.exists = exists;
    }


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    public String getMode() {
        return mode;
    }


    public int getRow() {
        return row;
    }


    public void setRow(int row) {
        this.row = row;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        if (path == null) {
            throw new GuiConfigurationException("Le path n'a pas été renseigné.");
        }

        NamedComponentFinder finder = new NamedComponentFinder(JTree.class, name);
        JTree tree = (JTree)findOnlyOne(finder, context, 0);

        if (tree == null) {
            throw new GuiFindException("L'arbre '" + getName() + "' est introuvable.");
        }

        path = context.replaceProperties(path);
        
        try {
            TreePath foundPath = convertIntoTreePath(tree, path, getAssertConverter(getMode()));
            if (!isExists()) {
                throw new GuiAssertException("Le noeud '" + path + "' existe.");
            }
            if (row != -1) {
                int rowForPath = tree.getRowForPath(foundPath);
                if (row != rowForPath) {
                    throw new GuiAssertException("Le noeud '" + path + "' ne se situe pas à la position '"
                                                 + row + "' mais à la position '" + rowForPath + "'");
                }
            }
        }
        catch (GuiFindException e) {
            if (isExists()) {
                throw new GuiAssertException(e.getMessage());
            }
        }
    }
}
