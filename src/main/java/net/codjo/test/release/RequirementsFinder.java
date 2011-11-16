/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.apache.tools.ant.DirectoryScanner;
/**
 * 
 */
public class RequirementsFinder {
    private String rootPath;

    public RequirementsFinder(String aRootPath) {
        this.rootPath = aRootPath;
    }

    public Set getPathes() {
        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setBasedir(rootPath);
        directoryScanner.setCaseSensitive(false);
        directoryScanner.setIncludes(new String[] {"**/_requirement.xrl"});
        directoryScanner.scan();

        String[] relativeFilePathArray = directoryScanner.getIncludedFiles();
        Set result = new HashSet();

        for (int i = 0; i < relativeFilePathArray.length; i++) {
            String relativeFilePath = relativeFilePathArray[i];
            result.add(new File(rootPath, relativeFilePath));
        }

        return result;
    }
}
