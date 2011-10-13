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
package com.era7.bioinfo.bio4j.tools;

import com.era7.bioinfo.bio4jmodel.util.Bio4jManager;
import com.era7.bioinfo.bioinfoneo4j.BasicEntity;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GetNodeAndRelsStatistics {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("This program expects one parameter: \n"
                    + "1. Bio4j DB folder\n");
        } else {

            Bio4jManager manager = null;

            try {

                manager = new Bio4jManager(args[0]);

                GraphDatabaseService graphService = manager.getGraphService();

                HashMap<String, Integer[]> nodesMap = new HashMap<String, Integer[]>();
                HashMap<String, Integer[]> relationshipsMap = new HashMap<String, Integer[]>();
                
                int counter = 0;

                for (Node node : graphService.getAllNodes()) {

                    String nodeType = String.valueOf(node.getProperty(BasicEntity.NODE_TYPE_PROPERTY));
                    Integer[] nodeCounter = nodesMap.get(nodeType);

                    if (nodeCounter == null) {

                        Integer[] miniArray = new Integer[1];
                        miniArray[0] = 1;
                        nodesMap.put(nodeType, miniArray);

                    } else {

                        nodeCounter[0] = nodeCounter[0] + 1;

                    }

                    for (Relationship rel : node.getRelationships(Direction.OUTGOING)) {

                        String relType = rel.getType().name();
                        Integer[] relCounter = relationshipsMap.get(relType);

                        if (relCounter == null) {

                            Integer[] miniArray = new Integer[1];
                            miniArray[0] = 1;
                            relationshipsMap.put(relType, miniArray);

                        } else {

                            relCounter[0] = relCounter[0] + 1;

                        }

                    }
                    
                    if(counter % 100000 == 0){
                        System.out.println(counter + " nodes analyzed!");
                    }
                    
                }

                System.out.println("Writing nodes file.....");
                BufferedWriter outBuff = new BufferedWriter(new FileWriter(new File("Bio4jNodeStatistics.txt")));                
                outBuff.write("NODE_NAME\tABSOLUTE_VALUE\n");
                for (String nodeKey : nodesMap.keySet()) {
                    outBuff.write(nodeKey + "\t" + nodesMap.get(nodeKey) + "\n");
                }                
                outBuff.close();
                
                System.out.println("Writing relationships file.....");
                outBuff = new BufferedWriter(new FileWriter(new File("Bio4jRelStatistics.txt")));    
                outBuff.write("RELATIONSHIP_NAME\tABSOLUTE_VALUE\n");
                for (String relKey : relationshipsMap.keySet()) {
                    outBuff.write(relKey + "\t" + relationshipsMap.get(relKey) + "\n");
                }
                outBuff.close();

                System.out.println("Done! :)");
                
                

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                manager.shutDown();
            }


        }
    }
}
