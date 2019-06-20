package utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Panel ABOUT
 * 
 * @author Jonnattan Griffiths
 * @since 20/12/2013
 * @version 1.0 Copyright(c) SISDEF - 2013
 */
public class About extends JDialog
{
  private static final long serialVersionUID = 1L;
  private JLabel            lblTitle         = null;
  private JLabel            txtVersion       = null;
  private JEditorPane       txtPane          = null;

  public About()
  {
    initComponents();
  }

  private void initComponents()
  {
    setSize(new Dimension(342, 170));
    setMinimumSize(getSize());
    setMaximumSize(getSize());
    setPreferredSize(getSize());

    setResizable(false);
    setUndecorated(true);
    setAlwaysOnTop(true);
    setModal(true);
    setModalExclusionType(ModalExclusionType.NO_EXCLUDE);

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("About ...");
    setBackground(new Color(0, 0, 0));
    getContentPane().setLayout(new BorderLayout(0, 0));

    lblTitle = new JLabel();
    lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
    getContentPane().add(lblTitle, BorderLayout.NORTH);

    lblTitle.setFont(new Font("Tahoma", 1, 12)); // NOI18N
    lblTitle.setForeground(SystemColor.activeCaption);
    lblTitle.setText("Listen/Record/Transmition UDP Program");

    txtPane = new JEditorPane();
    getContentPane().add(txtPane, BorderLayout.CENTER);
    txtPane.setOpaque(false);
    txtPane.setContentType("text/html");
    txtPane.setEditable(false);
    txtPane.setText("<html><body><font face=arial size=4 color=gray>"
        + "<p align=center> This program is developed by Jonnattan Griffiths <br> "
        + "for test needs and is facilitated for any use."
        + "Error report to jonnattan@gmail.com</font><p></body></html>");

    JPanel panel = new JPanel();
    getContentPane().add(panel, BorderLayout.SOUTH);
    panel.setLayout(new BorderLayout(0, 0));
    txtVersion = new JLabel();
    panel.add(txtVersion);
    txtVersion.setHorizontalAlignment(SwingConstants.CENTER);

    txtVersion.setFont(new Font("Tahoma", 1, 12)); // NOI18N
    txtVersion.setForeground(SystemColor.activeCaption);
    txtVersion.setText("Release 1.5 - JULIO 2017 -");

    JButton btnNewButton = new JButton("Cerrar");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e)
      {
        dispose();
      }
    });
    btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
    panel.add(btnNewButton, BorderLayout.SOUTH);
    pack();
  }
}
