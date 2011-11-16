/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.util.List;
import junit.framework.TestCase;
import junit.extensions.jfcunit.JFCTestCase;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
/**
 * Classe de test de {@link GroupStep}.
 */
public class GroupStepTest extends TestCase {
    private MockGroup groupTest;


    @Override
    protected void setUp() throws Exception {
        groupTest = new MockGroup();
    }


    public void test_proceed() throws Exception {
        groupTest = new MockGroup();
        groupTest.setEnabled(true);
        assertStepProceedCount(1);

        groupTest = new MockGroup();
        groupTest.setEnabled(false);
        assertStepProceedCount(0);
    }


    private void assertStepProceedCount(int count) {
        ClickStep mockedClickStep = Mockito.mock(ClickStep.class);
        groupTest.addClick(mockedClickStep);

        TestContext context = new TestContext(new JFCTestCase(""));
        groupTest.proceed(context);

        Mockito.verify(mockedClickStep, new Times(count)).proceed(context);
    }


    public void test_addXXX() throws Exception {
        MockGroup mock = new MockGroup();

        mock.addClick(new ClickStep());
        assertEquals(1, mock.getStepList().size());

        mock.addAssertFrame(new AssertFrameStep());
        assertEquals(2, mock.getStepList().size());

        mock.addAssertValue(new AssertValueStep());
        assertEquals(3, mock.getStepList().size());

        mock.addSetValue(new SetValueStep());
        assertEquals(4, mock.getStepList().size());

        mock.addAssertTable(new AssertTableStep());
        assertEquals(5, mock.getStepList().size());

        mock.addCloseFrame(new CloseFrameStep());
        assertEquals(6, mock.getStepList().size());

        mock.addPause(new PauseStep());
        assertEquals(7, mock.getStepList().size());

        mock.addSelect(new SelectStep());
        assertEquals(8, mock.getStepList().size());

        mock.addAssertSelected(new AssertSelectedStep());
        assertEquals(9, mock.getStepList().size());

        mock.addAssertTree(new AssertTreeStep());
        assertEquals(10, mock.getStepList().size());

        mock.addSelectTab(new SelectTabStep());
        assertEquals(11, mock.getStepList().size());

        mock.addAssertList(new AssertListStep());
        assertEquals(12, mock.getStepList().size());

        mock.addAssertListSize(new AssertListSizeStep());
        assertEquals(13, mock.getStepList().size());

        mock.addAssertEnabled(new AssertEnabledStep());
        assertEquals(14, mock.getStepList().size());

        mock.addAssertEditable(new AssertEditableStep());
        assertEquals(15, mock.getStepList().size());

        mock.addAssertTab(new AssertTabStep());
        assertEquals(16, mock.getStepList().size());

        mock.addAssertTabCount(new AssertTabCountStep());
        assertEquals(17, mock.getStepList().size());
    }


    public void testAddAssertProgressDisplayFailed() {
        try {
            groupTest.addAssertProgressDisplay(new AssertProgressDisplayStep());
            fail();
        }
        catch (GuiException e) {
            assertEquals("La balise 'assertProgressDisplay' ne doit pas être la première d'un groupe",
                         e.getMessage());
        }
    }


    public void testAddAssertProgressDisplaySuccess() {
        ClickStep clickStep = new ClickStep();
        AssertProgressDisplayStep assertProgressDisplayStep = new AssertProgressDisplayStep();

        groupTest.addClick(new ClickStep());
        groupTest.addClick(clickStep);
        groupTest.addAssertProgressDisplay(assertProgressDisplayStep);

        List<GuiStep> steps = groupTest.getStepList();

        assertEquals(4, steps.size());
        assertEquals(AssertProgressDisplayStep.ProgressDisplayPreparationStep.class, steps.get(1).getClass());
        assertEquals(clickStep, steps.get(2));
        assertEquals(assertProgressDisplayStep, steps.get(3));
    }


    private static class MockGroup extends GroupStep {
        @Override
        public void addStep(GuiStep step) {
            super.addStep(step);
            if (step == null) {
                throw new IllegalArgumentException();
            }
        }


        @Override
        public List<GuiStep> getStepList() {
            return super.getStepList();
        }
    }
}
