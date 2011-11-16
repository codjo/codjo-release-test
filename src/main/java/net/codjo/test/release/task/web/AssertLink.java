package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import java.io.IOException;
/**
 * Vérifie la présence d'un lien dans la page.
 */
public class AssertLink implements WebStep {
    private String text = "";
    private String target;
    private boolean present = true;


    public void addText(String aValue) {
        text += aValue;
    }


    public void setTarget(String target) {
        this.target = target;
    }


    public void setPresent(boolean present) {
        this.present = present;
    }


    public void proceed(WebContext context) throws IOException {
        checkFields();

        try {
            if (!"".equals(text)) {
                proceedText(context);
            }
            else if (target != null) {
                proceedTarget(context);
            }
        }
        catch (ElementNotFoundException e) {
            if (present) {
                throw new WebException(
                      String.format("Aucun lien trouvé avec le texte : '%s' et la cible : '%s'",
                                    text,
                                    target != null ? target : ""));
            }
        }
    }


    private void checkFields() {
        if ("".equals(text) && target == null) {
            throw new WebException("Vous devez spécifier au moins le texte ou target");
        }
    }


    private void proceedText(WebContext context) throws ElementNotFoundException {
        HtmlAnchor anchor = context.getHtmlPage().getAnchorByText(text);
        if (target != null) {
            if (present && !target.equals(anchor.getHrefAttribute())) {
                throw new WebException(String.format(
                      "Le lien '%s' n'a pas la bonne cible.\nAttendu : '%s'\nObtenu : '%s'",
                      text,
                      target,
                      anchor.getHrefAttribute()));
            }
            else if (!present && target.equals(anchor.getHrefAttribute())) {
                throw new WebException(String.format("Lien trouvé avec la cible : '%s' et le texte : '%s'",
                                                     target,
                                                     text));
            }
        }
        else if (!present) {
            throw new WebException(String.format("Lien trouvé avec le texte : '%s'", text));
        }
    }


    private void proceedTarget(WebContext context) throws ElementNotFoundException {
        context.getHtmlPage().getAnchorByHref(target);
        if (!present) {
            throw new WebException(String.format("Lien trouvé avec la cible : '%s'", target));
        }
    }
}
