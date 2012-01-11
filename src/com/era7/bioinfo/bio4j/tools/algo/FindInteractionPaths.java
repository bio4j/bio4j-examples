/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools.algo;

import com.era7.bioinfo.bio4jmodel.nodes.ProteinNode;
import com.era7.bioinfo.bio4jmodel.util.Bio4jManager;
import com.era7.bioinfo.bio4jmodel.util.NodeRetriever;
import com.era7.lib.bioinfo.bioinfoutil.Executable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PropertyContainer;
import org.openide.util.Exceptions;

/**
 * This program finds interaction paths between two proteins with a set of customizable parameters
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class FindInteractionPaths implements Executable {

    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String args[]) {

        if (args.length != 6) {
            System.out.println("This program expects four parameters:\n"
                    + "1. Source protein accession \n"
                    + "2. Target protein accession \n"
                    + "3. Max depth \n"
                    + "4. Bio4j DB folder \n"
                    + "5. Max number of results \n"
                    + "6. Results out file name (txt)");
        } else {
            BufferedWriter outBuff = null;

            try {

                Bio4jManager manager = new Bio4jManager(args[3]);
                NodeRetriever nodeRetriever = new NodeRetriever(manager);

                int maxDepth = Integer.parseInt(args[2]);
                int maxResults = Integer.parseInt(args[4]);

                String prot1St = args[0];
                String prot2St = args[1];

                ProteinNode sourceProt = nodeRetriever.getProteinNodeByAccession(prot1St);
                ProteinNode targetProt = nodeRetriever.getProteinNodeByAccession(prot2St);

                System.out.println("Looking for shortest paths from " + prot1St + " to " + prot2St + " ...");
                List<List<String>> results = InteractionsPathFinder.findShortestInteractionPath(sourceProt,
                        targetProt,
                        maxDepth,
                        maxResults);

                System.out.println("donee!");

                for (List<String> list : results) {
                    System.out.println("Path found: ");
                    String tempSt = "";
                    for (String string : list) {
                        tempSt += string + " --> ";
                    }
                    System.out.println(tempSt.substring(0, tempSt.length() - 5));
                }

                System.out.println("Looking now for all simple paths between these two nodes...");

                Iterator<Path> pathsIterator = InteractionsPathFinder.getAllSimpleInteractionPathsBetweenProteins(sourceProt,
                        targetProt,
                        maxDepth);

                outBuff = new BufferedWriter(new FileWriter(new File(args[5])));
                
                for (int i = 0; i < maxResults; i++) {
                    if (pathsIterator.hasNext()) {                        
                        String lineSt = "";
                        
                        Path currentPath = pathsIterator.next();       
                        Iterator<PropertyContainer> pathIterator = currentPath.iterator();
                        boolean isNode = true;
                        while (pathIterator.hasNext()) {
                            
                            if (isNode) {
                                lineSt += (new ProteinNode((Node) pathIterator.next()).getAccession() + ",");
                            } else {
                                pathIterator.next();
                            }
                            isNode = !isNode;
                        }
                        outBuff.write(lineSt.substring(0,lineSt.length() - 1) + "\n");
                    }
                }

                outBuff.close();

                System.out.println("closing manager...");
                manager.shutDown();

                System.out.println("done!");

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
