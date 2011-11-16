/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.converter.TreeNodeConverter;
import net.codjo.test.release.task.gui.converter.TreeNodeModelConverter;
import net.codjo.test.release.task.gui.converter.TreeNodeRendererConverter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
/**
 * Méthodes utilitaires pour la gestion des {@link JTree}
 *
 * @version $Revision: 1.7 $
 */
final class TreeUtils {
    public static final TreeNodeConverter MODEL_CONVERTER = new TreeNodeModelConverter();
    public static final TreeNodeConverter RENDERER_CONVERTER = new TreeNodeRendererConverter(false);
    public static final TreeNodeConverter RENDERER_ASSERT_CONVERTER = new TreeNodeRendererConverter(true);
    private static final String SEPARATOR = ":";


    private TreeUtils() {
    }


    /**
     * Convertit un path en {@link TreePath}
     *
     * @param tree l'arbre
     * @param path chemin du noeud, avec séparateur ':'
     *
     * @return l'objet {@link TreePath} correspondant, ou <code>null</code> si le noeud n'est pas trouvé.
     *
     * @throws GuiFindException si le noeud n'a pas été trouvé et renvoie la liste des noeuds disponibles.
     */
    public static TreePath convertIntoTreePath(JTree tree,
                                               String path,
                                               TreeNodeConverter converter) throws GuiFindException {
        List<String> foundPaths = new ArrayList<String>();

        TreePath treePath = convertIntoTreePathImpl(foundPaths, tree, path, converter);
        if (treePath == null) {
            waitALittle();
            treePath = convertIntoTreePathImpl(foundPaths, tree, path, converter);
        }

        if (treePath == null) {
            StringBuffer treePathes = new StringBuffer();
            for (Object findedPath : foundPaths) {
                treePathes.append("-").append((String)findedPath).append("\n");
            }
            throw new GuiFindException("Le noeud '" + path
                                       + "' n'existe pas.\nListe des noeuds présents :\n" + treePathes);
        }
        return treePath;
    }


    public static boolean isLeaf(JTree tree,
                                 TreePath path) {   //TODO Duplication avec TreeUtil de gui-toolkit
        return tree.getModel().isLeaf(path.getLastPathComponent());
    }


    public static void expandSubtree(JTree tree,
                                     TreePath path) {   //TODO Duplication avec TreeUtil de gui-toolkit
        TreeModel treeModel = tree.getModel();
        Object node = path.getLastPathComponent();
        for (int i = 0; i < treeModel.getChildCount(node); i++) {
            Object child = treeModel.getChild(node, i);
            TreePath childPath = path.pathByAddingChild(child);
            if (!isLeaf(tree, childPath)) {
                expandSubtree(tree, childPath);
            }
        }
        tree.expandPath(path);
    }


    private static void waitALittle() {
        try {
            Thread.sleep(200);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private static TreePath convertIntoTreePathImpl(List<String> findedPaths,
                                                    JTree tree,
                                                    String colonPath,
                                                    TreeNodeConverter converter) {
        findedPaths.clear();

        for (TreePath treePath : getTreePathList(tree.getModel())) {
            String actualPath = convertPath(tree, treePath, converter, SEPARATOR);
            findedPaths.add(actualPath);
            if (colonPath.equals(actualPath)) {
                return treePath;
            }
        }

        return null;
    }


    /**
     * Construction d'un treePath séparé par des ':' et utilisant le renderer du JTree.
     *
     * @return le path
     */
    private static String convertPath(JTree tree,
                                      TreePath treePath,
                                      TreeNodeConverter converter,
                                      String separator) {
        StringBuilder convertedPathBuffer = new StringBuilder();

        Object lastPathComponent = treePath.getLastPathComponent();
        Object[] path = treePath.getPath();
        for (Object node : path) {
            convertedPathBuffer.append(converter.getValue(tree, node));
            if (node != lastPathComponent) {
                convertedPathBuffer.append(separator);
            }
        }

        return convertedPathBuffer.toString();
    }


    private static List<TreePath> getTreePathList(TreeModel model) {
        List<TreePath> pathList = new ArrayList<TreePath>();
        List<Object> fatherBranch = new ArrayList<Object>();
        Object father = model.getRoot();

        fatherBranch.add(father);
        pathList.add(new TreePath(father));

        createBranch(model, father, fatherBranch, pathList);

        return pathList;
    }


    private static void createBranch(TreeModel model,
                                     Object father,
                                     List<Object> fatherBranch,
                                     List<TreePath> globalBranch) {
        int childCount = model.getChildCount(father);
        for (int index = 0; index < childCount; index++) {
            Object child = model.getChild(father, index);
            List<Object> currentBranch = new ArrayList<Object>(fatherBranch);
            currentBranch.add(child);
            globalBranch.add(new TreePath(currentBranch.toArray()));

            if (model.getChildCount(child) > 0) {
                createBranch(model, child, currentBranch, globalBranch);
            }
        }
    }
}
