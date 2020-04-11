package rone.ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

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

import rone.filemanager.Table;

public class ReactomeSearchDialog extends JDialog {
	
	private ReactomeSearchDialog mTargetMineSearchDialog;
	private final JPanel mContentPanel = new JPanel();
	private JTable mTable;
	private MainWindow mMainWindow; 
	private JLabel mLblSearchStatus;
	
	
	private String[] mGenelist;
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

	/**
	 * Create the dialog.
	 */
	public ReactomeSearchDialog(MainWindow mainWindow, String[] genelist) {
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Richard\\eclipse-workspace\\RONE\\icons\\reactomelogo.png"));
		setTitle("Reactome Search");
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
		
		this.mTargetMineSearchDialog = this;
		this.mMainWindow = mainWindow;
		this.mGenelist = genelist;
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
		this.mLblSearchStatus.setText("Total: " + mGenelist.length +
									  " | Searched: " + mSearched + 
									  " | Results Found: " + mResultsFound);
	}
	
	static private ArrayList<String> queryIdentifiers(){
		return new ArrayList<String>(
				Arrays.asList("Gene", "(R) Pathway", "(R) Species", "(R) stId", "(R) Coverage", "(R) P-Value", "(R) FDR"));
		}
	
	public static String[] getCommand(String gene, int resultsPerPage, int pageNumber) {
		String url= "\"https://reactome.org/AnalysisService/identifier/" 
					+ gene 
					+ "?interactors=false&pageSize=" 
					+ resultsPerPage 
					+ "&page=" 
					+ pageNumber 
					+ "&sortBy=ENTITIES_PVALUE&order=ASC&resource=TOTAL&pValue=1&includeDisease=true\"";
		
		return new String[] {"curl", "-X", "GET", url, "-H", "\"accept: application/json\""}; 
	}
	
	private static int getNumberOfPathways(String jsonQuery) {
		try {
			JSONObject jo = new JSONObject(jsonQuery);
			return jo.getInt("pathwaysFound");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	private static ArrayList<ArrayList<Object>> query(String[] genes) {
		
		String jsonQuery = null;
		ArrayList<ArrayList<Object>> pathways = new ArrayList<ArrayList<Object>>();
		for(int i = 0; i < genes.length; i++) {
			String gene = genes[i];
			
			try
			{
				int resultsPerPage = 1000; 
				int pathwaysSearched = 0;
				int numberOfPathways = 0;
				int pageNumber = 1;
				do {
					String[] command = getCommand(gene, resultsPerPage, pageNumber);
					ProcessBuilder process = new ProcessBuilder(command); 
					Process p;
					
					p = process.start();
					BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					StringBuilder builder = new StringBuilder();
					String line = null;
					while ( (line = reader.readLine()) != null) {
						builder.append(line);
						builder.append(System.getProperty("line.separator"));
					}
					jsonQuery = builder.toString();
					System.out.print(jsonQuery);
					
					numberOfPathways = getNumberOfPathways(jsonQuery);
					ArrayList<ArrayList<Object>> searchResults = parseQuery(jsonQuery);
					for(ArrayList<Object> o : searchResults) 
						o.add(0, gene);
					pathways.addAll(searchResults);
					
					pathwaysSearched = pathwaysSearched + resultsPerPage;
					pageNumber++;
				} while(pathwaysSearched < numberOfPathways);
				
			} catch (IOException e) {
				return null;
			}
		
		}
		return pathways;
	}
	
	private static ArrayList<ArrayList<Object>> parseQuery(String jsonQuery) {
		JSONObject jo;
		JSONArray pathways;
		
		try {
			jo = new JSONObject(jsonQuery);		
			pathways= jo.getJSONArray("pathways");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		ArrayList<ArrayList<Object>> rows = new ArrayList<ArrayList<Object>>();
		
		for(int i = 0; i < pathways.length(); i++) {
			
			JSONObject pathway;
			JSONObject species;
			JSONObject entities;
			try {
				pathway = pathways.getJSONObject(i);
				species = pathway.getJSONObject("species");
				entities = pathway.getJSONObject("entities");
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}

			
			ArrayList<Object> row = new ArrayList<Object>();
			try {
				// "(R) Pathway" - "name":"Citric acid cycle (TCA cycle)"
				row.add(pathway.getString("name"));
				
				// "(R) Species" - "name":"Homo sapiens"
				row.add(species.getString("name"));
							
				// "(R) stId" - "stId":"R-HSA-71403"
				row.add(pathway.getString("stId"));
				
				// "(R) Coverage" - "ratio":0.0013613068545803972
				row.add(entities.getString("ratio"));
				
				// "(R) P-Value"- "pValue":0.030749978135627742
				row.add(entities.getString("pValue"));
				
				// "(R) FDR" - "fdr":0.10114749050397542
				row.add(entities.getString("fdr"));
				
				// System.out.println(pathway.getString("name")  + " : " + species.getString("name") + " : " + pathway.getString("stId") + " : " + entities.getString("ratio"));
				
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			
			rows.add(row);	
		}
		
		return rows;
	}
    
    class WorkerThread extends Thread 
	{ 
    	private ArrayList<ArrayList<Object>> mResults;
    	private String[] mGenes;
    	private boolean mHasGivenResults;
    	
    	WorkerThread(){
    		mResults = new ArrayList<ArrayList<Object>>();
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
	    	mHasGivenResults = false;
	    	mResults = query(mGenes);
	    }

		public boolean hasGivenResults() {
			return mHasGivenResults;
		}


		public String[] getWork() {
			return this.mGenes;
		} 
	} 
    
    class MasterThread extends Thread {
    	final private int THREAD_POOL_SIZE = 4;
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
    		dtm.setColumnIdentifiers(mTargetMineSearchDialog.queryIdentifiers().toArray());
    		mTargetMineSearchDialog.mTable.setModel(dtm);
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
    			mMainWindow.loadTable(mResultsTable, "Reactome");
    		
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
