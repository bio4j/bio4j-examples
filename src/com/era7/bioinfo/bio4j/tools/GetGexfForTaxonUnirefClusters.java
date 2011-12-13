/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools;

import com.era7.bioinfo.bio4jmodel.nodes.OrganismNode;
import com.era7.bioinfo.bio4jmodel.nodes.ProteinNode;
import com.era7.bioinfo.bio4jmodel.nodes.TaxonNode;
import com.era7.bioinfo.bio4jmodel.nodes.ncbi.NCBITaxonNode;
import com.era7.bioinfo.bio4jmodel.nodes.refseq.GenomeElementNode;
import com.era7.bioinfo.bio4jmodel.relationships.ncbi.NCBITaxonRel;
import com.era7.bioinfo.bio4jmodel.relationships.protein.ProteinOrganismRel;
import com.era7.bioinfo.bio4jmodel.util.Bio4jManager;
import com.era7.bioinfo.bio4jmodel.util.NodeRetriever;
import com.era7.lib.bioinfoxml.gexf.AttValueXML;
import com.era7.lib.bioinfoxml.gexf.AttValuesXML;
import com.era7.lib.bioinfoxml.gexf.AttributeXML;
import com.era7.lib.bioinfoxml.gexf.AttributesXML;
import com.era7.lib.bioinfoxml.gexf.GexfXML;
import com.era7.lib.bioinfoxml.gexf.GraphXML;
import com.era7.lib.bioinfoxml.gexf.NodeXML;
import com.era7.lib.bioinfoxml.gexf.viz.VizColorXML;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jdom.Element;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GetGexfForTaxonUnirefClusters {

    public static int MAX_NODE_SIZE = 50;
    public static int MIN_NODE_SIZE = 5;

    public static void main(String[] args) {


        if (args.length != 4) {

            System.out.println("This program expects four parameters:\n"
                    + "1. Bio4j DB folder\n"
                    + "2. Organism NCBI Taxonomy ID\n"
                    + "3. Uniref Cluster (one of these values: 50,90,100\n"
                    + "4. Output GEXF file name");


        } else {

            String dbFolder = args[0];
            String organismNCBIID = args[1];
            int unirefCluster = Integer.parseInt(args[2]);
            File outFile = new File(args[3]);

            System.out.println("Creating Bio4j manager...");

            Bio4jManager manager = new Bio4jManager(dbFolder);
            NodeRetriever nodeRetriever = new NodeRetriever(manager);

            System.out.println("Getting organism node...");

            OrganismNode organismNode = nodeRetriever.getOrganismByNCBITaxonomyId(organismNCBIID);

            System.out.println("Organism scientific name: " + organismNode.getScientificName());

            Map<String, ProteinNode> proteinMap = new HashMap<String, ProteinNode>();
            Map<String, OrganismNode> organismMap = new HashMap<String, OrganismNode>();
            Map<String, GenomeElementNode> genomeElementMap = new HashMap<String, GenomeElementNode>();

            System.out.println("Getting taxon and descendants...");

            //getProteinConnectedToTaxonPlusDescendants(proteinMap, taxonNode, organismMap, genomeElementMap);
            getProteinsConnectedToOrganism(proteinMap, organismNode, organismMap, genomeElementMap);

            System.out.println("Looking for uniref clusters...");

            for (String proteinKey : proteinMap.keySet()) {

                ProteinNode proteinNode = proteinMap.get(proteinKey);

                if (unirefCluster == 50) {
                    for (ProteinNode proteinNode1 : proteinNode.getUniref50ClusterThisProteinBelongsTo()) {
                        proteinMap.put(proteinNode1.getAccession(), proteinNode1);
                        organismMap.put(proteinNode1.getOrganism().getScientificName(), proteinNode1.getOrganism());
                        for (GenomeElementNode genomeElementNode : proteinNode1.getGenomeElements()) {
                            genomeElementMap.put(genomeElementNode.getVersion(), genomeElementNode);
                        }
                    }
                } else if (unirefCluster == 90) {

                    for (ProteinNode proteinNode1 : proteinNode.getUniref90ClusterThisProteinBelongsTo()) {
                        proteinMap.put(proteinNode1.getAccession(), proteinNode1);
                        organismMap.put(proteinNode1.getOrganism().getScientificName(), proteinNode1.getOrganism());
                        for (GenomeElementNode genomeElementNode : proteinNode1.getGenomeElements()) {
                            genomeElementMap.put(genomeElementNode.getVersion(), genomeElementNode);
                        }
                    }

                } else if (unirefCluster == 100) {

                    for (ProteinNode proteinNode1 : proteinNode.getUniref100ClusterThisProteinBelongsTo()) {
                        proteinMap.put(proteinNode1.getAccession(), proteinNode1);
                        organismMap.put(proteinNode1.getOrganism().getScientificName(), proteinNode1.getOrganism());
                        for (GenomeElementNode genomeElementNode : proteinNode1.getGenomeElements()) {
                            genomeElementMap.put(genomeElementNode.getVersion(), genomeElementNode);
                        }
                    }

                }
            }


            VizColorXML proteinColor = new VizColorXML(241, 134, 21, 255);
            VizColorXML organismColor = new VizColorXML(21, 155, 241, 243);
            VizColorXML genomeElemColor = new VizColorXML(34, 177, 76, 243);


            System.out.println("Starting to write the output file...");

            try {

                BufferedWriter outBuff = new BufferedWriter(new FileWriter(outFile));

                outBuff.write("<?xml version=\"1.0\" encoding=\"UTF8\"?>" + "\n");
                outBuff.write("<" + GexfXML.TAG_NAME + ">\n");
                outBuff.write("<" + GraphXML.TAG_NAME + " defaultedgetype=\"directed\" >\n");

                //node attributes
                AttributesXML nodeAttributesXML = new AttributesXML();
                nodeAttributesXML.setClass(AttributesXML.NODE_CLASS);
                AttributeXML idAttributeXML = new AttributeXML();
                idAttributeXML.setId("0");
                idAttributeXML.setTitle("ID");
                idAttributeXML.setType("string");
                nodeAttributesXML.addAttribute(idAttributeXML);
                AttributeXML nameAttributeXML = new AttributeXML();
                nameAttributeXML.setId("1");
                nameAttributeXML.setTitle("Name");
                nameAttributeXML.setType("string");
                nodeAttributesXML.addAttribute(nameAttributeXML);
                AttributeXML nodeTypeAttributeXML = new AttributeXML();
                nodeTypeAttributeXML.setId("2");
                nodeTypeAttributeXML.setTitle("NodeType");
                nodeTypeAttributeXML.setType("string");
                nodeAttributesXML.addAttribute(nodeTypeAttributeXML);

                outBuff.write(nodeAttributesXML.toString() + "\n");

                //edge attributes
                AttributesXML edgeAttributesXML = new AttributesXML();
                edgeAttributesXML.setClass(AttributesXML.NODE_CLASS);
                AttributeXML edgeIdAttributeXML = new AttributeXML();
                edgeIdAttributeXML.setId("0");
                edgeIdAttributeXML.setTitle("ID");
                edgeIdAttributeXML.setType("string");
                edgeAttributesXML.addAttribute(edgeIdAttributeXML);
                AttributeXML edgeTypeAttributeXML = new AttributeXML();
                edgeTypeAttributeXML.setId("1");
                edgeTypeAttributeXML.setTitle("EdgeType");
                edgeTypeAttributeXML.setType("string");
                edgeAttributesXML.addAttribute(edgeTypeAttributeXML);

                outBuff.write(edgeAttributesXML.toString() + "\n");


                StringBuilder nodesXMLStBuilder = new StringBuilder("<nodes>\n");
                StringBuilder edgesXMLStBuilder = new StringBuilder("<edges>\n");


                //-----------PROTEIN NODES----------------------------

                for (String protKey : proteinMap.keySet()) {

                    ProteinNode protNode = proteinMap.get(protKey);
                    NodeXML protNodeXML = new NodeXML();
                    protNodeXML.setId(protNode.getAccession());
                    protNodeXML.setLabel(protNode.getFullName());
                    protNodeXML.setColor(new VizColorXML((Element) proteinColor.asJDomElement().clone()));

                    AttValuesXML protAttValuesXML = new AttValuesXML();

                    AttValueXML protAccessionAttValueXML = new AttValueXML();
                    protAccessionAttValueXML.setFor(0);
                    protAccessionAttValueXML.setValue(protNode.getAccession());
                    protAttValuesXML.addAttValue(protAccessionAttValueXML);

                    AttValueXML nameAttValueXML = new AttValueXML();
                    nameAttValueXML.setFor(1);
                    nameAttValueXML.setValue(protNode.getFullName());
                    protAttValuesXML.addAttValue(nameAttValueXML);

                    AttValueXML nodeTypeAttValueXML = new AttValueXML();
                    nodeTypeAttValueXML.setFor(2);
                    nodeTypeAttValueXML.setValue("Protein");
                    protAttValuesXML.addAttValue(nodeTypeAttValueXML);

                    protNodeXML.setAttvalues(protAttValuesXML);

                    nodesXMLStBuilder.append((protNodeXML.toString() + "\n"));

                }

                //-----------ORGANISM NODES----------------------------

                for (String organismKey : organismMap.keySet()) {

                    OrganismNode tempOrganismNode = organismMap.get(organismKey);
                    NodeXML organismNodeXML = new NodeXML();
                    organismNodeXML.setId(tempOrganismNode.getNcbiTaxonomyId());
                    organismNodeXML.setLabel(tempOrganismNode.getScientificName());
                    organismNodeXML.setColor(new VizColorXML((Element) organismColor.asJDomElement().clone()));

                    AttValuesXML organismAttValuesXML = new AttValuesXML();

                    AttValueXML organismIdAttValueXML = new AttValueXML();
                    organismIdAttValueXML.setFor(0);
                    organismIdAttValueXML.setValue(tempOrganismNode.getNcbiTaxonomyId());
                    organismAttValuesXML.addAttValue(organismIdAttValueXML);

                    AttValueXML nameAttValueXML = new AttValueXML();
                    nameAttValueXML.setFor(1);
                    nameAttValueXML.setValue(tempOrganismNode.getScientificName());
                    organismAttValuesXML.addAttValue(nameAttValueXML);

                    AttValueXML nodeTypeAttValueXML = new AttValueXML();
                    nodeTypeAttValueXML.setFor(2);
                    nodeTypeAttValueXML.setValue("Organism");
                    organismAttValuesXML.addAttValue(nodeTypeAttValueXML);

                    organismNodeXML.setAttvalues(organismAttValuesXML);

                    nodesXMLStBuilder.append((organismNodeXML.toString() + "\n"));

                }

                //-----------GENOME ELEMENT NODES----------------------------

                for (String genomeElemKey : genomeElementMap.keySet()) {

                    GenomeElementNode genomeElemNode = genomeElementMap.get(genomeElemKey);
                    NodeXML genomeElemNodeXML = new NodeXML();
                    genomeElemNodeXML.setId(genomeElemNode.getVersion());
                    genomeElemNodeXML.setLabel(genomeElemNode.getVersion());
                    genomeElemNodeXML.setColor(new VizColorXML((Element) organismColor.asJDomElement().clone()));

                    AttValuesXML genomeElemAttValuesXML = new AttValuesXML();

                    AttValueXML genomeElemVersionAttValueXML = new AttValueXML();
                    genomeElemVersionAttValueXML.setFor(0);
                    genomeElemVersionAttValueXML.setValue(genomeElemNode.getVersion());
                    genomeElemAttValuesXML.addAttValue(genomeElemVersionAttValueXML);

                    AttValueXML nameAttValueXML = new AttValueXML();
                    nameAttValueXML.setFor(1);
                    nameAttValueXML.setValue(genomeElemNode.getVersion());
                    genomeElemAttValuesXML.addAttValue(nameAttValueXML);

                    AttValueXML nodeTypeAttValueXML = new AttValueXML();
                    nodeTypeAttValueXML.setFor(2);
                    nodeTypeAttValueXML.setValue("GenomeElement");
                    genomeElemAttValuesXML.addAttValue(nodeTypeAttValueXML);

                    genomeElemNodeXML.setAttvalues(genomeElemAttValuesXML);

                    nodesXMLStBuilder.append((genomeElemNodeXML.toString() + "\n"));

                }


                outBuff.write(nodesXMLStBuilder.toString() + "</nodes>\n");
                outBuff.write(edgesXMLStBuilder.toString() + "</edges>\n");

                outBuff.write("</" + GraphXML.TAG_NAME + ">\n");
                outBuff.write("</" + GexfXML.TAG_NAME + ">\n");
                outBuff.close();



            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println("done!");

            System.out.println("Shutting down manager..");

            manager.shutDown();

            System.out.println("Cool! :)");


        }



    }

    private static void getProteinConnectedToTaxonPlusDescendants(Map<String, ProteinNode> proteinMap,
            TaxonNode taxon,
            Map<String, OrganismNode> organismMap,
            Map<String, GenomeElementNode> genomeElemsMap) {

        System.out.println("Current taxon: " + taxon.getName());

        for (OrganismNode organismNode : taxon.getOrganisms()) {
            getProteinsConnectedToOrganism(proteinMap, organismNode, organismMap, genomeElemsMap);
        }

        for (TaxonNode tempTaxonNode : taxon.getChildren()) {
            getProteinConnectedToTaxonPlusDescendants(proteinMap, tempTaxonNode, organismMap, genomeElemsMap);
        }
    }

    private static void getProteinsConnectedToOrganism(Map<String, ProteinNode> proteinMap,
            OrganismNode organismNode,
            Map<String, OrganismNode> organismMap,
            Map<String, GenomeElementNode> genomeElemsMap) {

        organismMap.put(organismNode.getScientificName(), organismNode);

        System.out.println("current organism: " + organismNode.getScientificName());

        int protCounter = 0;

        Iterator<Relationship> relIterator = organismNode.getNode().getRelationships(Direction.INCOMING, new ProteinOrganismRel(null)).iterator();
        while (relIterator.hasNext()) {
            protCounter++;
            ProteinNode proteinNode = new ProteinNode(relIterator.next().getStartNode());
            proteinMap.put(proteinNode.getAccession(), proteinNode);

            for (GenomeElementNode genomeElementNode : proteinNode.getGenomeElements()) {
                genomeElemsMap.put(genomeElementNode.getVersion(), genomeElementNode);
            }

            if (protCounter % 10 == 0) {
                System.out.println("protCounter = " + protCounter);
            }
        }

    }
}
