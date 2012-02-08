/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools.algo;

import com.era7.bioinfo.bio4jmodel.nodes.ncbi.NCBITaxonNode;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class TaxonomyAlgo {
    
    
    public static NCBITaxonNode lowestCommonAncestor(List<NCBITaxonNode> nodes){
     
        
        NCBITaxonNode lowerCommonAncestor = null;
        
        if(nodes.size() > 0){
            
            NCBITaxonNode firstNode = nodes.get(0);
            //System.out.println("First node: " + firstNode.getScientificName() + " " + firstNode.getTaxId());
            
            LinkedList<NCBITaxonNode> firstAncestors = getAncestorsPlusSelf(firstNode);
            
            //--------------debugging------------
//            System.out.println("Ancestors found: ");
//            for (NCBITaxonNode nCBITaxonNode : firstAncestors) {
//                System.out.print("(" + nCBITaxonNode.getScientificName() + "," + nCBITaxonNode.getTaxId() + ")-->");
//            }
            //----------------------------------
            
            for (int i = 1; i < nodes.size() && !firstAncestors.isEmpty(); i++) {
                NCBITaxonNode currentNode = nodes.get(i);
                lookForCommonAncestor(firstAncestors, currentNode);                
            }
            
            
            if(!firstAncestors.isEmpty()){
                lowerCommonAncestor = firstAncestors.get(0);
            }
        }        
        
        return lowerCommonAncestor;
        
    }
    
    
    
    private static LinkedList<NCBITaxonNode> getAncestorsPlusSelf(NCBITaxonNode node){
        
        LinkedList<NCBITaxonNode> ancestors = new LinkedList<NCBITaxonNode>();
        
        ancestors.add(node);
                
        while(node.getParent() != null){
            node = node.getParent();
            ancestors.add(node);
        }
        
        return ancestors;
        
    }
    

    private static void lookForCommonAncestor(LinkedList<NCBITaxonNode> commonAncestors, NCBITaxonNode currentNode){
        
        while(currentNode != null){
            
            for (int i = 0; i < commonAncestors.size(); i++) {
                NCBITaxonNode nCBITaxonNode = commonAncestors.get(i);
                if(nCBITaxonNode.getTaxId().equals(currentNode.getTaxId())){
                    for(int j=0; j<i; j++){
                        commonAncestors.pollFirst();
                    }
                    return;
                }
            }
            
            currentNode = currentNode.getParent();
        }
        
    }

}
