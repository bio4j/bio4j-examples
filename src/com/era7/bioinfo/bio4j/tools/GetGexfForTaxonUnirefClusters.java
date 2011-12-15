/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools;

import com.era7.bioinfo.bio4jmodel.nodes.OrganismNode;
import com.era7.bioinfo.bio4jmodel.nodes.ProteinNode;
import com.era7.bioinfo.bio4jmodel.nodes.TaxonNode;
import com.era7.bioinfo.bio4jmodel.nodes.refseq.GenomeElementNode;
import com.era7.bioinfo.bio4jmodel.relationships.protein.ProteinOrganismRel;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GetGexfForTaxonUnirefClusters implements Executable {

    public static double MAX_NODE_SIZE = 250.0;
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
                    + "2. Organism scientific name (the one specified in Uniprot)\n"
                    + "3. Uniref Cluster (one of these values: 50,90,100\n"
                    + "4. Output GEXF file name");


        } else {

            String dbFolder = args[0];
            String organismScientificName = args[1];
            int unirefClusterNumber = Integer.parseInt(args[2]);
            File outFile = new File(args[3]);

            System.out.println("Creating Bio4j manager...");

            Bio4jManager manager = new Bio4jManager(dbFolder);
            NodeRetriever nodeRetriever = new NodeRetriever(manager);

            System.out.println("Getting organism node...");

            OrganismNode organismNode = nodeRetriever.getOrganismByScientificName(organismScientificName);
            //OrganismNode organismNode = nodeRetriever.getOrganismByNCBITaxonomyId(organismNCBIID);

            System.out.println("Organism scientific name: " + organismNode.getScientificName());

            Map<String, ProteinNode> proteinMap = new HashMap<String, ProteinNode>();
            Map<String, OrganismNode> organismMap = new HashMap<String, OrganismNode>();
            Map<String, GenomeElementNode> genomeElementMap = new HashMap<String, GenomeElementNode>();
            Map<String, List<ProteinNode>> unirefClusters = new HashMap<String, List<ProteinNode>>();

            //----Here are stored the counters for the number of protein/organism/... links-----
            Map<String, Integer> clusterProteinsLinkedMap = new HashMap<String, Integer>();
            Map<String, Integer> organismProteinsLinkedMap = new HashMap<String, Integer>();
            Map<String, Integer> genomeElementProteinsLinkedMap = new HashMap<String, Integer>();


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

                System.out.println("Getting taxon and descendants...");

                //getProteinConnectedToTaxonPlusDescendants(proteinMap, taxonNode, organismMap, genomeElementMap);
                getProteinsConnectedToOrganism(proteinMap, organismNode, organismMap);

                System.out.println("Looking for uniref clusters...");

                for (String proteinKey : proteinMap.keySet()) {

                    ProteinNode proteinNode = proteinMap.get(proteinKey);

                    List<ProteinNode> unirefClusterList = new LinkedList<ProteinNode>();
                    unirefClusters.put(proteinKey, unirefClusterList);

                    System.out.println("Getting cluster for protein:" + proteinKey);

                    List<ProteinNode> unirefCluster = null;
                    ProteinNode clusterRepresentant = null;

                    if (unirefClusterNumber == 50) {

                        unirefCluster = proteinNode.getUniref50ClusterThisProteinBelongsTo();

                        for (ProteinNode tempProt : unirefCluster) {
                            if (tempProt.isUniref50Representant()) {
                                clusterRepresentant = tempProt;
                                break;
                            }
                        }

                    } else if (unirefClusterNumber == 90) {

                        unirefCluster = proteinNode.getUniref90ClusterThisProteinBelongsTo();

                        for (ProteinNode tempProt : unirefCluster) {
                            if (tempProt.isUniref90Representant()) {
                                clusterRepresentant = tempProt;
                                break;
                            }
                        }

                    } else if (unirefClusterNumber == 100) {

                        unirefCluster = proteinNode.getUniref100ClusterThisProteinBelongsTo();

                        for (ProteinNode tempProt : unirefCluster) {
                            if (tempProt.isUniref100Representant()) {
                                clusterRepresentant = tempProt;
                                break;
                            }
                        }

                    }

                    //---counter for number of proteins linked to protein-----------
                    clusterProteinsLinkedMap.put(clusterRepresentant.getAccession(), unirefCluster.size());

                    System.out.println("it has " + unirefCluster.size() + " members");

                    for (int i = 0; i < unirefCluster.size(); i++) {

                        ProteinNode proteinNode1 = unirefCluster.get(i);

                        unirefClusterList.add(proteinNode1);
                        
                        //adding protein organism to the map
                        organismMap.put(proteinNode1.getOrganism().getNcbiTaxonomyId(), proteinNode1.getOrganism());
                        //adding genome elements linked to the protein to their respective map
                        for (GenomeElementNode genomeElementNode : proteinNode1.getGenomeElements()) {
                            genomeElementMap.put(genomeElementNode.getVersion(), genomeElementNode);
                        }

                        //----if this protein is not the representant, we link it to the cluster representant------
                        if (!clusterRepresentant.getAccession().equals(proteinNode1.getAccession())) {

                            clusterProteinsLinkedMap.put(proteinNode1.getAccession(), 1);

                            EdgeXML edge = new EdgeXML();
                            edge.setId(String.valueOf(edgesIdCounter++));
                            edge.setSource(proteinNode1.getAccession());
                            edge.setTarget(clusterRepresentant.getAccession());
                            edge.setType(EdgeXML.DIRECTED_TYPE);

                            AttValuesXML edgeAttValuesXML = new AttValuesXML();

                            AttValueXML edgeIDAttValueXML = new AttValueXML();
                            edgeIDAttValueXML.setFor(0);
                            edgeIDAttValueXML.setValue("" + edgesIdCounter);
                            edgeAttValuesXML.addAttValue(edgeIDAttValueXML);
                            AttValueXML edgeTypeAttValueXML = new AttValueXML();
                            edgeTypeAttValueXML.setFor(1);
                            edgeTypeAttValueXML.setValue(UNIREF_CLUSTER_RELATIONSHIP);
                            edgeAttValuesXML.addAttValue(edgeTypeAttValueXML);

                            edge.setAttvalues(edgeAttValuesXML);

                            edgesXMLStBuilder.append((edge.toString() + "\n"));
                        }

                    }
                }

                System.out.println("Adding proteins from clusters...");

                //--------------adding proteins from associated clusters to proteinMap----------
                for (String tempProtKey : unirefClusters.keySet()) {
                    List<ProteinNode> list = unirefClusters.get(tempProtKey);
                    for (ProteinNode tempProtNode : list) {
                        proteinMap.put(tempProtNode.getAccession(), tempProtNode);
                    }
                }
                //-------------------------------------------------------------------------------


                VizColorXML proteinColor = new VizColorXML(241, 134, 21, 1);
                VizColorXML organismColor = new VizColorXML(21, 155, 241, 1);
                VizColorXML genomeElemColor = new VizColorXML(34, 177, 76, 1);


                //-----------PROTEIN NODES----------------------------

                //------loop through all proteins involved--------------
                for (String protKey : proteinMap.keySet()) {

                    ProteinNode protNode = proteinMap.get(protKey);
                    NodeXML protNodeXML = new NodeXML();
                    protNodeXML.setId(protNode.getAccession());
                    protNodeXML.setLabel(protNode.getFullName());
                    protNodeXML.setColor(new VizColorXML((Element) proteinColor.asJDomElement().clone()));

                    OrganismNode orgNode = protNode.getOrganism();
                    Integer orgCounter = organismProteinsLinkedMap.get(orgNode.getNcbiTaxonomyId());
                    if (orgCounter == null) {
                        organismProteinsLinkedMap.put(orgNode.getNcbiTaxonomyId(), 1);
                    } else {
                        organismProteinsLinkedMap.put(orgNode.getNcbiTaxonomyId(), orgCounter + 1);
                    }

                    //------protein node size--------
                    VizSizeXML protSize = new VizSizeXML();
                    Integer linksCount = clusterProteinsLinkedMap.get(protKey);
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

                    //-------------------------------------------------------------
                    //----------------Protein-Organism-edge-----------------------
                    EdgeXML edge = new EdgeXML();
                    edge.setId(String.valueOf(edgesIdCounter++));
                    edge.setSource(protNode.getAccession());
                    edge.setTarget(orgNode.getNcbiTaxonomyId());
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

                    for (GenomeElementNode genomeElementNode : protNode.getGenomeElements()) {

                        Integer genomElemCounter = genomeElementProteinsLinkedMap.get(genomeElementNode.getVersion());
                        if (genomElemCounter == null) {
                            genomeElementProteinsLinkedMap.put(genomeElementNode.getVersion(), 1);
                        } else {
                            genomeElementProteinsLinkedMap.put(genomeElementNode.getVersion(), genomElemCounter + 1);
                        }

                        //-------------------------------------------------------------
                        //----------------Protein-GenomeElement-edge-----------------------
                        edge = new EdgeXML();
                        edge.setId(String.valueOf(edgesIdCounter++));
                        edge.setSource(protNode.getAccession());
                        edge.setTarget(genomeElementNode.getVersion());
                        edge.setType(EdgeXML.DIRECTED_TYPE);

                        edgeAttValuesXML = new AttValuesXML();

                        edgeIDAttValueXML = new AttValueXML();
                        edgeIDAttValueXML.setFor(0);
                        edgeIDAttValueXML.setValue("" + edgesIdCounter);
                        edgeAttValuesXML.addAttValue(edgeIDAttValueXML);
                        edgeTypeAttValueXML = new AttValueXML();
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

                for (String organismKey : organismMap.keySet()) {

                    OrganismNode tempOrganismNode = organismMap.get(organismKey);
                    NodeXML organismNodeXML = new NodeXML();
                    organismNodeXML.setId(tempOrganismNode.getNcbiTaxonomyId());
                    organismNodeXML.setLabel(tempOrganismNode.getScientificName());
                    organismNodeXML.setColor(new VizColorXML((Element) organismColor.asJDomElement().clone()));

                    //------organism node size--------
                    VizSizeXML organismSize = new VizSizeXML();
                    Integer linksCount = organismProteinsLinkedMap.get(organismKey);
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

                for (String genomeElemKey : genomeElementMap.keySet()) {

                    GenomeElementNode genomeElemNode = genomeElementMap.get(genomeElemKey);
                    NodeXML genomeElemNodeXML = new NodeXML();
                    genomeElemNodeXML.setId(genomeElemNode.getVersion());
                    genomeElemNodeXML.setLabel(genomeElemNode.getVersion());
                    genomeElemNodeXML.setColor(new VizColorXML((Element) genomeElemColor.asJDomElement().clone()));

                    //------genome element node size--------
                    VizSizeXML genomElemSize = new VizSizeXML();
                    Integer linksCount = genomeElementProteinsLinkedMap.get(genomeElemKey);
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


            System.out.println("done!");

            System.out.println("Shutting down manager..");

            manager.shutDown();

            System.out.println("Cool! :)");


        }


    }

    private static void getProteinConnectedToTaxonPlusDescendants(Map<String, ProteinNode> proteinMap,
            TaxonNode taxon,
            Map<String, OrganismNode> organismMap) {

        System.out.println("Current taxon: " + taxon.getName());

        for (OrganismNode organismNode : taxon.getOrganisms()) {
            getProteinsConnectedToOrganism(proteinMap, organismNode, organismMap);
        }

        for (TaxonNode tempTaxonNode : taxon.getChildren()) {
            getProteinConnectedToTaxonPlusDescendants(proteinMap, tempTaxonNode, organismMap);
        }
    }

    private static void getProteinsConnectedToOrganism(Map<String, ProteinNode> proteinMap,
            OrganismNode organismNode,
            Map<String, OrganismNode> organismMap) {

        organismMap.put(organismNode.getNcbiTaxonomyId(), organismNode);

        System.out.println("current organism: " + organismNode.getScientificName());

        int protCounter = 0;

        Iterator<Relationship> relIterator = organismNode.getNode().getRelationships(Direction.INCOMING, new ProteinOrganismRel(null)).iterator();
        while (relIterator.hasNext()) {

            protCounter++;
            ProteinNode proteinNode = new ProteinNode(relIterator.next().getStartNode());
            proteinMap.put(proteinNode.getAccession(), proteinNode);

            if (protCounter % 10 == 0) {
                System.out.println("protCounter = " + protCounter);
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
