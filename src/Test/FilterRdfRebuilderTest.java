package Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import junit.framework.TestCase;
import Fif_Persistence.Rebuilder.FilterRebuilderFromRDF;
import fif_core.Attribute;
import fif_core.ClosedVeristicInterpretation;
import fif_core.DescriptionBasedFilter;
import fif_core.Filter;
import fif_core.FuzzySet;
import fif_core.Metadata;
import fif_core.OWA;
import fif_core.OpenVeristicInterpretation;
import fif_core.ParallelFilter;
import fif_core.PossibilisticInterpretation;
import fif_core.SequenceFilter;
import fif_core.interfaces.Aggregator;
import fif_representation.utility.CONSTANTS;
import fif_representation.writer.FilterRdfWriter;
import fif_representation.writer.RDFType;

public class FilterRdfRebuilderTest extends TestCase {
	
	private FilterRdfWriter testWriter;
	private FilterRebuilderFromRDF testRebuilder;
	private Filter[] filters;
	private String typeLanguage="";
	
	
	 // il metodo setUp viene eseguito prima di tutti gli altri
    protected void setUp() throws Exception {
    	
	testWriter =new FilterRdfWriter();
	testRebuilder =new FilterRebuilderFromRDF();
	filters=new Filter[10];
	typeLanguage="TURTLE";
	setPrefixes(testWriter);
	
	// inizializzo i filtri
	filters[0]=createFirstFilter();
	filters[1]=createSecondFilter();
	filters[2]=createThirdFilter();
	filters[3]=createFourthFilter();
	filters[4]=createFifthFilter();
	filters[5]=createSixthFilter();
	filters[6]=createSeventhFilter();
	filters[7]=createEightFilter();
	filters[8]=createNinthFilter();
	filters[9]=createTenthFilter();
	super.setUp();
    }
    
  

	// il metodo tear down viene eseguito immediatamente dopo i metodi di test
    protected void tearDown() throws Exception {
	testWriter = null;
	testRebuilder = null;
	filters= null;
	typeLanguage=null;
	super.tearDown();
    }
	
    
    
    
	public void testRebuilder() throws FileNotFoundException, URISyntaxException  {
		
	
	   // qui va il ciclo
		for (int i=0;i<=filters.length-1;i++){
			
			// converto il filtro in un documento RDF tramite il package fif_Representation
			testWriter.writeRdf(filters[i], new PrintWriter("src/file_rdf/testTURTLE_Filtro"+ (i + 1) + ".txt"),RDFType.TURTLE);
			
			// leggo il file appena creato
			InputStream inputFile=new FileInputStream("src/file_rdf/testTURTLE_Filtro"+ (i + 1) + ".txt");
			
			Filter filterAfter=testRebuilder.readRdfFile(inputFile,"TURTLE");
		
			//System.out.println(filters[i].equals(filterAfter));
			assertTrue(filters[i].equals(filterAfter));
			
		}
		

	}
	
	// DESCRIPTION BASED FILTER, 1 ATTRIBUTO,1 FUZZY SET>1
	private Filter createFirstFilter(){
		
		// creazione l'attributo
		Attribute att1 = new Attribute("anno");
		
		// creazione fuzzy set
		FuzzySet fs1 = new FuzzySet();
		fs1.setValue("horror", 0.7);
		fs1.setValue("thriller", 1.0);
		fs1.setValue("drammatico", 0.2);

		// creazione metadato
		Metadata m = new Metadata(att1, fs1,
			OpenVeristicInterpretation.getinstance());

		// mi creo un Description Based Filter
		Filter filtro = new DescriptionBasedFilter(m);
		
		return filtro;
		
	}
	
	//DESCRIPTION BASED FILTER, 1 ATTRIBUTO,1 FUZZY SET VUOTO
	private Filter createSecondFilter() {
		
		// creazione l'attributo
				Attribute att1 = new Attribute("anno");
				
				// creazione fuzzy set
				FuzzySet fs1 = new FuzzySet();
				
				// creazione metadato
				Metadata m = new Metadata(att1, fs1,
					ClosedVeristicInterpretation.getinstance());

				// mi creo un Description Based Filter
				Filter filtro = new DescriptionBasedFilter(m);
				
				return filtro;
	}
	
	
	
	// PARALLEL FILTER COMPOSTO DA 3 DESCRIPTION BASED FILTER DI CUI 1 CON FUZZY SET VUOTO E 2 CON FUZZY SET =1 ELEMENTO
	//UN AGGREGATORE CON 3 PESI
	// TRE METADATA CON STESSA INTERPRETAZIONE
	private Filter createThirdFilter() {
			
		Attribute att1 = new Attribute("genere");
		Attribute att2 = new Attribute("anno");
		Attribute att3 = new Attribute("attori");

		FuzzySet fs1 = new FuzzySet();
		FuzzySet fs2 = new FuzzySet();
		FuzzySet fs3= new FuzzySet();
		
		fs2.setValue("2015", 1);
		fs3.setValue("nina dobrev", 0.8);
		
		// creazione metadati
		Metadata m = new Metadata(att1, fs1,
			ClosedVeristicInterpretation.getinstance());
		Metadata m2 = new Metadata(att2, fs2,
			ClosedVeristicInterpretation.getinstance());
		Metadata m3 = new Metadata(att3, fs3,
				ClosedVeristicInterpretation.getinstance());
		
		// creo I Description Based Filter
		Filter f1 = new DescriptionBasedFilter(m);
		Filter f2 = new DescriptionBasedFilter(m2);
		Filter f3 = new DescriptionBasedFilter(m3);
		
		Aggregator owa = new OWA(0.5,0.4,0.7);

		// creazione PARALLEL filter
		Filter s1 = new ParallelFilter(owa, f1,f2,f3);

		return s1;
		
	
	}
	
	// 1 PARALLEL FILTER COMPOSTO DA 2 FILTRI PARALLELI CON DUE AGGREGATORI DIFFERENTI;
	// I DUE FILTRI PARALLELI SONO COMPOSTI DA 1 DESCRBASEDFILTER DI CUI UNO CON FUZZY SET >1 E 1 CON FUZZY SET =1 ELEMENTO;
	// 2 METADATA CON 2 INTERPRETAZIONI DIFFERENTI
	private Filter createFourthFilter() {
				
		Attribute att1 = new Attribute("genere");
		Attribute att2 = new Attribute("anno");

		FuzzySet fs1 = new FuzzySet();
		fs1.setValue("horror", 0.9);

		FuzzySet fs2 = new FuzzySet();
		// setto dei valori associati al secondo fuzzy set
		fs2.setValue("1970", 0.2);
		fs2.setValue("1971", 0.7);
		fs2.setValue("1975", 0.3);
		

		// creazione metadati
		Metadata m = new Metadata(att1, fs1,
			ClosedVeristicInterpretation.getinstance());
		Metadata m2 = new Metadata(att2, fs2,
			OpenVeristicInterpretation.getinstance());

		// Creo i Description Based Filter
		Filter f1 = new DescriptionBasedFilter(m);
		Filter f2 = new DescriptionBasedFilter(m2);

		Aggregator owa1 = new OWA(1.0);
		Aggregator owa2=new OWA(0.9);

		Aggregator owa3 = new OWA(0.5, 0.3);

		// creazione parallel filter
		Filter s1 = new ParallelFilter(owa1, f1);
		Filter s2 = new ParallelFilter(owa2, f2);

		Filter s3 = new ParallelFilter(owa3, s1, s2);

		return s3;
		
	}
	

	// UN PARALLEL FILTER COMPOSTO DA UN FILTRO SEQUENZIALE
	//2 METADATA,2 FUZZY SET >1,1 SEQUENCE FILTER CON DUE DESCRIPTION BASED FILTER
	private Filter createFifthFilter() {
		
		Attribute att1 = new Attribute("titolo");
		Attribute att2 = new Attribute("attori");

		FuzzySet fs1 = new FuzzySet();
		fs1.setValue("the vampire diares", 0.9);
		fs1.setValue("the originals", 0.7);

		FuzzySet fs2 = new FuzzySet();
		fs2.setValue("paul wesley", 0.2);
		fs2.setValue("katherine paul", 0.7);
		fs2.setValue("ian somerhlander",1);
		

		// creazione metadati
		Metadata m = new Metadata(att1, fs1,
			ClosedVeristicInterpretation.getinstance());
		Metadata m2 = new Metadata(att2, fs2,
			PossibilisticInterpretation.getinstance());

		// Creo i Description Based Filter
		Filter f1 = new DescriptionBasedFilter(m);
		Filter f2 = new DescriptionBasedFilter(m2);
		
		SequenceFilter f3=new SequenceFilter(f1,f2);
		
		Aggregator owa=new OWA(0.4);
		
		ParallelFilter toReturn=new ParallelFilter(owa,f3);
	
		return toReturn;
	}
	
	
	// 1 PARALLEL FILTER COMPOSTO DA UN DESCRIPTION BASED FILTER,UN SEQUENCE FILTER E UN PARALLEL FILTER
	// IL SEQUENCE FILTER è COMPOSTO DA 1 DESCRIPTIONBASEDFILTER CREATO TRAMITE IL METODO : createFirstFilter();
	//IL PARALLEL FILTER è CREATO TRAMITE IL METODO : createThirdFilter();
	// IL DESCRIPTIONBASEDFILTER è CREATO TRAMITE IL METODO:  createSecondFilter();
	
	private Filter createSixthFilter(){
		
		//filtro sequenziale
		SequenceFilter f1=new SequenceFilter(createFirstFilter());
		//filtro parallelo
		Filter f2=createThirdFilter();
		//descriptionBasedFilter
		Filter f3=createSecondFilter();
		
		Aggregator owa=new OWA(0.2,0.7,0.3);
		Filter toReturn=new ParallelFilter(owa,f1,f2,f3);
		
		return toReturn;
		
	}
	
	// 1 SEQUENCEFILTER COMPOSTO DA 2 PARALLELFILTER AVENTI LO STESSO AGGREGATORE
	// 2 PARALLEL FILTER CON UN DESCRIPTIONBASEDFILTER
	// 2 DESCRIPTIONBASEDFILTER CON FUZZY SET VUOTO 
	//2 METADATA CON STESSA INTERPRETAZIONE
	private Filter createSeventhFilter(){
		
		Attribute att1 = new Attribute("edizione");
		Attribute att2 = new Attribute("autori");

		FuzzySet fs1 = new FuzzySet();
		FuzzySet fs2 = new FuzzySet();
		
	
		// creazione metadati
		Metadata m = new Metadata(att1, fs1,
			PossibilisticInterpretation.getinstance());
		Metadata m2 = new Metadata(att2, fs2,
			PossibilisticInterpretation.getinstance());

		// Creo i Description Based Filter
		Filter f1 = new DescriptionBasedFilter(m);
		Filter f2 = new DescriptionBasedFilter(m2);
		
		Aggregator owa=new OWA(0.4);
		
		ParallelFilter f3=new ParallelFilter(owa,f1);
		ParallelFilter f4=new ParallelFilter(owa,f2);
	
		
		SequenceFilter toReturn=new SequenceFilter(f3,f4);
			
		return toReturn;
			
	}
	
	//1 SEQUENCEFILTER COMPOSTO DA TRE DESCRIPTIONBASEDFILTER
	//1 METADATA CON FUZZY SET VUOTO
	//1 METADATA CON FUZZY SET =1 ELEMENTO
	// 1 METADATA CON FUZZY SET >1
	// 2 METADATA CON STESSA INTERPRETAZIONE E 1 METADATA CON INTERPRETAZIONE DIVERSA  
	private Filter createEightFilter(){
		
		Attribute att1 = new Attribute("sapore");
		Attribute att2 = new Attribute("categoriaCibo");
		Attribute att3 = new Attribute("produzione");

		FuzzySet fs1 = new FuzzySet();
		FuzzySet fs2 = new FuzzySet();
		FuzzySet fs3= new FuzzySet();
		
		fs1.setValue("agrodolce", 1);
		fs1.setValue("salato", 0.5);
		fs1.setValue("dolce", 0.5);
		
		fs2.setValue("hamburger", 0.8);
		
		// creazione metadati
		Metadata m = new Metadata(att1, fs1,
			ClosedVeristicInterpretation.getinstance());
		Metadata m2 = new Metadata(att2, fs2,
			ClosedVeristicInterpretation.getinstance());
		Metadata m3 = new Metadata(att3, fs3,
				OpenVeristicInterpretation.getinstance());
		
		// creo I Description Based Filter
		Filter f1 = new DescriptionBasedFilter(m);
		Filter f2 = new DescriptionBasedFilter(m2);
		Filter f3 = new DescriptionBasedFilter(m3);
		
		SequenceFilter toReturn=new SequenceFilter(f1,f2,f3);
		
		return toReturn;
				
	}
	
	// 1 SEQUENCEFILTER COMPOSTO DA DUE SEQUENCE FILTER DI CUI:
	//1 SEQUENCEFILTER  CREATO TRAMITE IL METODO: createEightFilter();
	//1 SEQUENCEFILTER COMPOSTO DA UN PARALLELFILTER 
	// 1 PARALLEL FILTER CON UN DESCRIPTION BASED FILTER
	private Filter createNinthFilter(){
		
		// 1 filtro sequenziale
		Filter f1=createEightFilter();
	
		//creo un descriptionbasedfilter
		Attribute att1 = new Attribute("sapore");
		
		FuzzySet fs1 = new FuzzySet();
		fs1.setValue("agrodolce", 1);
		fs1.setValue("salato", 0.5);
		fs1.setValue("dolce", 0.5);
		
		Metadata m = new Metadata(att1, fs1,
				ClosedVeristicInterpretation.getinstance());
		Filter f2 = new DescriptionBasedFilter(m);
		//
		
		Aggregator owa=new OWA(0.2);
		
		ParallelFilter f3=new ParallelFilter(owa,f2);
		
		//2 filtro sequenziale
		
		SequenceFilter f4=new SequenceFilter(f3);
		
		//filtro finale
		SequenceFilter toReturn= new SequenceFilter(f1,f4);
		
		return toReturn;
		
	}
	
	//1 SEQUENCE FILTER COMPOSTO DA UN DESCRIPTION BASED FILTER,1 PARALLEL FILTER E UN SEQUENCE FILTER :
	// 1 DESCRIPTION BASED FILTER CREATO TRAMITE IL METODO:createFirstFilter()
	//1 parallelFilter CREATO TRAMITE IL METODO : createFifthFilter();
	//1 SEQUENCE FILTER CREATO TRAMITE IL METODO : createNinthFilter();
	private Filter createTenthFilter(){
		
		Filter f1=createFirstFilter();
		Filter f2=createFifthFilter();
		Filter f3=createNinthFilter();
		
		SequenceFilter toReturn=new SequenceFilter(f1,f2,f3);
		
		return toReturn;
		
	}
	
	// setta i prefissi per un document rdf
	 static void setPrefixes(FilterRdfWriter fw) {
			// settaggio dei prefissi del namespace
			fw.setRdfPrefix("fuzzy-owl", CONSTANTS.ONTOLOGY_URI);
			fw.setRdfPrefix("fuzzy", CONSTANTS.RESOURCE_URI);
			fw.setRdfPrefix("xsd", CONSTANTS.XSD_URI);
			fw.setRdfPrefix("rdf", CONSTANTS.RDF_URI);

		    }

}
