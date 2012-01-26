/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools.uniref;

import com.era7.bioinfo.bio4jmodel.nodes.ProteinNode;
import com.era7.bioinfo.bio4jmodel.relationships.uniref.UniRef100MemberRel;
import com.era7.bioinfo.bio4jmodel.util.Bio4jManager;
import com.era7.bioinfo.bio4jmodel.util.NodeRetriever;
import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class FindBiggestUniref100Clusters {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("This program expects the following parameters:\n"
                    + "1. Bio4j DB folder\n"
                    + "2. Bio4j DB config parameters file (neo4j.properties)\n"
                    + "3. Output results file name (txt)\n");
        } else {

            File outFile = new File(args[1]);

            Bio4jManager manager = new Bio4jManager(args[0], args[1], true);
            NodeRetriever nodeRetriever = new NodeRetriever(manager);

            TreeMap<String, Integer> clustersCountersMap = new TreeMap<String, Integer>();

            int protCounter = 0;

            //--looping through all proteins--
            Iterator<Node> iterator = manager.getNodeTypeIndex().get(Bio4jManager.NODE_TYPE_INDEX_NAME, ProteinNode.NODE_TYPE).iterator();

            UniRef100MemberRel uniRef100MemberRel = new UniRef100MemberRel(null);
            
            while (iterator.hasNext()) {
                ProteinNode protein = new ProteinNode(iterator.next());
                if (protein.isUniref100Representant()) {
                    int clusterLength = 1;

                    Iterator<Relationship> relIterator = protein.getNode().getRelationships(Direction.OUTGOING, uniRef100MemberRel).iterator();
                    while(relIterator.hasNext()){
                        relIterator.next();
                        clusterLength++;
                    }
                    
                    clustersCountersMap.put(protein.getAccession(), clusterLength);
                }

                protCounter++;

                if (protCounter % 10000 == 0) {
                    System.out.println(protCounter + " proteins analyzed... ");
                }
            }

            manager.shutDown();
        }

    }   
    
}
