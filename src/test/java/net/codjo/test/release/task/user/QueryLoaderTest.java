/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.user;
import java.io.File;
import junit.framework.TestCase;
import net.codjo.test.common.XmlUtil;

public class QueryLoaderTest extends TestCase {
    private QueryLoader loader;

    public void test_load() throws Exception {
        String pathname = QueryLoaderTest.class.getResource("queries.xml").getPath();
        String[] query = loader.load(new File(pathname));

        assertEquals(2, query.length);
        XmlUtil.assertEquivalent("<insert request_id='7'/>", query[0]);
        assertEquals("query é 2", query[1]);
    }


    @Override
    protected void setUp() throws Exception {
        loader = new QueryLoader();
    }
}
