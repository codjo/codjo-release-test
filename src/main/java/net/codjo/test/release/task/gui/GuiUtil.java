package net.codjo.test.release.task.gui;
import java.awt.Color;

import static java.lang.Integer.valueOf;

public class GuiUtil {
    public static Color convertToColor(String rgb) {
        String[] rgbArray = rgb.split(",");
        try {
            return new Color(valueOf(rgbArray[0]), valueOf(rgbArray[1]), valueOf(rgbArray[2]));
        }
        catch (NumberFormatException e) {
            throw new GuiException("Invalid rgb format : " + rgb, e);
        }
    }


    public static boolean equals(Color firstColor, Color secondColor) {
        return firstColor.getRed() == secondColor.getRed()
               && firstColor.getGreen() == secondColor.getGreen()
               && firstColor.getBlue() == secondColor.getBlue();
    }
}
