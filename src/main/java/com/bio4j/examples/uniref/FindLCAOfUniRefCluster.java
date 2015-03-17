/*

This program finds the lowest common ancestor _(LCA)_ for taxonomy associated to the protein members of the UniRef
cluster provided.

It expects the following parameters:

1. Bio4j DB folder
2. UniRef cluster type (100/90/50)
3. UniRef cluster ID

 */
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
	        conf.setProperty("storage.batch-loading", "true");
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

			        membersStringArray = uniRef100ClusterOptional.get().members();

		        }else{
			        System.out.println("The cluster ID: " + clusterId + " was not found... :(");
		        }
	        }else if(clusterType.equals("90")){

		        Optional<UniRef90Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> uniRef90ClusterOptional = titanUniRefGraph.uniRef90ClusterIdIndex().getVertex(clusterId);

		        if(uniRef90ClusterOptional.isPresent()){

			        membersStringArray = uniRef90ClusterOptional.get().members();

		        }else{
			        System.out.println("The cluster ID: " + clusterId + " was not found... :(");
		        }
	        }else if(clusterType.equals("50")){

		        Optional<UniRef50Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> uniRef50ClusterOptional = titanUniRefGraph.uniRef50ClusterIdIndex().getVertex(clusterId);

		        if(uniRef50ClusterOptional.isPresent()){

			        membersStringArray = uniRef50ClusterOptional.get().members();

		        }else{
			        System.out.println("The cluster ID: " + clusterId + " was not found... :(");
		        }
	        }

	        if(membersStringArray != null){

		        List<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> proteinMembers = new LinkedList<>();

		        for (String proteinId : membersStringArray){
			        Optional<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> proteinOptional = titanUniProtGraph.proteinAccessionIndex().getVertex(proteinId);
			        proteinMembers.add(proteinOptional.get());
		        }

		        Set<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> taxons = new HashSet<>();

		        for (Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> protein :proteinMembers ){
			        Optional<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> taxonOptional = protein.proteinNCBITaxon_outV();
			        if(taxonOptional.isPresent()){
				        taxons.add(taxonOptional.get());
			        }
		        }
		        List<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> taxonList = new LinkedList<>();
		        taxons.addAll(taxonList);
		        NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> lowestCommonAncestor = TaxonomyAlgo.lowestCommonAncestor(taxonList);

		        System.out.println("The lowest common ancestor is: " + lowestCommonAncestor.scientificName());

	        }else{
		        System.out.println("There were no members found for the cluster provided... :|");
	        }


	        System.out.println("Closing the database...");
	        titanGraph.shutdown();
            System.out.println("Done ;)");
        }

    }
}
