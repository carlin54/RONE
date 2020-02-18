package toxicologygadget.ui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ImportDataDialog extends JDialog implements ActionListener {
   private String[] data;
   private JComboBox<String> columnSelectA;
   private JComboBox<String> columnSelectB;
   private JButton btnOk;
   private JButton btnCancel;
   public ImportDataDialog(Frame parent, String[] columnA, String[] columnB) {
      super(parent, "Pick Constraint", true);
      Point loc = parent.getLocation();
      setLocation(loc.x+80,loc.y+80);
      data = new String[2]; // set to amount of data items
      JPanel panel = new JPanel();
      panel.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(2,2,2,2);
      JLabel descLabel = new JLabel("Table Columns:");
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridx = 0;
      gbc.gridy = 0;
      panel.add(descLabel,gbc);
      
      JLabel colorLabel = new JLabel("Import Columns:");
      gbc.gridwidth = 1;
      gbc.gridx = 0;
      gbc.gridy = 1;
      panel.add(colorLabel,gbc);
      
      columnSelectA = new JComboBox<String>(columnA);
      gbc.gridwidth = 1;
      gbc.gridx = 1;
      gbc.gridy = 1;
      panel.add(columnSelectA, gbc);
      
      columnSelectB = new JComboBox<String>(columnB);
      gbc.gridwidth = 2;
      gbc.gridx = 1;
      gbc.gridy = 0;
      panel.add(columnSelectB, gbc);
      
      int s = columnSelectA.getComponents().length;
      
    
      
      JLabel spacer = new JLabel(" ");
      gbc.gridx = 0;
      gbc.gridy = 2;
      panel.add(spacer,gbc);
      btnOk = new JButton("Ok");
      btnOk.addActionListener(this);
      gbc.gridwidth = 1;
      gbc.gridx = 0;
      gbc.gridy = 3;
      panel.add(btnOk,gbc);
      btnCancel = new JButton("Cancel");
      btnCancel.addActionListener(this);
      gbc.gridx = 1;
      gbc.gridy = 3;
      panel.add(btnCancel,gbc);
      getContentPane().add(panel);
      pack();
   }
   
   public void actionPerformed(ActionEvent ae) {
      Object source = ae.getSource();
      if (source == btnOk) {
         data[0] = (String)columnSelectA.getSelectedItem();
         data[1] = (String)columnSelectB.getSelectedItem();
      }
      else {
         data[0] = null;
      }
      dispose();
   }
   
   public String[] run() {
      this.setVisible(true);
      return data;
   }
   
}