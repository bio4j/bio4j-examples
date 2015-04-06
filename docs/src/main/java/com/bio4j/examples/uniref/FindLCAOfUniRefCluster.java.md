

This program finds the lowest common ancestor _(LCA)_ for taxonomy associated to the protein members of the UniRef
cluster provided.

It expects the following parameters:

1. Bio4j DB folder
2. UniRef cluster type (100/90/50)
3. UniRef cluster ID



```java
package com.bio4j.examples.uniref;

import com.bio4j.examples.ncbi_taxonomy.TaxonomyAlgo;
import com.bio4j.model.ncbiTaxonomy.vertices.NCBITaxon;
import com.bio4j.model.uniprot.vertices.Protein;
import com.bio4j.model.uniref.vertices.UniRef100Cluster;
import com.bio4j.model.uniref.vertices.UniRef50Cluster;
import com.bio4j.model.uniref.vertices.UniRef90Cluster;
import com.bio4j.titan.model.ncbiTaxonomy.TitanNCBITaxonomyGraph;
import com.bio4j.titan.model.uniprot.TitanUniProtGraph;
import com.bio4j.titan.model.uniprot_ncbiTaxonomy.TitanUniProtNCBITaxonomyGraph;
import com.bio4j.titan.model.uniprot_uniref.TitanUniProtUniRefGraph;
import com.bio4j.titan.model.uniref.TitanUniRefGraph;
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

public class FindLCAOfUniRefCluster implements Executable{

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
                    + "2. UniRef cluster type (100,90,50)\n"
                    + "3. UniRef cluster ID");
        } else {

	        String dbFolder = args[0];
	        String clusterType = args[1];
	        String clusterId = args[2];

	        //----------DB configuration------------------
	        Configuration conf = new BaseConfiguration();
	        conf.setProperty("storage.directory", dbFolder);
	        conf.setProperty("storage.backend", "berkeleyje");
	        //-------creating graph handlers---------------------
	        TitanGraph titanGraph = TitanFactory.open(conf);
	        DefaultTitanGraph defGraph = new DefaultTitanGraph(titanGraph);

	        System.out.println("Creating the graph managers....");

	        //====================================================================================
	        TitanUniRefGraph titanUniRefGraph = new TitanUniRefGraph(defGraph);
	        TitanUniProtGraph titanUniProtGraph = new TitanUniProtGraph(defGraph);
	        TitanNCBITaxonomyGraph titanNCBITaxonomyGraph = new TitanNCBITaxonomyGraph(defGraph);

	        TitanUniProtNCBITaxonomyGraph titanUniprotNCBITaxonomyGraph = new TitanUniProtNCBITaxonomyGraph(defGraph, titanUniProtGraph, titanNCBITaxonomyGraph);
	        TitanUniProtUniRefGraph titanUniProtUniRefGraph = new TitanUniProtUniRefGraph(defGraph, titanUniProtGraph, titanUniRefGraph);

	        titanUniRefGraph.withUniProtUniRefGraph(titanUniProtUniRefGraph);
	        titanUniProtGraph.withUniProtUniRefGraph(titanUniProtUniRefGraph);
	        titanUniProtGraph.withUniProtNCBITaxonomyGraph(titanUniprotNCBITaxonomyGraph);
	        titanNCBITaxonomyGraph.withUniProtNCBITaxonomyGraph(titanUniprotNCBITaxonomyGraph);
	        //====================================================================================

	        System.out.println("Done!");

	        String[] membersStringArray = null;

	        if(clusterType.equals("100")){

		        Optional<UniRef100Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> uniRef100ClusterOptional = titanUniRefGraph.uniRef100ClusterIdIndex().getVertex(clusterId);

		        if(uniRef100ClusterOptional.isPresent()){

			        UniRef100Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> cluster = uniRef100ClusterOptional.get();
			        System.out.println("cluster.id() = " + cluster.id());
			        System.out.println("cluster.name() = " + cluster.name());
			        System.out.println("cluster.updatedDate() = " + cluster.updatedDate());
			        membersStringArray = cluster.members();

		        }else{
			        System.out.println("The cluster ID: " + clusterId + " was not found... :(");
		        }
	        }else if(clusterType.equals("90")){

		        Optional<UniRef90Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> uniRef90ClusterOptional = titanUniRefGraph.uniRef90ClusterIdIndex().getVertex(clusterId);

		        if(uniRef90ClusterOptional.isPresent()){

			        UniRef90Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> cluster = uniRef90ClusterOptional.get();
			        System.out.println("cluster.id() = " + cluster.id());
			        System.out.println("cluster.name() = " + cluster.name());
			        System.out.println("cluster.updatedDate() = " + cluster.updatedDate());

			        membersStringArray = cluster.members();

		        }else{
			        System.out.println("The cluster ID: " + clusterId + " was not found... :(");
		        }
	        }else if(clusterType.equals("50")){

		        Optional<UniRef50Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> uniRef50ClusterOptional = titanUniRefGraph.uniRef50ClusterIdIndex().getVertex(clusterId);

		        if(uniRef50ClusterOptional.isPresent()){

			        UniRef50Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> cluster = uniRef50ClusterOptional.get();
			        System.out.println("cluster.id() = " + cluster.id());
			        System.out.println("cluster.name() = " + cluster.name());
			        System.out.println("cluster.updatedDate() = " + cluster.updatedDate());

			        membersStringArray = cluster.members();

		        }else{
			        System.out.println("The cluster ID: " + clusterId + " was not found... :(");
		        }
	        }

	        if(membersStringArray != null){

		        List<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> proteinMembers = new LinkedList<>();

		        System.out.println("Retrieving protein members...");
		        System.out.println("membersStringArray.length = " + membersStringArray.length);

		        for (String proteinId : membersStringArray){
			        Optional<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> proteinOptional = titanUniProtGraph.proteinAccessionIndex().getVertex(proteinId);
			        Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> protein = proteinOptional.get();
			        System.out.println("protein.accession() = " + protein.accession());
			        proteinMembers.add(protein);
		        }

		        Set<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> taxons = new HashSet<>();

		        System.out.println("Fetching taxonomy associated to proteins...");
		        for (Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> protein :proteinMembers ){
			        System.out.println("Current protein: " + protein.accession());
			        Optional<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> taxonOptional = protein.proteinNCBITaxon_outV();
			        if(taxonOptional.isPresent()){
				        NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> taxon = taxonOptional.get();
				        taxons.add(taxon);
				        System.out.println("NCBI taxon found: " + taxon.id() + ":" + taxon.scientificName());
			        }
		        }
		        System.out.println("Done!");
		        List<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> taxonList = new LinkedList<>();
		        taxonList.addAll(taxons);
		        System.out.println("Looking for the lowest common ancestor...");
		        NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> lowestCommonAncestor = TaxonomyAlgo.lowestCommonAncestor(taxonList);

		        if(lowestCommonAncestor != null){
			        System.out.println("The lowest common ancestor is: " + lowestCommonAncestor.scientificName());
		        }else{
			        System.out.println("There was no lower common ancestor found...");
		        }


	        }else{
		        System.out.println("There were no members found for the cluster provided... :|");
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
[main\java\com\bio4j\examples\uniref\FindLCAOfUniRefCluster.java]: FindLCAOfUniRefCluster.java.md