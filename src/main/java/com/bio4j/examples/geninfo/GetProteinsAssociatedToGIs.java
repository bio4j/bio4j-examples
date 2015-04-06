/*

This program retrieves the list of proteins that are associated to the set of GIs provided as a parameter.
The selection of the proteins is carried out by means of the NCBI taxons linked to the respective GIs.

The program expects the following parameters:

1. Bio4j DB folder
2. Input TXT file including a list of GIs (one per line)
3. Output JSON file including a set of UniProt proteins

 */
package com.bio4j.examples.geninfo;

import com.bio4j.model.enzymedb.vertices.Enzyme;
import com.bio4j.model.uniprot.vertices.Protein;
import com.bio4j.titan.model.enzyme.TitanEnzymeDBGraph;
import com.bio4j.titan.model.geninfo.TitanGenInfoGraph;
import com.bio4j.titan.model.ncbiTaxonomy.TitanNCBITaxonomyGraph;
import com.bio4j.titan.model.ncbiTaxonomy_geninfo.TitanNCBITaxonomyGenInfoGraph;
import com.bio4j.titan.model.uniprot.TitanUniProtGraph;
import com.bio4j.titan.model.uniprot_ncbiTaxonomy.TitanUniProtNCBITaxonomyGraph;
import com.bio4j.titan.util.DefaultTitanGraph;
import com.era7.bioinfo.bioinfoutil.Executable;
import com.thinkaurelius.titan.core.TitanEdge;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.schema.EdgeLabelMaker;
import com.thinkaurelius.titan.core.schema.VertexLabelMaker;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetProteinsAssociatedToGIs implements Executable{

	@Override
	public void execute(ArrayList<String> array) {
		String[] args = new String[array.size()];
		for (int i = 0; i < array.size(); i++) {
			args[i] = array.get(i);
		}
		main(args);
	}

	public static void main(String[] args){

		if (args.length != 3) {
			System.out.println("This program expects the following parameters:\n"
					+ "1. Bio4j DB folder\n"
					+ "2. Input TXT file including a list of GIs (one per line)\n"
					+ "3. Output JSON file including a set of UniProt proteins"
			);
		} else {

			String dbFolder = args[0];
			String inputFileSt = args[1];
			String outputFileSt = args[2];

			//----------DB configuration------------------
			Configuration conf = new BaseConfiguration();
			conf.setProperty("storage.directory", dbFolder);
			conf.setProperty("storage.backend", "berkeleyje");
			//-------creating graph handlers---------------------
			TitanGraph titanGraph = TitanFactory.open(conf);
			DefaultTitanGraph defGraph = new DefaultTitanGraph(titanGraph);

			System.out.println("Creating the graph managers....");

			//====================================================================================
			TitanGenInfoGraph titanGenInfoGraph = new TitanGenInfoGraph(defGraph);
			TitanUniProtGraph titanUniProtGraph = new TitanUniProtGraph(defGraph);
			TitanNCBITaxonomyGraph titanNCBITaxonomyGraph = new TitanNCBITaxonomyGraph(defGraph);

			TitanNCBITaxonomyGenInfoGraph titanNCBITaxonomyGenInfoGraph = new TitanNCBITaxonomyGenInfoGraph(defGraph, titanNCBITaxonomyGraph, titanGenInfoGraph);
			TitanUniProtNCBITaxonomyGraph titanUniProtNCBITaxonomyGraph = new TitanUniProtNCBITaxonomyGraph(defGraph, titanUniProtGraph, titanNCBITaxonomyGraph);

			titanGenInfoGraph.withNCBITaxonomyGenInfoGraph(titanNCBITaxonomyGenInfoGraph);
			titanUniProtGraph.withUniProtNCBITaxonomyGraph(titanUniProtNCBITaxonomyGraph);
			titanNCBITaxonomyGraph.withNCBITaxonomyGenInfoGraph(titanNCBITaxonomyGenInfoGraph);
			titanNCBITaxonomyGraph.withUniProtNCBITaxonomyGraph(titanUniProtNCBITaxonomyGraph);
			//====================================================================================

			System.out.println("Done!");

			Optional<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> proteinOptional = titanUniProtGraph.proteinAccessionIndex().getVertex(proteinAccession);
			if(proteinOptional.isPresent()){
				System.out.println("There was no protein found for the accession provided: " + proteinAccession);
			}else{
				Optional<Stream<Enzyme<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>>> enzymesOptionalStream = proteinOptional.get().enzymaticActivity_outV();
				if(enzymesOptionalStream.isPresent()){
					List<Enzyme<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> enzymeList = enzymesOptionalStream.get().collect((Collectors.toList()));
					System.out.println("The following enzymes were found to be related to the protein provided: ");
					for (Enzyme<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> enzyme : enzymeList){
						System.out.println(enzyme.id() + " : " + enzyme.officialName());
					}
				}else{
					System.out.println("The protein provided does not have any enzymatic acitivy associated... :|");
				}
			}


			System.out.println("Closing the database...");
			titanGraph.shutdown();
			System.out.println("Done ;)");
		}

	}
}
