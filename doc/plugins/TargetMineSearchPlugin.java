package rone.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class TargetMineSearchPlugin extends Plugin {

	public TargetMineSearchPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	@Extension(ordinal=1)
	public static class TargetMineSearchExtension extends SearchExtension {

		@Override
		public JMenu getMenu() {
			JMenu menuIndex = new JMenu("TargetMine");
			JMenuItem geneSearch = new JMenuItem("with Gene Symbols");
			geneSearch.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Selection selection = getCallback().getSelection();
					
					if(!selection.isEmpty()) {
						Object[] selectionArray = selection.toObjectArray();
						
						Search search = new TargetMineSearch();
						search.setThreadPoolSize(1);
						search.setSearchSize(1);
						search.setUniqueSearchRequests(selectionArray);
						
						getCallback().startSearch(search);
					} 
				}
				
			});
			menuIndex.add(geneSearch);
			return menuIndex; 
		}

	}
	
	public static class TargetMineSearch extends Search {
		
		public final static String TITLE = new String("TargetMine");
		
		public final static String[] COLUMNS = new String[]
		{
				"Gene Primary Identifier",
	            "Gene Symbol",
	            "Name",
	            "Pathways Identifier",
	            "Pathways Name",
	            "Organism Name",
	            "Gene Pathways Label 1",
	            "Gene Pathways Label 2"
		};
		
		public TargetMineSearch() {
			super(TITLE, COLUMNS);
			this.setSearchSize(1);
			this.setThreadPoolSize(5);
		}
		
		@Override
		public ArrayList<Object[]> getSearchResults(Object[] searchRequests) {

	        String[] searchData = new String[searchRequests.length];
	        System.arraycopy(searchRequests, 0, searchData, 0, searchRequests.length);
	        
			
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

	        // Add order by
	        query.addOrderBy("Gene.primaryIdentifier", OrderDirection.ASC);

	        // Filter the results with the following constraints:
	        String lookup = Arrays.toString(searchData);
	        lookup = lookup.replace("[", "");
	        lookup = lookup.replace("]", "");
	        query.addConstraint(Constraints.lookup("Gene", lookup, null));
	        
	        QueryService service = factory.getQueryService();
	        
	        Iterator<List<Object>> rows = service.getRowListIterator(query);
	        ArrayList<Object[]> results = new ArrayList<Object[]>();
	        
	        while (rows.hasNext()) {
	        	Object[] row = rows.next().toArray();
	        	for(int i = 0; i < row.length; i++) {
	        		if(row[i] == org.json.JSONObject.NULL) {
	        			row[i] = (Object)"NULL";
	        		}
	        	}
	        	results.add(row);
	        }
	        return results;
		}
		
	}
	
}
