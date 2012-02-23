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
package com.era7.bioinfo.bio4j.tools.gephi;

import com.era7.bioinfo.bio4jmodel.nodes.GoTermNode;
import com.era7.bioinfo.bio4jmodel.relationships.go.MainGoRel;
import com.era7.bioinfo.bio4jmodel.relationships.go.IsAGoRel;
import com.era7.bioinfo.bio4jmodel.util.Bio4jManager;
import com.era7.lib.bioinfoxml.gexf.AttValueXML;
import com.era7.lib.bioinfoxml.gexf.AttValuesXML;
import com.era7.lib.bioinfoxml.gexf.AttributeXML;
import com.era7.lib.bioinfoxml.gexf.AttributesXML;
import com.era7.lib.bioinfoxml.gexf.EdgeXML;
import com.era7.lib.bioinfoxml.gexf.GexfXML;
import com.era7.lib.bioinfoxml.gexf.GraphXML;
import com.era7.lib.bioinfoxml.gexf.NodeXML;
import com.era7.lib.bioinfoxml.gexf.viz.VizColorXML;
import com.era7.lib.bioinfoxml.gexf.viz.VizSizeXML;
import com.era7.lib.era7xmlapi.model.XMLElementException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GenerateGexfWholeGo {

    public static int edgesIdCounter = 0;
    public static int nodesCounter = 0;
    public static IsAGoRel isAGorel = new IsAGoRel(null);
    public static int maxTerms = 2000000;
    public static ArrayList<String> alreadyVisitedNodes = new ArrayList<String>();
    public static VizColorXML bioColor;
    public static VizColorXML molColor;
    public static VizColorXML cellColor;
    public static Bio4jManager manager = null;
    public static int MAX_NODE_SIZE = 50;
    public static int MIN_NODE_SIZE = 5;
    public static int Y_LEVEL_FACTOR = 100;
    public static int X_LEVEL_FACTOR = 10;
    public static String DEFAULT_END = "2011-01-31";
    public static String DEFAULT_START = "2011-01-01";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("This program expects two parameters: \n"
                    + "1. Bio4j DB folder"
                    + "2. Output gexf filename\n");
        } else {

            BufferedWriter outBuff = null;
            try {

                bioColor = new VizColorXML();
                bioColor.setR(241);
                bioColor.setG(134);
                bioColor.setB(21);
                bioColor.setA(255);
                molColor = new VizColorXML();
                molColor.setR(21);
                molColor.setG(155);
                molColor.setB(241);
                molColor.setA(243);
                cellColor = new VizColorXML();
                cellColor.setR(34);
                cellColor.setG(177);
                cellColor.setB(76);
                cellColor.setA(243);

                outBuff = new BufferedWriter(new FileWriter(new File(args[1])));

                outBuff.write("<?xml version=\"1.0\" encoding=\"UTF8\"?>" + "\n");
                outBuff.write("<" + GexfXML.TAG_NAME + ">\n");
                outBuff.write("<" + GraphXML.TAG_NAME + " defaultedgetype=\"directed\" >\n");


                //node attributes
                AttributesXML attributesXML = new AttributesXML();
                attributesXML.setClass(AttributesXML.NODE_CLASS);
                AttributeXML idAttributeXML = new AttributeXML();
                idAttributeXML.setId("0");
                idAttributeXML.setTitle("ID");
                idAttributeXML.setType("string");
                attributesXML.addAttribute(idAttributeXML);
                AttributeXML nameAttributeXML = new AttributeXML();
                nameAttributeXML.setId("1");
                nameAttributeXML.setTitle("Name");
                nameAttributeXML.setType("string");
                attributesXML.addAttribute(nameAttributeXML);
                AttributeXML aspectAttributeXML = new AttributeXML();
                aspectAttributeXML.setId("2");
                aspectAttributeXML.setTitle("Aspect");
                aspectAttributeXML.setType("string");
                attributesXML.addAttribute(aspectAttributeXML);

                outBuff.write(attributesXML.toString() + "\n");

                StringBuilder nodesXMLStBuilder = new StringBuilder("<nodes>\n");
                StringBuilder edgesXMLStBuilder = new StringBuilder("<edges>\n");

                try {
                    System.out.println("creating neo4j manager...");
                    manager = new Bio4jManager(args[0]);

                    Iterator<Relationship> iterator = manager.getReferenceNode().getRelationships(new MainGoRel(null), Direction.OUTGOING).iterator();
                    while (iterator.hasNext()) {
                        GoTermNode mainGoTermNode = new GoTermNode(iterator.next().getEndNode());
                        System.out.println("getting ontology for " + mainGoTermNode.getName());
                        getGoDescendants(mainGoTermNode, nodesXMLStBuilder, edgesXMLStBuilder, 1, 0);  
                    }

                } catch (Exception e) {
                    Logger.getLogger(GenerateGexfWholeGo.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    manager.shutDown();
                }

                outBuff.write(nodesXMLStBuilder.toString() + "</nodes>\n");
                outBuff.write(edgesXMLStBuilder.toString() + "</edges>\n");

                outBuff.write("</" + GraphXML.TAG_NAME + ">\n");
                outBuff.write("</" + GexfXML.TAG_NAME + ">\n");
                outBuff.close();

                System.out.println("done!! :)");


            } catch (IOException ex) {
                Logger.getLogger(GenerateGexfWholeGo.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private static void getGoDescendants(GoTermNode parent,
            StringBuilder nodes,
            StringBuilder edges, 
            int currentLevel,
            int xLevelFactor) throws XMLElementException {

        nodesCounter++;

        NodeXML nodeXML = new NodeXML();
        nodeXML.setId(parent.getId());
        nodeXML.setLabel(parent.getName());

        if (parent.getNamespace().equals(GoTermNode.BIOLOGICAL_PROCESS_NAMESPACE)) {
            nodeXML.setColor(new VizColorXML((Element) bioColor.getRoot().clone()));
        } else if (parent.getNamespace().equals(GoTermNode.MOLECULAR_FUNCTION_NAMESPACE)) {
            nodeXML.setColor(new VizColorXML((Element) molColor.getRoot().clone()));
        } else if (parent.getNamespace().equals(GoTermNode.CELLULAR_COMPONENT_NAMESPACE)) {
            nodeXML.setColor(new VizColorXML((Element) cellColor.getRoot().clone()));
        }

        alreadyVisitedNodes.add(parent.getId());

        AttValuesXML attValuesXML = new AttValuesXML();

        AttValueXML goIdAttValueXML = new AttValueXML();
        goIdAttValueXML.setFor(0);
        goIdAttValueXML.setValue(parent.getId());
        attValuesXML.addAttValue(goIdAttValueXML);

        AttValueXML goNameAttValueXML = new AttValueXML();
        goNameAttValueXML.setFor(1);
        goNameAttValueXML.setValue(parent.getName());
        attValuesXML.addAttValue(goNameAttValueXML);

        AttValueXML aspectAttValue = new AttValueXML();
        aspectAttValue.setFor(2);
        aspectAttValue.setValue(parent.getNamespace());
        attValuesXML.addAttValue(aspectAttValue);

        nodeXML.setAttvalues(attValuesXML);

        int subNodesCounter = 1;

        Iterator<Relationship> iterator = parent.getNode().getRelationships(isAGorel, Direction.INCOMING).iterator();
        int xPosition = 0;
        while (iterator.hasNext()) {

            isAGorel = new IsAGoRel(iterator.next());
            
            EdgeXML edge = new EdgeXML();
            edge.setId(String.valueOf(edgesIdCounter++));
            edge.setTarget(parent.getId());
            GoTermNode childGo = new GoTermNode(isAGorel.getRelationship().getStartNode());
            edge.setSource(childGo.getId());
            edge.setType(EdgeXML.DIRECTED_TYPE);
            //edge.setEnd(DEFAULT_END);
//            String tempSt = String.valueOf(currentLevel);
//            if (currentLevel < 10) {
//                tempSt = "0" + tempSt;
//            }
//            edge.setStart("2011-01-" + tempSt);
            //edges.addEdge(edge);

            edges.append((edge.toString() + "\n"));                       

            if (!alreadyVisitedNodes.contains(childGo.getId())) {
                getGoDescendants(childGo, nodes, edges, currentLevel + 1, xPosition * X_LEVEL_FACTOR);
            }

            subNodesCounter++;
            xPosition++;

        }

        //setting node size proportional to number of children
//        VizSizeXML goSize = new VizSizeXML();
//        goSize.setValue(subNodesCounter);
//        if (subNodesCounter > MAX_NODE_SIZE) {
//            goSize.setValue(MAX_NODE_SIZE);
//        }
        //setting node size proportional to label length
        VizSizeXML goSize = new VizSizeXML();
        goSize.setValue(nodeXML.getLabel().length());
//        if (subNodesCounter > MAX_NODE_SIZE) {
//            goSize.setValue(MAX_NODE_SIZE);
//        }
//        if(currentLevel < MIN_NODE_SIZE){
//            goSize.setValue(MIN_NODE_SIZE);
//        }else{
//            goSize.setValue(currentLevel);
//        }
        goSize.setValue(MIN_NODE_SIZE);
        nodeXML.setSize(goSize);

//        VizPositionXML goPosition = new VizPositionXML();
//        goPosition.setY(currentLevel * Y_LEVEL_FACTOR);
//        goPosition.setX(Math.round(Math.random() * 100));
//        goPosition.setZ(Math.round(Math.random() * 100));
//        nodeXML.setPosition(goPosition);


//        SpellsXML spells = new SpellsXML();
//        SpellXML spell = new SpellXML();
//        spell.setEnd(DEFAULT_END);
//        String tempSt = String.valueOf(currentLevel);
//        if (currentLevel < 10) {
//            tempSt = "0" + tempSt;
//        }
//        spell.setStart("2011-01-" + tempSt);
//        spells.addSpell(spell);
//        nodeXML.setSpells(spells);

//        nodeXML.setEnd(DEFAULT_END);
//        String tempSt = String.valueOf(currentLevel);
//        if (currentLevel < 10) {
//            tempSt = "0" + tempSt;
//        }
//        nodeXML.setStart("2011-01-" + tempSt);

        //nodes.addNode(nodeXML);
        nodes.append((nodeXML.toString() + "\n"));

    }
}
