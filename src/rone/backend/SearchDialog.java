package rone.backend;

package rone.ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rone.filemanager.FileManager;
import rone.filemanager.Table;
import rone.ui.MainWindow;

public class SearchDialog extends JDialog {
	
	private SearchInterface mSearch;
	
	private SearchDialog mSearchDialog;
	private final JPanel mContentPanel = new JPanel();
	private JTable mTable;
	private MainWindow mMainWindow; 
	private JLabel mLblSearchStatus;
	
	
	private String[] mSearchRequests;
	private Integer mSearched;
	private Integer mResultsFound;
	private Integer mStepSize;
	private MasterThread mMasterThread;


	private JScrollPane scrollPane;
	
    private void stopMasterThread() {
    	try {
    		
			mMasterThread.stopProcess();
			mMasterThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

	public SearchDialog(MainWindow mainWindow, SearchInterface search, String[] searchRequests) {
		this.mSearch = search;
		setIconImage(Toolkit.getDefaultToolkit().getImage(this.mSearch.iconLocation()));
		setTitle(this.mSearch.title());
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		mContentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(mContentPanel, BorderLayout.CENTER);
		mContentPanel.setLayout(new BorderLayout(0, 0));

		mTable = new JTable();
		{
			JPanel panel = new JPanel();
			mContentPanel.add(panel, BorderLayout.NORTH);
			{
				mLblSearchStatus = new JLabel("Total: 0 | Seached: 0 | Results Found: 0");
				panel.add(mLblSearchStatus);
			}
		}
		{
			scrollPane = new JScrollPane(mTable);
			mContentPanel.add(scrollPane, BorderLayout.CENTER);
		}
		
		this.mSearchDialog = this;
		this.mMainWindow = mainWindow;
		this.mSearchRequests = searchRequests;
		this.mSearched = 0;
		this.mResultsFound = 0;
		this.mStepSize = 1;
		
		this.updateSearchStatus();
		this.setVisible(true);
		this.mMasterThread = new MasterThread();
		
		this.addWindowListener(new WindowAdapter() 
    	{
    	  public void windowClosed(WindowEvent e)
    	  {
    	    System.out.println("jdialog window closed event received");
    	    
    	  }

    	  public void windowClosing(WindowEvent e)
    	  {
    	    System.out.println("jdialog window closing event received");
    	    stopMasterThread();
    	  }
    	});
		
		
	}

	private void updateSearchStatus() {
		this.mLblSearchStatus.setText("Total: " + mSearchRequests.length +
									  " | Searched: " + mSearched + 
									  " | Results Found: " + mResultsFound);
	}

	
	

    
    class WorkerThread extends Thread 
	{ 
    	private ArrayList<ArrayList<Object>> mResults;
    	private String[] mSearchRequests;
    	private boolean mHasGivenResults;
    	
    	WorkerThread(){
    		mResults = new ArrayList<ArrayList<Object>>();
    		this.setPriority(MAX_PRIORITY);
    	}
    	
    	public void setWork(String[] affyProbeIDs) {
    		this.mSearchRequests = affyProbeIDs;
    	}
    	
    	public ArrayList<ArrayList<Object>> getResults(){
    		mHasGivenResults = true;
    		return mResults;
    	}
    	
	    public void run() 
	    { 
	    	for(int i = 0; i < mSearchRequests.length; i++) {
	    		System.out.println(this.getId() + "- Processing: " + mSearchRequests[i]);
	    	}
	    	mHasGivenResults = false;
	    	mResults = mSearch.query(mSearchRequests);
	    }

		public boolean hasGivenResults() {
			return mHasGivenResults;
		}


		public String[] getWork() {
			return this.mSearchRequests;
		} 
	
	} 
    
    class MasterThread extends Thread {
    	final private int THREAD_POOL_SIZE = 5;
    	final private int WORK_SIZE = 20;
    	
    	private WorkerThread mThreadPool[];
    	
    	private boolean mStopProcess;
    	private int mWorkHeadIndex;
    	private Table mResultsTable;
    	
    	public void stopProcess() {
    		System.out.println("stopProcess()");
    		mStopProcess = true;
    	}
    	
    	private int nextIndex(int i) {
        	return ((i+WORK_SIZE > mSearchRequests.length) ? mSearchRequests.length : i+WORK_SIZE);
        }
    	
    	public String[] nextWork() {
    		if(mWorkHeadIndex < mSearchRequests.length) {
    			int mNewWorkHead = nextIndex(mWorkHeadIndex);
    			String[] slice = Arrays.copyOfRange(mSearchRequests, mWorkHeadIndex, mNewWorkHead);
    			mWorkHeadIndex = mNewWorkHead;
    			return slice;
    		}else{
    			return new String[] {};
    		}
    	}
    	
    	public void processResults(String[] completeWork, ArrayList<ArrayList<Object>> results) {
    		if(completeWork != null && completeWork.length != 0) {
    			mSearched = mSearched + completeWork.length;
    		}

    		if(results == null || results.size() == 0) 
    			return;
    		
    		
    		mResultsFound = mResultsFound + results.size();
    		
    		Iterator<ArrayList<Object>> it = results.iterator();
    		while(it.hasNext()) {
    			ArrayList<Object> row = it.next();
    			DefaultTableModel model = (DefaultTableModel) mTable.getModel();
    			model.addRow(row.toArray());
    			mTable.setModel(model);
    			mResultsTable.addRow(row);
    		}
    		
    		updateSearchStatus();
    	}
    	
    	public void initTableModel() {
    		DefaultTableModel dtm = new DefaultTableModel();
    		dtm.setColumnIdentifiers(mSearchDialog.queryIdentifiers().toArray());
    		mSearchDialog.mTable.setModel(dtm);
    	}
    	
    	public boolean unsuccessful(ArrayList<ArrayList<Object>> ptr) {
    		return ptr == null || ptr.size() == 0;
    	}
    	
    	public void run() {
    		
    		initTableModel();
    		
    		mResultsTable = new Table(queryIdentifiers());
    		
    		mThreadPool = new WorkerThread[THREAD_POOL_SIZE];
    		for(int i = 0; i < THREAD_POOL_SIZE; i++) {
    			mThreadPool[i] = new WorkerThread();
    			mThreadPool[i].setPriority(MAX_PRIORITY);
    		}
    		
    		String[] work;
    		int i = 0;
    		while(((work = nextWork()).length != 0)) {
    			if(mStopProcess) {
    				System.out.println("Ending Process!");
    				break;
    			}
    			
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
						String completeWork[] = mThreadPool[i].getWork();
						
						mThreadPool[i] = new WorkerThread();
	        			mThreadPool[i].setWork(work);
	        			mThreadPool[i].start();
	    				
    					processResults(completeWork, results);
    				 					
	    				
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
    					String[] completeWork = worker.getWork();
    					processResults(completeWork, results);
    				}
    				
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		
    		if(!mStopProcess)
    			mMainWindow.loadTable(mResultsTable, "Percellome");
    		
    	}

		public boolean isRunning() {
			return false;
		}
    }
    
    public void start() {
    	if(!mMasterThread.isAlive()) {
    		mMasterThread = new MasterThread();
    		mMasterThread.start();
    	}
    }
       
}
