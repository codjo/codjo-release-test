package net.codjo.test.release.util.ssh;
import net.codjo.test.common.LogString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.verify;
/**
 *
 */
public abstract class SecureCommandTestCase<T extends SecureCommand> {
    private final LogString log = new LogString();
    private T secureCommand;
    private UnixSessionFactoryMock sessionFactoryMock;


    @Before
    public void setUp() {
        secureCommand = createSecureCommand();

        sessionFactoryMock = new UnixSessionFactoryMock(secureCommand.getUser(), secureCommand.getHost());

        secureCommand.setUnixSessionFactory(sessionFactoryMock);
    }


    protected abstract T createSecureCommand();


    public LogString getLog() {
        return log;
    }


    public UnixSessionFactoryMock getSessionFactoryMock() {
        return sessionFactoryMock;
    }


    public T getSecureCommand() {
        return secureCommand;
    }


    @Test
    public void test_execute_sessionKo() throws Exception {
        sessionFactoryMock.mockInvalidSession();

        try {
            secureCommand.execute();
            fail();
        }
        catch (Exception e) {
            assertEquals("Exception in createSession", e.getMessage());
        }
    }


    @Test
    public void test_execute_commandKo() throws Exception {
        sessionFactoryMock.mockCommandError();

        assertExecuteKo();
    }


    protected void assertExecuteOk(String... logContent) throws Exception {
        secureCommand.execute();

        log.assertContent(logContent);

        verify(sessionFactoryMock.getSessionMock()).connect();
        verify(sessionFactoryMock.getSessionMock()).disconnect();
    }


    protected void assertExecuteKo(String... logContent) throws Exception {
        try {
            secureCommand.execute();
            fail();
        }
        catch (Exception e) {
            ;
        }

        log.assertContent(logContent);

        verify(sessionFactoryMock.getSessionMock()).connect();
        verify(sessionFactoryMock.getSessionMock()).disconnect();
    }
}
