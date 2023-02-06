package program;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import file.FileManager;
import icon.IconTray;
import udp.UDPListener;
import udp.UDPWriter;
import utils.About;
import utils.PixelsUtils;
import utils.UtilLine;
import javax.swing.JSlider;

/**
 * Clase sin descripcion aun
 * 
 * @author Jonnattan Griffiths
 * @since 20/12/2013
 * @version 1.0 Copyright(c) - 2013
 */
public class toolsUdp extends JFrame implements Observer, Runnable
{
  private static final long        serialVersionUID     = 1L;
  private final Dimension          SIZE                 = new Dimension(881,
      510);
  private final Font               FONT_SYSTEM          = new Font("Monospaced",
      Font.BOLD, 14);
  private JLabel                   lblFrecRx            = null;
  private DefaultListModel<String> listModel            = null;
  private UDPWriter                udpEscritor          = null;
  private IconTray                 trayIcon             = null;
  private UDPListener              udpEscuchador        = null;
  private FileManager              fileIn               = null;
  private FileManager              fileOut              = null;
  private String                   nameFileOut          = null;
  private String                   simpleName           = null;
  private boolean                  isOnlyListen         = true;
  private JTextField               addressOut           = null;
  private JButton                  btnExaminar          = null;
  private JToggleButton            btnDisplay           = null;
  private JButton                  btnLimpiar           = null;
  private JButton                  btnListen            = null;
  private JButton                  btnStopRec           = null;
  private JButton                  btnPararReproduccion = null;
  private JButton                  btnStartRec          = null;
  private JButton                  btnReproduccion      = null;
  private JCheckBox                checkCircular        = null;
  private JList<String>            debugText            = null;
  private JLabel                   lblPortInText        = null;
  private JLabel                   lblPortOut           = null;
  private JLabel                   lblIPOut             = null;
  private JLabel                   lblNameFile          = null;
  private JLabel                   lblFrec              = null;
  private JLabel                   lblHour              = null;
  private JLabel                   lblMS                = null;
  private JPanel                   pnlText              = null;
  private JLabel                   pnlGrabacion         = null;
  private JPanel                   pnlReproduccion      = null;
  private JPanel                   pnlReprodutor        = null;
  private JPanel                   pnlConfigGrabacion   = null;
  private JTextField               txtPortOut           = null;
  private JRadioButton             rdbDataHexa          = null;
  private JRadioButton             rdbDataString        = null;
  private JCheckBox                chkHexaFormat        = null;
  private JTextField               txtFrec              = null;
  private JTextField               txtNameFile          = null;
  private JTextField               txtNameRepro         = null;
  private JTextField               txtPortEscucha       = null;
  private JPanel                   pnlBtnsGrabar        = null;
  private JPanel                   pnlInPort            = null;
  private JPanel                   pnlFileName          = null;
  private JPanel                   pnlCheckOut          = null;
  private JPanel                   pnlOpcionesRep       = null;
  private JPanel                   pnlEleccion          = null;
  private JPanel                   pnlPortOut           = null;
  private JPanel                   pnlBtnesOut          = null;
  private JPanel                   pnlFrecRep           = null;
  private JPanel                   pnlAddressOut        = null;
  private JPanel                   pnlCenterOut         = null;
  private JPanel                   panel                = null;
  private JLabel                   lblHorMuestra        = null;
  private JLabel                   lblHourData          = null;
  private JPanel                   panel_1              = null;
  private BlockingQueue<UtilLine>  queue                = null;
  private Thread                   thread               = null;
  private volatile boolean         terminated           = true;
  private JSlider                  barrTransmit         = null;
  private JSlider                  barrListen           = null;

  public toolsUdp()
  {
    initComponents();
    queue = new LinkedBlockingQueue<UtilLine>();

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e)
      {
        detenerAll();
        System.exit(0);
      }
    });
  }

  public void startMain()
  {
    this.terminated = false;
    thread = new Thread(this);
    thread.setName("MAIN_Thread");
    thread.start();
  }

  protected void stopMain()
  {
    this.terminated = true;
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
    queue.clear();
  }

  private URL getIconPath(String iconFileName)
  {
    return getClass().getResource("/icon/" + iconFileName);
  }

  private void initComponents()
  {
    BufferedImage imageIcon = PixelsUtils.getInstance()
        .fileToBufferedImage(getIconPath("icon.png"), 16, 16);
    setIconImage(imageIcon);
    getContentPane().setFont(FONT_SYSTEM);
    setResizable(false);
    setSize(SIZE);
    setPreferredSize(getSize());
    setMinimumSize(getSize());
    setMaximumSize(getSize());
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("Listen/Record/Reproduction Tool");
    setBackground(Color.white);
    setBounds(new Rectangle(400, 400, 200, 150));
    setFont(new Font("Arial Unicode MS", Font.BOLD, 10)); // NOI18N
    setModalExclusionType(null);
    setName("principalFrame");

    getAccessibleContext().setAccessibleDescription("Tools");

    getContentPane().setLayout(new BorderLayout(0, 0));

    JPanel pnlConfig = new JPanel();
    getContentPane().add(pnlConfig, BorderLayout.NORTH);
    pnlConfig.setLayout(new BorderLayout(0, 0));
    pnlConfigGrabacion = new JPanel();
    pnlConfigGrabacion
        .setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
            "Listen - Record Configuration", TitledBorder.LEADING,
            TitledBorder.TOP, null, new Color(0, 0, 0)));
    pnlConfig.add(pnlConfigGrabacion, BorderLayout.WEST);
    pnlReprodutor = new JPanel();
    pnlReprodutor
        .setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
            "Reproduction Configuration", TitledBorder.LEADING,
            TitledBorder.TOP, null, new Color(0, 0, 0)));
    pnlReproduccion = new JPanel();
    pnlReproduccion.setOpaque(true);
    pnlReproduccion.setBackground(Color.RED);

    pnlConfig.add(pnlReprodutor);
    pnlReprodutor.setLayout(new BorderLayout(0, 0));

    pnlEleccion = new JPanel();
    pnlReprodutor.add(pnlEleccion, BorderLayout.NORTH);
    pnlEleccion.setLayout(new BorderLayout(0, 0));
    txtNameRepro = new JTextField();
    txtNameRepro.setFont(FONT_SYSTEM);
    txtNameRepro.setColumns(15);
    pnlEleccion.add(txtNameRepro, BorderLayout.CENTER);

    txtNameRepro.setText(" ");
    btnExaminar = new JButton();
    btnExaminar.setFont(FONT_SYSTEM);
    pnlEleccion.add(btnExaminar, BorderLayout.EAST);

    btnExaminar.setText("Search");

    barrTransmit = new JSlider();
    barrTransmit.setPaintLabels(true);
    pnlEleccion.add(barrTransmit, BorderLayout.WEST);
    barrTransmit.setPreferredSize(new Dimension(80, 20));
    barrTransmit.setFont(new Font("Tahoma", Font.BOLD, 11));
    barrTransmit.setValue(0);

    pnlBtnesOut = new JPanel();
    pnlReprodutor.add(pnlBtnesOut, BorderLayout.SOUTH);
    btnReproduccion = new JButton();
    pnlBtnesOut.add(btnReproduccion);
    btnReproduccion.setFont(FONT_SYSTEM);

    btnReproduccion.setText("Start");
    btnPararReproduccion = new JButton();
    pnlBtnesOut.add(btnPararReproduccion);
    btnPararReproduccion.setFont(FONT_SYSTEM);
    btnPararReproduccion.setEnabled(false);

    btnPararReproduccion.setText("Stop");

    lblHorMuestra = new JLabel("Hour:");
    lblHorMuestra.setFont(FONT_SYSTEM);
    pnlBtnesOut.add(lblHorMuestra);

    lblHourData = new JLabel("00:00:00");
    lblHourData.setFont(FONT_SYSTEM);
    lblHourData.setHorizontalAlignment(SwingConstants.CENTER);
    pnlBtnesOut.add(lblHourData);

    pnlCenterOut = new JPanel();
    pnlReprodutor.add(pnlCenterOut);
    pnlCenterOut.setLayout(new BorderLayout(0, 0));

    pnlCheckOut = new JPanel();
    pnlCenterOut.add(pnlCheckOut, BorderLayout.SOUTH);
    pnlCheckOut.setLayout(new BorderLayout(0, 0));
    chkHexaFormat = new JCheckBox();
    chkHexaFormat.setHorizontalAlignment(SwingConstants.LEFT);
    pnlCheckOut.add(chkHexaFormat);
    chkHexaFormat.setFont(FONT_SYSTEM);

    chkHexaFormat.setSelected(true);
    chkHexaFormat.setText("Standard format");
    chkHexaFormat.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        sendFormatoSisdefActionPerformed(evt);
      }
    });
    checkCircular = new JCheckBox();
    checkCircular.setHorizontalAlignment(SwingConstants.LEFT);
    pnlCheckOut.add(checkCircular, BorderLayout.EAST);
    checkCircular.setFont(FONT_SYSTEM);

    checkCircular.setSelected(true);
    checkCircular.setText("Circular Reproduction");

    pnlOpcionesRep = new JPanel();
    pnlCenterOut.add(pnlOpcionesRep);
    pnlOpcionesRep.setLayout(new BorderLayout(0, 0));

    pnlFrecRep = new JPanel();
    pnlOpcionesRep.add(pnlFrecRep, BorderLayout.CENTER);
    lblFrec = new JLabel();
    lblFrec.setVisible(false);
    pnlFrecRep.add(lblFrec);
    lblFrec.setFont(FONT_SYSTEM);

    lblFrec.setText("Frecuency");
    txtFrec = new JTextField();
    txtFrec.setText("1000");
    txtFrec.setVisible(false);
    pnlFrecRep.add(txtFrec);
    txtFrec.setColumns(5);
    txtFrec.setFont(FONT_SYSTEM);
    lblMS = new JLabel();
    lblMS.setVisible(false);
    pnlFrecRep.add(lblMS);
    lblMS.setFont(FONT_SYSTEM);
    lblMS.setText("[ms]");

    txtFrec.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        txtSetFrec();
      }
    });

    panel_1 = new JPanel();
    pnlOpcionesRep.add(panel_1, BorderLayout.WEST);
    panel_1.setLayout(new BorderLayout(0, 0));

    pnlPortOut = new JPanel();
    panel_1.add(pnlPortOut, BorderLayout.EAST);
    lblPortOut = new JLabel();
    lblPortOut.setFont(FONT_SYSTEM);
    pnlPortOut.add(lblPortOut);

    lblPortOut.setText("Port");
    txtPortOut = new JTextField();
    txtPortOut.setColumns(5);
    txtPortOut.setFont(FONT_SYSTEM);
    pnlPortOut.add(txtPortOut);

    txtPortOut.setText("9600");

    pnlAddressOut = new JPanel();
    panel_1.add(pnlAddressOut);
    lblIPOut = new JLabel();
    pnlAddressOut.add(lblIPOut);
    lblIPOut.setFont(FONT_SYSTEM);

    lblIPOut.setText("IP:");
    addressOut = new JTextField();
    pnlAddressOut.add(addressOut);
    addressOut.setColumns(15);
    addressOut.setFont(FONT_SYSTEM);

    addressOut.setText("192.168.255.255");
    checkCircular.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        checkCircular();
      }
    });
    btnPararReproduccion.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        btnStopPlay();
      }
    });
    btnReproduccion.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        btnPlay();
      }
    });
    btnExaminar.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        examinar();
      }
    });
    pnlConfigGrabacion.setLayout(new BorderLayout(0, 0));

    pnlInPort = new JPanel();
    pnlConfigGrabacion.add(pnlInPort, BorderLayout.NORTH);
    pnlInPort.setLayout(new BorderLayout(0, 0));
    lblPortInText = new JLabel();
    lblPortInText.setFont(FONT_SYSTEM);
    pnlInPort.add(lblPortInText, BorderLayout.WEST);

    lblPortInText.setText("UDP Port Listen");
    txtPortEscucha = new JTextField();
    txtPortEscucha.setFont(FONT_SYSTEM);
    txtPortEscucha.setColumns(10);
    pnlInPort.add(txtPortEscucha, BorderLayout.CENTER);

    txtPortEscucha.setText("9600");

    barrListen = new JSlider();
    barrListen.setPaintLabels(true);
    barrListen.setValue(0);
    barrListen.setPreferredSize(new Dimension(80, 20));
    barrListen.setFont(new Font("Tahoma", Font.BOLD, 11));
    pnlInPort.add(barrListen, BorderLayout.EAST);

    pnlFileName = new JPanel();
    pnlConfigGrabacion.add(pnlFileName);
    pnlFileName.setLayout(new BorderLayout(0, 0));

    panel = new JPanel();
    pnlFileName.add(panel, BorderLayout.NORTH);
    panel.setLayout(new BorderLayout(0, 0));
    pnlGrabacion = new JLabel();
    panel.add(pnlGrabacion, BorderLayout.EAST);
    pnlGrabacion.setFont(FONT_SYSTEM);
    pnlGrabacion.setText("   ");

    pnlGrabacion.setBorder(BorderFactory.createEtchedBorder());

    javax.swing.GroupLayout pnlGrabacionLayout = new javax.swing.GroupLayout(
        pnlGrabacion);
    pnlGrabacion.setLayout(pnlGrabacionLayout);
    pnlGrabacionLayout.setHorizontalGroup(
        pnlGrabacionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE));
    pnlGrabacionLayout.setVerticalGroup(
        pnlGrabacionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE));
    pnlGrabacion.setOpaque(true);
    pnlGrabacion.setBackground(Color.RED);
    txtNameFile = new JTextField();
    panel.add(txtNameFile, BorderLayout.CENTER);
    txtNameFile.setFont(FONT_SYSTEM);

    txtNameFile.setText("udpRecord");
    lblNameFile = new JLabel();
    panel.add(lblNameFile, BorderLayout.WEST);
    lblNameFile.setFont(FONT_SYSTEM);

    lblNameFile.setText("File Name      ");

    JPanel panel_2 = new JPanel();
    pnlFileName.add(panel_2, BorderLayout.CENTER);
    panel_2.setLayout(new BorderLayout(0, 0));

    lblFrecRx = new JLabel("Time Interval:");
    lblFrecRx.setHorizontalAlignment(SwingConstants.LEFT);
    lblFrecRx.setFont(FONT_SYSTEM);
    panel_2.add(lblFrecRx);

    pnlBtnsGrabar = new JPanel();
    pnlConfigGrabacion.add(pnlBtnsGrabar, BorderLayout.SOUTH);
    pnlBtnsGrabar.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    btnListen = new JButton();
    btnListen.setFont(FONT_SYSTEM);
    pnlBtnsGrabar.add(btnListen);

    btnListen.setText("Listen Port");
    btnStartRec = new JButton();
    btnStartRec.setFont(FONT_SYSTEM);
    pnlBtnsGrabar.add(btnStartRec);

    btnStartRec.setText("Start Record");
    btnStopRec = new JButton();
    btnStopRec.setFont(FONT_SYSTEM);
    pnlBtnsGrabar.add(btnStopRec);

    btnStopRec.setText("Stop");
    btnStopRec.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        btnStopRec();
      }
    });
    btnStopRec.setEnabled(false);
    btnStartRec.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        btnRec();
      }
    });
    btnListen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        btnListen();
      }
    });
    pnlText = new JPanel();
    getContentPane().add(pnlText, BorderLayout.CENTER);

    pnlText.setBorder(new TitledBorder(null, "Info", TitledBorder.LEADING,
        TitledBorder.TOP, null, null));
    pnlText.setLayout(new BorderLayout(0, 0));

    JScrollPane scrollText = new JScrollPane();
    listModel = new DefaultListModel<String>();
    debugText = new JList<String>();
    debugText.setFont(FONT_SYSTEM);
    debugText.setModel(listModel);
    debugText.setBackground(new Color(0, 0, 0));
    debugText.setForeground(new Color(204, 204, 204));
    listModel.addElement("Standard Format is the file with this app create...");

    scrollText.setViewportView(debugText);
    pnlText.add(scrollText, BorderLayout.CENTER);

    JPanel pnlAbajo = new JPanel();
    pnlAbajo.setBorder(new TitledBorder(null, "", TitledBorder.LEADING,
        TitledBorder.TOP, null, null));
    getContentPane().add(pnlAbajo, BorderLayout.SOUTH);
    pnlAbajo.setLayout(new GridLayout(0, 3, 0, 0));
    lblHour = new JLabel();
    pnlAbajo.add(lblHour);
    lblHour.setHorizontalAlignment(SwingConstants.LEFT);
    lblHour.setText("00/00/0000 00:00:00 ");
    lblHour.setFont(FONT_SYSTEM);

    JPanel pnlBtn = new JPanel();
    pnlAbajo.add(pnlBtn);
    pnlBtn.setLayout(new GridLayout(0, 2, 0, 0));
    btnDisplay = new JToggleButton();
    btnDisplay.setFont(FONT_SYSTEM);
    btnDisplay.setText("Display Off");
    btnDisplay.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        btnDisplay();
      }
    });
    pnlBtn.add(btnDisplay);

    btnLimpiar = new JButton();
    pnlBtn.add(btnLimpiar);
    btnLimpiar.setFont(FONT_SYSTEM);

    btnLimpiar.setText("Clean");
    btnLimpiar.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        btnLimpiar();
      }
    });

    JPanel pnlDer = new JPanel();
    pnlAbajo.add(pnlDer);
    pnlDer.setLayout(new GridLayout(0, 3, 0, 0));
    rdbDataHexa = new JRadioButton();
    pnlDer.add(rdbDataHexa);
    rdbDataHexa.setText("Hexa");
    rdbDataHexa.setSelected(true);
    rdbDataHexa.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        rdbDataHexa();
      }
    });
    rdbDataHexa.setFont(FONT_SYSTEM);
    rdbDataHexa.setHorizontalAlignment(SwingConstants.LEFT);

    rdbDataString = new JRadioButton();
    pnlDer.add(rdbDataString);
    rdbDataString.setText("String");
    rdbDataString.setFont(FONT_SYSTEM);
    rdbDataString.setSelected(false);
    rdbDataString.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt)
      {
        rdbDataString();
      }
    });
    rdbDataString.setHorizontalAlignment(SwingConstants.LEFT);

    JLabel lblVersion = new JLabel(FileManager.APPVERSION + " 2017");
    pnlDer.add(lblVersion);
    lblVersion.setFont(FONT_SYSTEM);
    lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
    ShowDateTime();
    // --------------------------------------------
    trayIcon = new IconTray(this);
    if (!trayIcon.hiddenFrame())
      System.out.println("The program not have trayIcon");
    pack();
  }

  private void examinar()
  {
    JFileChooser fchooser = new JFileChooser();
    fchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fchooser.setMultiSelectionEnabled(false);
    fchooser.setCurrentDirectory(new File("."));
    int res = fchooser.showOpenDialog(null);
    if (res == JFileChooser.APPROVE_OPTION)
    {
      File f = fchooser.getSelectedFile();
      simpleName = f.getName().toString();
      nameFileOut = f.getAbsolutePath().toString();
      txtNameRepro.setText(nameFileOut);
    }
  }

  /** Ckeck que indica si el archivo que se preproduce sera en formato */
  private void sendFormatoSisdefActionPerformed(ActionEvent evt)
  {
    lblFrec.setVisible(!chkHexaFormat.isSelected());
    txtFrec.setVisible(!chkHexaFormat.isSelected());
    lblMS.setVisible(!chkHexaFormat.isSelected());
    lblHorMuestra.setVisible(chkHexaFormat.isSelected());
    lblHourData.setVisible(chkHexaFormat.isSelected());
  }

  private void txtSetFrec()
  {
    if (!chkHexaFormat.isSelected())
    {
      if (txtFrec.getText().matches("[0-9]*"))
      {
        try
        {

          long frec = Integer.parseInt(txtFrec.getText());
          if (fileOut != null)
            fileOut.setFrecuency_ms(frec);
        } catch (NumberFormatException ex)
        {
          // ex.printStackTrace();
          String strError = "Only Number Frecuency.";
          JOptionPane.showMessageDialog(null, strError, "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
      else
      {
        String strError = "Only Number Frecuency.";
        JOptionPane.showMessageDialog(null, strError, "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void checkCircular()
  {
    if (fileOut != null)
      fileOut.setCircular(checkCircular.isSelected());
  }

  private void rdbDataHexa()
  {
    rdbDataString.setSelected(!rdbDataHexa.isSelected());
  }

  private void rdbDataString()
  {
    rdbDataHexa.setSelected(!rdbDataString.isSelected());
  }

  private void btnListen()
  {
    int port = 0;
    if (udpEscuchador == null)
    {
      try
      {
        startMain();
        port = Integer.parseInt(txtPortEscucha.getText());
        udpEscuchador = new UDPListener(port);
        udpEscuchador.addObserver(this);
        udpEscuchador.start();
        // Se inicia el escuchador UDP
        btnListen.setEnabled(false);
        btnStartRec.setEnabled(true);
        btnStopRec.setEnabled(true);
        setTitle("Listen data in port " + port);
        pnlGrabacion.setBackground(Color.ORANGE);
        isOnlyListen = true;
      } catch (NumberFormatException ex)
      {
        String strError = "Listen port is not number field\nProbe other value.";
        JOptionPane.showMessageDialog(null, strError, "Error",
            JOptionPane.ERROR_MESSAGE);
        pnlGrabacion.setBackground(Color.RED);
        isOnlyListen = false;
        return;
      }
    }
  }

  private void btnRec()
  {
    if (udpEscuchador == null)
      btnListen();
    // ----------------------------------------------------------
    if (udpEscuchador != null)
    {
      fileIn = new FileManager(txtNameFile.getText(), null);
      fileIn.setTitle("Recording Port: " + udpEscuchador.getPort());
      fileIn.start();
      // ---------------------------------------------------------
      setTitle("Recording data for port " + udpEscuchador.getPort());
      isOnlyListen = false;
      btnStartRec.setEnabled(false);
      btnListen.setEnabled(false);
      btnStopRec.setEnabled(true);
      pnlGrabacion.setBackground(Color.GREEN);
    }
  }

  private void btnStopRec()
  {
    if (udpEscuchador != null)
      udpEscuchador.stop();
    if (fileIn != null)
      fileIn.stop();
    stopMain();
    udpEscuchador = null;
    fileIn = null;
    btnStopRec.setEnabled(false);
    btnStartRec.setEnabled(true);
    btnListen.setEnabled(true);
    pnlGrabacion.setBackground(Color.RED);
    setTitle("Listen/Record/Reproduction Tool");

  }

  /**
   * Reproduce un archivo
   * 
   * @param evt
   */
  private void btnPlay()
  {
    int portOut = -1;
    long frecuency = 10;
    // -------------------------------------------------------
    try
    {
      if (!chkHexaFormat.isSelected())
      {
        frecuency = Integer.parseInt(txtFrec.getText());
      }
    } catch (NumberFormatException ex)
    {
      JOptionPane.showMessageDialog(this,
          "ERROR: Incorrect Frecuency, please enter a number", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    // -------------------------------------------------------
    try
    {
      portOut = Integer.parseInt(this.txtPortOut.getText());
    } catch (NumberFormatException ex)
    {
      JOptionPane.showMessageDialog(this, "Please, Writte numeric port out",
          "ERROR", JOptionPane.ERROR_MESSAGE);
      return;
    }
    // -------------------------------------------------------
    if (simpleName == null)
    {
      JOptionPane.showMessageDialog(this,
          "Please, select a file using search button", "ERROR",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    startMain();
    // -------------------------------------------------------
    // Puerto UDP por donde escupir
    barrTransmit.setValue(0);
    udpEscritor = new UDPWriter(portOut, barrTransmit);
    udpEscritor.setServer(addressOut.getText());
    udpEscritor.start();

    // Manejador de archivo
    fileOut = new FileManager(nameFileOut, lblHourData);
    fileOut.setHexaFormat(chkHexaFormat.isSelected());
    if (!chkHexaFormat.isSelected())
      fileOut.setFrecuency_ms(frecuency);
    fileOut.setCircular(checkCircular.isSelected());
    fileOut.addObserver(this);
    fileOut.start();
    // -------------------------------------------------------
    setTitle("Reproduction file " + simpleName + " to UDP Port " + portOut);
    pnlReproduccion.setBackground(Color.GREEN);
    // -------------------------------------------------------
    btnExaminar.setEnabled(false);
    btnPararReproduccion.setEnabled(true);
    btnReproduccion.setEnabled(false);
  }

  private void btnStopPlay()
  {
    fileOut.stop();
    fileOut = null;
    udpEscritor.stop();
    stopMain();

    btnExaminar.setEnabled(true);
    btnPararReproduccion.setEnabled(false);
    btnReproduccion.setEnabled(true);
    pnlReproduccion.setBackground(Color.RED);
    setTitle("Listen/Record/Reproduction Tool");
  }

  private void btnLimpiar()
  {
    SwingUtilities.invokeLater(new Runnable() {
      public void run()
      {
        listModel.removeAllElements();
      }
    });
  }

  private void btnDisplay()
  {
    if (btnDisplay.isSelected())
      btnDisplay.setText("Display On");
    else
      btnDisplay.setText("Display Off");
  }

  public void update(Observable o, Object arg)
  {
    UtilLine utilLine = (UtilLine) arg;
    if (o instanceof UDPListener)
    {
      if (!isOnlyListen && fileIn != null)
        fileIn.sendToFile(utilLine);
      queue.add(utilLine.uglyClone("Rx"));
    }
    if (o instanceof FileManager)
    {
      if (udpEscritor != null)
        udpEscritor.sendToUDP(utilLine);
      txtFrec.setText(String.format("%d", fileOut.getFrecuency()));
      queue.add(utilLine.uglyClone("Tx"));
    }
  }

  public void seeAbout()
  {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run()
      {
        About frame = new About();
        Point current = getLocation();
        Point point = new Point(
            (current.x + ((SIZE.width - frame.getSize().width) / 2)),
            (current.y + ((SIZE.height - frame.getSize().height) / 2)));
        frame.setLocation(point);
        frame.setVisible(true);
        frame = null;
      }
    });
  }

  private void ShowDateTime()
  {
    new Thread(new Runnable() {
      @Override
      public void run()
      {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        Date currentDate = new Date();
        lblHour.setText(format.format(currentDate));
        while (true)
        {
          try
          {
            Thread.sleep(1000);
            currentDate.setTime(System.currentTimeMillis());
            lblHour.setText(format.format(currentDate));

          } catch (InterruptedException ex)
          {
            System.err.println("ERROR: Hour " + ex.getMessage());
          }

        }
      }
    }).start();
  }

  public void detenerAll()
  {
    if (udpEscuchador != null)
      udpEscuchador.stop();
    if (fileIn != null)
      fileIn.stop();
    udpEscuchador = null;
    fileIn = null;
    stopMain();
  }

  @Override
  public void run()
  {
    while (!terminated)
    {
      try
      {
        UtilLine pl = queue.take();
        // System.out.println(
        // "Wait " + (int) pl.getTimeMark() + " ns -> " + msec + " ms");
        Thread.sleep(pl.getWaitMS());
        addDebug(pl, queue.size());
        pl = null;
      } catch (Exception e)
      {
        break;
      }
    }
    System.out.println("Visualizador Finish");
  }

  private void addDebug(final UtilLine line, final int size)
  {
    SwingUtilities.invokeLater(new Runnable() {
      public void run()
      {
        if (!btnDisplay.isSelected())
        {
          while (listModel.size() >= 500)
            listModel.remove(0);
          String current = "";
          if (rdbDataString.isSelected())
            current = line.getString();
          if (rdbDataHexa.isSelected())
            current = line.getHexa();
          barrListen.setValue(size);
          listModel.addElement(current);
          debugText.ensureIndexIsVisible(listModel.getSize() - 1);
          lblFrecRx.setText(
              String.format("Time Interval: %d nsec", line.getWaitNS()));
        }
      }
    });
  }

  /**
   * @param args
   *          the command line arguments
   */
  public static void main(String args[])
  {
    try
    {
      // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
      UIManager
          .setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.luna.LunaLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
      // UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
    } catch (ClassNotFoundException ex)
    {
      ex.printStackTrace();
    } catch (InstantiationException ex)
    {
      ex.printStackTrace();
    } catch (IllegalAccessException ex)
    {
      ex.printStackTrace();
    } catch (UnsupportedLookAndFeelException ex)
    {
      ex.printStackTrace();
    }

    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run()
      {
        toolsUdp ventana = new toolsUdp();
        ventana.setVisible(true);
      }
    });
  }
}
