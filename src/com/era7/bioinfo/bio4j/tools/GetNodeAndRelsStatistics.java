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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.MapUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GetNodeAndRelsStatistics {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("This program expects one parameter: \n"
                    + "1. Bio4j DB folder\n"
                    + "2. Config Properties file (neo4j.properties)");
        } else {

            Bio4jManager manager = null;

            try {

                BufferedWriter logBuff = new BufferedWriter(new FileWriter(new File("GetNodeAndRelsStatistics.log")));

                manager = new Bio4jManager(args[0], MapUtil.load(new File(args[1])), true);

                GraphDatabaseService graphService = manager.getGraphService();

                HashMap<String, Integer> nodesMap = new HashMap<String, Integer>();
                HashMap<String, Integer> relationshipsMap = new HashMap<String, Integer>();

                int nCounter = 0;
                int rCounter = 0;

                long startTime = new Date().getTime();


                for (Node node : graphService.getAllNodes()) {

                    String nodeType = "unknown";

                    try {
                        nodeType = String.valueOf(node.getProperty(BasicEntity.NODE_TYPE_PROPERTY));
                    } catch (org.neo4j.graphdb.NotFoundException ex) {

                        System.out.println("Node with no node type specified... :(");
                        System.out.println("Its properties are:\n");
                        Iterator<String> iterator = node.getPropertyKeys().iterator();
                        while (iterator.hasNext()) {
                            System.out.println(iterator.next());
                        }
                    }

                    Integer nodeCounter = nodesMap.get(nodeType);

                    if (nodeCounter == null) {

                        nodeCounter = new Integer(1);
                        nodesMap.put(nodeType, nodeCounter);

                    } else {

                        nodesMap.put(nodeType, (nodeCounter + 1));

                    }

                    for (Relationship rel : node.getRelationships(Direction.OUTGOING)) {

                        String relType = rel.getType().name();
                        Integer relCounter = relationshipsMap.get(relType);

                        if (relCounter == null) {

                            relCounter = new Integer(1);
                            relationshipsMap.put(relType, relCounter);

                        } else {

                            relationshipsMap.put(relType, (relCounter + 1));

                        }

                        rCounter++;

                        if (rCounter % 100000 == 0) {
                            System.out.println(nCounter + " nodes and " + rCounter + " rels analyzed! (current nodeType = " + nodeType + ")");
                            logBuff.write(nCounter + " " + rCounter + "\n");
                            logBuff.flush();
                        }

                        if (rCounter % 100000 == 0) {

                            long currentTime = new Date().getTime();
                            long difference = currentTime - startTime;
                            double seconds = difference / 1000.0;

                            System.out.println(nCounter + " nodes and " + rCounter + " rels analyzed!");
                            logBuff.write(nCounter + " " + rCounter + "\n");
                            System.out.println(nCounter + " nodes " + rCounter + " rels. " + seconds + " seconds...");
                            logBuff.write(nCounter + "\t" + rCounter + "\t" + seconds + "\n");
                            System.out.println(nCounter + " nodes " + seconds + " seconds...");
                            logBuff.write(nCounter + "\t" + seconds + "\n");
                            System.out.println(rCounter + " relationships " + seconds + " seconds...");
                            logBuff.write(rCounter + "\t" + seconds + "\n");
                            logBuff.flush();

                            startTime = currentTime;
                        }

                    }



                    nCounter++;

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

                logBuff.close();



            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            } finally {

                manager.shutDown();
            }


        }
    }
}
