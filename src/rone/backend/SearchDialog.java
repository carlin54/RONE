package rone.backend;

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
import java.sql.SQLException;
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

import rone.filemanager.Database;
import rone.filemanager.FileManager;
import rone.filemanager.Table;
import rone.ui.MainWindow;

import javax.swing.JToolBar;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class SearchDialog extends JDialog {
	
	private SearchInterface mSearchInterface;
	
	private SearchDialog mSearchInterfaceDialog;

	private String[] mSearchInterfaceRequests;
	private final JPanel mContentPanel = new JPanel();
	private JTable mTable;
	private MainWindow mMainWindow; 
	private JLabel mLblSearchStatus;
	
	private Integer mResultsFound;
	private Integer mRequestsSearched;
	private Integer mStepSize;
	private MasterThread mMasterThread;

	private JScrollPane scrollPane;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmClose;
	private JMenu mnExport;
	private JMenuItem mntmToFile;
	private JMenuItem mntmToTable;
	private JMenu mnToGaruda;
	private JMenuItem mntmGenelist;
	private JMenuItem mntmEnsemble;
	private JMenu mnNewMenu;
	private JMenu mnPercellome;
	private JMenu mnTargetMine;
	private JMenu mnReactome;
	private JMenu mnBioCompendium;
	private JMenuItem mntmPercellomeProbeIDs;
	private JMenuItem mntmTargetMineGeneSymbols;
	private JMenuItem mntmReactomeGeneSymbols;
	private JMenu mnJoin;
	private JMenuItem mntmJoinTable;
	private JMenuItem mntmWithSelect;
	
	private final ActionFileImportFromFile actionImportActionFromFile = new ActionFileImportFromFile();
	private final ActionFileExportToFile actionExportActionToFile = new ActionFileExportToFile();
	private final ActionFileExportToGarudaAsGenelist actionExportGarudaToGenelist = new ActionFileExportToGarudaAsGenelist();
	private final ActionFileExportToGarudaAsEnsemble actionExportGarudaToEnsemble = new ActionFileExportToGarudaAsEnsemble();
	private final ActionFileExportToTable actionFileExportToTable = new ActionFileExportToTable();
	private final ActionSearchTargetMineWithGeneSymbols actionTargetMineWithGeneSymbols = new ActionSearchTargetMineWithGeneSymbols();
	private final ActionSearchPercellomeWithProbeID actionPercellomeProbeID = new ActionSearchPercellomeWithProbeID();
	private final ActionSearchReactomeWithGeneSymbols actionReactomeWithGeneSymbols = new ActionSearchReactomeWithGeneSymbols();
	private final ActionSearchBioCompendiumWithSelect actionBioCompendiumWithSelect = new ActionSearchBioCompendiumWithSelect();
	private final ActionTableJoin actionTableJoin = new ActionTableJoin();
	private final ActionTableClear actionTableClear = new ActionTableClear();
	
	private JMenu mnImport;
	private JMenuItem mntmFromFile;
	private JMenuItem mntmClear;
	
    private void stopMasterThread() {
    	try {
    		
			mMasterThread.stopProcess();
			mMasterThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    
	public SearchDialog(MainWindow mainWindow, SearchInterface searchInterface, String[] searchRequests) {
		this.mSearchInterface = searchInterface;
		System.out.println("SearchDialog()");
		setIconImage(Toolkit.getDefaultToolkit().getImage(this.mSearchInterface.getIconLocation()));
		setTitle(this.mSearchInterface.getTitle());
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
				mLblSearchStatus.setVisible(false);
				panel.add(mLblSearchStatus);
			}
		}
		{
			scrollPane = new JScrollPane(mTable);
			mContentPanel.add(scrollPane, BorderLayout.CENTER);
		}
		{
			menuBar = new JMenuBar();
			getContentPane().add(menuBar, BorderLayout.NORTH);
			{
				mnFile = new JMenu("File");
				menuBar.add(mnFile);
				{
					mnImport = new JMenu("Import");
					mnFile.add(mnImport);
					{
						mntmFromFile = new JMenuItem("from File");
						mntmFromFile.setAction(actionImportActionFromFile);
						mnImport.add(mntmFromFile);
					}
				}
				{
					mnExport = new JMenu("Export");
					mnFile.add(mnExport);
					{
						mntmToFile = new JMenuItem("to File");
						mntmToFile.setAction(actionExportActionToFile);
						mnExport.add(mntmToFile);
					}
					{
						mntmToTable = new JMenuItem("to Table");
						mntmToTable.setAction(actionFileExportToTable);
						mnExport.add(mntmToTable);
					}
					{
						mnToGaruda = new JMenu("to Garuda");
						mnExport.add(mnToGaruda);
						{
							mntmGenelist = new JMenuItem("Genelist");
							mntmGenelist.setAction(actionExportGarudaToGenelist);
							mnToGaruda.add(mntmGenelist);
						}
						{
							mntmEnsemble = new JMenuItem("Ensemble");
							mntmEnsemble.setAction(actionExportGarudaToEnsemble);
							mnToGaruda.add(mntmEnsemble);
						}
					}
				}
				{
					mntmClose = new JMenuItem("Close");
					mnFile.add(mntmClose);
				}
			}
			{
				mnNewMenu = new JMenu("Search");
				menuBar.add(mnNewMenu);
				{
					mnPercellome = new JMenu("Percellome");
					mnNewMenu.add(mnPercellome);
					{
						mntmPercellomeProbeIDs = new JMenuItem("with Probe IDs (Affy IDs)");
						mntmPercellomeProbeIDs.setAction(actionPercellomeProbeID);
						mnPercellome.add(mntmPercellomeProbeIDs);
					}
				}
				{
					mnTargetMine = new JMenu("TargetMine");
					mnNewMenu.add(mnTargetMine);
					{
						mntmTargetMineGeneSymbols = new JMenuItem("with Gene Symbols");
						mntmTargetMineGeneSymbols.setAction(actionTargetMineWithGeneSymbols);
						mnTargetMine.add(mntmTargetMineGeneSymbols);
					}
				}
				{
					mnReactome = new JMenu("Reactome");
					mnNewMenu.add(mnReactome);
					{
						mntmReactomeGeneSymbols = new JMenuItem("with Gene Symbols");
						mntmReactomeGeneSymbols.setAction(actionReactomeWithGeneSymbols);
						mnReactome.add(mntmReactomeGeneSymbols);
					}
				}
				{
					mnBioCompendium = new JMenu("bioCompendium (unavaliable)");
					mnNewMenu.add(mnBioCompendium);
					{
						mntmWithSelect = new JMenuItem("with Select");
						mntmWithSelect.setAction(actionBioCompendiumWithSelect);
						mnBioCompendium.add(mntmWithSelect);
					}
				}
			}
			{
				mnJoin = new JMenu("Table");
				menuBar.add(mnJoin);
				{
					mntmJoinTable = new JMenuItem("Join Table");
					mntmJoinTable.setAction(actionTableJoin);
					mnJoin.add(mntmJoinTable);
				}
				{
					mntmClear = new JMenuItem("Clear");
					mntmJoinTable.setAction(actionTableClear);
					mnJoin.add(mntmClear);
				}
			}

		}
		
		this.mSearchInterfaceDialog = this;
		this.mMainWindow = mainWindow;
		this.mSearchInterfaceRequests = searchRequests;
		this.mSearchInterface = searchInterface;
		this.mRequestsSearched = 0;
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
		this.mLblSearchStatus.setText("Total: " + mSearchInterfaceRequests.length +
									  " | Searched: " + mRequestsSearched + 
									  " | Results Found: " + mResultsFound);
	}

	
    class WorkerThread extends Thread 
	{ 
    	private ArrayList<Object[]> mResults;
    	private String[] mSearchInterfaceRequests;
    	private boolean mHasGivenResults;
    	
    	WorkerThread(){
    		mResults = new ArrayList<Object[]>();
    	}
    	
    	public void setWork(String[] work) {
    		this.mSearchInterfaceRequests = work;
    	}
    	
    	public ArrayList<Object[]> getResults(){
    		setHasGivenResults(true);
    		return mResults;
    	}

    	public void setHasGivenResults(boolean set) {
    		mHasGivenResults = set;
    	}
    	
    	public void setResults(ArrayList<Object[]> results) {
    		mResults = results;
    	}
    	
	    public void run() 
	    { 
    		System.out.println(this.getId() + "- Processing: " + mSearchInterfaceRequests.length);
    		ArrayList<Object[]> results = mSearchInterface.query(mSearchInterfaceRequests);
    		setResults(results);
    		setHasGivenResults(false);
	    }

		public boolean hasGivenResults() {
			return mHasGivenResults;
		}


		public String[] getWork() {
			return this.mSearchInterfaceRequests;
		} 
	
	} 
    
    
    class MasterThread extends Thread {
    	private int mThreadPoolSize = 5;
    	private int mWorkSize = 20;
    	
    	private WorkerThread mThreadPool[];
    	
    	private boolean mProcessSuccess;
    	private boolean mStopWorking;
    	private int mWorkHeadIndex;
    	private Database.Table mResultsTable;
    	
    	public MasterThread() {
    		mThreadPoolSize = mSearchInterface.getThreadPoolSize();
    		mWorkSize = mSearchInterface.getWorkSize();
    	}
    	
    	public void stopProcess() {
    		System.out.println("stopProcess()");
    		mStopWorking = true;
    	}
    	
    	private int nextIndex(int i) {
        	return ((i+mWorkSize > mSearchInterfaceRequests.length) ? mSearchInterfaceRequests.length : i+mWorkSize);
        }
    	
    	public String[] getNextWork() {
    		if(mWorkHeadIndex < mSearchInterfaceRequests.length) {
    			int mNewWorkHead = nextIndex(mWorkHeadIndex);
    			String[] slice = Arrays.copyOfRange(mSearchInterfaceRequests, mWorkHeadIndex, mNewWorkHead);
    			mWorkHeadIndex = mNewWorkHead;
    			return slice;
    		}else{
    			return new String[] {};
    		}
    	}
    	
    	private boolean hasResults(ArrayList<Object[]> results) {
    		return (results != null) && results.size() > 0;
    	}
    	
    	private int totalCompletedWork(String[] work) {
    		return work == null ? 0 : work.length; 
    	}
    	
    	private void updateTableModel(ArrayList<Object[]> results) {
    		
    		Iterator<Object[]> it = results.iterator();
    		
    		while(it.hasNext()) {
    			Object[] row = it.next();
    			DefaultTableModel model = (DefaultTableModel) mTable.getModel();
    			model.addRow(row);
    			mTable.setModel(model);
    		}
    		
    	}
    	public void processResults(String[] completeWork, ArrayList<Object[]> results) {
    		
    		mRequestsSearched = mRequestsSearched + totalCompletedWork(completeWork); 
    		if(!hasResults(results)) 
    			return;
    		
    		mResultsFound = mResultsFound + results.size();
    		
    		try {
				mResultsTable.insertRows(results);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		updateTableModel(results);
    		
    		updateSearchStatus();
    	}
    	
    	private DefaultTableModel makeDefaultTableModel() {
    		DefaultTableModel dtm = new DefaultTableModel();
    		dtm.setColumnIdentifiers(mSearchInterface.getColumnIdentifers());
    		return dtm; 
    	}
    	
    	public boolean unsuccessful(ArrayList<Object[]> ptr) {
    		return ptr == null || ptr.size() == 0;
    	}
    	
    	public boolean hasWork(String[] work) {
    		return work.length > 0;
    	}
    	
    	
    	private WorkerThread[] makeThreadPool(int size, int priority) {
    		WorkerThread[] threadPool = new WorkerThread[size];
    		for(int i = 0; i < mThreadPoolSize; i++) {
    			threadPool[i] = new WorkerThread();
    			threadPool[i].setPriority(priority);
    		}
    		return threadPool;
    	}
    	
    	public void giveWork(String[] work) {
			int i = 0;
			boolean givenWork = false;
			while(!givenWork) {
				
				
				if(!mThreadPool[i].isAlive()) {
					try {
						mThreadPool[i].join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ArrayList<Object[]> results = mThreadPool[i].getResults();
					String completeWork[] = mThreadPool[i].getWork();
					
					//TODO: check if i don't need a new worker thread
					mThreadPool[i] = new WorkerThread();
        			mThreadPool[i].setWork(work);
        			mThreadPool[i].start();
        			
					processResults(completeWork, results);
    				
    				givenWork = true;
    			}
				i = (i + 1) % mThreadPoolSize;
			}
    	}
    	
    	private void cleanUp() {
    		for(int i = 0; i < mThreadPool.length; i++) {
    			WorkerThread worker = mThreadPool[i];
    			try {
    				if(worker.isAlive()) {
    					worker.join();
    				}
    				
    				if(!worker.hasGivenResults()) {
    					ArrayList<Object[]> results = worker.getResults();
    					String[] completeWork = worker.getWork();
    					processResults(completeWork, results);
    				}
    				
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		mThreadPool = null;
    		mWorkHeadIndex = 0;
    	}
    	
    	private boolean hasToStopWorking() {
    		return mStopWorking;
    	}
    	
    	public void setProcessSuccess(boolean set) {
    		mProcessSuccess = set;
    	}
    	
    	public void run() {
    		
    		DefaultTableModel defaultTableModel = makeDefaultTableModel();
    		mSearchInterfaceDialog.mTable.setModel(defaultTableModel);
    		
    		mThreadPool = makeThreadPool(mThreadPoolSize, MAX_PRIORITY);
    		
    		String[] work = null;
    		
    		setProcessSuccess(false);
    		
    		while(hasWork(work = getNextWork())) {
    			if(hasToStopWorking()) {
    				System.out.println("Ending Process!");
    				break;
    			}
    			giveWork(work);
			}
	    		
    		cleanUp();
    		
    		if(!hasToStopWorking()) {
    			setProcessSuccess(true);
    		}
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
    
	private class ActionFileImportFromFile extends AbstractAction {
		public ActionFileImportFromFile() {
			putValue(NAME, "from File");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionFileExportToFile extends AbstractAction {
		public ActionFileExportToFile() {
			putValue(NAME, "to File");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionFileExportToGarudaAsGenelist extends AbstractAction {
		public ActionFileExportToGarudaAsGenelist() {
			putValue(NAME, "as Genelist");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionFileExportToGarudaAsEnsemble extends AbstractAction {
		public ActionFileExportToGarudaAsEnsemble() {
			putValue(NAME, "as Ensemble");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class ActionFileExportToTable extends AbstractAction {
		public ActionFileExportToTable() {
			putValue(NAME, "to Table");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionSearchTargetMineWithGeneSymbols extends AbstractAction {
		public ActionSearchTargetMineWithGeneSymbols() {
			putValue(NAME, "with Gene Symbols");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionSearchPercellomeWithProbeID extends AbstractAction {
		public ActionSearchPercellomeWithProbeID() {
			putValue(NAME, "with Probe IDs (Affy ID)");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionSearchReactomeWithGeneSymbols extends AbstractAction {
		public ActionSearchReactomeWithGeneSymbols() {
			putValue(NAME, "with Gene Symbols");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionSearchBioCompendiumWithSelect extends AbstractAction {
		public ActionSearchBioCompendiumWithSelect() {
			putValue(NAME, "with Select");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionTableJoin extends AbstractAction {
		public ActionTableJoin() {
			putValue(NAME, "Join");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionTableClear extends AbstractAction {
		public ActionTableClear() {
			putValue(NAME, "Clear");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
    


}
