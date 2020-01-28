package jp.sbi.toxicologygadget.targetmine;


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
    
    
    
    public static void query(String geneList) throws IOException {
        ServiceFactory factory = new ServiceFactory(ROOT);
        Model model = factory.getModel();
        PathQuery query = new PathQuery(model);

        // Select the output columns:
        query.addViews(	"Gene.primaryIdentifier",
                		"Gene.symbol",
                		"Gene.name",
                		"Gene.organism.name");

        // Add orderby
        query.addOrderBy("Gene.secondaryIdentifier", OrderDirection.ASC);

        // Filter the results with the following constraints:
        query.addConstraint(Constraints.lookup("Gene", geneList, null));
        

        QueryService service = factory.getQueryService();
        PrintStream out = System.out;
        String format = "%-13.13s | %-13.13s | %-13.13s | %-13.13s \n";
        out.printf(format, query.getView().toArray());
        Iterator<List<Object>> rows = service.getRowListIterator(query);
        while (rows.hasNext()) {
            out.printf(format, rows.next().toArray());
        }
        out.printf("%d rows\n", service.getCount(query));
    }

}