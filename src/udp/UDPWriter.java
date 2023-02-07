/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import utils.UtilLine;

/**
 * @author Copyright(c) Jonnattan Griffiths
 * @version 1.1 de 07-02-2023
 * @since {@link https://dev.jonnattan.com}
 */
public class UDPWriter implements Runnable {
  private InetAddress server = null;
  private DatagramSocket socket = null;
  private int port = -1;
  private Thread thread = null;
  private BlockingQueue<UtilLine> queue = null;
  private volatile boolean terminated = true;
  private JSlider barr = null;

  public UDPWriter(int port, JSlider barr) {
    this.barr = barr;
    this.port = port;
    try {
      this.socket = new DatagramSocket();
      this.socket.setBroadcast(true);
      queue = new LinkedBlockingQueue<UtilLine>();
    } catch (SocketException ex) {
      ex.printStackTrace();
    }
  }

  public void sendToUDP(final UtilLine aData) {
    queue.add(aData.uglyClone(""));
    setBarr(queue.size());
  }

  public void setServer(String address) {
    try {
      this.server = InetAddress.getByName(address);
    } catch (UnknownHostException ex) {
      ex.printStackTrace();
    }
  }

  public void start() {
    terminated = false;
    thread = new Thread(this);
    thread.start();
  }

  public void stop() {
    terminated = true;
    if (thread != null)
      thread.interrupt();
    try {
      if (thread != null)
        thread.join();
    } catch (Exception e) {
      // TODO: handle exception
    }
    thread = null;
  }

  private boolean write(final byte[] aData) {
    boolean success = false;
    DatagramPacket output = new DatagramPacket(aData, aData.length, server,
        port);
    if (server != null) {
      try {
        socket.send(output);
        success = true;
      } catch (SocketException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "The message is larger than the maximum supported", "Error",
            JOptionPane.ERROR_MESSAGE);
        System.exit(0);
        success = false;
      } catch (IOException e) {
        e.printStackTrace();
        success = false;
      }
    }
    return success;
  }

  public InetAddress getServer() {
    return server;
  }

  @Override
  public void run() {
    System.out.println("Init UDPWriter to " + port);
    while (!terminated) {
      try {
        UtilLine fl = queue.take();
        Thread.sleep(fl.getWaitMS());
        if (!write(fl.getData()))
          System.err.println("ERROR: No enviando a " + server);
        fl = null;
      } catch (InterruptedException e) {
        break;
      }
    }
    System.out.println("Thread UDPWriter finish");
  }

  private void setBarr(final int value) {
    if (barr != null) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          barr.setValue(value);
        }
      });
    }
  }
}
