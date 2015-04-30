package Fif_Persistence.Rebuilder;

import java.util.ArrayList;

import Fif_Persistence.Factory.RebuilderRecursion;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fif_core.Filter;
import fif_core.OWA;
import fif_core.ParallelFilter;
import fif_representation.vocabulary.OWA_AGGREGATOR;
import fif_representation.vocabulary.PARALLEL_FILTER;

// STRATEGY PATTERN

/**
 * This class implements the RebuilderRecursion Interface and provides an
 * implementation of a recursive reconstruction of a Parallel Filter.
 * 
 * @author Ilaria Mastronardi
 * @version 1.0
 * @see RebuilderRecursion
 *
 */
public class ParallelFilterRebuild implements RebuilderRecursion{

	@Override
	public Filter rebuildFilter(Model model, Resource resourceFilter) {
		
		assert model != null : "Il grafo delle relazioni non può essere null.";
		assert resourceFilter!=null:" la risorsa che identifica il filtro non può essere null";
		
		Resource owaResource=null; // risorsa che identifica l'aggregatore del filtro
		double[] weight; // array per i pesi dell'aggregatore;
		Filter[] filter = null ;// array che memorizzi i filtri di un parallelFilter
		OWA owa = null;// aggregatore per il filtro
		
		// Iteratore che filtra gli statements.
		// Seleziono solo quelli che hanno la risorsa ricevuta in input(filtro) e la proprietà : PARALLEL_FILTER.hasAggregator
		
		StmtIterator iterStmts = model
				.listStatements(new SimpleSelector(resourceFilter,PARALLEL_FILTER.hasAggregator,
					(RDFNode) null));
		
		// individuo il nodo RDF che identifica L'Aggregatore del filtro
		RDFNode owaNode=iterStmts.next().getObject();
		
		// trasformo il nodo RDF che identifica l'aggregatore in una risorsa
		owaResource=owaNode.asResource();
		
        //iteratore che filtra gli statements aventi come risorsa la risorsa del aggregatore e il predicato OWA_AGGREGATOR.HasValue
		iterStmts = model
				.listStatements(new SimpleSelector(owaResource,OWA_AGGREGATOR.hasValues,
					(RDFNode) null));
		
		if (iterStmts.hasNext()){
			
		
			Statement state=iterStmts.next();
			RDFNode object = state.getObject();
			
			Resource resAnon=object.asResource();// memorizzo la risorsa anonima che rappresenta la sequenza dei pesi dell'aggregatori
			
			// iteratore Seq per la risorsa anonima che contiene i pesi dell'aggregatore del filtro
			NodeIterator iterSeq = model.getSeq(resAnon).iterator();
			
			ArrayList<Double> w=new ArrayList<Double>();
			
			while (iterSeq.hasNext()) {
				
				RDFNode nodeSeq = iterSeq.next();
				//System.out.println(nodeSeq.asLiteral().getDouble());
				w.add(nodeSeq.asLiteral().getDouble());
				
				
			}
			
			// memorizzo i pesi in un array
			weight=new double[w.size()];
			for (int i=0;i<w.size();i++){
				weight[i]=w.get(i);
			}
			
			//CREO l'aggregatore per il filtro
			owa=new OWA(weight);
					
		}
		
		
		// creo l'iteratore Bag per recuperare tutti i filtri di cui è composto il filtro parallelo
		
		NodeIterator iterBag = model.getBag(resourceFilter).iterator();
    
		if (iterBag.hasNext()){ 
    	
			ArrayList<Filter> f=new ArrayList<Filter>();
			
			while (iterBag.hasNext()) {
				
				Resource filterResource= iterBag.next().asResource(); 
				f.add(FilterRebuilderFromRDF.rebuildRecursion(model, filterResource));
			}
			
			filter=new Filter[f.size()];
			for (int i=0;i<f.size();i++){
				filter[i]=f.get(i);
			}
			
		}
		
		return new ParallelFilter(owa,filter);
	}

}
