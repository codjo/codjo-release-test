/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.agent;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
/**
 * Classe de test de {@link AgentTestEnvironment}.
 */
public class AgentTestEnvironmentTest extends TestCase {
    public void test_sendDisabled() throws Exception {
        AgentTestEnvironment agentTestEnvironment = new AgentTestEnvironment(new Project());
        try {
            agentTestEnvironment.createSendStrategy("user", "password");
            fail();
        }
        catch (UnsupportedOperationException ex) {
            ; // Ok
        }
    }
}
