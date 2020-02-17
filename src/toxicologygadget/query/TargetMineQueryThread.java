package toxicologygadget.query;


import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import toxicologygadget.filemanager.Database;

import org.intermine.metadata.ConstraintOp;
import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathConstraint;
import org.intermine.pathquery.PathQuery;



public class TargetMineQueryThread extends Thread
{
    private static final String ROOT = "https://targetmine.mizuguchilab.org/targetmine/service";
    private String[] genelist;
    private QueryThreadCallback callback;
    private boolean processRunning;
    private boolean stopProcess;
    private int stepSize;
  
    
    private static String formatGenelistForTargetMine(String[] genelist){
    	String genestring = new String("");
    	for(int i = 0; i < genelist.length-2; i++) {
    		genestring += genestring + genelist[i] + ", ";
    	}
    	genestring = genestring + genelist[genelist.length-1];
		return genestring;
    	
    }
    
    public void setGenelist(String[] geneList){
    		this.genelist = geneList;
     }
    
    public TargetMineQueryThread(QueryThreadCallback callback){
    	this.callback = callback;
    	this.processRunning = false;
    	this.stopProcess = false;
    	this.stepSize = 5;
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
        	ArrayList<Object> rowList = new ArrayList<Object>();
        	
        	for(int i = 0; i < row.length; i++) 
        		rowList.add(row[i]);
        	
        	results.add(rowList);
        	
        	
        }
        
		return results;
    }
    
    private String makeSearch(int i, int j) {
    	String search = new String("");
    	while(i < j && i < genelist.length) {
    		search = search + genelist[i] + ", ";
    		i++;
    	}
    	return search;
    }
    
    public boolean isRunning() {
		return processRunning;
    }
    
    public void stopRunning() {
    	stopProcess = true;
    }
    
    public void setStepSize(int stepSize) {
    	this.stepSize = stepSize;
    }
    
    public void run(){ 
    	
    	processRunning = true;
    	stopProcess = false;
    	String[] columnIdentifiers = 
    		{"Gene", "Secondary Identifier", "Organism Name", 
    		 "GO Identifier", "GO Name", "GO Code", "GO Namespace"};
    	ArrayList<String> columnList = new ArrayList<String>();
    	Collections.addAll(columnList, columnIdentifiers);
    	
    	ArrayList<ArrayList<Object>> table = new ArrayList<ArrayList<Object>>();
     
    	
    	for(int i = 0; i < genelist.length; i = i + stepSize) {
    		if(stopProcess) break;
    		String search = makeSearch(i, i+stepSize);
    		ArrayList<ArrayList<Object>> stepResults = query(search);
    		table.addAll(stepResults);
    	}
    	
    	if(stopProcess) {
    		callback.unsuccessfulSearch("process terminated");
    	} else {
    		Database targetMineTable = new Database(table, columnList);
    		callback.completeSearch(targetMineTable);
    	}
    	
    	
    	
      
    }
    

}