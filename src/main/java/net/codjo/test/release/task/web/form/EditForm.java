package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import net.codjo.test.release.task.web.WebStep;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Tag englobant pour la manipulation des formulaires. Exemple d'utilisation:
 * <pre><![CDATA[
 *   <editForm id="loginForm">
 *     <setField name="idField" value="admin"/>
 *     <setField name="passwordField" value="blah"/>
 *     <clickSubmit value="Valider"/>
 *   </editForm>]]>
 * </pre>
 */
public class EditForm implements WebStep {
    private List<WebStep> steps = new ArrayList<WebStep>();
    private String id;
    private int position;


    public void proceed(WebContext context) throws IOException, WebException {
        HtmlForm form;

        if (id != null) {
            form = (HtmlForm)context.getHtmlPage().getHtmlElementById(id);
        }
        else {
            form = (HtmlForm)context.getHtmlPage().getByXPath("//form").get(position);
        }

        context.setForm(form);

        for (WebStep step : steps) {
            step.proceed(context);
        }
    }


    public void setId(String id) {
        this.id = id;
    }


    public void addClickSubmit(ClickSubmit step) {
        steps.add(step);
    }


    public void addSetField(SetField step) {
        steps.add(step);
    }


    public void addAssertField(AssertField step) {
        steps.add(step);
    }


    public void addClickCheckBox(ClickCheckBox step) {
        steps.add(step);
    }


    public void addAssertSelectOptions(AssertSelectOptions step) {
        steps.add(step);
    }


    public void addSelect(Select step) {
        steps.add(step);
    }


    public void addAssertCheckBox(AssertCheckBox step) {
        steps.add(step);
    }


    public void addClickRadioButton(ClickRadioButton step) {
        steps.add(step);
    }


    public void setPosition(int position) {
        this.position = position;
    }
}
