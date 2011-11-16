package net.codjo.test.release.task.gui.metainfo;
import javax.swing.JLabel;
import javax.swing.JTextField;
import junit.framework.TestCase;
/**
 *
 */
public class IntrospectorTest extends TestCase {
    public void test_getTestBehavior() throws Exception {
        assertNull(Introspector.getTestBehavior(JLabel.class, SetValueDescriptor.class));

        assertNotNull(Introspector.getTestBehavior(MyComponentMock.class, SetValueDescriptor.class));
        assertTrue(Introspector.getTestBehavior(MyComponentMock.class,
                                                SetValueDescriptor.class) instanceof MyComponentMockTestBehavior);

        assertNull(Introspector.getTestBehavior(MyComponentMock.class, ClickDescriptor.class));
    }


    public void test_testBehaviorCache() throws Exception {
        assertTrue(Introspector.getTestBehaviorCache().isEmpty());

        Introspector.getTestBehavior(MyComponentMock.class, SetValueDescriptor.class);
        assertEquals(1, Introspector.getTestBehaviorCache().size());
        assertTrue(Introspector.testBehaviorCacheContains(MyComponentMock.class, SetValueDescriptor.class));

        Introspector.flushCache();
        assertTrue(Introspector.getTestBehaviorCache().isEmpty());
        Introspector.getTestBehavior(MyComponentMock.class, SetValueDescriptor.class);
        assertEquals(1, Introspector.getTestBehaviorCache().size());
        Introspector.getTestBehavior(AnotherComponentMock.class, SetValueDescriptor.class);
        assertEquals(2, Introspector.getTestBehaviorCache().size());
        Introspector.getTestBehavior(YetAnotherComponentMock.class, SetValueDescriptor.class);
        assertEquals(3, Introspector.getTestBehaviorCache().size());
        assertTrue(Introspector.testBehaviorCacheContains(MyComponentMock.class, SetValueDescriptor.class));
        assertTrue(
              Introspector.testBehaviorCacheContains(AnotherComponentMock.class, SetValueDescriptor.class));
        assertTrue(Introspector.testBehaviorCacheContains(YetAnotherComponentMock.class,
                                                          SetValueDescriptor.class));
        assertFalse(
              Introspector.testBehaviorCacheContains(YetAnotherComponentMock.class, ClickDescriptor.class));
    }


    public void test_testBehaviorCache_swingComponent() throws Exception {
        assertTrue(Introspector.getTestBehaviorCache().isEmpty());
        Introspector.getTestBehavior(JTextField.class, SetValueDescriptor.class);
        assertTrue(Introspector.getTestBehaviorCache().isEmpty());
    }


    public void test_testBehaviorCache_inheritedClass() throws Exception {
        assertTrue(Introspector.getTestBehaviorCache().isEmpty());
        Introspector.getTestBehavior(YetAnotherComponentMock.class, SetValueDescriptor.class);
        assertEquals(2, Introspector.getTestBehaviorCache().size());
        assertTrue(Introspector.testBehaviorCacheContains(MyComponentMock.class, SetValueDescriptor.class));
        assertNotNull(Introspector.testBehaviorCacheGet(MyComponentMock.class, SetValueDescriptor.class));
        assertTrue(Introspector.testBehaviorCacheContains(YetAnotherComponentMock.class,
                                                          SetValueDescriptor.class));
        assertNotNull(
              Introspector.testBehaviorCacheGet(YetAnotherComponentMock.class, SetValueDescriptor.class));
    }


    public void test_testBehaviorCache_noTestBehaviorCacheClass() throws Exception {
        assertTrue(Introspector.getTestBehaviorCache().isEmpty());
        Introspector.getTestBehavior(MyComponentMock.class, ClickDescriptor.class);
        assertEquals(1, Introspector.getTestBehaviorCache().size());
        assertNull(Introspector.testBehaviorCacheGet(MyComponentMock.class, ClickDescriptor.class));
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Introspector.flushCache();
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Introspector.flushCache();
    }
}
