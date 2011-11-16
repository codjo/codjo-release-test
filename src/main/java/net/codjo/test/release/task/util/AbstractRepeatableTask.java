package net.codjo.test.release.task.util;
import org.apache.log4j.Logger;

public abstract class AbstractRepeatableTask {
    private static final Logger LOG = Logger.getLogger(AbstractRepeatableTask.class);

    private int timeout = 15;
    private long waitingNumber = 1000L;
    /**
     * Délai d'attente en millisecondes entre deux retries.
     */
    private long delay = 300;


    protected AbstractRepeatableTask() {
    }


    protected AbstractRepeatableTask(int timeout, long waitingNumber, long delay) {
        this.timeout = timeout;
        this.waitingNumber = waitingNumber;
        this.delay = delay;
    }


    public boolean execute() throws Exception {
        long begin = System.currentTimeMillis();
        int attemptIndex = 0;
        boolean success;
        do {
            success = internalExecute();
            if (!success) {
                try {
                    LOG.debug(getMessageError() + " La tentative numéro " + attemptIndex
                              + " a échoué. Mise en attente pour laisser le thread travailler...");
                    attemptIndex++;
                    Thread.sleep(delay);
                }
                catch (InterruptedException e) {
                    ;
                }
            }
        }
        while (!success && (System.currentTimeMillis() - begin < timeout * waitingNumber));

        return success;
    }


    protected abstract boolean internalExecute() throws Exception;


    protected String getMessageError() {
        return "";
    }


    ;
}