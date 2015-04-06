

This program simply outputs the enzymes related to the protein provided

It expects the following parameters:

1. Bio4j DB folder
2. UniProt protein accession



```java
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
					+ "1. Bio4j DB folder\n"
					+ "2. UniProt protein accession");
		} else {

			String dbFolder = args[0];
			String proteinAccession = args[1];

			//----------DB configuration------------------
			Configuration conf = new BaseConfiguration();
			conf.setProperty("storage.directory", dbFolder);
			conf.setProperty("storage.backend", "berkeleyje");
			//-------creating graph handlers---------------------
			TitanGraph titanGraph = TitanFactory.open(conf);
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
[main\java\com\bio4j\examples\enzyme\GetProteinEnzymaticActivity.java]: GetProteinEnzymaticActivity.java.md
[main\java\com\bio4j\examples\ExecuteBio4jExample.java]: ..\ExecuteBio4jExample.java.md
[main\java\com\bio4j\examples\geninfo\GetProteinsAssociatedToGIs.java]: ..\geninfo\GetProteinsAssociatedToGIs.java.md
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