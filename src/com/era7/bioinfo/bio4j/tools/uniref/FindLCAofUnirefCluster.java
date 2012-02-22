/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools.uniref;

import com.era7.bioinfo.bio4j.tools.algo.TaxonomyAlgo;
import com.era7.bioinfo.bio4jmodel.nodes.ProteinNode;
import com.era7.bioinfo.bio4jmodel.nodes.ncbi.NCBITaxonNode;
import com.era7.bioinfo.bio4jmodel.util.Bio4jManager;
import com.era7.bioinfo.bio4jmodel.util.NodeRetriever;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class FindLCAofUnirefCluster {

    
    public static void main(String[] args) throws IOException {

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
