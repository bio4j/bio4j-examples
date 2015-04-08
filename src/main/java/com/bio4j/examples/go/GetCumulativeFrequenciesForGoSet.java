/*
This program calculates cumulative frequencies for the GoSet passed as a JSON input file.
The output of the program is another JSON file with the cumulative values added to all terms.

The program expects the following parameters:

1. Bio4j DB folder
2. Input JSON file including a GoSet
3. Output JSON file including the GO annotation set with cumulative frequencies

 */

package com.bio4j.examples.go;

import com.bio4j.examples.json.model.go.GOTerm;
import com.bio4j.examples.json.model.go.GoSet;
import com.bio4j.model.go.vertices.GoTerm;
import com.bio4j.titan.model.go.TitanGoGraph;
import com.bio4j.titan.model.uniprot.TitanUniProtGraph;
import com.bio4j.titan.model.uniprot_go.TitanUniProtGoGraph;
import com.bio4j.titan.util.DefaultTitanGraph;
import com.era7.bioinfo.bioinfoutil.Executable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thinkaurelius.titan.core.TitanEdge;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.schema.EdgeLabelMaker;
import com.thinkaurelius.titan.core.schema.VertexLabelMaker;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetCumulativeFrequenciesForGoSet implements Executable {

	@Override
	public void execute(ArrayList<String> array) {
		String[] args = new String[array.size()];
		for (int i = 0; i < array.size(); i++) {
			args[i] = array.get(i);
		}
		main(args);
	}

	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println("This program expects the following parameters:\n"
					+ "1. Bio4j DB folder\n"
					+ "2. Input JSON file including a GoSet\n"
					+ "3. Output JSON file including the GO annotation set with cumulative frequencies");
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
			TitanUniProtGraph titanUniProtGraph = new TitanUniProtGraph(defGraph);
			TitanGoGraph titanGoGraph = new TitanGoGraph(defGraph);

			TitanUniProtGoGraph titanUniProtGoGraph = new TitanUniProtGoGraph(defGraph, titanUniProtGraph, titanGoGraph);

			titanGoGraph.withUniProtGoGraph(titanUniProtGoGraph);
			titanUniProtGraph.withUniProtGoGraph(titanUniProtGoGraph);
			//====================================================================================

			System.out.println("Done!");

			try{

				System.out.println("Reading JSON input file...");
				BufferedReader reader = new BufferedReader(new FileReader(new File(inputFileSt)));
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				GoSet goSet = gson.fromJson(reader, GoSet.class);

				Set<GOTerm> goTermSet = goSet.getGoTerms();
				Map<String, Integer> goCumulativeFrequencies = new HashMap<>();
				Map<String, Integer> goFrequencies = new HashMap<>();

				for (GOTerm goTerm : goTermSet){
					goCumulativeFrequencies.put(goTerm.getId(), goTerm.getTermCount());
					goFrequencies.put(goTerm.getId(), goTerm.getTermCount());
				}

				System.out.println("Calculating cumulative frequencies....");

				for(String goId : goCumulativeFrequencies.keySet()){
					Optional<GoTerm<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> goTermOptional = titanGoGraph.goTermIdIndex().getVertex(goId);
					if(goTermOptional.isPresent()){
						GoTerm<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> goTerm = goTermOptional.get();
						updateCumulativeFrequenciesWithCountOfTerm(goTerm, goCumulativeFrequencies, goFrequencies.get(goId));
					}
				}
				System.out.println("Done! :D");

				System.out.println("Closing the database...");
				titanGraph.shutdown();
				System.out.println("Done!");

				for (GOTerm goTerm : goTermSet){
					goTerm.setCumulativeCount(goCumulativeFrequencies.get(goTerm.getId()));
				}

				System.out.println("Writing output file....");
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFileSt)));
				writer.write(gson.toJson(goSet));
				System.out.println("Closing output file...");
				writer.close();

				System.out.println("The output file was created successfully!!");

			}catch(Exception e){
				e.printStackTrace();
			}


		}

	}

	private static void updateCumulativeFrequenciesWithCountOfTerm(GoTerm<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> term,
	                                                               Map<String, Integer> goCumulativeFrequencies,
	                                                               int termCount){

		Optional<Stream<GoTerm<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>>> streamOptional =  term.isA_outV();
		if(streamOptional.isPresent()){

			List<GoTerm<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> termList = streamOptional.get().collect((Collectors.toList()));
			for (GoTerm<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> tempTerm : termList){
				Integer cumulativeFrequency = goCumulativeFrequencies.get(tempTerm.id());
				if(cumulativeFrequency != null){
					goCumulativeFrequencies.put(tempTerm.id(), (cumulativeFrequency + termCount));
				}
				updateCumulativeFrequenciesWithCountOfTerm(tempTerm, goCumulativeFrequencies, termCount);
			}
		}

	}
}
