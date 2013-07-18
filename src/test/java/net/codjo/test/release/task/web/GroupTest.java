package net.codjo.test.release.task.web;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.codjo.test.release.task.util.TestLocation;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import static net.codjo.test.common.matcher.JUnitMatchers.*;

public class GroupTest {
    private Group groupTest;


    @Test
    public void test_proceed() throws Exception {
        groupTest = new Group();
        groupTest.setName("groupEnabled");
        groupTest.setEnabled(true);
        assertStepProceed(1, "groupEnabled", 1, "Step 1 du groupe 'groupEnabled' ");

        groupTest = new Group();
        groupTest.setName("groupDisabled");
        groupTest.setEnabled(false);
        assertStepProceed(0, "groupDisabled", 0, "Localisation impossible");
    }


    @Test
    public void test_allTagsAreDeclared() throws Exception {
        final List<String> webTaskMethods = extractAddMethods(WebTask.class);
        final List<String> groupMethods = extractAddMethods(Group.class);

        for (String groupMethod : groupMethods) {
            final boolean remove = webTaskMethods.remove(groupMethod);
            if (!remove) {
                break;
            }
        }

        assertThat(webTaskMethods.isEmpty(), is(true));
    }


    private List<String> extractAddMethods(Class webTaskClass) {
        final Method[] webtaskMethods = webTaskClass.getDeclaredMethods();
        List<String> resultList = new ArrayList<String>();
        for (Method webtaskMethod : webtaskMethods) {
            final String name = webtaskMethod.getName();
            if (name.startsWith("add")) {
                resultList.add(name);
            }
        }
        return resultList;
    }


    private void assertStepProceed(int count, String groupName, int stepNumber, String locationMessage)
          throws IOException {
        Click mockedClickStep = Mockito.mock(Click.class);

        WebContext context = Mockito.mock(WebContext.class);
        final TestLocation testLocation = new TestLocation();
        Mockito.when(context.getTestLocation()).thenReturn(testLocation);

        groupTest.addClick(mockedClickStep);

        groupTest.proceed(context);

        Mockito.verify(mockedClickStep, new Times(count)).proceed(context);
        assertThat(context.getTestLocation().getLocationMessage(),
                   containsString(locationMessage));

        assertThat(testLocation.getGroupName(), is(groupName));
        assertThat(testLocation.getStepNumber(), is(stepNumber));
    }
}
