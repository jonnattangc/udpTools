package utils;

import java.util.Arrays;

/**
 * Clase que guarda una linea
 * 
 * @author Jonnattan Griffiths
 * @since 20/12/2013
 * @version 1.0 Copyright(c) SISDEF - 2017
 */
public class UtilLine
{
  private byte[] data             = null;
  private long   waitTime_nanoSec = 0;
  private String preffix          = "Rx";

  public UtilLine(final byte[] data, long waitns)
  {
    super();
    this.data = Arrays.copyOf(data, data.length);
    this.waitTime_nanoSec = waitns;
  }

  public byte[] getData()
  {
    return data;
  }

  public long getWaitNS()
  {
    return waitTime_nanoSec;
  }

  public long getWaitMS()
  {
    return (waitTime_nanoSec / 1000000l);
  }

  public UtilLine uglyClone(final String preffix)
  {
    this.preffix = preffix;
    UtilLine ul = new UtilLine(getData(), waitTime_nanoSec);
    return ul;
  }

  public UtilLine Clone()
  {
    this.preffix = "";
    UtilLine ul = new UtilLine(data, waitTime_nanoSec);
    return ul;
  }
  
  public String getString()
  {
    String out = "[" + preffix + " " + String.format("%05d", data.length)
        + " Bytes] ";
    for (int i = 0; i < data.length; i++)
    {
      out += String.format("%c", (char) data[i]);
      if (i > 300)
      {
        out += "...";
        break;
      }
    }
    return out;
  }

  public String getHexa()
  {
    String out = "[" + preffix + " " + String.format("%05d", data.length)
        + " Bytes] ";
    for (int i = 0; i < data.length; i++)
    {
      out += String.format("%02X ", data[i]);
      if (i > 300)
      {
        out += "...";
        break;
      }
    }

    return out;
  }
}
