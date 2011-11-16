package net.codjo.test.release.util.comparisonstrategy;
import net.codjo.test.common.excel.ExcelComparator;
import net.codjo.test.common.excel.ExcelUtil;
import net.codjo.test.common.excel.matchers.SheetMatcher;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
/**
 *
 */
public class ExcelComparisonStrategy implements ComparisonStrategy {

    private File expectedFile;
    private File actualFile;
    private final boolean styleTest;


    public ExcelComparisonStrategy(File expectedFile, File actualFile, boolean styleTest) {
        this.expectedFile = expectedFile;
        this.actualFile = actualFile;
        this.styleTest = styleTest;
    }


    public boolean compare() throws Exception {
        HSSFWorkbook expectedWorkbook = ExcelUtil.loadWorkbook(expectedFile);
        HSSFWorkbook actualWorkbook = ExcelUtil.loadWorkbook(actualFile);

        final List<SheetMatcher> sheetMatch = new ArrayList<SheetMatcher>();
        if (styleTest) {
            sheetMatch.addAll(ExcelComparator.STYLE_MATCHER_LIST.values());
        }

//        try {
            return ExcelComparator.execute(expectedWorkbook, actualWorkbook, Collections.<String>emptyList(),
                                           sheetMatch);
//        }
//        catch (ExcelMatchingException ex) {
//            throw new Exception(ex.getMessage());
//        }
    }
}
