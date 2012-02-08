/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools.taxonomy;

import com.era7.bioinfo.bio4jmodel.nodes.ncbi.NCBITaxonNode;
import com.era7.bioinfo.bio4jmodel.util.Bio4jManager;
import com.era7.bioinfo.bio4jmodel.util.NodeRetriever;
import com.era7.bioinfo.bio4jmodel.util.TaxonomyAlgo;
import java.util.LinkedList;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class LowestCommonAncestorTest {
    
    public static void main(String args[]){
        
        LinkedList<NCBITaxonNode> nodes = new LinkedList<NCBITaxonNode>();
        
        Bio4jManager bio4jManager = new Bio4jManager(args[0]);
        NodeRetriever nodeRetriever = new NodeRetriever(bio4jManager);
                
        for (int i = 1; i < args.length; i++) {
            nodes.add(nodeRetriever.getNCBITaxonByTaxId(args[i]));            
        }
        
        NCBITaxonNode commonAncestor = TaxonomyAlgo.lowerCommonAncestor(nodes);
        
        System.out.println("Common ancestor: " + commonAncestor.getScientificName() + " , " + commonAncestor.getTaxId());
        
        bio4jManager.shutDown();
        
    }
    
}
