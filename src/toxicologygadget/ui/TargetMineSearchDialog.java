package toxicologygadget.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import toxicologygadget.filemanager.Table;
import toxicologygadget.query.QueryThreadCallback;

import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.JTable;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.awt.event.ActionEvent;

public class TargetMineSearchDialog extends JDialog {
	
	private TargetMineSearchDialog mTargetMineSearchDialog;
	private final JPanel mContentPanel = new JPanel();
	private JTable mTable;
	private MainWindow mMainWindow; 
	private JLabel mLblConnectionStatus;
	private JLabel mLblSearchStatus;
	
	
	private String[] mGenelist;
	private Integer mSearched;
	private Integer mResultsFound;
	private Integer mStepSize;
	private boolean mProcessRunning;
	private boolean mStopProcess;
	private MasterThread mMasterThread;

    

	/**
	 * Create the dialog.
	 */
	public TargetMineSearchDialog(MainWindow mainWindow, String[] genelist) {
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\icons\\targetminelogo.png"));
		setTitle("TargetMine Search");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		mContentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(mContentPanel, BorderLayout.CENTER);
		mContentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			mContentPanel.add(panel, BorderLayout.NORTH);
			{
				mLblSearchStatus = new JLabel("Total: 0 | Seached: 0 | Results Found: 0");
				panel.add(mLblSearchStatus);
			}
		}
		{
			mTable = new JTable();
			mContentPanel.add(mTable, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				mLblConnectionStatus = new JLabel("[Disconnected]");
				buttonPane.add(mLblConnectionStatus);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							mMasterThread.stopProcess();
							mMasterThread.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		this.mTargetMineSearchDialog = this;
		this.mMainWindow = mainWindow;
		this.mGenelist = genelist;
		this.mSearched = 0;
		this.mResultsFound = 0;
		this.mStepSize = 1;
		updateStatus("Disconnected");
		this.updateSearchStatus();
		this.setVisible(true);
		this.mMasterThread = new MasterThread();
		
	}
	
	private void updateStatus(String status) {
		
		this.mLblConnectionStatus.setText("[" + status + "]");
	}
	
	private void updateSearchStatus() {
		this.mLblSearchStatus.setText("Total: " + mGenelist.length +
									  " | Searched: " + mSearched + 
									  " | Results Found: " + mResultsFound);
	}
	
	static private ArrayList<String> queryIdentifiers(){
		return new ArrayList<String>( Arrays.asList( "(TM) Gene" , "1" , "2", "3", "4", "5", "6" ) );
	}
	
    static private ArrayList<ArrayList<Object>> query(String genes) {
    	
    	final String ROOT = "https://targetmine.mizuguchilab.org/targetmine/service";
    	
    	ServiceFactory factory = new ServiceFactory(ROOT);
    	Model model = factory.getModel();
   
        
        PathQuery query = new PathQuery(model);

        // Select the output columns:
        query.addViews(	"Gene.symbol",
        				"Gene.secondaryIdentifier",
        				"Gene.organism.name",
        				"Gene.goAnnotation.ontologyTerm.identifier",
        				"Gene.goAnnotation.ontologyTerm.name",
        				"Gene.goAnnotation.evidence.code.code",
        				"Gene.goAnnotation.ontologyTerm.namespace");

        // Add orderby
        query.addOrderBy("Gene.symbol", OrderDirection.ASC);

        // Filter the results with the following constraints:
        query.addConstraint(Constraints.lookup("Gene", genes, null));
        
        QueryService service = factory.getQueryService();
        
        Iterator<List<Object>> rows = service.getRowListIterator(query);
        ArrayList<ArrayList<Object>> results = new ArrayList<ArrayList<Object>>();
        
        while (rows.hasNext()) {
        	Object[] row = rows.next().toArray();
        	
        	//for(int i = 0; i < row.length; i++) {
        		//System.out.print(row[i] + " : ");
        	//}
        	
        	ArrayList<Object> rowList = new ArrayList<Object>();
        	
        	for(int i = 0; i < row.length; i++) 
        		rowList.add(row[i]);
        	
        	results.add(rowList);
        	
        	
        }
        
		return results;
    }
	
    private String makeSearch(int i, int j) {
    	String search = new String("");
    	while(i < j && i < mGenelist.length) {
    		search = search + mGenelist[i] + ", ";
    		i++;
    	}
    	return search;
    }
    
    private int numGenesToSearch(int i) {
    	return ((i+mStepSize > mGenelist.length) ? mGenelist.length : i+mStepSize);
    }
    
    
    class WorkerThread extends Thread 
	{ 
    	private ArrayList<ArrayList<Object>> mResults;
    	private String[] mGenes;
    	private boolean mHasGivenResults;
    	
    	WorkerThread(){
    		mProcessRunning = false;
    		this.setPriority(MAX_PRIORITY);
    	}
    	
    	
    	public void setWork(String[] genes) {
    		this.mGenes = genes;
    	}
    	
    	public ArrayList<ArrayList<Object>> getResults(){
    		mHasGivenResults = true;
    		return mResults;
    	}
    	
	    public void run() 
	    { 
	    	
	    	for(int i = 0; i < mGenes.length; i++) {
	    		System.out.println(this.getId() + "- Processing: " + mGenes[i]);
	    	}
	    	mResults = null;
	    	mProcessRunning = true;
	    	mHasGivenResults = false;
	    	String search = "";
	    	for(int i = 0; i < mGenes.length; i++) {
	    		search = search + mGenes[i] + ", ";
	    	}
	    	System.out.println(this.getId() + "Quering");
	    	mResults = query(search);
	    	System.out.println(this.getId() + "Queried");
	    	mProcessRunning = false;
	    }

		public boolean hasGivenResults() {
			return mHasGivenResults;
		} 
	} 
    
    class MasterThread extends Thread {
    	final private int THREAD_POOL_SIZE = 24;
    	final private int WORK_SIZE = 1;
    	
    	private WorkerThread mThreadPool[];
    	
    	private boolean mProcessRunning;
    	private boolean mStopProcess;
    	private int mWorkHeadIndex;
    	private Table mResultsTable;
    	
    	
    	public void stopProcess() {
    		mStopProcess = true;
    	}
    	
    	private int nextIndex(int i) {
        	return ((i+WORK_SIZE > mGenelist.length) ? mGenelist.length : i+WORK_SIZE);
        }
    	
    	public String[] nextWork() {
    		if(mWorkHeadIndex < mGenelist.length) {
    			int mNewWorkHead = nextIndex(mWorkHeadIndex);
    			String[] slice = Arrays.copyOfRange(mGenelist, mWorkHeadIndex, mNewWorkHead);
    			mWorkHeadIndex = mNewWorkHead;
    			return slice;
    		}else{
    			return new String[] {};
    		}
    	}
    	
    	public void processResults(ArrayList<ArrayList<Object>> results) {
    		
    		if(results.size() == 0) return;
    		
    		Iterator<ArrayList<Object>> it = results.iterator();
    		while(it.hasNext()) {
    			ArrayList<Object> row = it.next();
    			DefaultTableModel model = (DefaultTableModel) mTable.getModel();
    			model.addRow(row.toArray());
    			mTable.setModel(model);
    			mResultsTable.addRow(row);
    		}
    		
    	}
    	
    	public void run() {
    		mResultsTable = new Table(queryIdentifiers());
    		
    		mProcessRunning = true;
    		mThreadPool = new WorkerThread[THREAD_POOL_SIZE];
    		for(int i = 0; i < THREAD_POOL_SIZE; i++) {
    			mThreadPool[i] = new WorkerThread();
    			mThreadPool[i].setPriority(MAX_PRIORITY);
    		}
    		
    		String[] work;
    		int i = 0;
    		while(((work = nextWork()).length != 0)) {
    			if(mStopProcess) break;
    			
				boolean givenWork = false;
				System.out.println(work.length + "");
				while(!givenWork) {
					
					if(!mThreadPool[i].isAlive()) {
						try {
							mThreadPool[i].join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ArrayList<ArrayList<Object>> results = mThreadPool[i].getResults();
						
						mThreadPool[i] = new WorkerThread();
	        			mThreadPool[i].setWork(work);
	        			mThreadPool[i].start();
	    				
	    				if(results != null)
	    					processResults(results);
	    				
	    				givenWork = true;
	    			}
					i = (i + 1) % THREAD_POOL_SIZE;
				}
				
			}
	    		
    		for(i = 0; i < mThreadPool.length; i++) {
    			WorkerThread worker = mThreadPool[i];
    			try {
    				if(worker.isAlive()) {
    					worker.join();
    				}
    				
    				if(!worker.hasGivenResults()) {
    					ArrayList<ArrayList<Object>> results = worker.getResults();
    					processResults(results);
    				}
    				
    				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		
    		mMainWindow.loadTable(mResultsTable, "TargetMine");
    		
    		mProcessRunning = false;
    	}

		public boolean isRunning() {
			return false;
		}
    }
     
    public void start() {
    	if(!mMasterThread.isRunning()) {
    		mMasterThread.start();
    	}
    }
    
	public void run() {
		
    	mProcessRunning = true;
    	mStopProcess = false;
    	String[] columnIdentifiers = 
    		{"(TM) Gene", "(TM) Secondary Identifier", "(TM) Organism Name", 
    		 "(TM) GO Identifier", "(TM) GO Name", "(TM) GO Code", "(TM) GO Namespace"};
    	ArrayList<String> columnList = new ArrayList<String>();
    	
    	DefaultTableModel model = new DefaultTableModel();
    	model.setColumnIdentifiers(columnIdentifiers);
    	mTable.setModel(model);
    	
    	Collections.addAll(columnList, columnIdentifiers);
    	
    	Table targetMineTable = new Table(columnList);
    	
    	int totalFound = 0;
    	ArrayList<ArrayList<Object>> stepResults = null;
    	
    	
    	for(int i = 0; i < mGenelist.length; i = i + mStepSize) {
    		String search = makeSearch(i, i+mStepSize);
    		stepResults = query(search);
    		
    		Iterator<ArrayList<Object>> it = stepResults.iterator();
    		while(it.hasNext()) {
    			ArrayList<Object> row = it.next();
    			
    			model = (DefaultTableModel) mTable.getModel();
    			model.addRow(row.toArray());
    			mTable.setModel(model);
    			
    			if(!targetMineTable.addRow(row)) {
    				System.out.println("Failed to add row to ");
    			}
    		}
    		
    		totalFound = totalFound + stepResults.size();
    		
    		
    		updateSearchStatus();
    		
    	}
 
    	mMainWindow.loadTable(targetMineTable, "TargetMine");
    	
	}
	

}
