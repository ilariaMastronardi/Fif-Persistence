package Fif_Persistence.Rebuilder;

import java.util.ArrayList;

import Fif_Persistence.Factory.RebuilderRecursion;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import fif_core.Filter;
import fif_core.SequenceFilter;

//STRATEGY PATTERN

/**
* This class implements the RebuilderRecursion Interface and provides an
* implementation of a recursive reconstruction of a Sequence Filter.
* 
* @author Ilaria Mastronardi
* @version 1.0
* @see RebuilderRecursion
*
*/
public class SequenceFilterRebuild implements RebuilderRecursion {

	@Override
	public Filter rebuildFilter(Model model, Resource resourceFilter) {
		
		
		assert model != null : "Il grafo delle relazioni non può essere null.";
		assert resourceFilter!=null:" la risorsa che identifica il filtro non può essere null";
		
		Filter[] filter = null ;// array che memorizzi i filtri di un sequencelFilter
		
		
		// creo l'iteratore Seq per recuperare tutti i filtri di cui è composto il filtro sequenziale
		
		NodeIterator iterSeq = model.getSeq(resourceFilter).iterator();
		    
		if (iterSeq.hasNext()){ 
		    	
			ArrayList<Filter> f=new ArrayList<Filter>();
					
			while (iterSeq.hasNext()) {
						
				Resource filterResource= iterSeq.next().asResource(); 
				f.add(FilterRebuilderFromRDF.rebuildRecursion(model, filterResource));
			}
					
			filter=new Filter[f.size()];
			for (int i=0;i<f.size();i++){
				filter[i]=f.get(i);
			}
					
		}
		
		return new SequenceFilter(filter);
	}

}
