/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.ant;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import junit.framework.TestCase;
/**
 * Classe de test de {@link AntRunner}.
 */
public class AntRunnerTest extends TestCase {
    private PrintStream realOut;
    private LoggedOutputStream loggedOutputStream;

    public void test_simple() throws Exception {
        AntRunner.start(getResourceFile(AntRunnerTest.class, "AntRunnerTest.ant"));

        ByteArrayOutputStream expectedOutputStream = new ByteArrayOutputStream();
        PrintWriter expectedPrintWriter = new PrintWriter(expectedOutputStream, true);
        expectedPrintWriter.println();
        expectedPrintWriter.println("test:");
        expectedPrintWriter.println("     [echo] Hello World !");
        expectedPrintWriter.println("     [echo] version = Apache Ant version 1.6.5 compiled on June 2 2005");

        assertEquals(expectedOutputStream.toString(), loggedOutputStream.toString());
    }


    /**
     * Permet d'obtenir l'objet {@link File} associé à une ressource relative à une classe donnée. La
     * ressource doit être dans un répertoire du disque local, et non située dans un fichier jar.
     *
     * @param clazz classe à laquelle le nom de resource est relatif
     * @param fileName nom relatif du fichier ressource
     *
     * @return l'objet File.
     */
    private static File getResourceFile(Class clazz, String fileName) {
        URL url = clazz.getResource(fileName);
        return new File(url.getFile());
    }


    @Override
    protected void setUp() throws Exception {
        realOut = System.out;
        loggedOutputStream = new LoggedOutputStream();
        System.setOut(new PrintStream(loggedOutputStream));
    }


    @Override
    protected void tearDown() throws Exception {
        System.setOut(realOut);
    }

    /**
     * Cette classe est un OutputStream qui redirige ses données vers System.out et qui les conderve
     * en mémoire.
     */
    private class LoggedOutputStream extends ByteArrayOutputStream {
        /**
         * Writes the specified byte to this byte array output stream.
         *
         * @param aByte the byte to be written.
         */
        @Override
        public synchronized void write(int aByte) {
            realOut.write(aByte);
            super.write(aByte);
        }


        /**
         * Writes <code>len</code> bytes from the specified byte array starting at offset
         * <code>off</code> to this byte array output stream.
         *
         * @param aByte the data.
         * @param off the start offset in the data.
         * @param len the number of bytes to write.
         */
        @Override
        public synchronized void write(byte[] aByte, int off, int len) {
            realOut.write(aByte, off, len);
            super.write(aByte, off, len);
        }
    }
}
