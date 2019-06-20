package icon;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.JFrame;
import program.toolsUdp;
import utils.PixelsUtils;

/**
 * @author Jonnattan Griffiths
 * @version 1.0 de 21-03-2012 Copyright(c) - SISDEF
 */
public class IconTray implements ActionListener
{

  private SystemTray systemTray         = null;
  private BufferedImage      icon               = null;
  private TrayIcon   trayIcon           = null;
  private PopupMenu  ScreenKeyPopupMenu = null;
  private MenuItem   mnuSalir           = null;
  private MenuItem   mnuAbout           = null;
  private JFrame     frame              = null;

  public IconTray(JFrame frame)
  {
    icon = PixelsUtils.getInstance()
        .fileToBufferedImage(getIconPath("icon.png"), 16, 16);
    this.frame = frame;
  }

  private URL getIconPath(String iconFileName)
  {
    return getClass().getResource("/icon/" + iconFileName);
  }

  public boolean hiddenFrame()
  {
    boolean success = false;
    if (SystemTray.isSupported())
    {
      systemTray = SystemTray.getSystemTray();
      success = setSystemTray(icon, frame.getTitle(), getMenuSistema());
    }
    return success;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    boolean resp = false;
    if (e.getSource().equals(mnuSalir))
    {
      ((toolsUdp) frame).detenerAll();
      System.exit(0);
      resp = true;
    }
    if (e.getSource().equals(mnuAbout))
    {
      ((toolsUdp) frame).seeAbout();
      resp = true;
    }

    if (!resp)
      frame.setVisible(!frame.isVisible());
  }

  public void closeTray()
  {
    if (systemTray != null && trayIcon != null)
      systemTray.remove(trayIcon);
  }

  private boolean setSystemTray(Image image, String toolTip, PopupMenu menu)
  {
    boolean success = false;
    try
    {
      if (systemTray != null)
      {
        if (systemTray.getTrayIcons().length == 0)
        {
          if (image != null)
          {
            trayIcon = new TrayIcon(image, toolTip);
            trayIcon.addActionListener(this);
            trayIcon.setPopupMenu(menu);
            systemTray.add(trayIcon);
            success = true;
          }
        }
        else
        {
          systemTray.getTrayIcons()[0].setImage(image);
          systemTray.getTrayIcons()[0].setToolTip(toolTip);
          systemTray.getTrayIcons()[0].setPopupMenu(menu);
        }
      }
    } catch (AWTException ex)
    {
      System.out.println("ERROR TRAY: " + ex.getMessage());
      success = false;
    }
    return success;
  }

  private PopupMenu getMenuSistema()
  {
    if (ScreenKeyPopupMenu == null)
    {
      ScreenKeyPopupMenu = new PopupMenu();
      mnuSalir = new MenuItem("Close...");
      mnuSalir.addActionListener(this);
      mnuAbout = new MenuItem("About...");
      mnuAbout.addActionListener(this);
      ScreenKeyPopupMenu.add(mnuSalir);
      ScreenKeyPopupMenu.add(mnuAbout);
    }
    return ScreenKeyPopupMenu;
  }
}
