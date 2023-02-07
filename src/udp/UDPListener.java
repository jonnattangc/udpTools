/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Observable;
import utils.UtilLine;

/**
 * @author Jonnattan Griffiths
 * @version 1.0 de 21-03-2012 Copyright(c)
*/
public class UDPListener extends Observable implements Runnable
{
  private int              port       = 0;
  private byte[]           buffer     = null;
  private DatagramSocket   socket     = null;
  private Thread           thread     = null;
  private volatile boolean terminated = true;
  private volatile long    mark       = 0;

  public UDPListener(int p)
  {
    port = p;
    initialize();
  }

  public void start()
  {
    terminated = false;
    thread = new Thread(this);
    thread.setName("Thread_UDP");
    thread.start();
  }

  public int getPort()
  {
    return port;
  }

  /**
   * Detiene la ejecucion del Thread de Cliente JMS
   */
  public void stop()
  {
    terminated = true;
    // socket.disconnect();
    socket.close();
    if (thread != null)
      thread.interrupt();
    try
    {
      if (thread != null)
        thread.join();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    thread = null;
  }

  private void initialize()
  {
    buffer = new byte[65000];
    try
    {
      socket = new DatagramSocket(null);
      socket.setReuseAddress(true);
      socket.bind(new InetSocketAddress(port));
      // socket.setBroadcast(true);
    } catch (SocketException ex)
    {
      ex.printStackTrace();
    }
  }

  @Override
  public void run()
  {
    System.out.println("Run listen port: " + port);
    long current = 0;
    DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
    while (!terminated)
    {
      try
      {
        socket.receive(incoming);
        byte[] data = Arrays.copyOf(buffer, incoming.getLength());
        current = System.nanoTime();
        System.out.println("Rx " + incoming.getLength() + " bytes despues de "
            + (current - mark) + " ns");
        UtilLine fline = new UtilLine(data, (mark == 0) ? 0 : (current - mark));
        setChanged();
        notifyObservers(fline);
        mark = current;
        data = null;
        fline = null;

      } catch (IOException ex)
      {
        // ex.printStackTrace();
        break;
      }
    }
    if (socket != null)
    {
      socket.disconnect();
      socket.close();
      socket = null;
    }
    System.out.println("Thread UDP listener finish ");
  }
}
