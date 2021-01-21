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
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

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
			i = i + 1;
		}
		return uniqueName;
	}
	
	DataTable importDatabaseTable(String tableName, String[] columnIdentifiers, Database.Table table, ActionListener actionListenerTabClose) throws SQLException {
		tableName = getUniqueTabName(tableName);
		DataTable dataTable = new DataTable(columnIdentifiers);
		dataTable.setTable(table);
		dataTable.setFillsViewportHeight(true);
		JScrollPane tabBody = new JScrollPane(dataTable);
		this.addTab(getUniqueTabName(tableName), tabBody);
		
		int index = indexOfTab(tableName);
		
		ButtonTabComponent tabHeader = new ButtonTabComponent(this, tabBody, actionListenerTabClose);
		this.setTabComponentAt(index, tabHeader);
		return dataTable;
		
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
	    private final DatabaseTabbedPane mPane;
	    private final ActionListener actionListenerTabClose;
	    private final JScrollPane mTabBody;
	    public ButtonTabComponent(final DatabaseTabbedPane pane, final JScrollPane tabBody, ActionListener actionListenerTabClose) {
	        //unset default FlowLayout' gaps
	        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
	        if (pane == null) {
	            throw new NullPointerException("TabbedPane is null");
	        }
	        this.mPane = pane;
	        this.mTabBody = tabBody;
	        
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
	        JButton button = new TabButton(this.mTabBody);
	        button.addActionListener(actionListenerTabClose);
	        add(button);
	        //add more space to the top of the component
	        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	    }

	    private class TabButton extends JButton implements ActionListener {
	    	
	    	final JScrollPane mTabBody;
	    	
	        public TabButton(JScrollPane tabBody) {
	        	this.mTabBody = tabBody;
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
	        
	        public DataTable getDataTable() {
	        	Component[] components = mPane.getComponents();
	            Component component = null;
	            System.out.println("Selected tab:" + mPane.getSelectedIndex());
	            
	            
	            
	            for(Component c : components) {
	            	if(c.getClass().equals(JScrollPane.class)) {
	            		component = c;
	            		break;
	            	}
	            }
	            if(component == null) {
	            	System.out.println("Close button found no JScrollPane!");
	            	return null;
	            }
	            
	            JScrollPane scrollPane = (JScrollPane) component;
	            JViewport viewport = scrollPane.getViewport();
	            DataTable dataTable = (DataTable) viewport.getComponent(0);
	            return dataTable;
	        }
	        
	        public JScrollPane[] getTabs() {
	        	Component[] components = mPane.getComponents();
	        	ArrayList<JScrollPane> tabs = new ArrayList<JScrollPane>();
	        	for(Component c : components) {
	        		if(c.getClass().equals(JScrollPane.class)) 
	        			tabs.add((JScrollPane)c);
	        	}
	        	JScrollPane[] arr = new JScrollPane[tabs.size()];
	        	return (JScrollPane[]) tabs.toArray(arr);
	        }
	        
	        public DataTable getActiveDataTable() {
	        	int i = mPane.indexOfTabComponent(ButtonTabComponent.this);
	            if (i != -1) {
		            JScrollPane scrollPane = getTabs()[i];
		            JViewport viewport = scrollPane.getViewport();
		            DataTable dataTable = (DataTable) viewport.getComponent(0);
		            return dataTable;
	            } else {
	            	return null;
	            }
	        }
	        
	        public void actionPerformed(ActionEvent e) {
	            int i = mPane.indexOfTabComponent(ButtonTabComponent.this);
	            System.out.println("Close tab, index of tab component: " + i);
	            
	            if (i != -1) {
		            JScrollPane scrollPane = getTabs()[i];
		            JViewport viewport = scrollPane.getViewport();
		            DataTable dataTable = (DataTable) viewport.getComponent(0);
		            
		            try {
		            	System.out.println("TabButton:actionPerformed:Clearing table");
						dataTable.clearTable();
						System.out.println("TabButton:actionPerformed:Table cleared.");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                mPane.remove(i);
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
	
    public JScrollPane[] getTabs() {
    	Component[] components = getComponents();
    	ArrayList<JScrollPane> tabs = new ArrayList<JScrollPane>();
    	for(Component c : components) {
    		if(c.getClass().equals(JScrollPane.class)) 
    			tabs.add((JScrollPane)c);
    	}
    	JScrollPane[] arr = new JScrollPane[tabs.size()];
    	return (JScrollPane[]) tabs.toArray(arr);
    }
	
    public DataTable getActiveDataTable() {
    	int i = getSelectedIndex();
        System.out.println("Close tab, index of tab component: " + i);
        if (i != -1) {
            JScrollPane scrollPane = getTabs()[i];
            JViewport viewport = scrollPane.getViewport();
            DataTable dataTable = (DataTable) viewport.getComponent(0);
            return dataTable;
        } else {
        	return null;
        }
    }
	
	public String[] getSelection() {
		
		
		DataTable a = this.getActiveDataTable();
		
		if(a != null) {
			return a.getUniqueSelected();
		}
		
		
		
		return null;
	}

    public void stateChanged(ChangeEvent e) {
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        int selectedIndex = tabbedPane.getSelectedIndex();
        DataTable dataTable = getActiveDataTable();
        try {
			dataTable.updateTable();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
        JOptionPane.showMessageDialog(null, "Selected Index: " + selectedIndex);
    }
	
}
