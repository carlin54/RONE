package rone.backend;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.intermine.metadata.Model;
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
		return null;
	}

	@Override
	public ArrayList<Object[]> query(String[] searchData) {
		final String ROOT = "https://targetmine.mizuguchilab.org/targetmine/service";
    	
    	ServiceFactory factory = new ServiceFactory(ROOT);
    	Model model = factory.getModel();
        
        PathQuery query = new PathQuery(model);
        
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
        ArrayList<Object[]> results = new ArrayList<Object[]>();
        
        while (rows.hasNext()) {
        	Object[] row = rows.next().toArray();
        	results.add(row);
        	
        }
		return results;
	}

	@Override
	public int getWorkSize() {
		return 20;
	}

	@Override
	public int getThreadPoolSize() {
		return 5;
	}

}
