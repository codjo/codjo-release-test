package net.codjo.test.release.util.comparisonstrategy;
import net.codjo.test.common.PathUtil;
import java.io.File;
/**
 *
 */
public class ComparisonStrategyTestCase {

    protected File toFile(String fileName) {
        return PathUtil.find(getClass(), fileName);
    }
}
