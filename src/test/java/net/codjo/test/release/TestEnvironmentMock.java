/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.ConnectionMock;
import net.codjo.test.release.task.user.SenderHelperable;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.tools.ant.Project;
/**
 *
 */
public class TestEnvironmentMock extends AbstractTestEnvironment {
    private LogString log = new LogString();
    private Connection connectionMock = new ConnectionMock().getStub();


    public TestEnvironmentMock(Project project) {
        super(project);
        log.setPrefix("TestEnvironment");
    }


    public SenderHelperable createSendStrategy(String user, String password) {
        log.call("createSendStrategy");
        return new SenderHelperableMock(new LogString("SenderHelper", log));
    }


    public LogString getLog() {
        return log;
    }


    @Override
    public Connection getConnection() throws SQLException {
        return connectionMock;
    }


    public TestEnvironmentMock mockGetConnection(Connection connection) {
        connectionMock = connection;
        return this;
    }


    private static final class SenderHelperableMock implements SenderHelperable {
        private LogString log;


        private SenderHelperableMock(LogString log) {
            this.log = log;
        }


        public void open() {
            log.call("open");
        }


        public void close() {
            log.call("close");
        }


        public String sendRequest(String text) {
            log.call("sendRequest", text);
            return "resultat";
        }
    }
}
