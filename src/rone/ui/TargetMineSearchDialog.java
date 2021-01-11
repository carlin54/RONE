package rone.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import rone.filemanager.Table;

import javax.swing.JLabel;

import javax.swing.JTable;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TargetMineSearchDialog extends JDialog {
	
	private TargetMineSearchDialog mTargetMineSearchDialog;
	private final JPanel mContentPanel = new JPanel();
	private JTable mTable;
	private MainWindow mMainWindow; 
	private JLabel mLblSearchStatus;
	
	
	private String[] mGenelist;
	private Integer mSearched;
	private Integer mResultsFound;
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
	public TargetMineSearchDialog(MainWindow mainWindow, String[] genelist) {
		Path currentRelativePath = Paths.get("");
		String targetMineIconPath = currentRelativePath.toAbsolutePath().toString() + "\\targetmine_logo.png";
		setIconImage(Toolkit.getDefaultToolkit().getImage(targetMineIconPath));
		setTitle("TargetMine Search");
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
		//updateStatus("Disconnected");
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
		/*return new ArrayList<String>( Arrays.asList(
				"(TM) ncbiGeneId" , 
				"(TM) symbol" , 
				"(TM) probeSetId", 
				"(TM) organism.name", 
				"(TM) pathways primaryIdentifier", 
				"(TM) pathways name", 
				"(TM) pathways label1", 
				"(TM) pathways label2") );*/
		
		/*return new ArrayList<String>( Arrays.asList(
				"(TM) Pathway.genes.symbol",
                "(TM) Pathway.name",
                "(TM) Pathway.identifier",
                "(TM) Pathway.label1",
                "(TM) Pathway.label2",
                "(TM) Pathway.genes.primaryIdentifier",
                "(TM) Pathway.genes.name",
                "(TM) Pathway.genes.organism.name",
                "(TM) Pathway.genes.ncbiGeneId",
                "(TM) Pathway.genes.secondaryIdentifier",
                "(TM) Pathway.organism.name",
                "(TM) Pathway.organism.species") );*/
		
		return new ArrayList<String>( Arrays.asList(
				"Gene.ncbiGeneId",
                "Gene.symbol",
                "Gene.probeSets.probeSetId",
                "Gene.organism.name",
                "Gene.name",
                "Gene.probeSets.primaryIdentifier",
                "Gene.pathways.name",
                "Gene.pathways.identifier",
                "Gene.pathways.label1",
                "Gene.pathways.label2",
                "Gene.transcripts.symbol",
                "Gene.transcripts.secondaryIdentifier",
                "Gene.transcripts.primaryIdentifier",
                "Gene.transcripts.name",
                "Gene.transcripts.length") );
	}
	
    static private ArrayList<ArrayList<Object>> query(String genes) {
    	
    	final String ROOT = "https://targetmine.mizuguchilab.org/targetmine/service";
    	
    	ServiceFactory factory = new ServiceFactory(ROOT);
    	Model model = factory.getModel();
   
        
        PathQuery query = new PathQuery(model);

        // Select the output columns:
        /*query.addViews("Gene.ncbiGeneId",
                "Gene.symbol",
                "Gene.probeSets.probeSetId",
                "Gene.organism.name",
                "Gene.name",
                "Gene.probeSets.primaryIdentifier",
                "Gene.pathways.name",
                "Gene.pathways.identifier",
                "Gene.pathways.label1",
                "Gene.pathways.label2");
        
        // Add orderby
        query.addOrderBy("Gene.symbol", OrderDirection.ASC);*/
        
        // Filter the results with the following constraints:
        // query.addConstraint(Constraints.lookup("Gene", genes, null));
        
        /*query.addViews("Pathway.genes.symbol",
                "Pathway.name",
                "Pathway.identifier",
                "Pathway.label1",
                "Pathway.label2",
                "Pathway.genes.primaryIdentifier",
                "Pathway.genes.name",
                "Pathway.genes.organism.name",
                "Pathway.genes.ncbiGeneId",
                "Pathway.genes.secondaryIdentifier",
                "Pathway.organism.name",
                "Pathway.organism.species");
        
        query.addOrderBy("Pathway.genes.symbol", OrderDirection.ASC);
        
        // Filter the results with the following constraints:
        query.addConstraint(Constraints.lookup("Pathway", genes, null));*/
        
        query.addViews("Gene.ncbiGeneId",
                "Gene.symbol",
                "Gene.probeSets.probeSetId",
                "Gene.organism.name",
                "Gene.name",
                "Gene.probeSets.primaryIdentifier",
                "Gene.pathways.name",
                "Gene.pathways.identifier",
                "Gene.pathways.label1",
                "Gene.pathways.label2",
                "Gene.transcripts.symbol",
                "Gene.transcripts.secondaryIdentifier",
                "Gene.transcripts.primaryIdentifier",
                "Gene.transcripts.name",
                "Gene.transcripts.length");

        //query.addConstraint(Constraints.lookup("Gene", genes, null));
        
        QueryService service = factory.getQueryService();
        
        Iterator<List<Object>> rows = service.getRowListIterator(query);
        ArrayList<ArrayList<Object>> results = new ArrayList<ArrayList<Object>>();
        
        while (rows.hasNext()) {
        	Object[] row = rows.next().toArray();
        	
        	ArrayList<Object> rowList = new ArrayList<Object>();
        	
        	for(int i = 0; i < row.length; i++) {
        		rowList.add(row[i]);
        		System.out.println(row[i].toString());
        	}
        	results.add(rowList);
        	
        }
		return results;
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
	    	String search = "";
	    	for(int i = 0; i < mGenes.length; i++) {
	    		search = search + mGenes[i] + ", ";
	    	}
	    	System.out.println(this.getId() + "Quering");
	    	mResults = query(search);
	    	if(mResults.size() > 0) {
	    		System.out.println(mResults.toString());
	    	}
	    	System.out.println(this.getId() + "Queried");
	    }

		public boolean hasGivenResults() {
			return mHasGivenResults;
		}


		public String[] getWork() {
			return this.mGenes;
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
    			mMainWindow.loadTable(mResultsTable, "TargetMine");
    		
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
