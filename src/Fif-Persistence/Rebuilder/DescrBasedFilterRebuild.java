package Fif_Persistence.Rebuilder;


import Fif_Persistence.Factory.RebuilderRecursion;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fif_core.Attribute;
import fif_core.ClosedVeristicInterpretation;
import fif_core.DescriptionBasedFilter;
import fif_core.Filter;
import fif_core.FuzzySet;
import fif_core.Metadata;
import fif_core.OpenVeristicInterpretation;
import fif_core.PossibilisticInterpretation;
import fif_core.interfaces.Interpretation;
import fif_representation.vocabulary.DESCRIPTION_BASED_FILTER;
import fif_representation.vocabulary.FUZZY_SET_ITEM;
import fif_representation.vocabulary.METADATA;

//STRATEGY PATTERN

/**
 * This class implements the RebuilderRecursion Interface and provides an
 * implementation of a recursive reconstruction of a Description Based Filter.
 * 
 * @author Ilaria Mastronardi
 * @version 1.0
 * @see RebuilderRecursion
 *
 */
public class DescrBasedFilterRebuild implements RebuilderRecursion{

	@SuppressWarnings("static-access")
	@Override
	public Filter rebuildFilter(Model model, Resource resourceFilter) {
		
		assert model != null : "Il grafo delle relazioni non può essere null.";
		assert resourceFilter!=null:" la risorsa che identifica il filtro non può essere null";
		
		Resource metaResource=null; // risorsa che identifica il metadata del DescrBasedFilter
		Resource fuzzyResource=null; // risorsa che identifica il fuzzySet del DescrBasedFilter
		
		//variabili per la costruzione del DescrBasedFilter
		
		Attribute attr=null;
		FuzzySet fuzzy=null;
		Interpretation interpr=null;
		
		// Iteratore che filtra gli statements.
		// Seleziono solo quelli che hanno la risorsa ricevuta in input(filtro) e la proprietà : DESCRIPTION_BASED_FILTER.hasDescription
		StmtIterator iterStmts = model
						.listStatements(new SimpleSelector(resourceFilter,DESCRIPTION_BASED_FILTER.hasDescription,
							(RDFNode) null));
		
			
		// individuo il nodo RDF che identifica il metadato del filtro
		RDFNode metaNode=iterStmts.next().getObject();
		// trasformo il nodo RDF che identifica il metadato in una risorsa
			
		metaResource=metaNode.asResource();

		// iteratore che filtra gli statements aventi come risorsa la risorsa del metadato
		
		iterStmts = model
				.listStatements(new SimpleSelector(metaResource,null,
					(RDFNode) null));
		
			while(iterStmts.hasNext()){
				
				Statement state=iterStmts.next();
				
				Property pred=state.getPredicate(); // prelevo il predicato della tripla RDF
				RDFNode object =state.getObject(); // prelevo l'oggetto della tripla RDF
				
				if (pred.equals(METADATA.hasAttribute)) {
					
					String attribute=object.asResource().getLocalName();
					attr=new Attribute(attribute);
					
				}else if (pred.equals(METADATA.hasInterpretation)) {
					
					String interpretation=object.asLiteral().getString();
				
					switch(interpretation){
						
					case "ClosedVeristicInterpretation" : interpr= new ClosedVeristicInterpretation().getinstance();break;
					case "PossibilisticInterpretation": interpr=new PossibilisticInterpretation().getinstance();break;
					case "OpenVeristicInterpretation": interpr=new OpenVeristicInterpretation().getinstance();break;
					
					}
				
				} else if (pred.equals(METADATA.hasFuzzySet)){
					
					fuzzyResource=object.asResource();
					
					//verifico che ci siano delle triple RDF associate al Fuzzy Set nel grafo 
					StmtIterator fuzzyStmts = model
							.listStatements(new SimpleSelector(fuzzyResource,null,(RDFNode) null));
					if (fuzzyStmts.hasNext()){
					
						// creo un fuzzy set vuoto
						fuzzy=new FuzzySet();
					
						// creo l'iteratore associato al soggetto della relazione Bag del fuzzySet
					
						NodeIterator iterBag = model.getBag(fuzzyResource).iterator();
				    
						if (iterBag.hasNext()){ // se ci sono elementi nel fuzzy set 
				    	
							while (iterBag.hasNext()) {

								String elem="";
								Double memberShipValue=0.0; 
			    			
								// final perche' utilizzata in una classe anonima
								final RDFNode nodeSeq = iterBag.next();

								// Iteratore specifico per i fuzzySet
								// items
								StmtIterator fuzzyItemsIter = model.listStatements(new SimpleSelector(nodeSeq.asResource(),null, (RDFNode) null));
				    		
								while (fuzzyItemsIter.hasNext()){ // per ogni tripla RDF avente come oggetto un elemento del fuzzySet
				    			
				    			
										Statement nextFsItem = fuzzyItemsIter.next();
										Property propItem = nextFsItem.getPredicate();

										RDFNode objectItem = nextFsItem.getObject();
								    
										if (propItem.equals(FUZZY_SET_ITEM.hasElement)){
								    	
									    	elem=objectItem.asLiteral().getString();
									    	
										}else if ( propItem.equals(FUZZY_SET_ITEM.hasMembershipValue)){
								    	
											memberShipValue=objectItem.asLiteral().getDouble();
										}
								      
								}
				    			
								fuzzy.setValue(elem, memberShipValue);	
				    			
							}//chiusura while
						}//chiusura if	
					}
				}// chiusura else
				  
			}// chiusura while
	// costruisco il filtro base
	 Metadata meta =new Metadata(attr,fuzzy,interpr);
	 Filter filterToReturn=new DescriptionBasedFilter(meta);
	 
	return filterToReturn;
	}// chiusura metodo
	
	

}
