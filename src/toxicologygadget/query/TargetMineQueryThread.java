package toxicologygadget.query;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import toxicologygadget.filemanager.Table;
import toxicologygadget.ui.TargetMineSearchDialog;

import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;

public class TargetMineQueryThread extends Thread
{
    private static final String ROOT = "https://targetmine.mizuguchilab.org/targetmine/service";
    private String[] mGenelist;
    private QueryThreadCallback mCallback;
    private boolean mProcessRunning;
    private boolean mStopProcess;
    private int mStepSize;
    
    public void setGenelist(String[] geneList){
    	this.mGenelist = geneList;
    }
    
    public TargetMineQueryThread(QueryThreadCallback callback){
    	this.mCallback = callback;
    	this.mProcessRunning = false;
    	this.mStopProcess = false;
    	this.mStepSize = 5;
 
    	
    }
    
    
    public ArrayList<ArrayList<Object>> query(String genes) {
    	
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
        	
        	for(int i = 0; i < row.length; i++) {
        		System.out.print(row[i] + " : ");
        		
        	}
        	System.out.print("\n");
        	
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
    
    public boolean isRunning() {
		return mProcessRunning;
    }
    
    public void stopRunning() {
    	mStopProcess = true;
    }
    
    public void setStepSize(int stepSize) {
    	this.mStepSize = stepSize;
    }
    
    private int numGenesToSearch(int i) {
    	return ((i+mStepSize > mGenelist.length) ? mGenelist.length : i+mStepSize);
    }
    
    public void run(){ 
    	System.out.println("TargetMineQueryThread.run()");
    	mProcessRunning = true;
    	mStopProcess = false;
    	String[] columnIdentifiers = 
    		{"(TM) Gene", "(TM) Secondary Identifier", "(TM) Organism Name", 
    		 "(TM) GO Identifier", "(TM) GO Name", "(TM) GO Code", "(TM) GO Namespace"};
    	ArrayList<String> columnList = new ArrayList<String>();
    	Collections.addAll(columnList, columnIdentifiers);
    	
    	Table targetMineTable = new Table(columnList);
    	
    	int totalFound = 0;
    	ArrayList<ArrayList<Object>> stepResults = null;
    	mCallback.startSearch(mGenelist.length);
    	
    	for(int i = 0; i < mGenelist.length; i = i + mStepSize) {
    		if(mStopProcess) 
    			break;
    		
    		String search = makeSearch(i, i+mStepSize);
    		
    		stepResults = query(search);
    		
    		Iterator<ArrayList<Object>> it = stepResults.iterator();
    		while(it.hasNext()) {
    			if(!targetMineTable.addRow(it.next())) {
    				System.out.println("ERROR");
    			}
    		}
    		
    		totalFound = totalFound + stepResults.size();
    		int genesSearched = numGenesToSearch(i) ;
    		mCallback.statusUpdate(genesSearched, mGenelist.length, totalFound);
    	}
    	
    	if(mStopProcess) {
    		mCallback.completeSearch(targetMineTable, QueryThreadCallback.statusCodeFinishStopped);
    	} else {
    		mCallback.completeSearch(targetMineTable, QueryThreadCallback.statusCodeFinishSuccess);
    	}
    	
      
    }
    

}