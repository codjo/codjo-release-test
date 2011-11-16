package net.codjo.test.release.task.gui;
import static net.codjo.test.release.task.gui.AssertProgressDisplayStep.DisplayStep.hidden;
import static net.codjo.test.release.task.gui.AssertProgressDisplayStep.DisplayStep.neverShown;
import static net.codjo.test.release.task.gui.AssertProgressDisplayStep.DisplayStep.shown;
import net.codjo.test.release.task.gui.finder.FastGlassPaneComponentFinder;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class AssertProgressDisplayStep extends AbstractAssertStep implements ComponentListener {
    private String name;
    private Component glassPane;
    enum DisplayStep {
        neverShown,
        shown,
        hidden
    }
    private DisplayStep state = neverShown;


    public AssertProgressDisplayStep() {
        setTimeout(50);
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        try {
            runAwtCode(context, new Runnable() {
                public void run() {
                    if (state == neverShown) {
                        throw new GuiFindException("WaitingPanel '" + name + "' ne s'est pas affiché.");
                    }
                    if (state == shown) {
                        throw new GuiFindException("WaitingPanel '" + name + "' n'a pas disparu.");
                    }
                    // state = hidden : OK.
                }
            });
        }
        catch (Exception e) {
            throw new GuiFindException(e.getCause().getMessage(), e.getCause());
        }
    }


    public GuiStep getPreparationStep() {
        return new ProgressDisplayPreparationStep();
    }


    public void componentResized(ComponentEvent event) {
    }


    public void componentMoved(ComponentEvent event) {
    }


    public void componentShown(ComponentEvent event) {
        if (state == neverShown) {
            state = shown;
        }
    }


    public void componentHidden(ComponentEvent event) {
        if (state == shown) {
            state = hidden;
        }
    }


    @Override
    protected void beforeStop() {
        glassPane.removeComponentListener(this);
    }


    class ProgressDisplayPreparationStep extends AbstractGuiStep {

        public void proceed(TestContext context) {
            try {
                runAwtCode(context, new Runnable() {
                    public void run() {
                        glassPane = new FastGlassPaneComponentFinder(name).findOnlyOne();
                        if (glassPane == null) {
                            throw new GuiFindException("WaitingPanel '" + name + "' non trouvé.");
                        }

                        glassPane.addComponentListener(AssertProgressDisplayStep.this);
                    }
                });
            }
            catch (Exception e) {
                throw new GuiFindException(e.getCause().getMessage(), e.getCause());
            }
        }
    }
}
