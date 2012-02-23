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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.openide.util.Exceptions;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class RemoveRepetitionsFromPPICircuits {
    
    public static void main(String[] args){
        
        if(args.length != 3){
            System.out.println("This program expects the following parameters:\n" +
                    "1. Input file name including the PPI circuits\n" +
                    "2. Output results file name\n" +
                    "3. Output proteins frequency file name\n"); 
        }else{
            try {
                
                BufferedReader buff = new BufferedReader(new FileReader(new File(args[0])));
                String line = null;
                
                BufferedWriter outBuff = new BufferedWriter(new FileWriter(new File(args[1])));
                BufferedWriter protBuff = new BufferedWriter(new FileWriter(new File(args[2])));
                
                LinkedList<LinkedList<String>> list = new LinkedList<LinkedList<String>>();
                HashMap<String, Integer> proteinCounter = new HashMap<String, Integer>();
                
                System.out.println("Reading file...");
                int repetitionsCounter = 0;
                
                while((line = buff.readLine()) != null){
                    String[] columns = line.trim().split(" ");
                    boolean repeated = false;
                    for (LinkedList<String> linkedList : list) {
                        if( (linkedList.get(0).equals(columns[0]) && linkedList.get(1).equals(columns[1]) && linkedList.get(2).equals(columns[2]) ) ||
                            (linkedList.get(0).equals(columns[1]) && linkedList.get(1).equals(columns[2]) && linkedList.get(2).equals(columns[0]) ) ||   
                            (linkedList.get(0).equals(columns[2]) && linkedList.get(1).equals(columns[0]) && linkedList.get(2).equals(columns[1]) ) ){
                            
                            repeated = true;
                            repetitionsCounter++;
                            break;
                            
                        }
                    }
                    if(!repeated){
                        LinkedList<String> lList = new LinkedList<String>();
                        lList.add(columns[0]);
                        lList.add(columns[1]);
                        lList.add(columns[2]);
                        list.add(lList);
                    }
                }               
                
                buff.close();
                
                System.out.println("Done!");
                
                System.out.println(repetitionsCounter + " repetitions were found...");
                
                System.out.println("Writing results!");
                
                for (LinkedList<String> linkedList : list) {
                    Iterator<String> it = linkedList.iterator();  
                    String tempSt = "";                    
                    while(it.hasNext()){
                        String protSt = it.next();
                        tempSt = tempSt + protSt + "\t";
                        
                        //updating prot counters
                        Integer counter = proteinCounter.get(protSt);
                        if(counter == null){
                            proteinCounter.put(protSt, 1);
                        }else{
                            proteinCounter.put(protSt, (counter + 1));
                        }                        
                    }
                    tempSt = tempSt.substring(0,tempSt.length() - 1) + "\n";
                    outBuff.write(tempSt);
                }
                
                outBuff.close();
                
                Set<String> proteinSet = proteinCounter.keySet();
                for (String protSt : proteinSet) {
                    protBuff.write(protSt + "\t" + proteinCounter.get(protSt) + "\n");
                }                
                
                protBuff.close();
                
                System.out.println("done! ;)");
                
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }       
            
            
            
        }
        
    }
    
}
