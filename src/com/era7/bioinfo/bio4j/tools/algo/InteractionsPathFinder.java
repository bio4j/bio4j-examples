/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools.algo;

import com.era7.bioinfo.bio4jmodel.nodes.ProteinNode;
import com.era7.bioinfo.bio4jmodel.relationships.protein.ProteinProteinInteractionRel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.kernel.Traversal;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class InteractionsPathFinder {

    public static List<List<String>> findShortestInteractionPath(ProteinNode proteinSource,
            ProteinNode proteinTarget,
            int maxDepth,
            int maxResultsNumber) {


        List<List<String>> results = new LinkedList<List<String>>();

        PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
                Traversal.expanderForTypes(new ProteinProteinInteractionRel(null), Direction.INCOMING),
                maxDepth, maxResultsNumber);

        Iterator<Path> paths = finder.findAllPaths(proteinSource.getNode(), proteinTarget.getNode()).iterator();

        while (paths.hasNext()) {
            Path path = paths.next();
            LinkedList<String> proteinsList = new LinkedList<String>();
            
            Iterator pathIterator = path.iterator();
            
            if(pathIterator.hasNext()){
                            
                boolean isNode = true;
                while(pathIterator.hasNext()){
                    if(isNode){
                        proteinsList.add(new ProteinNode((Node)pathIterator.next()).getAccession());
                    }else{
                        pathIterator.next();
                    }
                    isNode = !isNode;
                }
            }

            results.add(proteinsList);
        }
        
        
        return results;

    }
}
