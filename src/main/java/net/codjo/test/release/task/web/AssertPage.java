package net.codjo.test.release.task.web;
/**
 * Vérifie le titre de la page courante.
 */
public class AssertPage implements WebStep {
    private String title;
    private String statusCode;


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getStatusCode() {
        return statusCode;
    }


    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }


    public void proceed(WebContext context) {
        if (title != null) {
            checkTitle(context, title);
        }
        else if (statusCode != null) {
            checkErrorCode(context, statusCode);
        }
        else {
            throw new RuntimeException("L'un des attributs 'title' ou 'statusCode' doit être défini.");
        }
    }


    private void checkTitle(WebContext context, String expected) {
        String actual = context.getHtmlPage().getTitleText();
        if (!expected.equalsIgnoreCase(actual)) {
            throw new WebException("Page inattendue : " + expected + " obtenu : " + actual);
        }
    }


    private void checkErrorCode(WebContext context, String expected) {
        String actual = String.valueOf(context.getStatusCode());
        if (!expected.equalsIgnoreCase(actual)) {
            throw new WebException("Code d'erreur inattendu : " + expected + " obtenu : " + actual);
        }
    }
}
