package net.codjo.test.release.task.tokio;
import net.codjo.test.release.TestReport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.tools.ant.BuildEvent;

public class TokioLoadListener extends AbstractTokioListener {
    private final TestReport report;
    private long debTime;
    private BigDecimal duration = BigDecimal.ZERO;


    public TokioLoadListener(TestReport report) {
        super("Tokio load time", Load.class);
        this.report = report;
    }


    @Override
    public void taskStarted(BuildEvent event) {
        if (isNotClassTask(event.getTask())) {
            return;
        }
        debTime = System.currentTimeMillis();
    }


    @Override
    public void taskFinished(BuildEvent event) {
        if (isNotClassTask(event.getTask())) {
            return;
        }
        BigDecimal taskDuration = new BigDecimal(System.currentTimeMillis() - debTime);
        duration = duration.add(taskDuration.setScale(0, RoundingMode.HALF_EVEN));
        report.setFieldValue(reportColumnName, duration);
    }
}
