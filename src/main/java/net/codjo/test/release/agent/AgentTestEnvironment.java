/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.agent;
import net.codjo.test.release.AbstractTestEnvironment;
import net.codjo.test.release.task.user.SenderHelperable;
import org.apache.tools.ant.Project;
/**
 * Environement de test pour le mode agent.
 */
public class AgentTestEnvironment extends AbstractTestEnvironment {
    public AgentTestEnvironment(Project project) {
        super(project);
    }

    public SenderHelperable createSendStrategy(String userParam, String passwordParam) {
        throw new UnsupportedOperationException("L'utilisation de la balise send-request est interdit. "
                                                + "Veulliez utiliser les tests IHM");
    }
}
