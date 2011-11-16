package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebStep;
/**
 *
 */
public abstract class AbstractFormStep implements WebStep {
    private String name;


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
}
