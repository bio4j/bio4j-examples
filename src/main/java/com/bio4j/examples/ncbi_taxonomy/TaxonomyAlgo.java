/*

Class including utility methods around NCBI taxonomic units.

 */
package com.bio4j.examples.ncbi_taxonomy;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.bio4j.model.ncbiTaxonomy.vertices.NCBITaxon;
import com.bio4j.titan.util.DefaultTitanGraph;
import com.thinkaurelius.titan.core.TitanEdge;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.schema.EdgeLabelMaker;
import com.thinkaurelius.titan.core.schema.VertexLabelMaker;


public class TaxonomyAlgo {
    
    
    public static NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> lowestCommonAncestor(List<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> nodes){

        if(nodes.size() > 0){

	        NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> firstNode = nodes.get(0);
            
            LinkedList<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> firstAncestors = getAncestorsPlusSelf(firstNode);
            
            for (int i = 1; i < nodes.size() && !firstAncestors.isEmpty(); i++) {
	            NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> currentNode = nodes.get(i);
                lookForCommonAncestor(firstAncestors, currentNode);                
            }
            
            if(!firstAncestors.isEmpty()){
                return firstAncestors.get(0);
            }else{
	            return null;
            }


        }else{
	        return null;
        }

    }
    
    
    
    private static LinkedList<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> getAncestorsPlusSelf(NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> taxon){
        
        LinkedList<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> ancestors = new LinkedList<>();
        
        ancestors.add(taxon);

	    Optional<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> taxonParentOptional = taxon.ncbiTaxonParent_inV();
                
        while(taxonParentOptional.isPresent()){
	        NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> taxonParent = taxonParentOptional.get();
            ancestors.add(taxonParent);
	        taxonParentOptional = taxonParent.ncbiTaxonParent_inV();
        }
        
        return ancestors;
        
    }
    

    private static void lookForCommonAncestor(LinkedList<NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker>> commonAncestors, NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> currentNode){
        
        while(currentNode != null){
            
            for (int i = 0; i < commonAncestors.size(); i++) {
	            NCBITaxon<DefaultTitanGraph, TitanVertex, VertexLabelMaker, TitanEdge, EdgeLabelMaker> taxonNode = commonAncestors.get(i);
                if(taxonNode.id().equals(currentNode.id())){
                    for(int j=0; j<i; j++){
                        commonAncestors.pollFirst();
                    }
                    return;
                }
            }

	        if(currentNode.ncbiTaxonParent_inV().isPresent()){
		        currentNode = currentNode.ncbiTaxonParent_inV().get();

	        }else{
		        currentNode = null;
	        }
        }
        
    }

}
