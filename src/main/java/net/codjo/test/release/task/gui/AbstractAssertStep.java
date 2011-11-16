/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import org.apache.log4j.Logger;
/**
 * Classe mère de tous les AssertSteps : elle permet d'utiliser un mécanisme de retry et de timeout.
 *
 * @version $Revision: 1.6 $
 */
public abstract class AbstractAssertStep extends AbstractGuiStep {
    private final Logger logger = Logger.getLogger(getClass());
    /**
     * Délai d'attente en millisecondes entre deux retries.
     */
    private long delay = 300;

    private long waitingNumber = 1000L;


    /**
     * Appelle de manière répétée {@link #proceedOnce(TestContext)} tant qu'une exception survient.
     *
     * @param context le contexte du test
     */
    public final void proceed(TestContext context) {
        GuiException exception;
        long begin = System.currentTimeMillis();
        int attemptIndex = 0;
        do {
            attemptIndex++;
            try {
                proceedOnce(context);
                beforeStop();
                return;
            }
            catch (GuiException ex) {
                exception = ex;
                logger.debug(String.format(
                      "La tentative numéro %d de l'assert a échouée. Mise en attente pour laisser le thread AWT travailler...",
                      attemptIndex));
                sleep();
            }
        }
        while (System.currentTimeMillis() - begin < getTimeout() * waitingNumber);
        beforeStop();
        throw exception;
    }


    public long getDelay() {
        return delay;
    }


    public void setDelay(long delay) {
        this.delay = delay;
    }


    public long getWaitingNumber() {
        return waitingNumber;
    }


    protected void setWaitingNumber(long waitingNumber) {
        this.waitingNumber = waitingNumber;
    }


    protected void beforeStop() {
    }


    /**
     * Effectue l'assert une fois. Renvoie une exception si l'assert est faux. Cela peut arriver si l'IHM n'a
     * pas eu le temps de se mettre à jour.
     *
     * @param context le contexte du test
     */
    protected abstract void proceedOnce(TestContext context);


    protected void sleep() {
        try {
            Thread.sleep(delay);
        }
        catch (InterruptedException e) {
            ;
        }
    }
}
