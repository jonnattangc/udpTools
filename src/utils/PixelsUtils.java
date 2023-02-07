package utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Clase que provee operaciones sobre pixeles e imagenes
 * 
 * @author Eliana Providel
 */
public class PixelsUtils {

  private static PixelsUtils pixelUtils = null;

  /**
   * Contructor privado
   */
  private PixelsUtils() {
  }

  public static PixelsUtils getInstance() {
    if (pixelUtils == null)
      pixelUtils = new PixelsUtils();
    return pixelUtils;
  }

  public BufferedImage createBufferedImage(int width, int height) {
    return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  }

  /**
   * Retorna una arreglo de pixeles de tamaño [height][width] con los pixeles
   * de la imagen entregada, partiendo desde la posición x,y hasta completar el
   * area especificada
   * 
   * @param pic    imagen base
   * @param x      posicion inicial horizontal
   * @param y      posicion inicial vertical
   * @param width  ancho
   * @param height alto
   * @return arreglo de pixeles
   */
  public Color[][] getPixels(BufferedImage pic, int x, int y, int width,
      int height) {
    assert pic != null : "Error en getpixel por la imagen";
    // System.out.println("getPixels: x="+x+" y="+y+" width="+width+"
    // height="+height);
    Color bitmap[][] = new Color[height][width];
    int a = 0, b = 0;
    for (int i = y; i < (y + height); i++) {
      b = 0;
      for (int j = x; j < (x + width); j++) {
        bitmap[a][b] = new Color(pic.getRGB(j, i));
        b++;
      }
      a++;
    }
    assert bitmap.length != 0 : "Error en bitmap, pixeles de la imagen";
    return bitmap;
  }

  /**
   * retorna un bufferedImage del tamaño especificado
   * 
   * @param path ruta de la imagen
   */
  public BufferedImage fileToBufferedImage(String path, int width, int height) {
    PixelsUtils pixelsUtils = PixelsUtils.getInstance();
    assert !path.isEmpty() : "Error al abrir imagen, path no especificado";
    BufferedImage imagen = null;
    try {

      imagen = ImageIO.read(new File(path));
      imagen = pixelsUtils.resizeImage(imagen, width, height);
    } catch (IOException ex) {
      Logger.getLogger(PixelsUtils.class.getName()).log(Level.SEVERE, null, ex);
    }
    return imagen;
  }

  /**
   * retorna un bufferedImage del tamaño especificado
   * 
   * @param path ruta de la imagen
   */
  public BufferedImage fileToBufferedImage(URL path, int width, int height) {
    PixelsUtils pixelsUtils = PixelsUtils.getInstance();
    BufferedImage imagen = null;
    try {
      if (path != null) {
        imagen = ImageIO.read(path);
        imagen = pixelsUtils.resizeImage(imagen, width, height);
      } else
        System.err.println("Icon Path is null");
    } catch (IOException ex) {
      System.err.println("Error al abrir " + path.toExternalForm());
      Logger.getLogger(PixelsUtils.class.getName()).log(Level.SEVERE, null, ex);
    }
    return imagen;
  }

  /**
   * Transforma de java.awt.Image a java.awt.image.BufferedImage
   * 
   * @param image imagen como Image
   * @return imagen como BufferedImage
   */
  public BufferedImage toBufferedImage(Image image) {
    if (image instanceof BufferedImage) {
      return (BufferedImage) image;
    }
    image = new ImageIcon(image).getImage();
    boolean hasAlpha = hasAlpha(image);
    BufferedImage bimage = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try {
      int transparency = Transparency.OPAQUE;
      if (hasAlpha) {
        transparency = Transparency.BITMASK;
      }

      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();
      bimage = gc.createCompatibleImage(image.getWidth(null),
          image.getHeight(null), transparency);
    } catch (HeadlessException e) {
    }

    if (bimage == null) {
      int type = BufferedImage.TYPE_INT_RGB;
      if (hasAlpha) {
        type = BufferedImage.TYPE_INT_ARGB;
      }
      bimage = new BufferedImage(image.getWidth(null), image.getHeight(null),
          type);
    }
    Graphics g = bimage.createGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();

    return bimage;
  }

  /**
   * Retorna el color promedio de un arreglo de pixeles
   * 
   * @param pixels arreglo de pixeles base
   * @param width  ancho
   * @param height alto
   * @return color promedio
   */
  public int getAverageColorOfPixelArray(Color[][] pixels, int width,
      int height) {
    int red = 0, green = 0, blue = 0, alpha = 0;
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        red += pixels[j][i].getRed();
        green += pixels[j][i].getGreen();
        blue += pixels[j][i].getBlue();
        alpha += pixels[j][i].getAlpha();
      }
    }
    red = red / (width * height);
    green = green / (width * height);
    blue = blue / (width * height);
    alpha = alpha / (width * height);
    return ((alpha << 24) & 0xFF000000) | ((red << 16) & 0x00FF0000)
        | ((green << 8) & 0x0000FF00) | ((blue) & 0x000000FF);
  }

  public int getGreenIntesity(byte intensity) {
    return ((((int) intensity) << 8) & 0x0000FF00);
  }

  public void saveBufferedImageToPngFile(Object objectToSave, String path) {
    try {
      BufferedImage imageToSave = (BufferedImage) objectToSave;
      ImageIO.write(imageToSave, "png", new File(path + ".png"));
    } catch (IOException ex) {
      Logger.getLogger(PixelsUtils.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Calcula la varianza de los pixeles de un arreglo dado
   * 
   * @param pixels arreglo de pixeles
   * @param width  ancho
   * @param height alto
   * @return varianza de intensidad de los pixeles del arreglo
   */
  public double getVarianceOfPixelsArray(Color[][] pixels, int width,
      int height) {
    assert pixels.length > 0 : "ERROR al obtener la diferencia máxima de pixeles";
    double sum = 0;
    int average = getAverageIntensityOfPixelsArray(pixels, width, height);
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        double res = Math
            .pow(getIntensityOfRGBColor(pixels[j][i].getRGB()) - average, 2);
        sum += res;
      }
    }
    return (sum / ((width * height) - 1));
  }

  /**
   * retorna un vector ordenado de intensidad de pixeles
   * 
   * @param pixels arreglo de pixeles
   * @param width  ancho
   * @param height alto
   * @return vector ordenado de intensidad de pixeles
   */
  @SuppressWarnings("deprecation")
  public Vector<Integer> getSortedVectorOfIntensities(Color[][] pixels,
      int width, int height) {
    Vector<Integer> vec = new Vector<Integer>(width * height);
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        vec.add(new Integer(getIntensityOfRGBColor(pixels[j][i].getRGB())));
      }
    }
    Collections.sort(vec);
    return vec;
  }

  /**
   * Retorna la diferencia maxima entre 2 pixeles de una arreglo dado
   * 
   * @param pixels arreglo de pixeles
   * @param width  ancho
   * @param height alto
   * @return diferencia maxima
   */
  public int getMaxDiffOfPixelsArray(Color[][] pixels, int width, int height) {
    assert pixels.length > 0 : "ERROR al obtener la diferencia máxima de pixeles";
    int value;
    int min = 766, max = -1;
    int minPos[] = new int[2];
    int maxPos[] = new int[2];
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        value = getIntensityOfRGBColor(pixels[j][i].getRGB());
        if (value < min) {
          min = value;
          minPos[0] = j;
          minPos[1] = i;
        }
        if (value > max) {
          max = value;
          maxPos[0] = j;
          maxPos[1] = i;
        }
      }
    }
    return getDifferenceBetweenTwoColorInts(
        pixels[minPos[0]][minPos[1]].getRGB(),
        pixels[maxPos[0]][maxPos[1]].getRGB());
  }

  /**
   * Calcula el promedio de intensidad de un arreglo dado
   * 
   * @param pixels arreglo de pixeles
   * @param width  ancho
   * @param height alto
   * @return promedio de intensidad
   */
  private int getAverageIntensityOfPixelsArray(Color[][] pixels, int width,
      int height) {
    assert pixels.length > 0 : "ERROR al obtener el promedio";
    int sum = 0, value;
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        value = getIntensityOfRGBColor(pixels[j][i].getRGB());
        sum += value;
      }
    }
    return (sum / (height * width));
  }

  /**
   * redimensiona una imagen manteniendo su aspecto, para cuadrar el tamaño
   * dado
   * 
   * @param myimg  imagen a redimensionar
   * @param width  ancho máximo
   * @param height alto máximo
   * @return imagen redimensionada
   */
  public BufferedImage resizeImage(BufferedImage myimg, int width, int height) {
    assert myimg != null : "Error en la imagen en resizeImage(..)";
    BufferedImage imageResized;
    float ratio = ((float) myimg.getWidth()) / ((float) myimg.getHeight());
    if (ratio >= ((float) width / (float) height)) {
      imageResized = toBufferedImage(
          myimg.getScaledInstance(width, -1, Image.SCALE_SMOOTH));
    } else {
      imageResized = toBufferedImage(
          myimg.getScaledInstance(-1, height, Image.SCALE_SMOOTH));
    }
    return imageResized;
  }

  private int getDifferenceBetweenTwoColorInts(int c1, int c2) {
    assert c1 >= 0
        && c2 >= 0 : "Error al encontrar la diferencia entre colores";
    return (int) Math
        .sqrt(Math.pow((((c1 >> 24) & 0xff) - ((c2 >> 24) & 0xff)), 2)
            + Math.pow((((c1 >> 16) & 0xff) - ((c2 >> 16) & 0xff)), 2)
            + Math.pow((((c1 >> 8) & 0xff) - ((c2 >> 8) & 0xff)), 2)
            + Math.pow(((c1 & 0xff) - (c2 & 0xff)), 2));
  }

  private int getIntensityOfRGBColor(int rgb) {
    assert rgb >= 0 : "Error de RGB";
    return (((rgb >> 16) & 0xff) + ((rgb >> 8) & 0xff) + (rgb & 0xff));
  }

  private boolean hasAlpha(Image image) {
    if (image instanceof BufferedImage) {
      BufferedImage bimage = (BufferedImage) image;
      return bimage.getColorModel().hasAlpha();
    }
    PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
    }

    ColorModel cm = pg.getColorModel();
    return cm.hasAlpha();
  }
}
