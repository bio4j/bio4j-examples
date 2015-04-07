/*

This program simply outputs the enzymes related to the protein provided

It expects the following parameters:

1. Bio4j DB folder
2. UniProt protein accession

 */
package com.bio4j.examples.enzyme;

import com.bio4j.model.enzymedb.vertices.Enzyme;
import com.bio4j.model.uniprot.vertices.Protein;
import com.bio4j.titan.model.enzyme.TitanEnzymeDBGraph;
import com.bio4j.titan.model.uniprot.TitanUniProtGraph;
import com.bio4j.titan.model.uniprot_enzyme.TitanUniProtEnzymeGraph;
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

public class GetProteinEnzymaticActivity implements Executable{

	@Override
	public void execute(ArrayList<String> array) {
		String[] args = new String[array.size()];
		for (int i = 0; i < array.size(); i++) {
			args[i] = array.get(i);
		}
		main(args);
	}

	public static void main(String[] args){

		if (args.length != 2) {
			System.out.println("This program expects the following parameters:\n"
					+ "1. Titan properties file (.properties)\n"
					+ "2. UniProt protein accession");
		} else {

			String propertiesFileName = args[0];
			String proteinAccession = args[1];

			System.out.println("Opening the database...");
			TitanGraph titanGraph = TitanFactory.open(propertiesFileName);
			DefaultTitanGraph defGraph = new DefaultTitanGraph(titanGraph);

			System.out.println("Creating the graph managers....");

			//====================================================================================
			TitanEnzymeDBGraph titanEnzymeDBGraph = new TitanEnzymeDBGraph(defGraph);
			TitanUniProtGraph titanUniProtGraph = new TitanUniProtGraph(defGraph);

			TitanUniProtEnzymeGraph titanUniProtEnzymeGraph = new TitanUniProtEnzymeGraph(defGraph, titanUniProtGraph, titanEnzymeDBGraph);

			titanEnzymeDBGraph.withUniProtEnzymeGraph(titanUniProtEnzymeGraph);
			titanUniProtGraph.withUniProtEnzymeGraph(titanUniProtEnzymeGraph);
			//====================================================================================

			System.out.println("Done!");

			Optional<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> proteinOptional = titanUniProtGraph.proteinAccessionIndex().getVertex(proteinAccession);
			if(!proteinOptional.isPresent()){
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
