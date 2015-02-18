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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
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
                    + "2. Uniref cluster type (100,90,50)\n"
                    + "3. Uniref representant uniprot accession");
        } else {

	        String dbFolder = args[0];
	        String clusterType = args[1];
	        String representantAccession = args[2];

	        //----------DB configuration------------------
	        Configuration conf = new BaseConfiguration();
	        conf.setProperty("storage.directory", dbFolder);
	        conf.setProperty("storage.backend", "local");
	        conf.setProperty("autotype", "none");
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


	        Optional<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> representantOptional = titanUniProtGraph.proteinAccessionIndex().getVertex(representantAccession);
            
            List<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> nodes = new LinkedList<>();
            
            if(representantOptional.isPresent()){

	            Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> representant = representantOptional.get();
	            Stream<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> membersStream = null;

	            if(clusterType.equals("100")){

	                Optional<UniRef100Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> uniRef100ClusterOptional = representant.uniref100Representant_outV();

	                if(uniRef100ClusterOptional.isPresent()){

		                UniRef100Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> uniRef100Cluster = uniRef100ClusterOptional.get();
		                membersStream = uniRef100Cluster.uniRef100Member_inV();
                        
                    }else{
                        System.out.println("The protein " + representantAccession + " is not a Uniref 100 representant");
                    }
                }else if(clusterType.equals("90")){

		            Optional<UniRef90Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> uniRef90ClusterOptional = representant.uniref90Representant_outV();

                    if(uniRef90ClusterOptional.isPresent()){

	                    UniRef90Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> uniRef90Cluster = uniRef90ClusterOptional.get();
	                    membersStream = uniRef90Cluster.uniRef90Member_inV();
                        
                    }else{
                        System.out.println("The protein " + representantAccession + " is not a Uniref 90 representant");
                    }
                }else if(clusterType.equals("50")){
		            Optional<UniRef50Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> uniRef50ClusterOptional = representant.uniref50Representant_outV();

		            if(uniRef50ClusterOptional.isPresent()){

			            UniRef50Cluster<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> uniRef50Cluster = uniRef50ClusterOptional.get();
			            membersStream = uniRef50Cluster.uniRef50Member_inV();

		            }else{
			            System.out.println("The protein " + representantAccession + " is not a Uniref 50 representant");
		            }
                }

                List<Protein<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> proteinMembers = membersStream.collect(Collectors.toList());
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
            }

	        System.out.println("Closing the database...");
	        titanGraph.shutdown();
            System.out.println("Done ;)");
        }

    }
}
