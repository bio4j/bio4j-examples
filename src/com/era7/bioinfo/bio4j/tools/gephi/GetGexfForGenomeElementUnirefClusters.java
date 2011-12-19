/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools.gephi;

import com.era7.bioinfo.bio4jmodel.nodes.OrganismNode;
import com.era7.bioinfo.bio4jmodel.nodes.ProteinNode;
import com.era7.bioinfo.bio4jmodel.nodes.refseq.GenomeElementNode;
import com.era7.bioinfo.bio4jmodel.relationships.protein.ProteinGenomeElementRel;
import com.era7.bioinfo.bio4jmodel.util.Bio4jManager;
import com.era7.bioinfo.bio4jmodel.util.NodeRetriever;
import com.era7.lib.bioinfo.bioinfoutil.Executable;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom.Element;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GetGexfForGenomeElementUnirefClusters implements Executable {

    public static double MAX_NODE_SIZE = 200.0;
    public static double MIN_NODE_SIZE = 5.0;
    public static double DEFAULT_PROTEIN_SIZE = 5.0;
    public static double DEFAULT_ORGANISM_SIZE = 15.0;
    public static double DEFAULT_GENOME_ELEMENT_SIZE = 10.0;
    public static int edgesIdCounter = 0;
    public static String UNIREF_CLUSTER_RELATIONSHIP = "UnirefClusterRelationship";
    public static String PROTEIN_ORGANISM_RELATIONSHIP = "ProteinOrganismRelationship";
    public static String PROTEIN_GENOME_ELEMENT_RELATIONSHIP = "ProteinGenomeElementRelationship";

    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String[] args) {
        if (args.length != 4) {

            System.out.println("This program expects four parameters:\n"
                    + "1. Bio4j DB folder\n"
                    + "2. Genome Element version\n"
                    + "3. Uniref Cluster (one of these values: 50,90,100\n"
                    + "4. Output GEXF file name");


        } else {

            String dbFolder = args[0];
            String genomeElementVersion = args[1];
            int unirefClusterNumber = Integer.parseInt(args[2]);
            File outFile = new File(args[3]);

            System.out.println("Creating Bio4j manager...");

            Bio4jManager manager = new Bio4jManager(dbFolder);
            NodeRetriever nodeRetriever = new NodeRetriever(manager);

            System.out.println("Getting genome element node...");

            GenomeElementNode genomeElementNode = nodeRetriever.getGenomeElementByVersion(genomeElementVersion);
            //OrganismNode organismNode = nodeRetriever.getOrganismByNCBITaxonomyId(organismNCBIID);

            System.out.println("Genome element version: " + genomeElementNode.getVersion());

            Map<String, ProteinNode> proteinMap = new HashMap<String, ProteinNode>();
            Map<String, OrganismNode> organismMap = new HashMap<String, OrganismNode>();
            Map<String, GenomeElementNode> genomeElementMap = new HashMap<String, GenomeElementNode>();
            //Map<String, List<ProteinNode>> unirefClusters = new HashMap<String, List<ProteinNode>>();

            Map<String, ProteinNode> representantsMap = new HashMap<String, ProteinNode>();
            Map<String, Integer> representantsCounterMap = new HashMap<String, Integer>();

            //----Here are stored the counters for the number of protein/organism/... links-----
            //Map<String, Integer> clusterProteinsLinkedMap = new HashMap<String, Integer>();
            Map<String, Integer> organismProteinsCounterMap = new HashMap<String, Integer>();
            Map<String, Integer> genomeElementProteinsCounterMap = new HashMap<String, Integer>();


            Map<String, Set<String>> unirefClusterOrganismLinks = new HashMap<String, Set<String>>(); //keys are representant accessions
            Map<String, Set<String>> unirefClusterGenomeElementsLinks = new HashMap<String, Set<String>>(); //keys are representant accessions

            //------node colors------------
            VizColorXML proteinColor = new VizColorXML(241, 134, 21, 1);
            VizColorXML organismColor = new VizColorXML(21, 155, 241, 1);
            VizColorXML genomeElemColor = new VizColorXML(34, 177, 76, 1);


            System.out.println("Starting to write the output file...");

            try {

                BufferedWriter outBuff = new BufferedWriter(new FileWriter(outFile));

                outBuff.write("<?xml version=\"1.0\" encoding=\"UTF8\"?>" + "\n");
                outBuff.write("<" + GexfXML.TAG_NAME + ">\n");
                outBuff.write("<" + GraphXML.TAG_NAME + " defaultedgetype=\"directed\" >\n");

                //node attributes
                AttributesXML nodeAttributesXML = getNodeAttributes();
                outBuff.write(nodeAttributesXML.toString() + "\n");

                //edge attributes
                AttributesXML edgeAttributesXML = getEdgeAttributes();
                outBuff.write(edgeAttributesXML.toString() + "\n");

                StringBuilder nodesXMLStBuilder = new StringBuilder("<nodes>\n");
                StringBuilder edgesXMLStBuilder = new StringBuilder("<edges>\n");

                System.out.println("Getting genome element and proteins related...");


                Iterator<Relationship> relIterator = genomeElementNode.getNode().getRelationships(Direction.INCOMING, new ProteinGenomeElementRel(null)).iterator();

                while (relIterator.hasNext()) {

                    ProteinNode proteinNode = new ProteinNode(relIterator.next().getStartNode());

                    List<ProteinNode> unirefCluster = null;
                    ProteinNode clusterRepresentant = null;

                    if (unirefClusterNumber == 50) {

                        unirefCluster = proteinNode.getUniref50ClusterThisProteinBelongsTo();

                        //---looking for representant---
                        for (ProteinNode tempProt : unirefCluster) {
                            if (tempProt.isUniref50Representant()) {
                                clusterRepresentant = tempProt;
                                break;
                            }
                        }

                    } else if (unirefClusterNumber == 90) {

                        unirefCluster = proteinNode.getUniref90ClusterThisProteinBelongsTo();

                        //---looking for representant---
                        for (ProteinNode tempProt : unirefCluster) {
                            if (tempProt.isUniref90Representant()) {
                                clusterRepresentant = tempProt;
                                break;
                            }
                        }

                    } else if (unirefClusterNumber == 100) {

                        unirefCluster = proteinNode.getUniref100ClusterThisProteinBelongsTo();

                        //---looking for representant---
                        for (ProteinNode tempProt : unirefCluster) {
                            if (tempProt.isUniref100Representant()) {
                                clusterRepresentant = tempProt;
                                break;
                            }
                        }
                    }

                    //----updating representants counter and map----
                    representantsCounterMap.put(clusterRepresentant.getAccession(), unirefCluster.size());
                    representantsMap.put(clusterRepresentant.getAccession(), clusterRepresentant);

                    Set<String> organismLinks = unirefClusterOrganismLinks.get(clusterRepresentant.getAccession());
                    if (organismLinks == null) {
                        organismLinks = new HashSet<String>();
                        unirefClusterOrganismLinks.put(clusterRepresentant.getAccession(), organismLinks);
                    }
                    Set<String> genomeElemLinks = unirefClusterGenomeElementsLinks.get(clusterRepresentant.getAccession());
                    if (genomeElemLinks == null) {
                        genomeElemLinks = new HashSet<String>();
                        unirefClusterGenomeElementsLinks.put(clusterRepresentant.getAccession(), genomeElemLinks);
                    }

                    System.out.println("it has " + unirefCluster.size() + " members");

                    //----looping through the Uniref cluster searching for different organisms and Genome Elements----
                    for (int i = 0; i < unirefCluster.size(); i++) {

                        ProteinNode proteinNode1 = unirefCluster.get(i);

                        OrganismNode protOrganism = proteinNode1.getOrganism();

                        //adding protein organism to the map
                        organismMap.put(protOrganism.getNcbiTaxonomyId(), protOrganism);

                        //storing link between uniref cluster and organism
                        organismLinks.add(protOrganism.getNcbiTaxonomyId());

                        //adding genome elements linked to the protein to their respective map
                        for (GenomeElementNode tempGenomeElemNode : proteinNode1.getGenomeElements()) {
                            //storing link between uniref cluster and genome element                            
                            genomeElemLinks.add(tempGenomeElemNode.getVersion());
                            genomeElementMap.put(tempGenomeElemNode.getVersion(), tempGenomeElemNode);
                        }


                    }

                }

                System.out.println("Looping through representants....");
                for (String protKey : representantsMap.keySet()) {

                    //ProteinNode protNode = proteinMap.get(protKey);
                    ProteinNode protNode = representantsMap.get(protKey);
                    NodeXML protNodeXML = new NodeXML();
                    protNodeXML.setId(protNode.getAccession());
                    //protNodeXML.setLabel(protNode.getFullName());
                    protNodeXML.setLabel(protNode.getAccession());
                    protNodeXML.setColor(new VizColorXML((Element) proteinColor.asJDomElement().clone()));

                    //------protein node size--------
                    VizSizeXML protSize = new VizSizeXML();
                    //Integer linksCount = clusterProteinsLinkedMap.get(protKey);
                    Integer linksCount = representantsCounterMap.get(protKey);
                    if (linksCount < MIN_NODE_SIZE) {
                        protSize.setValue(MIN_NODE_SIZE);
                    } else if (linksCount >= MAX_NODE_SIZE) {
                        protSize.setValue(MAX_NODE_SIZE);
                    } else {
                        protSize.setValue(linksCount);
                    }
                    protNodeXML.setSize(protSize);
                    //---------------------------

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

                    Set<String> organismSet = unirefClusterOrganismLinks.get(protKey);

                    for (String organismKey : organismSet) {

                        Integer orgCounter = organismProteinsCounterMap.get(organismKey);
                        if (orgCounter == null) {
                            organismProteinsCounterMap.put(organismKey, 1);
                        } else {
                            organismProteinsCounterMap.put(organismKey, orgCounter + 1);
                        }

                        //-------------------------------------------------------------
                        //----------------Protein-Organism-edge-----------------------
                        EdgeXML edge = new EdgeXML();
                        edge.setId(String.valueOf(edgesIdCounter++));
                        edge.setSource(protNode.getAccession());
                        edge.setTarget(organismKey);
                        edge.setType(EdgeXML.DIRECTED_TYPE);

                        AttValuesXML edgeAttValuesXML = new AttValuesXML();

                        AttValueXML edgeIDAttValueXML = new AttValueXML();
                        edgeIDAttValueXML.setFor(0);
                        edgeIDAttValueXML.setValue("" + edgesIdCounter);
                        edgeAttValuesXML.addAttValue(edgeIDAttValueXML);
                        AttValueXML edgeTypeAttValueXML = new AttValueXML();
                        edgeTypeAttValueXML.setFor(1);
                        edgeTypeAttValueXML.setValue(PROTEIN_ORGANISM_RELATIONSHIP);
                        edgeAttValuesXML.addAttValue(edgeTypeAttValueXML);

                        edge.setAttvalues(edgeAttValuesXML);

                        edgesXMLStBuilder.append((edge.toString() + "\n"));
                        //-------------------------------------------------------------
                        //-------------------------------------------------------------
                    }

                    Set<String> genomeElemsSet = unirefClusterGenomeElementsLinks.get(protKey);
                    for (String genomeElemKey : genomeElemsSet) {

                        Integer genomElemCounter = genomeElementProteinsCounterMap.get(genomeElemKey);
                        if (genomElemCounter == null) {
                            genomeElementProteinsCounterMap.put(genomeElemKey, 1);
                        } else {
                            genomeElementProteinsCounterMap.put(genomeElemKey, genomElemCounter + 1);
                        }

                        //-------------------------------------------------------------
                        //----------------Protein-GenomeElement-edge-----------------------
                        EdgeXML edge = new EdgeXML();
                        edge.setId(String.valueOf(edgesIdCounter++));
                        edge.setSource(protNode.getAccession());
                        edge.setTarget(genomeElemKey);
                        edge.setType(EdgeXML.DIRECTED_TYPE);

                        AttValuesXML edgeAttValuesXML = new AttValuesXML();

                        AttValueXML edgeIDAttValueXML = new AttValueXML();
                        edgeIDAttValueXML.setFor(0);
                        edgeIDAttValueXML.setValue("" + edgesIdCounter);
                        edgeAttValuesXML.addAttValue(edgeIDAttValueXML);
                        AttValueXML edgeTypeAttValueXML = new AttValueXML();
                        edgeTypeAttValueXML.setFor(1);
                        edgeTypeAttValueXML.setValue(PROTEIN_GENOME_ELEMENT_RELATIONSHIP);
                        edgeAttValuesXML.addAttValue(edgeTypeAttValueXML);

                        edge.setAttvalues(edgeAttValuesXML);

                        edgesXMLStBuilder.append((edge.toString() + "\n"));
                        //-------------------------------------------------------------
                        //-------------------------------------------------------------
                    }



                }

                //-----------ORGANISM NODES----------------------------

                System.out.println("Looping through organisms....");
                for (String organismKey : organismMap.keySet()) {

                    OrganismNode tempOrganismNode = organismMap.get(organismKey);
                    NodeXML organismNodeXML = new NodeXML();
                    organismNodeXML.setId(tempOrganismNode.getNcbiTaxonomyId());
                    organismNodeXML.setLabel(tempOrganismNode.getScientificName());
                    organismNodeXML.setColor(new VizColorXML((Element) organismColor.asJDomElement().clone()));

                    //------organism node size--------
                    VizSizeXML organismSize = new VizSizeXML();

                    Integer linksCount = organismProteinsCounterMap.get(organismKey);
                    if (linksCount < MIN_NODE_SIZE) {
                        organismSize.setValue(MIN_NODE_SIZE);
                    } else if (linksCount >= MAX_NODE_SIZE) {
                        organismSize.setValue(MAX_NODE_SIZE);
                    } else {
                        organismSize.setValue(linksCount);
                    }
                    organismNodeXML.setSize(organismSize);
                    //---------------------------

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

                System.out.println("Looping through genome elements....");
                for (String genomeElemKey : genomeElementMap.keySet()) {

                    GenomeElementNode genomeElemNode = genomeElementMap.get(genomeElemKey);
                    NodeXML genomeElemNodeXML = new NodeXML();
                    genomeElemNodeXML.setId(genomeElemNode.getVersion());
                    genomeElemNodeXML.setLabel(genomeElemNode.getVersion());
                    genomeElemNodeXML.setColor(new VizColorXML((Element) genomeElemColor.asJDomElement().clone()));

                    //------genome element node size--------
                    VizSizeXML genomElemSize = new VizSizeXML();
                    Integer linksCount = genomeElementProteinsCounterMap.get(genomeElemKey);
                    if (linksCount < MIN_NODE_SIZE) {
                        genomElemSize.setValue(MIN_NODE_SIZE);
                    } else if (linksCount >= MAX_NODE_SIZE) {
                        genomElemSize.setValue(MAX_NODE_SIZE);
                    } else {
                        genomElemSize.setValue(linksCount);
                    }
                    genomeElemNodeXML.setSize(genomElemSize);
                    //---------------------------

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

        }
    }

    private static AttributesXML getNodeAttributes() {

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

        return nodeAttributesXML;

    }

    private static AttributesXML getEdgeAttributes() {

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

        return edgeAttributesXML;
    }
}
