package net.codjo.test.release.task.web.finder;
import net.codjo.test.release.task.web.WebContext;
/**
 *
 */
public interface IComponentFinder<T> {
    public T find(WebContext context, ResultHandler resultHandler);
}
