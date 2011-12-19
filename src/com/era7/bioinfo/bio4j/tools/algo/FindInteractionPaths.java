/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools.algo;

import com.era7.bioinfo.bio4jmodel.nodes.ProteinNode;
import com.era7.bioinfo.bio4jmodel.util.Bio4jManager;
import com.era7.bioinfo.bio4jmodel.util.NodeRetriever;
import com.era7.lib.bioinfo.bioinfoutil.Executable;
import java.util.ArrayList;
import java.util.List;

/**
 *
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
    
    public static void main(String args[]){
        
        if(args.length != 5){
            System.out.println("This program expects four parameters:\n"
                    + "1. Source protein accession \n"
                    + "2. Target protein accession \n"
                    + "3. Max depth \n"
                    + "4. Bio4j DB folder \n"
                    + "5. Results out file name (txt)");
        }else{
            
            Bio4jManager manager = new Bio4jManager(args[3]);
            NodeRetriever nodeRetriever = new NodeRetriever(manager);
            int maxDepth = Integer.parseInt(args[2]);
            
            String prot1St = args[0];
            String prot2St = args[1];
            ProteinNode sourceProt = nodeRetriever.getProteinNodeByAccession(prot1St);
            ProteinNode targetProt = nodeRetriever.getProteinNodeByAccession(prot2St);
            
            System.out.println("Looking for possible paths from " + prot1St + " to " + prot2St + " ...");
            
            List<List<String>> results = InteractionsPathFinder.findShortestInteractionPath(sourceProt, 
                    targetProt, 
                    maxDepth, 
                    5);
            
            System.out.println("donee!");
            
            for (List<String> list : results) {
                System.out.println("Path found: ");
                String tempSt = "";
                for (String string : list) {
                    tempSt += string + " --> ";
                }
                System.out.println(tempSt.substring(0, tempSt.length() -5));
            }
            
            System.out.println("closing manager...");
            manager.shutDown();
            
            System.out.println("done!");
                    
                    
        }
        
        
        
    }
    
}
