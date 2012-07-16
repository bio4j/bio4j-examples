/*
 * Copyright (C) 2010-2011  "Bio4j"
 *
 * This file is part of Bio4j
 *
 * Bio4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
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
            
            //------freeing space---------
            firstAncestors.clear();
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
