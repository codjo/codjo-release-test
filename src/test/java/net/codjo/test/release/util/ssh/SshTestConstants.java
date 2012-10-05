package net.codjo.test.release.util.ssh;


/**
 *
 */
public interface SshTestConstants {
    String SAM_HOST = "wd-sam";
    String SAM_PORT = "22"; // default ssh port
    String SAM_LOGIN = "samexdev";
    String SAM_PROJECT_DIR = "/AGFAM/DEV/OSI_WEB1/SAM";
    
    public static class SAMChecker extends HostChecker {
    	public SAMChecker() {
    		super(SAM_HOST, SAM_PORT);
		}
    }
}
