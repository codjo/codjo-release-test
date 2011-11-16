package net.codjo.test.release.task.gui.metainfo;
import net.codjo.test.release.task.gui.GuiException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
/**
 *
 */
public class Introspector {
    public static final String SWING_PACKAGE_PREFIX = "javax.swing";
    public final static String DESCRIPTOR_POSTFIX = "TestBehavior";
    private Class descriptorClass;
    // Static Caches to speed up introspection.
    private static final Map<Class, Map<Class, Object>> testBehaviorCache = Collections
          .synchronizedMap(new WeakHashMap<Class, Map<Class, Object>>());


    private Introspector(Class descriptorClass) {
        this.descriptorClass = descriptorClass;
    }


    public static <T> T getTestBehavior(Class componentClass, Class<T> descriptorClass) {
        return descriptorClass.cast(new Introspector(descriptorClass).getTestBehavior(componentClass));
    }


    public static void flushCache() {
        testBehaviorCache.clear();
    }


    private Object getTestBehavior(Class compClass) {
        if (compClass == null) {
            return null;
        }
        if (compClass.getPackage() == null
            || compClass.getPackage().getName() == null
            || compClass.getPackage().getName().startsWith(SWING_PACKAGE_PREFIX)) {
            return null;
        }
        if (testBehaviorCacheContains(compClass, descriptorClass)) {
            return testBehaviorCacheGet(compClass, descriptorClass);
        }

        Object genericBehavior = findTestBehaviorClass(compClass);
        testBehaviorCachePut(compClass, descriptorClass, genericBehavior);
        return genericBehavior;
    }


    private Object findTestBehaviorClass(Class compClass) {
        Object genericBehavior;
        String testBehaviorClassName = compClass.getName() + DESCRIPTOR_POSTFIX;
        try {
            genericBehavior = Class.forName(testBehaviorClassName).newInstance();
            if (!descriptorClass.isInstance(genericBehavior)) {
                genericBehavior = getTestBehavior(compClass.getSuperclass());
            }
        }
        catch (ClassNotFoundException exception) {
            genericBehavior = getTestBehavior(compClass.getSuperclass());
        }
        catch (IllegalAccessException exception) {
            throw new GuiException("Attention la classe " + testBehaviorClassName + " n'est pas accessible.",
                                   exception);
        }
        catch (InstantiationException exception) {
            throw new GuiException("Erreur d'instanciation de la classe " + testBehaviorClassName + ".",
                                   exception);
        }

        return genericBehavior;
    }


    public static Map<Class, Map<Class, Object>> getTestBehaviorCache() {
        return testBehaviorCache;
    }


    static void testBehaviorCachePut(Class componentClass, Class descriptorClass, Object genericBehavior) {
        if (!testBehaviorCache.containsKey(componentClass)) {
            testBehaviorCache
                  .put(componentClass, Collections.synchronizedMap(new WeakHashMap<Class, Object>()));
        }
        testBehaviorCache.get(componentClass).put(descriptorClass, genericBehavior);
    }


    static boolean testBehaviorCacheContains(Class componentClass, Class descriptorClass) {
        return testBehaviorCache.containsKey(componentClass)
               && testBehaviorCache.get(componentClass).containsKey(descriptorClass);
    }


    static Object testBehaviorCacheGet(Class componentClass, Class descriptorClass) {
        if (testBehaviorCache.containsKey(componentClass)) {
            return testBehaviorCache.get(componentClass).get(descriptorClass);
        }
        return null;
    }
}
