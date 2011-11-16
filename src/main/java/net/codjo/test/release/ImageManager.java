package net.codjo.test.release;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;

/**
 * This class gathers the functions useful for the images.
 */

public class ImageManager

{
	// Log
	private static final Logger logger = Logger.getLogger(ImageManager.class);

	// Constructeur


	/**
	 * Constructeur de la classe.
	 */
	private ImageManager() {
	}

	// Methods


	/**
	 * Save a buffzered image in a file.
	 *
	 * @param fileName	  The name of the file.
	 * @param bufferedImage The buffered image.
	 */
	public static void bufferedImageToImage(String fileName, BufferedImage bufferedImage) {
		FileOutputStream out;
		JPEGImageEncoder encoder;
		JPEGEncodeParam param;

		try {
			File file = new File(fileName);
			file.createNewFile();
			out = new FileOutputStream(file);

			encoder = JPEGCodec.createJPEGEncoder(out);
			param = encoder.getDefaultJPEGEncodeParam(bufferedImage);

			encoder.setJPEGEncodeParam(param);
			encoder.encode(bufferedImage);

			out.flush();
			out.close();
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
	}


	/**
	 * Convert a buffered image in an image.
	 *
	 * @param bufferedImage The buffered image to convert.
	 *
	 * @return The Image object.
	 */
	public static Image toImage(BufferedImage bufferedImage) {
		int width;
		int height;
		Image image;

		width = bufferedImage.getWidth();
		height = bufferedImage.getHeight();
		image = bufferedImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);

		return image;
	}


	/**
	 * Convert an image in an instance of BufferedImage.
	 *
	 * @param image The image to convert.
	 *
	 * @return The BufferedImage object.
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage)image;
		}
		else {
			image = new ImageIcon(image).getImage();
			BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
															BufferedImage.TYPE_INT_RGB);
			Graphics graphics = bufferedImage.createGraphics();
			graphics.drawImage(image, 0, 0, null);
			graphics.dispose();

			return bufferedImage;
		}
	}


	/**
	 * Set an homothety on a image.
	 *
	 * @param bufferedImage The image.
	 * @param scaleValue	The homothetie value.
	 *
	 * @return The new image.
	 */
	public static BufferedImage scale(BufferedImage bufferedImage, double scaleValue) {
		AffineTransform tx = new AffineTransform();
		tx.scale(scaleValue, scaleValue);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage biNew = new BufferedImage((int)(bufferedImage.getWidth() * scaleValue),
												(int)(bufferedImage.getHeight() * scaleValue),
												bufferedImage.getType());
		return op.filter(bufferedImage, biNew);
	}


	/**
	 * Convert a color image in a grey level image.
	 *
	 * @param bufferedImage The color image.
	 *
	 * @return The grey level image.
	 */
	public static BufferedImage toGreyLevel(BufferedImage bufferedImage) {
		ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		return op.filter(bufferedImage, null);
	}


	/**
	 * Get a screen capture in an image representation.
	 *
	 * @param xCoord	  The specified xCoord coordinate of the capture
	 * @param yCoord	  The specified yCoord coordinate of the capture
	 * @param width  The width of the capture
	 * @param height The height of the capture
	 *
	 * @return The buffered image.
	 */
	public static BufferedImage getScreenCapture(int xCoord, int yCoord, int width, int height) {
		Robot robot;
		Rectangle rectangle;
		BufferedImage bufferedImage = null;

		try {
			robot = new Robot();

			rectangle = new Rectangle(xCoord, yCoord, width, height);
			bufferedImage = robot.createScreenCapture(rectangle);
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
		return bufferedImage;
	}


	/**
	 * Get a screen capture in an image representation.
	 *
	 * @param component The component to capture
	 *
	 * @return The buffered image.
	 */
	public static BufferedImage getScreenCapture(Component component) {
		Robot robot;
		Rectangle rectangle;
		BufferedImage bufferedImage = null;

		try {
			robot = new Robot();

			rectangle = new Rectangle(component.getLocation().x, component.getLocation().y,
									  component.getWidth(), component.getHeight());
			bufferedImage = robot.createScreenCapture(rectangle);
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
		return bufferedImage;
	}
}