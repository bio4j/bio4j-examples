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
package com.era7.bioinfo.bio4j.tools.uniref;

import com.era7.bioinfo.bio4j.model.nodes.ProteinNode;
import com.era7.bioinfo.bio4j.model.nodes.ncbi.NCBITaxonNode;
import com.era7.bioinfo.bio4j.model.util.Bio4jManager;
import com.era7.bioinfo.bio4j.model.util.NodeRetriever;
import com.era7.bioinfo.bio4j.tools.algo.TaxonomyAlgo;
import com.era7.lib.bioinfo.bioinfoutil.Executable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class FindLCAofUnirefCluster implements Executable{

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


            Bio4jManager manager = new Bio4jManager(args[0]);
            String clusterType = args[1];
            String representantAccession = args[2];


            NodeRetriever nodeRetriever = new NodeRetriever(manager);
            
            ProteinNode representantNode = nodeRetriever.getProteinNodeByAccession(representantAccession);
            
            List<NCBITaxonNode> nodes = new LinkedList<NCBITaxonNode>();
            
            if(representantNode != null){
                if(clusterType.equals("100")){
                    if(representantNode.isUniref100Representant()){
                        
                        Iterator<ProteinNode> iterator = representantNode.getUniref100ClusterThisProteinBelongsTo().iterator();
                        while(iterator.hasNext()){
                            nodes.add(nodeRetriever.getNCBITaxonByTaxId(iterator.next().getOrganism().getNcbiTaxonomyId()));
                        }
                        
                    }else{
                        System.out.println("The protein " + representantAccession + " is not a Uniref 100 representant");
                    }
                }else if(clusterType.equals("90")){
                    if(representantNode.isUniref90Representant()){
                        
                        Iterator<ProteinNode> iterator = representantNode.getUniref90ClusterThisProteinBelongsTo().iterator();
                        while(iterator.hasNext()){
                            nodes.add(nodeRetriever.getNCBITaxonByTaxId(iterator.next().getOrganism().getNcbiTaxonomyId()));
                        }
                        
                    }else{
                        System.out.println("The protein " + representantAccession + " is not a Uniref 90 representant");
                    }
                }else if(clusterType.equals("50")){
                    if(representantNode.isUniref50Representant()){
                        
                        Iterator<ProteinNode> iterator = representantNode.getUniref50ClusterThisProteinBelongsTo().iterator();
                        while(iterator.hasNext()){
                            nodes.add(nodeRetriever.getNCBITaxonByTaxId(iterator.next().getOrganism().getNcbiTaxonomyId()));
                        }
                        
                    }else{
                        System.out.println("The protein " + representantAccession + " is not a Uniref 50 representant");
                    }
                }
                
                if(!nodes.isEmpty()){
                    NCBITaxonNode lcaNode = TaxonomyAlgo.lowestCommonAncestor(nodes);
                    System.out.println("The LCA node is: " + lcaNode.getScientificName() + " (tax_id = " + lcaNode.getTaxId() + " )");
                }
            }
            
            manager.shutDown();
            
            System.out.println("Done ;)");
        }

    }
}
