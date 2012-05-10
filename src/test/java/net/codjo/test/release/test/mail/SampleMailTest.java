package net.codjo.test.release.test.mail;
import net.codjo.test.release.test.AbstractSampleGuiTestCase;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class SampleMailTest extends AbstractSampleGuiTestCase {
    public void test_standardScenario() throws Exception {
        runScenario("MailScenario_standard.xml");
    }


    public void test_messageExpectedFound() throws Exception {
        runScenario("MailScenario_messageExpectedFound.xml");
    }


    public void test_messageExpectedNotFound() throws Exception {
        try {
            runScenario("MailScenario_messageExpectedNotFound.xml");
            fail();
        }
        catch (BuildException e) {
            assertEquals(
                  "Message non reçu : from='luke@rebellion.org' to='dark@vador.net' subject='Fight ?'\nInbox=\n",
                  e.getMessage());
        }
    }


    public void test_messageNotExpectedFound() throws Exception {
        try {
            runScenario("MailScenario_messageNotExpectedFound.xml");
            fail();
        }
        catch (BuildException e) {
            assertEquals(
                  "Message non attendu reçu : from='luke@rebellion.org' to='dark@vador.net' subject='Disponibilité ?'\nInbox=\n",
                  e.getMessage());
        }
    }


    public void test_messageNotExpectedNotFound() throws Exception {
        runScenario("MailScenario_messageNotExpectedNotFound.xml");
    }


    public void test_bodyAssertFailure() throws Exception {
        try {
            runScenario("MailScenario_bodyError.xml");
            fail();
        }
        catch (BuildException e) {
            assertTrue(e.getMessage().contains("Comparaison en erreur : Assert Body contains"));
        }
    }


    public void test_messageWithAttachments() throws Exception {
        runScenario("MailScenario_withAttachments.xml");
    }


    public void test_messageWithAttachmentsComparisonFailure() throws Exception {
        try {
            runScenario("MailScenario_withAttachmentsComparisonFailure.xml");
            fail();
        }
        catch (BuildException e) {
            assertTrue(e.getCause().getMessage().contains("Contenu de la feuille 'Errors in quarantine' en erreur."));
        }
    }
}
