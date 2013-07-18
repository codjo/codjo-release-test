package net.codjo.test.release.task.web;
import net.codjo.test.common.PathUtil;
import net.codjo.test.release.task.AgfTask;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
/**
 *
 */
public class DownloadFileTest extends WebStepTestCase {
    public void test_download() throws Exception {
        String fileName = "test.file";
        String targetHtml = wrapHtml("Target title", "Target content");
        addPage("target.html", targetHtml);
        WebContext context = loadPage(wrapHtml("<a href='target.html'>go!</a>"));
        download("go!", fileName, context);
        File actualFile = new File(project.getProperty(AgfTask.BROADCAST_LOCAL_DIR), fileName);
        String actual = FileUtil.loadContent(actualFile);
        assertEquals(targetHtml, actual);
        actualFile.delete();
    }


    private void download(String link, String target, WebContext context) throws IOException {
        DownloadFile step = new DownloadFile();
        step.setLink(link);
        step.setTarget(target);
        step.proceed(context);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        project.setProperty(AgfTask.BROADCAST_LOCAL_DIR,
                            PathUtil.findTargetDirectory(getClass()).getAbsolutePath());
    }
}
