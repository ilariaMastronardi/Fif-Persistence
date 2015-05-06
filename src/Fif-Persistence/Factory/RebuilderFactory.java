package Fif_Persistence.Factory;

import Fif_Persistence.Rebuilder.DescrBasedFilterRebuild;
import Fif_Persistence.Rebuilder.ParallelFilterRebuild;
import Fif_Persistence.Rebuilder.SequenceFilterRebuild;


// SINGLETON
public class RebuilderFactory {

	
	  private RebuilderFactory() {
	    }

	   
	    /**
	     * Variable that holds the recursive implementation of a rebuilder  of a Parallel Filter .
	     */
	    private static RebuilderRecursion parallelFilter = new ParallelFilterRebuild();
	   
	    /**
	     * Variable that holds the recursive implementation of a rebuilder  of a Sequence Filter .
	     */
	    private static RebuilderRecursion sequenceFilter = new SequenceFilterRebuild();
	    /**
	     * Variable that holds the recursive implementation of a rebuilder  of a Description Base Filter .
	     */
	    private static RebuilderRecursion descrBasedFilter = new DescrBasedFilterRebuild();

	    /**
	     * Returns an object that exhibits the RebuilderRecursion interface but holds
	     * the recursive implementation of a Parallel Filter's reconstruction .
	     * 
	     * @return An object that contains an implementation of a Parallel Filter's
	     *         reconstruction.
	     */
	    public static RebuilderRecursion getParallelFilterRebuild() {
	   
		return parallelFilter;
	    }

	    /**
	     * Returns an object that exhibits the RebuilderRecursion interface but holds
	     * the recursive implementation of a Sequence Filter's reconstruction.
	     * 
	     * @return An object that contains an implementation of a Sequence Filter's
	     *     reconstruction.
	     */
	    public static RebuilderRecursion getSequenceFilterRebuild() {
		return sequenceFilter;
	    }

	    /**
	     * Returns an object that exhibits the RebuilderRecursion interface but holds
	     * the recursive implementation of a Description Based Filter's reconstruction.
	     * 
	     * @return An object that contains an implementation of a Description Based
	     *         Filter's reconstruction.
	     */
	    public static RebuilderRecursion getDescrBasedFilterRebuild() {
		return descrBasedFilter;
	    }
}
