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

import com.era7.bioinfo.bio4j.neo4j.model.nodes.ProteinNode;
import com.era7.bioinfo.bio4j.neo4j.model.relationships.uniref.UniRef100MemberRel;
import com.era7.bioinfo.bio4j.neo4j.model.util.Bio4jManager;
import com.era7.lib.bioinfo.bioinfoutil.Executable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.MapUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class FindBiggestUniref100Clusters implements Executable{
    
    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    
    public static void main(String[] args){

        if (args.length != 4) {
            System.out.println("This program expects the following parameters:\n"
                    + "1. Bio4j DB folder\n"
                    + "2. Bio4j DB config parameters file (neo4j.properties)\n"
                    + "3. Output results file name (txt)\n"
                    + "4. Minimum cluster size (integer)\n");
        } else {
            BufferedWriter outBuff = null;
            try {
                
                File outFile = new File(args[2]);
                
                int minimumClusterLength = Integer.parseInt(args[3]);
                
                outBuff = new BufferedWriter(new FileWriter(outFile));
                Bio4jManager manager = new Bio4jManager(args[0], MapUtil.load(new File(args[1])), true);
                
                List<ProteinCounter> list = new LinkedList<ProteinCounter>();
                int protCounter = 0;
                
                //--looping through all proteins--
                Iterator<Node> iterator = manager.getNodeTypeIndex().get(Bio4jManager.NODE_TYPE_INDEX_NAME, ProteinNode.NODE_TYPE).iterator();
                UniRef100MemberRel uniRef100MemberRel = new UniRef100MemberRel(null);
                while (iterator.hasNext()) {
                    ProteinNode protein = new ProteinNode(iterator.next());
                    if (protein.isUniref100Representant()) {
                        int clusterLength = 1;

                        Iterator<Relationship> relIterator = protein.getNode().getRelationships(Direction.OUTGOING, uniRef100MemberRel).iterator();
                        while (relIterator.hasNext()) {
                            relIterator.next();
                            clusterLength++;
                        }

                        if(clusterLength >= minimumClusterLength){
                            list.add(new ProteinCounter(protein.getAccession(), clusterLength));
                        }                    
                    }

                    protCounter++;

                    if (protCounter % 10000 == 0) {
                        System.out.println(protCounter + " proteins analyzed... " + " list.size() = " + list.size());
                    }
                }
                manager.shutDown();
                System.out.println("Done!");
                System.out.println("Sorting values...");
                Collections.sort(list);
                
                System.out.println("Writing now the size of all clusters!");
                for (int i = list.size()-1; i >= 0 ; i--) {
                    ProteinCounter proteinCounter = list.get(i);
                    outBuff.write(proteinCounter.protein + "\t" + proteinCounter.counter + "\n");
                }
                
                outBuff.close();
                System.out.println("Done ;)");
                
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    outBuff.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

    }
}

class ProteinCounter implements Comparable<ProteinCounter> {
    
    public String protein;
    public int counter;
    
    public ProteinCounter(String prot, int c){
        protein = prot;
        counter = c;
    }

    public int compareTo(ProteinCounter o) {
        if(o.counter > this.counter){
            return -1;
        }else if(o.counter < this.counter){
            return 1;
        }else{
            return 0;
        }
    }
}
