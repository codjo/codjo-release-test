package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebStepTestCase;
/**
 *
 */
public abstract class WebFormTestCase extends WebStepTestCase {
    protected WebContext context;


    @Override
    protected void setUp() throws Exception {
        context = loadPage(wrapHtml(
              "<form name='loginForm' action='mails.html'>"
              + "    <table>"
              + "        <tr>"
              + "            <td>Identifiant</td>"
              + "            <td><input name='loginField' type='text' value='tonLogin'/></td>"
              + "        </tr>"
              + "        <tr>"
              + "            <td>Zone de texte</td>"
              + "            <td><textarea name='content'>contenu</textarea></td>"
              + "        </tr>"
              + "        <tr>"
              + "            <td>Mot de passe</td>"
              + "            <td><input name='passwordField' type='password'/></td>"
              + "        </tr>"
              + "        <tr>"
              + "            <td></td>"
              + "            <td><input type='submit' value='Valider' name='ok'/></td>"
              + "        </tr>"
              + "    </table>"
              + "</form>"
        ));
        context.setForm(context.getHtmlPage().getFormByName("loginForm"));
    }
}
