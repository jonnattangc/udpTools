/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import utils.UtilLine;

/**
 * @author Copyright(c) Jonnattan Griffiths
 * @version 1.1 de 07-02-2023
 * @since {@link  https://dev.jonnattan.com}
 */
@SuppressWarnings("deprecation")
public class FileManager extends Observable implements Runnable {
  public static String APPVERSION = "V2.0";
  private final static String TAG = "@UDPTOOLS";
  private final static int TOKEN = 3;
  private FileWriter fichero = null;
  private PrintWriter pw = null;
  private String name = null;
  private long waitTimeNS = 0;
  private boolean isCircular = true;
  private boolean isHexaFormat = true;
  private String title = "";
  private JLabel lbl = null;
  private SimpleDateFormat hourFormat = null;
  private Date currentDate = null;
  private BlockingQueue<UtilLine> queue = null;
  private volatile boolean terminated = true;
  private boolean isWrite = false;
  private Thread thread = null;
  private String version = APPVERSION;

  public FileManager(String name, JLabel albl) {
    this.name = name;
    this.lbl = albl;
    this.isWrite = (albl == null);
    hourFormat = new SimpleDateFormat("HH:mm:ss");
    currentDate = new Date();
    queue = new LinkedBlockingQueue<UtilLine>();
  }

  public void start() {
    terminated = false;
    thread = new Thread(this);
    thread.start();
  }

  public void stop() {
    terminated = true;
    try {
      if (thread != null)
        thread.interrupt();
      fileClose();
      if (thread != null)
        thread.join();
    } catch (Exception e) {
      e.printStackTrace();
    }
    thread = null;
  }

  public long getFrecuency() {
    return waitTimeNS;
  }

  public void setCircular(boolean value) {
    isCircular = value;
  }

  public void setHexaFormat(boolean aValue) {
    this.isHexaFormat = aValue;
  }

  public void setFrecuency_ms(long frecuency) {
    this.waitTimeNS = frecuency;
  }

  public void setTitle(String p) {
    this.title = p;
  }

  public int sendToFile(final UtilLine utilLine) {
    queue.add(utilLine.Clone());
    return queue.size();
  }

  private boolean writeInFile(final UtilLine line) {
    boolean success = true;
    currentDate.setTime(System.currentTimeMillis());
    String out = hourFormat.format(currentDate) + ";" + line.getWaitNS() + ";"
        + getHexaDataText(line.getData());
    pw.println(out);
    return success;
  }

  private String getHexaDataText(final byte[] bytes) {
    String out = "";
    for (int i = 0; i < bytes.length; i++)
      out += String.format("%02X", bytes[i]);
    return out;
  }

  public void fileClose() {
    if (fichero != null) {
      try {
        pw.flush();
        fichero.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  @Override
  public void run() {
    if (isWrite) {
      try {
        currentDate.setTime(System.currentTimeMillis());
        SimpleDateFormat nameFormat = new SimpleDateFormat(
            "_dd_MM_yyyy_HH_mm_ss");
        String dateActual = nameFormat.format(currentDate);
        fichero = new FileWriter(name + dateActual + ".txt");
        pw = new PrintWriter(fichero);
        pw.println(TAG + ";" + APPVERSION + ";" + title);
        nameFormat = null;
      } catch (Exception e) {
        e.printStackTrace();
      }

      while (!terminated) {
        try {
          UtilLine line = queue.take();
          if (!writeInFile(line))
            System.err.println("ERROR: Escribiendo Archivo ");
          line = null;
        } catch (InterruptedException e) {
          break;
        }
      }
    } else {
      int linesCount = 0;
      BufferedReader br = null;
      boolean formato = false;
      while (!terminated) {
        try {
          linesCount = 0;
          br = new BufferedReader(new FileReader(new File(name)));
          String linea = new String();
          while ((linea = br.readLine()) != null && !terminated) {
            linesCount++;
            if (isHexaFormat) {
              StringTokenizer stk = new StringTokenizer(linea, ";");
              if (stk.countTokens() == TOKEN) {
                while (stk.hasMoreTokens()) {
                  String firstWord = stk.nextToken();
                  if (firstWord.equals(TAG)) {
                    // Primera linea del protocolo
                    // Segundo parametro es la version
                    version = stk.nextToken();
                    System.out.println("HEADER: " + stk.nextToken());
                    formato = true;
                  } else {
                    setDateHour(firstWord);
                    String sleep = stk.nextToken();
                    try {
                      waitTimeNS = Long.parseLong(sleep);
                      // Paso a nano seg para trabajar en nano todo
                      if (version.equalsIgnoreCase(APPVERSION))
                        waitTimeNS = waitTimeNS * 1000000l;
                      byte[] bArray = getEncode(stk.nextToken());
                      UtilLine fline = new UtilLine(bArray, waitTimeNS);
                      setChanged();
                      notifyObservers(fline);
                      bArray = null;
                      fline = null;
                    } catch (NumberFormatException ex) {
                      // ex.printStackTrace();
                    }
                  }
                  if (!formato) {
                    JOptionPane.showMessageDialog(null,
                        "The file is not in standard format", "Error",
                        JOptionPane.ERROR_MESSAGE);
                    terminated = true;
                  }
                }
              } else {
                JOptionPane.showMessageDialog(null,
                    "Read file whit fail messagge format", "Error",
                    JOptionPane.ERROR_MESSAGE);
                terminated = true;
              }
            } else {
              linea = linea + "\r" + "\n";
              byte[] bArray = getEncode(linea);
              UtilLine fline = new UtilLine(bArray, waitTimeNS);
              setChanged();
              notifyObservers(fline);
              bArray = null;
              fline = null;
            }
          } // Fin de archivo
          if (!isCircular) {
            terminated = true;
          } else {
            if (!terminated) {
              long espera = linesCount * waitTimeNS;
              if (version.equalsIgnoreCase(APPVERSION))
                espera = (espera / 1000000l);
              System.out.println(
                  "Comienzo nuevamente a leer archivo en " + espera + " ms");
              Thread.sleep(espera);
            }
          }
          linesCount = 0;
        } catch (IOException ex) {
          // ex.printStackTrace();
        } catch (InterruptedException ex) {
          // ex.printStackTrace();
        }
      }
      fileClose();
    }
    System.out.println("Thread FileManager Terminado");
  }

  private byte[] getEncode(final String lineInHexa) {
    ByteBuffer bb;
    if (isHexaFormat) {
      bb = ByteBuffer.allocate(lineInHexa.length() / 2);
      bb.order(ByteOrder.LITTLE_ENDIAN);
      // System.out.println("Linea: " + linea);
      for (int i = 0; i < lineInHexa.length(); i = i + 2)
        bb.put(getHexa(lineInHexa.substring(i, i + 2)));
    } else {
      bb = ByteBuffer.allocate(lineInHexa.length());
      bb.order(ByteOrder.LITTLE_ENDIAN);
      for (int i = 0; i < lineInHexa.length(); i++)
        bb.put((byte) lineInHexa.charAt(i));
    }
    bb.flip();
    byte[] array = new byte[bb.remaining()];
    bb.get(array);
    bb = null;
    return array;
  }

  private byte getHexa(String dosChars) {
    byte dato = 0;
    String aux = "";
    for (int i = 0; i < 256; i++) {
      aux = Integer.toHexString(i).toUpperCase();
      if (i < 16)
        aux = "0" + aux;
      if (dosChars.equals(aux)) {
        dato = (byte) i;
        break;
      }
    }
    return dato;
  }

  /**
   * @param dateHour the dateHour to set
   */
  public synchronized void setDateHour(String dateHour) {
    if (lbl != null) {
      lbl.setText(dateHour);
      lbl.setVisible(true);
    }
  }
}
