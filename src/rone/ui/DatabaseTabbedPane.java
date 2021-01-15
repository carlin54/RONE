package rone.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.plaf.basic.BasicButtonUI;

import rone.filemanager.Database;

public class DatabaseTabbedPane extends JTabbedPane {
	
	DatabaseTabbedPane(int position){
		super(position);
	}
	
	boolean isUniqueTabName(String name) {
		return indexOfTab(name) == -1;
	}
	
	String getUniqueTabName(String name) {
		String uniqueName = new String(name);
		Integer i = 1;
		while(!isUniqueTabName(uniqueName)) {
			uniqueName = name + " (" + i.toString() + ")";
		}
		return uniqueName;
	}
	
	void importDatabaseTable(String tableName, String[] columnIdentifiers, Database.Table table, ActionListener actionListenerTabClose) throws SQLException {
		tableName = getUniqueTabName(tableName);
		DataTable dataTable = new DataTable(columnIdentifiers);
		dataTable.setTable(table);
		dataTable.setFillsViewportHeight(true);
		JScrollPane tabBody = new JScrollPane(dataTable);
		this.addTab(getUniqueTabName(tableName), tabBody);
		
		int index = indexOfTab(tableName);
		
		ButtonTabComponent tabHeader = new ButtonTabComponent(this, actionListenerTabClose);
		this.setTabComponentAt(index, tabHeader);
		
		
	}
	
	void joinDatabaseTable(String[] activeTableJoinColumns, String[] importTableJoinColumns, Database.Table importTable) {
		
	}
	
	public boolean isEmpty() {
		return this.getTabCount() == 0;
	}
	
	void deleteTable(String tableName) {


		
	}
	
	//https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TabComponentsDemoProject/src/components/ButtonTabComponent.java
	public class ButtonTabComponent extends JPanel {
	    private final DatabaseTabbedPane pane;
	    private final ActionListener actionListenerTabClose;
	    public ButtonTabComponent(final DatabaseTabbedPane pane, ActionListener actionListenerTabClose) {
	        //unset default FlowLayout' gaps
	        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
	        if (pane == null) {
	            throw new NullPointerException("TabbedPane is null");
	        }
	        this.pane = pane;
	        setOpaque(false);
	        
	        this.actionListenerTabClose = actionListenerTabClose;
	        
	        //make JLabel read titles from JTabbedPane
	        JLabel label = new JLabel() {
	            public String getText() {
	                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
	                if (i != -1) {
	                    return pane.getTitleAt(i);
	                }
	                return null;
	            }
	        };
	        
	        add(label);
	        //add more space between the label and the button
	        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
	        //tab button
	        JButton button = new TabButton();
	        add(button);
	        //add more space to the top of the component
	        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	    }

	    private class TabButton extends JButton implements ActionListener {
	        public TabButton() {
	        	
	            int size = 17;
	            setPreferredSize(new Dimension(size, size));
	            setToolTipText("close this tab");
	            //Make the button looks the same for all Laf's
	            setUI(new BasicButtonUI());
	            //Make it transparent
	            setContentAreaFilled(false);
	            //No need to be focusable
	            setFocusable(false);
	            setBorder(BorderFactory.createEtchedBorder());
	            setBorderPainted(false);
	            //Making nice roll over effect
	            //we use the same listener for all buttons
	            addMouseListener(buttonMouseListener);
	            setRolloverEnabled(true);
	            //Close the proper tab by clicking the button
	            addActionListener(this);
	        }

	        public void actionPerformed(ActionEvent e) {
	        	
	            int i = pane.indexOfTabComponent(ButtonTabComponent.this);

	            if (i != -1) {
		            Component component = pane.getComponent(i);
		            JScrollPane scrollPane = (JScrollPane) component;
		            JViewport viewport = scrollPane.getViewport();
		            DataTable dataTable = (DataTable) viewport.getComponent(0);
		            
		            try {
						dataTable.clearTable();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                pane.remove(i);
	            }
	        }

	        //we don't want to update UI for this button
	        public void updateUI() {
	        }

	        //paint the cross
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            Graphics2D g2 = (Graphics2D) g.create();
	            //shift the image for pressed buttons
	            if (getModel().isPressed()) {
	                g2.translate(1, 1);
	            }
	            g2.setStroke(new BasicStroke(2));
	            g2.setColor(Color.BLACK);
	            if (getModel().isRollover()) {
	                g2.setColor(Color.MAGENTA);
	            }
	            int delta = 6;
	            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
	            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
	            g2.dispose();
	        }
	    }

	    private final MouseListener buttonMouseListener = new MouseAdapter() {
	        public void mouseEntered(MouseEvent e) {
	            Component component = e.getComponent();
	            if (component instanceof AbstractButton) {
	                AbstractButton button = (AbstractButton) component;
	                button.setBorderPainted(true);
	            }
	        }

	        public void mouseExited(MouseEvent e) {
	            Component component = e.getComponent();
	            if (component instanceof AbstractButton) {
	                AbstractButton button = (AbstractButton) component;
	                button.setBorderPainted(false);
	            }
	        }
	    };
	}
}
