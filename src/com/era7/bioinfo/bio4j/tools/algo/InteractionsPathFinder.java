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

import com.era7.bioinfo.bio4j.neo4j.model.nodes.ProteinNode;
import com.era7.bioinfo.bio4j.neo4j.model.relationships.protein.ProteinProteinInteractionRel;
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


    public static Iterator<Path> getAllSimpleInteractionPathsBetweenProteins(ProteinNode proteinSource,
            ProteinNode proteinTarget,
            int maxDepth){
               
        
        PathFinder<Path> finder = GraphAlgoFactory.allSimplePaths(
                Traversal.expanderForTypes(new ProteinProteinInteractionRel(null), Direction.INCOMING), maxDepth);
        
        Iterator<Path> paths = finder.findAllPaths(proteinSource.getNode(), proteinTarget.getNode()).iterator();
        
        return paths;
        
    }

}
