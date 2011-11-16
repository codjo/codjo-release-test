/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.test.standard;
import net.codjo.test.common.PathUtil;
import static net.codjo.test.release.task.AgfTask.TEST_DIRECTORY;
import net.codjo.test.release.task.gui.GuiException;
import net.codjo.test.release.test.AbstractSampleGuiTestCase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
/**
 * Classe de test de {@link net.codjo.test.release.test.standard.SampleGui}.
 *
 * @see net.codjo.test.release.task.gui.GuiTask
 */
public class SampleGuiTest extends AbstractSampleGuiTestCase {
    private Project project;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        project = new Project();
        project.setProperty(TEST_DIRECTORY, PathUtil.findResourcesFileDirectory(getClass()).getPath());
        AssertArgsClass.mainArgs = null;
    }


    /**
     * Affiche la liste (via le menu) et vérifie qu'elle existe avec titre OK.
     *
     * @throws Exception s'il y a une erreur inattendue
     */
    public void test_story1() throws Exception {
        runScenario("SampleGuiScenario.xml", project);
    }


    /**
     * Affiche un dialogue (JOptionPane).
     *
     * @throws Exception s'il y a une erreur inattendue
     */
    public void test_story_quit() throws Exception {
        runScenario("SampleGuiScenario_Quit.xml");
    }


    /**
     * Affiche un dialogue (JOptionPane).
     *
     * @throws Exception s'il y a une erreur inattendue
     */
    public void test_story_dialog() throws Exception {
        runScenario("SampleGuiScenario_dialog.xml");
    }


    /**
     * Echec du lancement du scénario à cause d'une mauvaise configuration (nom de la méthode de l'affichage
     * de l'IHM)
     *
     * @throws Exception s'il y a une erreur inattendue
     */
    public void test_story_fail_config() throws Exception {
        try {
            runScenario("SampleGuiScenario_fail_config.xml");
            fail("L'assert aurait dû échouer car la méthode d'affichage de l'IHM est invalide.");
        }
        catch (GuiException ex) {
            ; // Cas normal
        }
    }


    /**
     * Affiche la liste (via le menu) et vérifie le champ ptfCode. Ce test permet de vérifier que s'il y a une
     * erreur dans une step, l'exception remonte bien jusqu'ici.
     *
     * @throws Exception s'il y a une erreur inattendue
     */
    public void test_story_fail_assertValue() throws Exception {
        try {
            runScenario("SampleGuiScenario_fail_assertValue.xml");
            fail("L'assert aurait dû échouer car la valeur de ptfCode n'est pas bonne.");
        }
        catch (GuiException ex) {
            ; // Cas normal
        }
    }


    /**
     * Vérifie qu'il y a une erreur lorsque l'élément sélectionné n'existe pas dans une ComboBox éditable
     *
     * @throws Exception s'il y a une erreur inattendue
     */
    public void test_story_fail_combo() throws Exception {
        try {
            runScenario("SampleGuiScenario_fail_combo.xml");
            fail("L'assert aurait dû échouer car l'élément selectionné n'existe pas.");
        }
        catch (GuiException ex) {
            ; // Cas normal
        }
    }


    /**
     * Affecte et vérifie une nouvelle valeur dans une ComboBox éditable
     */
    public void test_story_assertValue_combo() {
        runScenario("SampleGuiScenario_assertValue_combo.xml");
    }


    /**
     * Affiche une ComboBox et vérifie la valeur en utilisant l'attribut 'mode'.
     */
    public void test_story_assertValue_mode() {
        runScenario("SampleGuiScenario_assertValue_mode.xml");
    }


    public void test_story_assertTable_mode() {
        runScenario("SampleGuiScenario_assertTable_mode.xml");
    }


    public void test_args() throws Exception {
        runScenario("SampleGuiScenario_args.xml");

        String[] args = AssertArgsClass.mainArgs;
        assertNotNull(args);
        assertEquals(4, args.length);
        assertEquals("val0", args[0]);
        assertEquals("val1", args[1]);
        assertEquals("val2", args[2]);
        assertEquals("val3", args[3]);
    }


    public void test_args_overrided() throws Exception {
        runScenario("SampleGuiScenario_args_overrided.xml");

        String[] args = AssertArgsClass.mainArgs;
        assertNotNull(args);
        assertEquals(4, args.length);
        assertEquals("boris", args[0]);
        assertEquals("jelediraispas", args[1]);
        assertEquals("val2", args[2]);
        assertEquals("val3", args[3]);
    }


    public void test_story_fail_duplicateName() throws Exception {
        try {
            runScenario("SampleGuiScenario_fail_duplicateName.xml");
            fail("Le clic aurait dû échouer car deux composants portent le nom 'Doublon'");
        }
        catch (GuiException ex) {
            ; // Cas normal
        }
    }


    public void test_story_fail_unexpectedColumnCount() throws Exception {
        try {
            runScenario("SampleGuiScenario_fail_unexpectedColumnCount.xml", project);
            fail("Le nombre de colonnes spécifié ne matche PAS le nombre de colonnes obtenu.");
        }
        catch (GuiException ex) {
            ; // Cas normal
        }
    }


    public void test_excelStandardScenario() throws Exception {
        runScenario("SampleGuiScenario_excel.xml", project);
    }


    public void test_excelStandardScenarioFail() throws Exception {
        try {
            runScenario("SampleGuiScenario_excel_fail.xml", project);
            fail();
        }
        catch (BuildException buildException) {
            // ok
        }
    }


    public static final class AssertArgsClass {
        public static String[] mainArgs;


        private AssertArgsClass() {
        }


        public static void main(String[] args) {
            mainArgs = args;
        }
    }
}
