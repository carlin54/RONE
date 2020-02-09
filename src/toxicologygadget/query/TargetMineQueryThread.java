package toxicologygadget.query;


import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import toxicologygadget.filemanager.DataTable;

import org.intermine.metadata.ConstraintOp;
import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathConstraint;
import org.intermine.pathquery.PathQuery;



public class TargetMineQueryThread extends Thread
{
    private static final String ROOT = "https://targetmine.mizuguchilab.org/targetmine/service";
    private String[] geneList;
    private QueryThreadCallback callback;
    
    
    private static String formatGenelistForTargetMine(String[] genelist){
    	String genestring = new String("");
    	for(int i = 0; i < genelist.length-2; i++) {
    		genestring += genestring + genelist[i] + ", ";
    	}
    	genestring = genestring + genelist[genelist.length-1];
		return genestring;
    	
    }
    
    public void setGenelist(String[] geneList){
    	
    		this.geneList = geneList;
        
     }
    
    public TargetMineQueryThread(QueryThreadCallback callback){
    	this.callback = callback;
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
    	while(i < j && i < geneList.length) {
    		search = search + geneList[i] + ", ";
    		i++;
    	}
    	return search;
    }
    
    public void run(){ 
    	
    	String[] columnIdentifiers = {"Gene", "Secondary Identifier", "Organism Name", "GO Identifier", "GO Name", "GO Code", "GO Namespace"};
    	ArrayList<ArrayList<Object>> table = new ArrayList<ArrayList<Object>>();
    	
    	int stepSize = 5; 
    	
    	for(int i = 0; i < geneList.length; i = i + stepSize) {
    		
    		String search = makeSearch(i, i+stepSize);
    		ArrayList<ArrayList<Object>> stepResults = query(search);
    		
    		table.addAll(stepResults);
    	}
    	
    	
    	DataTable targetMineTable = new DataTable(table, columnIdentifiers);
    	
    	callback.completeSearch(targetMineTable);
      
    }
    

}