package Fif_Persistence.Factory;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import fif_core.Filter;


/**
 * Interface that defines an object which can reconstruct a Filter
 * 
 * @author Ilaria Mastronardi
 * @version 1.0
 */
public interface RebuilderRecursion {
	 /**
     * Defines a way to rebuild a Filter. The input variables cannot be null.
     * 
     * @param model
     *            The model which hold all the relations beetween the
     *            filters
     * @param resouceFilter
     *            The resource that represents filter
     * @return An instance of the Filter class of the fif core package
     
     */
    Filter rebuildFilter(Model model, Resource resourceFilter) ;

}
