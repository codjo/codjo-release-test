package net.codjo.test.release.task.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickStepTest extends AbstractClickStepTest {

    @Override
    protected AbstractButtonClickStep createClickStep() {
        return new ClickStep();
    }


    @Override
    protected boolean isCorrectClickEvent(MouseEvent event) {
        return (event.getModifiers() & MouseEvent.BUTTON1_MASK) != 0;
    }


    public void test_ok_menu() throws Exception {
        showFrame();

        // Clic menu
        step.setMenu("Fichier:Ouvrir");
        step.proceed(new TestContext(this));
        assertEquals(1, menuOuvrirListener.nbCalled);
    }


    public void test_ok_sousMenu() throws Exception {
        showFrame();

        // Clic menu
        step.setMenu("Fichier:Nouveau:Document");
        step.proceed(new TestContext(this));
        assertEquals(1, menuDocumentListener.nbCalled);
    }


    public void test_ok_sousMenuWithQuote() throws Exception {
        showFrame();

        // Clic menu
        step.setMenu("Fichier:Nouveau:L'espace");
        step.proceed(new TestContext(this));
        assertEquals(1, menuLespaceListener.nbCalled);
    }


    public void test_nok_noMenu() throws Exception {
        showFrame();

        try {
            step.setMenu("Edition:Copier");
            step.proceed(new TestContext(this));
            fail("Le clic doit échouer si le menu est introuvable.");
        }
        catch (GuiFindException ex) {
            ; // OK
        }
    }


    public void test_nok_noMenuItem() throws Exception {
        showFrame();

        try {
            step.setMenu("Fichier:Fermer");
            step.proceed(new TestContext(this));
            fail("Le clic doit échouer si l'élément de menu est introuvable.");
        }
        catch (GuiFindException ex) {
            ; // OK
        }
    }


    public void test_nok_noSubMenu() throws Exception {
        showFrame();

        try {
            step.setMenu("Fichier:Nouveau:Document:Texte");
            step.proceed(new TestContext(this));
            fail("Le clic doit échouer car Document n'est pas un sous-menu.");
        }
        catch (GuiFindException ex) {
            ; // OK
        }
    }


    public void test_nok_noButton() throws Exception {
        showFrame();

        try {
            step.setName("BoutonVider");
            step.proceed(new TestContext(this));
            fail("Le clic doit échouer si le bouton est introuvable.");
        }
        catch (GuiFindException ex) {
            ; // OK
        }
    }


    public void test_ok_bouton_onName() throws Exception {
        showFrame();

        // Clic bouton
        step.setName(BUTTON_NAME);
        step.proceed(new TestContext(this));
        assertEquals(1, buttonListener.nbCalled);
    }


    public void test_ok_bouton_onLabel() throws Exception {
        showFrame();

        // Clic bouton
        step.setLabel(BUTTON_LABEL);
        step.proceed(new TestContext(this));
        assertEquals(1, buttonListener.nbCalled);
    }


    public void test_ok_bouton_onNameAndLabel() throws Exception {
        showFrame();

        // Clic bouton
        step.setLabel(BUTTON_LABEL);
        step.setName(BUTTON_NAME);
        step.proceed(new TestContext(this));
        assertEquals(1, buttonListener.nbCalled);
    }


    public void test_koName_bouton_onNameAndLabel() throws Exception {
        showFrame();

        // Clic bouton
        step.setLabel(BUTTON_LABEL);
        step.setName(BUTTON_NAME + "KO");
        try {
            step.proceed(new TestContext(this));
            fail("Expected Error Message !!");
        }
        catch (Exception e) {
            assertEquals("Le composant 'Détails' est introuvable.", e.getMessage());
        }
        assertEquals(0, buttonListener.nbCalled);
    }


    public void test_koLabel_bouton_onNameAndLabel() throws Exception {
        showFrame();

        // Clic bouton
        step.setLabel(BUTTON_LABEL + "KO");
        step.setName(BUTTON_NAME);
        try {
            step.proceed(new TestContext(this));
            fail("Expected Error Message !!");
        }
        catch (Exception e) {
            assertEquals("Le composant 'DétailsKO' est introuvable.", e.getMessage());
        }
        assertEquals(0, buttonListener.nbCalled);
    }


    public void test_nok_bouton_disabled() throws Exception {
        showFrame();
        button.setEnabled(false);

        // Clic bouton
        step.setLabel(BUTTON_LABEL);
        try {
            step.proceed(new TestContext(this));
            fail("Expected Error Message !!");
        }
        catch (Exception e) {
            assertEquals("Le composant 'Détails' est inactif.", e.getMessage());
        }
        assertEquals(0, buttonListener.nbCalled);
    }


    public void test_ok_bouton_disabled() throws Exception {
        showFrame();
        step.setTimeout(15);
        button.setEnabled(false);

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    button.setEnabled(true);
                }
                catch (InterruptedException e) {
                    ;
                }
            }
        }.start();

        // Clic bouton
        step.setLabel(BUTTON_LABEL);
        step.proceed(new TestContext(this));
        assertEquals(1, buttonListener.nbCalled);
    }


    public void test_clickOnTableMakeASelection() throws Exception {
        showFrame();

        assertEquals(0, table.getSelectedRows().length);

        step.setName(table.getName());
        step.setRow(0);
        step.proceed(new TestContext(this));

        assertEquals(1, table.getSelectedRows().length);
        assertEquals(0, table.getSelectedRows()[0]);
    }


    public void test_doubleClickOnTableStartsEditingByColumnNumber() throws Exception {
        showFrame();

        step.setName(table.getName());
        step.setRow(0);
        step.setColumn("2");
        step.setCount(2);
        step.proceed(new TestContext(this));

        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingRow());
        assertEquals(2, table.getEditingColumn());
    }


    public void test_doubleClickOnTableStartsEditingByColumnName() throws Exception {
        showFrame();

        step.setName(table.getName());
        step.setRow(0);
        step.setColumn("C");
        step.setCount(2);
        step.proceed(new TestContext(this));

        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingRow());
        assertEquals(2, table.getEditingColumn());
    }


    public void test_clickOnListMakeASelection() throws Exception {
        showFrame();

        assertEquals(0, list.getSelectedValues().length);

        step.setName(list.getName());
        step.setRow(0);
        step.proceed(new TestContext(this));

        assertEquals(1, list.getSelectedValues().length);
        assertEquals(0, list.getSelectedIndices()[0]);
    }


    public void test_noRowAndNoColumnOnTableSelectsTheFirstCell() throws Exception {
        showFrame();

        step.setName(table.getName());
        step.setCount(2);
        step.proceed(new TestContext(this));

        assertTrue(table.isEditing());
        assertEquals(0, table.getEditingRow());
        assertEquals(0, table.getEditingColumn());
    }


    public void test_modifiers_ctrlModifier() throws Exception {
        showFrame();
        ClickStepTest.TestingModifierListener testingListener = new TestingModifierListener();
        button.addMouseListener(testingListener);

        step.setName(button.getName());
        step.setModifier("control");
        try{
            step.proceed(new TestContext(this));
            fail();
        }
        catch(IllegalArgumentException e){
            assertEquals("Pas de modifier standard correspondant à control", e.getMessage());
        }

        step.setName(button.getName());
        step.setModifier("ctrl");
        step.proceed(new TestContext(this));

        assertEquals(testingListener.getNbControlClicks(), 1);
    }


    public void test_modifiers_shiftModifier() throws Exception {
        showFrame();
        ClickStepTest.TestingModifierListener testingListener = new TestingModifierListener();
        button.addMouseListener(testingListener);

        step.setName(button.getName());
        step.setModifier("shift");
        step.proceed(new TestContext(this));

        assertEquals(testingListener.getNbShiftClicks(), 1);
    }


    private class TestingModifierListener extends MouseAdapter {
        private int nbControlClicks = 0;
        private int nbShiftClicks = 0;


        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.isControlDown()) {
                nbControlClicks++;
            }
            else if (mouseEvent.isShiftDown()) {
                nbShiftClicks++;
            }
        }


        public int getNbControlClicks() {
            return nbControlClicks;
        }


        public int getNbShiftClicks() {
            return nbShiftClicks;
        }
    }
}
