package net.codjo.test.release.task.gui;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
public class GuiContext {
    private StepPlayer stepPlayer;
    private final List<Window> windows = new ArrayList<Window>();


    public GuiContext(StepPlayer stepPlayer) {
        this.stepPlayer = stepPlayer;
    }


    public StepPlayer getStepPlayer() {
        return stepPlayer;
    }


    public List<Window> getWindows() {
        return windows;
    }
}
