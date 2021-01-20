package rone.backend;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

public class TargetMineSearchInterface implements SearchInterface {
	
	public TargetMineSearchInterface() {
		System.out.println("Hello World! TargetMine Search Interface!!");
	};
	
	@Override
	public String getTitle() {
		return "TargetMine";
	}

	@Override
	public String getIconLocation() {
		Path currentRelativePath = Paths.get("");
		return currentRelativePath.toAbsolutePath().toString() + "\\targetmine_logo.png";
	}

	@Override
	public String[] getColumnIdentifers() {
		
		return new String[]{
				"Gene.primaryIdentifier",
                "Gene.symbol",
                "Gene.name",
                "Gene.pathways.identifier",
                "Gene.pathways.name",
                "Gene.pathways.organism.name",
                "Gene.pathways.label1",
                "Gene.pathways.label2"
		};
		
	}
	
	@Override
	public ArrayList<Object[]> query(String[] searchData) {
		System.out.println("TargetMineSearchInterface: Query begun.");
		final String ROOT = "https://targetmine.mizuguchilab.org/targetmine/service";
    	
    	ServiceFactory factory = new ServiceFactory(ROOT);
    	Model model = null;
    	try {
    		model = factory.getModel();	
    	} catch (java.lang.RuntimeException e) {
    		System.out.println("Runtime Exception!");
    	}
    	
        PathQuery query = new PathQuery(model);
        
        query.addViews(	"Gene.primaryIdentifier",
		                "Gene.symbol",
		                "Gene.name",
		                "Gene.pathways.identifier",
		                "Gene.pathways.name",
		                "Gene.pathways.organism.name",
		                "Gene.pathways.label1",
		                "Gene.pathways.label2"
		                );

        // Add orderby
        query.addOrderBy("Gene.primaryIdentifier", OrderDirection.ASC);

        // Filter the results with the following constraints:
        
        String lookup = Arrays.toString(searchData);
        //lookup = lookup.replace(",", " ");
        lookup = lookup.replace("[", "");
        lookup = lookup.replace("]", "");
        System.out.println(">" + lookup);
        query.addConstraint(Constraints.lookup("Gene", lookup, null));
        
        QueryService service = factory.getQueryService();
        
        Iterator<List<Object>> rows = service.getRowListIterator(query);
        ArrayList<Object[]> results = new ArrayList<Object[]>();
        
        System.out.println("TargetMineSearchInterface: Query complete! " + service.getCount(query));
        while (rows.hasNext()) {
        	Object[] row = rows.next().toArray();
        	for(int i = 0; i < row.length; i++) {
        		if(row[i] == org.json.JSONObject.NULL) {
        			row[i] = (Object)"NULL";
        		}
        	}
        	results.add(row);
        	
        	System.out.println("Result - " + Arrays.toString(row));
        }
        System.out.println("TargetMineSearchInterface: Returning results.");
		return results;
	}

	@Override
	public int getWorkSize() {
		return 20; //20
	}

	@Override
	public int getThreadPoolSize() {
		return 5;
	}

	
	@Override
	public int[] getPrimaryKeys() {
		return new int[] {};
	}

}
