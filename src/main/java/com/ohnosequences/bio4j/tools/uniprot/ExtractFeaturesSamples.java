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
package com.ohnosequences.bio4j.tools.uniprot;

import java.io.*;
import java.util.*;

import org.jdom2.Element;

import com.ohnosequences.util.Executable;
import com.ohnosequences.xml.api.model.XMLElement;

/**
 * 
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class ExtractFeaturesSamples implements Executable{
    
    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("This program expects the following parameters: \n"
                    + "1. Input XML file \n");
        } else {
            BufferedWriter outBuff = null;
            
            try {
                
                File inFile = new File(args[0]);
                
                Map<String, String> featureTypesMap = new HashMap<String, String>();
                
                outBuff = new BufferedWriter(new FileWriter(new File("features.xml")));
                outBuff.write("<features>\n");
                
                BufferedReader reader = new BufferedReader(new FileReader(inFile));
                String line = null;
                StringBuilder entryStBuilder = new StringBuilder();
                
                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("<entry")) {

                        while (!line.trim().startsWith("</entry>")) {
                            entryStBuilder.append(line);
                            line = reader.readLine();
                        }
                        //linea final del organism
                        entryStBuilder.append(line);
                        //System.out.println("organismStBuilder.toString() = " + organismStBuilder.toString());
                        XMLElement entryXMLElem = new XMLElement(entryStBuilder.toString());
                        entryStBuilder.delete(0, entryStBuilder.length());

                        List<Element> featuresList = entryXMLElem.asJDomElement().getChildren("feature");
                        for (Element element : featuresList) {
                            if(featureTypesMap.get(element.getAttributeValue("type")) == null){
                                XMLElement feature = new XMLElement(element);
                                System.out.println("feature = " + feature);
                                featureTypesMap.put(element.getAttributeValue("type"), feature.toString());
                            }
                        }

                    }
                }
                Set<String> keys = featureTypesMap.keySet();
                for (String key : keys) {
                    outBuff.write(featureTypesMap.get(key));
                }
                
                outBuff.write("</features>\n");
                outBuff.close();
                System.out.println("Features file created successfully!");
                
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    outBuff.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }
}
