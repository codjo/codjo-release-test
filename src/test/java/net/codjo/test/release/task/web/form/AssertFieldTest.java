package net.codjo.test.release.task.web.form;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class AssertFieldTest extends WebFormTestCase {

    public void test_input() throws Exception {
        checkField("loginField", "tonLogin");
    }


    public void test_textArea() throws Exception {
        checkField("content", "contenu");
    }


    public void test_error() throws Exception {
        try {
            checkField("loginField", "unknown");
            fail();
        }
        catch (BuildException e) {
            assertEquals("Contenu du champ 'loginField' en erreur : unknown obtenu : tonLogin",
                         e.getMessage());
        }
    }


    private void checkField(String field, String value) {
        AssertField step = new AssertField();
        step.setName(field);
        step.setValue(value);
        step.proceed(context);
    }
}
