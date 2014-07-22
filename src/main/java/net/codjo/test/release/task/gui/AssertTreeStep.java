/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
import net.codjo.test.release.task.AgfTask;

import static net.codjo.test.release.task.gui.TreeStepUtils.getAssertConverter;
import static net.codjo.test.release.task.gui.TreeUtils.convertIntoTreePath;
/**
 * Classe permettant de vérifier le contenu d'une {@link javax.swing.JTree}.
 */
public class AssertTreeStep extends AbstractAssertStep {
    private String name;
    private String path;
    private int row = -1;
    private Color foreground;
    private Color background;
    private String icon;
    private boolean exists = true;
    private String mode = DISPLAY_MODE;
    private Boolean opaque;


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


    public Color getBackground() {
        return background;
    }


    public void setBackground(String rgb) {
        this.background = GuiUtil.convertToColor(rgb);
    }


    public Color getForeground() {
        return foreground;
    }


    public void setForeground(String rgb) {
        foreground = GuiUtil.convertToColor(rgb);
    }


    public String getIcon() {
        return icon;
    }


    public void setIcon(String icon) {
        this.icon = icon;
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
            if (getForeground() != null) {
                assertForeground(getRendererComponent(tree, foundPath));
            }
            if (opaque != null) {
                assertOpaque(getRendererComponent(tree, foundPath));
            }
            if (getBackground() != null) {
                assertBackground(getRendererComponent(tree, foundPath));
            }
            if (getIcon() != null) {
                assertIcon(context, getRendererComponent(tree, foundPath));
            }
        }
        catch (GuiFindException e) {
            if (isExists()) {
                throw new GuiAssertException(e.getMessage());
            }
        }
    }


    private Component getRendererComponent(JTree tree, TreePath foundPath) {
        boolean expanded = tree.isExpanded(foundPath);
        boolean selected = tree.getSelectionModel().isPathSelected(foundPath);
        Object value = foundPath.getLastPathComponent();
        boolean leaf = tree.getModel().isLeaf(value);
        boolean focus = tree.hasFocus();
        return tree.getCellRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, focus);
    }


    private void assertIcon(TestContext context, Component rendererComponent) {
        try {
            if (!(rendererComponent instanceof JLabel)) {
                throw new GuiAssertException(
                      "Le noeud '" + path + "' n'est pas rendu sous forme d'un type géré.");
            }
            String parentPath = context.getProperty(AgfTask.TEST_DIRECTORY);
            Icon actualIcon = ((JLabel)rendererComponent).getIcon();
            ImageIcon actualImageIcon = (ImageIcon)actualIcon;

            String expectedIconName = new File(parentPath, getIcon()).getName();
            String actualIconName = new File(new URL(actualImageIcon.getDescription()).getPath()).getName();
            if (!expectedIconName.equals(actualIconName)) {
                throw new GuiAssertException("Erreur de l'icone sur '" + getName() + "' au niveau de '" + path +
                                             "' : attendu='" + expectedIconName + "' obtenu='" + actualIconName + "'");
            }
        }
        catch (IOException e) {
            throw new GuiAssertException(e.getMessage());
        }
    }


    private void assertForeground(Component rendererComponent) {
        if (foreground == null) {
            return;
        }

        Color actualForeground = rendererComponent.getForeground();
        if (!GuiUtil.equals(actualForeground, foreground)) {
            throw new GuiAssertException("Couleur de police du composant '" + getName() + "' au niveau de '" + path +
                                         "' : attendu='" + foreground + "' obtenu='" + actualForeground + "'");
        }
    }


    private void assertOpaque(Component rendererComponent) {
        if (opaque == null) {
            return;
        }

        boolean actualOpaque = rendererComponent.isOpaque();
        if (actualOpaque != opaque) {
            throw new GuiAssertException("Opacité du composant '" + getName() + "' au niveau de '" + path +
                                         "' : attendu='" + opaque + "' obtenu='" + actualOpaque + "'");
        }
    }


    private void assertBackground(Component rendererComponent) {
        if (background == null) {
            return;
        }

        Color actualBackground = rendererComponent.getBackground();
        if (!GuiUtil.equals(actualBackground, background)) {
            throw new GuiAssertException("Couleur de fond du composant '" + getName() + "' au niveau de '" + path +
                                         "' : attendu='" + background + "' obtenu='" + actualBackground + "'");
        }
    }


    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
    }
}
