package net.codjo.test.release.task.tokio;
import net.codjo.test.release.TestReport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.tools.ant.BuildEvent;

public class TokioInsertListener extends AbstractTokioListener {
    private BigDecimal duration = BigDecimal.ZERO;
    private final TestReport testReport;
    private long debTime;


    public TokioInsertListener(final TestReport testReport) {
        super("Tokio insert time", SetDb.class);
        this.testReport = testReport;
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
        duration = duration.add(new BigDecimal(System.currentTimeMillis() - debTime)
              .setScale(0, RoundingMode.HALF_EVEN));
        testReport.setFieldValue(reportColumnName, duration);
    }
}
