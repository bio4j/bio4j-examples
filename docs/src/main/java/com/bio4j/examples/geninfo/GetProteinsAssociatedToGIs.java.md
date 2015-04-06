

This program retrieves the list of proteins that are associated to the set of GIs provided as a parameter.
The selection of the proteins is carried out by means of the NCBI taxons linked to the respective GIs.

The program expects the following parameters:

1. Bio4j DB folder
2. Input TXT file including a list of GIs (one per line)
3. Output JSON file including a set of UniProt proteins



```java
package com.bio4j.examples.geninfo;

import com.bio4j.examples.json.model.uniprot.ProteinSet;
import com.bio4j.model.enzymedb.vertices.Enzyme;
import com.bio4j.model.geninfo.vertices.GenInfo;
import com.bio4j.model.ncbiTaxonomy.vertices.NCBITaxon;
import com.bio4j.model.uniprot.vertices.Protein;
import com.bio4j.titan.model.enzyme.TitanEnzymeDBGraph;
import com.bio4j.titan.model.geninfo.TitanGenInfoGraph;
import com.bio4j.titan.model.ncbiTaxonomy.TitanNCBITaxonomyGraph;
import com.bio4j.titan.model.ncbiTaxonomy_geninfo.TitanNCBITaxonomyGenInfoGraph;
import com.bio4j.titan.model.uniprot.TitanUniProtGraph;
import com.bio4j.titan.model.uniprot_ncbiTaxonomy.TitanUniProtNCBITaxonomyGraph;
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

			File inputFile = new File(inputFileSt);
			File outputFile = new File(outputFileSt);

			try {

				BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
				BufferedReader reader = new BufferedReader(new FileReader(inputFile));

				String line;
				LinkedList<String> giList = new LinkedList<>();
				HashMap<String, com.bio4j.examples.json.model.uniprot.Protein> proteinHashMap = new HashMap<>();

				System.out.println("Reading GI list...");
				while((line = reader.readLine()) != null){
					giList.add(line.trim());
				}
				reader.close();
				System.out.println("Done!");

				System.out.println("Looking for NCBI taxons associated...");

				for (String giSt : giList){
					Optional<GenInfo<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> genInfoOptional = titanGenInfoGraph.genInfoIdIndex().getVertex(giSt);
					if(genInfoOptional.isPresent()){
						GenInfo<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> genInfo = genInfoOptional.get();
						NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> ncbiTaxon = genInfo.genInfoNCBITaxon_outV();
						Optional<Stream<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>>> proteinStreamOptional = ncbiTaxon.proteinNCBITaxon_inV();
						if(proteinStreamOptional.isPresent()){
							List<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> proteins = proteinStreamOptional.get().collect((Collectors.toList()));
							for (Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> protein : proteins){
								com.bio4j.examples.json.model.uniprot.Protein proteinJSON = proteinHashMap.get(protein.accession());
								if(proteinJSON == null){
									proteinJSON = new com.bio4j.examples.json.model.uniprot.Protein(protein);
									proteinHashMap.put(protein.accession(), proteinJSON);
								}
							}

						}
					}
				}

				System.out.println("Done!");

				ProteinSet proteinSet = new ProteinSet();
				for (String key : proteinHashMap.keySet()){
					com.bio4j.examples.json.model.uniprot.Protein proteinJSON = proteinHashMap.get(key);
					proteinSet.addProtein(proteinJSON);
				}

				System.out.println("Writing output file....");
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				writer.write(gson.toJson(proteinSet));
				System.out.println("Closing output file...");

			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Closing the database...");
			titanGraph.shutdown();
			System.out.println("Done ;)");
		}

	}
}

```


------

### Index

+ src
  + main
    + java
      + com
        + bio4j
          + examples
            + [BasicProteinManipulation.java][main\java\com\bio4j\examples\BasicProteinManipulation.java]
            + enzyme
              + [GetProteinEnzymaticActivity.java][main\java\com\bio4j\examples\enzyme\GetProteinEnzymaticActivity.java]
            + [ExecuteBio4jExample.java][main\java\com\bio4j\examples\ExecuteBio4jExample.java]
            + geninfo
              + [GetProteinsAssociatedToGIs.java][main\java\com\bio4j\examples\geninfo\GetProteinsAssociatedToGIs.java]
            + go
              + [ExportGOJSONToCSV.java][main\java\com\bio4j\examples\go\ExportGOJSONToCSV.java]
              + [GetCumulativeFrequenciesForGoSet.java][main\java\com\bio4j\examples\go\GetCumulativeFrequenciesForGoSet.java]
              + [GetGOAnnotation.java][main\java\com\bio4j\examples\go\GetGOAnnotation.java]
              + [TransformGOJSONtoHierarchicalJSON.java][main\java\com\bio4j\examples\go\TransformGOJSONtoHierarchicalJSON.java]
            + json
              + model
                + go
                  + [GoSet.java][main\java\com\bio4j\examples\json\model\go\GoSet.java]
                  + [GOTerm.java][main\java\com\bio4j\examples\json\model\go\GOTerm.java]
                + uniprot
                  + [Protein.java][main\java\com\bio4j\examples\json\model\uniprot\Protein.java]
                  + [ProteinSet.java][main\java\com\bio4j\examples\json\model\uniprot\ProteinSet.java]
            + ncbi_taxonomy
              + [TaxonomyAlgo.java][main\java\com\bio4j\examples\ncbi_taxonomy\TaxonomyAlgo.java]
            + uniref
              + [FindLCAOfUniRefCluster.java][main\java\com\bio4j\examples\uniref\FindLCAOfUniRefCluster.java]

[main\java\com\bio4j\examples\BasicProteinManipulation.java]: ..\BasicProteinManipulation.java.md
[main\java\com\bio4j\examples\enzyme\GetProteinEnzymaticActivity.java]: ..\enzyme\GetProteinEnzymaticActivity.java.md
[main\java\com\bio4j\examples\ExecuteBio4jExample.java]: ..\ExecuteBio4jExample.java.md
[main\java\com\bio4j\examples\geninfo\GetProteinsAssociatedToGIs.java]: GetProteinsAssociatedToGIs.java.md
[main\java\com\bio4j\examples\go\ExportGOJSONToCSV.java]: ..\go\ExportGOJSONToCSV.java.md
[main\java\com\bio4j\examples\go\GetCumulativeFrequenciesForGoSet.java]: ..\go\GetCumulativeFrequenciesForGoSet.java.md
[main\java\com\bio4j\examples\go\GetGOAnnotation.java]: ..\go\GetGOAnnotation.java.md
[main\java\com\bio4j\examples\go\TransformGOJSONtoHierarchicalJSON.java]: ..\go\TransformGOJSONtoHierarchicalJSON.java.md
[main\java\com\bio4j\examples\json\model\go\GoSet.java]: ..\json\model\go\GoSet.java.md
[main\java\com\bio4j\examples\json\model\go\GOTerm.java]: ..\json\model\go\GOTerm.java.md
[main\java\com\bio4j\examples\json\model\uniprot\Protein.java]: ..\json\model\uniprot\Protein.java.md
[main\java\com\bio4j\examples\json\model\uniprot\ProteinSet.java]: ..\json\model\uniprot\ProteinSet.java.md
[main\java\com\bio4j\examples\ncbi_taxonomy\TaxonomyAlgo.java]: ..\ncbi_taxonomy\TaxonomyAlgo.java.md
[main\java\com\bio4j\examples\uniref\FindLCAOfUniRefCluster.java]: ..\uniref\FindLCAOfUniRefCluster.java.md