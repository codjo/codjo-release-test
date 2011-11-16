/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task;
import junit.framework.AssertionFailedError;
/**
 */
public final class Util {
    private Util() {
    }


    public static boolean isNotEmpty(String value) {
        return value != null && !"".equalsIgnoreCase(value.trim());
    }


    public static void compare(String expected, String actual) {
        if (expected.equals(actual)) {
            return;
        }
        for (int i = 0; i <= expected.length(); i++) {
            if (!actual.startsWith(expected.substring(0, i))) {
                int min = Math.max(0, i - 30);
                String expect = "..." + expected.substring(min, Math.min(i + 30, expected.length())) + "...";
                String result = "..." + actual.substring(min, Math.min(i + 30, actual.length())) + "...";
                throw new AssertionFailedError("Comparaison"
                                               + "\n\texpected = " + expect
                                               + "\n\tactual   = " + result);
            }
        }
    }


    /**
     * Mise à plat de la chaîne de charactère (sans saut de ligne).
     */
    public static String flatten(String str) {
        StringBuffer buffer = new StringBuffer();
        boolean previousWhite = true;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\r' || ch == '\n') {
                ; // à ignorer
            }
            else if (Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
                if (!previousWhite) {
                    buffer.append(" ");
                }
                previousWhite = true;
            }
            else {
                buffer.append(ch);
                previousWhite = false;
            }
        }

        return buffer.toString();
    }


    public static String computeClassName(Class aClass) {
        String fullName = aClass.getName();
        if (!fullName.contains(".")) {
            return fullName;
        }
        return fullName.substring(fullName.lastIndexOf(".") + 1, fullName.length());
    }
}
