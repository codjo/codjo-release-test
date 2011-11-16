package net.codjo.test.release.task.tokio;
import org.apache.tools.ant.Task;
/**
 *
 */
public abstract class AbstractTokioListener extends BuildListenerAdapter {
    protected final Class<? extends Task> taskClass;
    protected final String reportColumnName;


    protected AbstractTokioListener(final String reportColumnName, final Class<? extends Task> taskClass) {
        this.reportColumnName = reportColumnName;
        this.taskClass = taskClass;
    }


    protected boolean isNotClassTask(Task sourceTask) {
        return sourceTask == null || !(taskClass.isInstance(sourceTask));
    }
}
