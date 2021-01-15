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
import rone.ui.ReactomeSearchDialog.MasterThread;
import rone.ui.ReactomeSearchDialog.WorkerThread;

public class PercellomeSearchDialog extends JDialog {
	
	private PercellomeSearchDialog mPercellomeSearchDialog;
	private final JPanel mContentPanel = new JPanel();
	private JTable mTable;
	private MainWindow mMainWindow; 
	private JLabel mLblSearchStatus;
	
	
	private String[] mAffyProbeIDs;
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
	public PercellomeSearchDialog(MainWindow mainWindow, String[] affyProbeIDs) {
		Path currentRelativePath = Paths.get("");
		String percellomeIconPath = currentRelativePath.toAbsolutePath().toString() + "\\percellome_logo.png";
		setIconImage(Toolkit.getDefaultToolkit().getImage(percellomeIconPath));
		setTitle("Percellome Search");
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
		
		this.mPercellomeSearchDialog = this;
		this.mMainWindow = mainWindow;
		this.mAffyProbeIDs = affyProbeIDs;
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
		this.mLblSearchStatus.setText("Total: " + mAffyProbeIDs.length +
									  " | Searched: " + mSearched + 
									  " | Results Found: " + mResultsFound);
	}
	
	static private ArrayList<String> queryIdentifiers(){
		return new ArrayList<String>(
				Arrays.asList("(R) AffyID", "(R) Common", "(R) Biological Process", "(R) Cellular Component", "(R) Molecular Function"));
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
	
	
	private static ArrayList<Object[]> query(String[] affyProbeIDs) {
		
		String urlString = null; 
		String jsonString = null;
		JSONObject jsonObj = null;
		ArrayList<ArrayList<Object>> searchResults = new ArrayList<ArrayList<Object>>();
		
		for(int i = 0; i < affyProbeIDs.length; i++) {
			try {
				ArrayList<Object> row = new ArrayList<Object>();
			
				String probe = affyProbeIDs[i];
				urlString = "http://percellome.nihs.go.jp/PDBR/v1.dll/ds/rest/tools/geneinfo/mouse/" + probe;
				
				System.out.println("Fetching: " + urlString);
				jsonString = fetchPecellomeURL(urlString);
				
				System.out.println("Fetched JSON String: " + jsonString);
				
				// Probe ID
				jsonObj = new JSONObject(jsonString);
				System.out.println("AffyID: " + jsonObj.get("AffyID"));
				row.add(probe);
				
				// Gene Symbol
				System.out.println("Common: " + jsonObj.get("Common"));
				row.add(jsonObj.get("Common"));
				
				String description = (String)jsonObj.get("Descruption");
				String[] found = parseDescription(description);
				
				// Biological Function
				row.add(found[0]);
				
				// Cellular Function
				row.add(found[1]);
				
				// Molecular Function
				row.add(found[2]);
				
				searchResults.add(row);
					
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			return searchResults; 
			
		}
	}
	
	private static ArrayList<Object[]> parseQuery(String jsonQuery) {
		return null; 
	}
    
    class WorkerThread extends Thread 
	{ 
    	private ArrayList<Object[]> mResults;
    	private String[] mAffyProbeIDs;
    	private boolean mHasGivenResults;
    	
    	WorkerThread(){
    		mResults = new ArrayList<Object[]>();
    		this.setPriority(MAX_PRIORITY);
    	}
    	
    	public void setWork(String[] affyProbeIDs) {
    		this.mAffyProbeIDs = affyProbeIDs;
    	}
    	
    	public ArrayList<Object[]> getResults(){
    		mHasGivenResults = true;
    		return mResults;
    	}
    	
	    public void run() 
	    { 
	    	for(int i = 0; i < mAffyProbeIDs.length; i++) {
	    		System.out.println(this.getId() + "- Processing: " + mAffyProbeIDs[i]);
	    	}
	    	mResults = null;
	    	mHasGivenResults = false;
	    	mResults = query(mAffyProbeIDs);
	    }

		public boolean hasGivenResults() {
			return mHasGivenResults;
		}


		public String[] getWork() {
			return this.mAffyProbeIDs;
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
        	return ((i+WORK_SIZE > mAffyProbeIDs.length) ? mAffyProbeIDs.length : i+WORK_SIZE);
        }
    	
    	public String[] nextWork() {
    		if(mWorkHeadIndex < mAffyProbeIDs.length) {
    			int mNewWorkHead = nextIndex(mWorkHeadIndex);
    			String[] slice = Arrays.copyOfRange(mAffyProbeIDs, mWorkHeadIndex, mNewWorkHead);
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
    		dtm.setColumnIdentifiers(mPercellomeSearchDialog.queryIdentifiers().toArray());
    		mPercellomeSearchDialog.mTable.setModel(dtm);
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
    
    public static String fetchPecellomeURL(String urlString) throws IOException {
    	StringBuilder stringToBuild = new StringBuilder();
		URL url = new URL(urlString);
	    BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(url.openStream()));
	    String line;
	    while ((line = reader.readLine()) != null)
	    {
	    	stringToBuild.append(line);
	    	System.out.println(line);
	    }
	    reader.close();
	    String output = stringToBuild.toString();
	    return output.substring(2, output.length()-2);
    }
    
    public static String[] parseDescription(String description) {
		String[] find = {"<<<BiologicalProcess>>>.*<<<CellularComponent>>>", 
                		 "<<<CellularComponent>>>.*<<<MolecularFunction>>>", 
                		 "<<<MolecularFunction>>>.*"};

		String[] found = {"", "", ""};

		for (int i = 0; i < find.length; i++) { 
			Pattern pattern = Pattern.compile(find[i], Pattern.DOTALL | Pattern.MULTILINE);
			Matcher m = pattern.matcher(description);
			System.out.println("----------");
			if (m.find()) {
				System.out.println(m.group(0));
				found[i] = m.group(0);
			} else {
				System.out.println("None");
				found[i] = null;
			}
		}
		
		if(found[0] != null) found[0] = found[0].replace("\n", "").substring(23, found[0].length()-23);
		else found[0] = "";
		
		if(found[1] != null) found[1] = found[1].replace("\n", "").substring(23, found[1].length()-23);
		else found[1] = "";
		
		if(found[2] != null) found[2] = found[2].replace("\n", "").substring(23);
		else found[2] = "";
		
		return found;
    }
    
    
}
