package Fif_Persistence.Rebuilder;


import java.io.InputStream;
import java.util.ArrayList;

import Fif_Persistence.Factory.RebuilderFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import fif_core.Filter;


/**
 * This class allows to reconstruct Fuzzy Filters from RDF files
 * <p>
 * @author Ilaria Mastronardi
 * @version 1.0
 * @see Filter
 *
 */

public class FilterRebuilderFromRDF {
	/**
	 * Public Constructor that create an object of FilterRebuilderFromRDF class
	 */
	
	public FilterRebuilderFromRDF(){
		
	}
	
	/**
     * Reads a Rdf file to rebuild the filter represented in it
     * The File which will be read and the language of this have to be istantiated (not null).
     * <p>
     * @param fileRdf 
     *            The File RDF representing the fuzzy filter to reconstruct
     * @param typeLanguage
     *		the language of the Rdf file
     * @return The filter reconstructed from the Rdf file 
	
   
     */
    public Filter readRdfFile(InputStream fileRdf,String typeLanguage) {
    	
    	assert (fileRdf!=null) : "il file deve essere stato valorizzato";
    	assert (typeLanguage!=null) :"il linguaggio del file RDF deve essere valorizzato";
    	
    	Model model = ModelFactory.createDefaultModel();
    	
		model.read(fileRdf, null,typeLanguage);
		
		//IDentifico il nodo risorsa  che rappresenta il filtro
		Resource resourceFilter=identifyResource(model);
		
		// inizio la ricorsione passando il modello e la risorsa che identifica il filtro
    	Filter filter=rebuildRecursion(model,resourceFilter);
    
    	return filter;
		
    }
    
    
    /**
     * Identifies the Resource that represent the filter in the Rdf file
     * The resource refers to the Resource node of RDF triples and absolutely does not refer 
     * to the concept of resource introduced in the fif_core package
     * <p>
     * 
     * @param model
     *    The model which will hold all the relations between the filters
     * @return The Resource that represent the filter in the file
     */
    static Resource identifyResource(Model model) {
    	
    	assert (model!=null) : "il modello deve essere stato valorizzato";
    	
    	Resource resourceFilter = null;
    	
    	// ottengo la lista di TUTTI i soggetti nel grafo
    	ResIterator it = model.listSubjects();
    	
    	while (it.hasNext()) { // per ogni soggetto
    		
    		Resource res = it.next();
    		String localName = res.getLocalName(); // ottiene la stringa corrispondente al nome della risorsa, senza prefisso
    		
    		// se non e' un nodo anonimo
    		if (!(res.isAnon())) {
    			// se la risorsa descrive un DescBasedFilter e non vi sono Risorse che descrivono rispettivamente
    			//Parallel o Sequence filter allora la risorsa che sto cercando è questa
    			if (localName.contains("DescriptionBasedFilter")&&!(checkParallelFilter(model))&&!(checkSequenceFilter(model))){
    				
    				resourceFilter=res; break;
    				
    			}else if (localName.contains("ParallelFilter")|| localName.contains("SequenceFilter")){
    				
    				resourceFilter=checkResourceBase(model);break;
    			}
    		
    		}
    		
    	}
    	
		return resourceFilter;
    	
    }
    
    /**
     * checks if in the file exists a resource that represents a sequence filter
     * 
     * The resource refers to the Resource node of RDF triples and absolutely does not refer 
     * to the concept of resource introduced in the fif_core package
     * 
     * @param model
     * 		 The model which will hold all the relations between the filters
     * @return true if the resource exists, false otherwise
     */
    private static  boolean checkSequenceFilter(Model model) {
    
    	assert (model!=null) : "il modello deve essere stato valorizzato";
	
    	// ottengo la lista di TUTTI i soggetti nel grafo
    	ResIterator it = model.listSubjects();
    	
    	while (it.hasNext()) { // per ogni soggetto
    		
    		Resource res = it.next();
    		String localName = res.getLocalName();
    		
    		// se non e' un nodo anonimo
    		if (!(res.isAnon())) {
    			if (localName.contains("SequenceFilter")) return true;
    		}
    	}
    	
		return false;
	}

	/**
     * checks if in the file exists a resource that represents a parallel filter
     * 
     * The resource refers to the Resource node of RDF triples and absolutely does not refer 
     * to the concept of resource introduced in the fif_core package
     * 
     * @param model
     * 		 The model which will hold all the relations between the filters
     * @return true if the resource exists, false otherwise
     */
    private static boolean checkParallelFilter(Model model) {
    	
    	assert (model!=null) : "il modello deve essere stato valorizzato";
    	
    	// ottengo la lista di TUTTI i soggetti nel grafo
    	ResIterator it = model.listSubjects();
    	
    	while (it.hasNext()) { // per ogni soggetto
    		
    		Resource res = it.next();
    		String localName = res.getLocalName();
    		
    		// se non e' un nodo anonimo
    		if (!(res.isAnon())) {
    			if (localName.contains("ParallelFilter")) return true;
    		}
    	}
    	
		return false;
	}
    
    
    /**
     * checks all the resources that can describe a filter in the file and returns the Resource that represents
     * the filter base necessary for reconstruction 
     * 
     * This method is called if the input file does not describe a DescriptionBasedFilter
     * 
     * @param model
     * 		 The model which will hold all the relations between the filters
     * @return The resource that describe the filter base
     */
    private static Resource checkResourceBase(Model model) {
    	
    	assert (model!=null) : "il modello deve essere stato valorizzato";
    	
    	
    	// qui verranno memorizzate tutti i local name delle risorse che possono descrivere un filtro
    	ArrayList<Resource> allres=new ArrayList<Resource>();
    	
    	// ottengo la lista di TUTTI i soggetti nel grafo
    	ResIterator it = model.listSubjects();
    	
    	while (it.hasNext()) { // per ogni soggetto
    		
    		Resource resource = it.next();
    		String localName = resource.getLocalName();
    		
    		// se non e' un nodo anonimo
    		if (!(resource.isAnon())) {
    			
    			if (localName.contains("ParallelFilter")|| localName.contains("SequenceFilter")){
    				allres.add(resource);
    			}
    		}
    	}
    	
    	/* MIO TEST
    	for (Resource r:allres){
    		System.out.println(r.toString());
    	}
    	
    	*/
    
    	
    	int posMin=0;
    	// cerco la risorsa con l'identificatore più piccolo;
    	// nel package fif-representation(Versione estesa) gli identificatore che affiancano i nomi delle risorse
    	// sono in ordine crescente;
    	// esempi di nomi di risorsa sono: SequenceFilter_1, ParallelFilter_23 e cosi via ,L'identificatore della risorsa
    	//si trova dalla posizione 15 in poi.
    	for (int i=1; i<=allres.size()-1;i++){
    		
    		int id=Integer.parseInt(allres.get(i).getLocalName().substring(15));
    		
    		if (id<Integer.parseInt(allres.get(posMin).getLocalName().substring(15))){
    			
    			posMin=i;
    		}
    	}
    
    	//System.out.println("MIN: "+allres.get(posMin).getLocalName()+"");
    	return allres.get(posMin);
	}
    
    

	// CHAIN OF RESPONSIBILITY
    /**
     * This method contains the reading logic of a RDF file.
     * 
     * @param model
     *            The model that stores all the relations beetween Resources
     * @param resourceFilter
     *            The Resource that represent the filter in the file
     * @return The filter reconstructed
     
   
     */
   static Filter rebuildRecursion(Model model, Resource resourceFilter){
    	
    	assert (model!=null) : "il modello deve essere stato valorizzato";
    	assert (resourceFilter!=null): "La risorsa non può essere null";
    	
    	String localName=resourceFilter.getLocalName();
    	Filter filter=null;
    	
    	if (localName.contains("DescriptionBasedFilter")){
    		
    		filter=RebuilderFactory.getDescrBasedFilterRebuild().rebuildFilter(model, resourceFilter);
    		
    		
    	}else if (localName.contains("ParallelFilter")){
    		
    		filter=RebuilderFactory.getParallelFilterRebuild().rebuildFilter(model, resourceFilter);
    		
    	}else if (localName.contains("SequenceFilter")){
    		
    		filter=RebuilderFactory.getSequenceFilterRebuild().rebuildFilter(model, resourceFilter);
    		
    	}
    	
		return filter;
    	
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
