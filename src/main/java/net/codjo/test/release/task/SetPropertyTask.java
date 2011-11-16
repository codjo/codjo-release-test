package net.codjo.test.release.task;
/**
 *
 */
public class SetPropertyTask extends AgfTask {

    private String name;

    private String remoteValue;

    private String localValue;


    @Override
    public void execute() {
        if (getTestEnvironement().isRemoteMode()) {
            getProject().setNewProperty(name, getProject().replaceProperties(remoteValue));
        }
        else {
            getProject().setNewProperty(name, getProject().replaceProperties(localValue));
        }
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getRemoteValue() {
        return remoteValue;
    }


    public void setRemoteValue(String remoteValue) {
        this.remoteValue = remoteValue;
    }


    public String getLocalValue() {
        return localValue;
    }


    public void setLocalValue(String localValue) {
        this.localValue = localValue;
    }
}
