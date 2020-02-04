package toxicologygadget.targetmine;


import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;
import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;

public class TargetMineQueryClient
{
    private static final String ROOT = "https://targetmine.mizuguchilab.org/targetmine/service";
    
    private static String formatGenelistForTargetMine(String[] genelist){
    	String genestring = new String("");
    	for(int i = 0; i < genelist.length-2; i++) {
    		genestring += genestring + genelist[i] + ", ";
    	}
    	genestring = genestring + genelist[genelist.length-1];
		return genestring;
    	
    }
    
    public void setGenelist(){
        System.out.println("MyThread running");
     }
    
    public static String[] query(String[] genelist) throws IOException { //, String... args0
    	
    	String genestring = formatGenelistForTargetMine(genelist);
    	
        ServiceFactory factory = new ServiceFactory(ROOT);
        Model model = factory.getModel();
        PathQuery query = new PathQuery(model);

        // Select the output columns:
        query.addViews(	"Gene.primaryIdentifier",
                		"Gene.symbol",
                		"Gene.name",
                		"Gene.organism.name");

        // Add orderby
        query.addOrderBy("Gene.primaryIdentifier", OrderDirection.ASC);
        
        
        // Filter the results with the following constraints:
        if(query.isEmpty()) 
        	System.out.println("Didn't find anything from TargetMine!");
        query.addConstraint(Constraints.lookup("Gene", genestring, null));
        

        QueryService service = factory.getQueryService();
        PrintStream out = System.out;
        String format = "%-13.13s | %-13.13s | %-13.13s | %-13.13s \n";
        
        out.printf(format, query.getView().toArray());
        
        out.printf("%d rows\n", service.getCount(query));
        
        Iterator<List<Object>> rows = service.getRowListIterator(query);
        
        while (rows.hasNext()) {
            out.printf(format, rows.next().toArray());
        }
        
        
		return genelist;
    }


}