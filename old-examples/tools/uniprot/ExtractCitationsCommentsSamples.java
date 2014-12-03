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

import com.ohnosequences.util.Executable;
import com.ohnosequences.xml.api.model.XMLElement;

import java.io.*;
import java.util.*;

import org.jdom2.Element;

/**
 * 
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class ExtractCitationsCommentsSamples implements Executable{

    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String[] args){

        if (args.length != 1) {
            System.out.println("This program expects the following parameters: \n"
                    + "1. Input XML file \n");
        } else {
            BufferedWriter citationsOutBuff = null;
            
            try {
                
                File inFile = new File(args[0]);
                
                Map<String, String> citationsTypesMap = new HashMap<String, String>();
                Map<String, String> commentsTypesMap = new HashMap<String, String>();
                
                citationsOutBuff = new BufferedWriter(new FileWriter(new File("citations.xml")));
                BufferedWriter commentsOutBuff = new BufferedWriter(new FileWriter(new File("comments.xml")));
                
                citationsOutBuff.write("<citations>\n");
                commentsOutBuff.write("<comments>\n");
                
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

                        List<Element> referenceList = entryXMLElem.asJDomElement().getChildren("reference");
                        for (Element reference : referenceList) {
                            List<Element> citationsList = reference.getChildren("citation");
                            for (Element citation : citationsList) {
                                if (citationsTypesMap.get(citation.getAttributeValue("type")) == null) {
                                    XMLElement citationXML = new XMLElement(citation);
                                    System.out.println("citation = " + citationXML);
                                    citationsTypesMap.put(citation.getAttributeValue("type"), citationXML.toString());
                                }
                            }
                        }
                        List<Element> commentsList = entryXMLElem.asJDomElement().getChildren("comment");
                        for (Element comment : commentsList) {
                            if (commentsTypesMap.get(comment.getAttributeValue("type")) == null) {
                                XMLElement commentXML = new XMLElement(comment);
                                System.out.println("comment = " + commentXML);
                                commentsTypesMap.put(comment.getAttributeValue("type"), commentXML.toString());
                            }
                        }

                    }
                }
                Set<String> keys = citationsTypesMap.keySet();
                
                for (String key : keys) {
                    citationsOutBuff.write(citationsTypesMap.get(key));
                }
                citationsOutBuff.write("</citations>\n");
                citationsOutBuff.close();
                
                System.out.println("Citations file created successfully!");
                Set<String> keysComments = commentsTypesMap.keySet();
                for (String key : keysComments) {
                    commentsOutBuff.write(commentsTypesMap.get(key));
                }
                
                commentsOutBuff.write("</comments>\n");
                commentsOutBuff.close();
                
                System.out.println("Comments file created successfully!");
                
            } catch (Exception ex) {
                e.xprintStackTrace();
            } finally {
                try {
                    citationsOutBuff.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }
}
