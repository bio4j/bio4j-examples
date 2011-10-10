/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.era7.bioinfo.bioinfoaws.util.CredentialsRetriever;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class CreateSimpleDBModel {

    public static final String BIO4J_DOMAIN = "bio4j";
    public static final String NODES_FILENAME = "NodesBio4j.txt";
    public static final String RELATIONSHIPS_FILENAME = "RelationshipsBio4j.txt";
    public static final String NODE_INDEXES_FILENAME = "NodeIndexesBio4j.txt";
    public static final String RELATIONSHIP_INDEXES_FILENAME = "RelationshipIndexesBio4j.txt";
    public static final String EMPTY_CHARACTER = "-";
    public static final String ITEM_TYPE_ATTRIBUTE = "ITEM_TYPE";
    public static final String ITEM_TYPE_NODE = "node";
    public static final String ITEM_TYPE_RELATIONSHIP = "relationship";
    public static final String ITEM_TYPE_NODE_INDEX = "node_index";
    public static final String ITEM_TYPE_RELATIONSHIP_INDEX = "relationship_index";

    public static void main(String[] args) {

        try {

            AmazonSimpleDBClient simpleDBClient = new AmazonSimpleDBClient(CredentialsRetriever.getBasicAWSCredentialsFromOurAMI());

            System.out.println("Creating domain...");

            //creating domain
            simpleDBClient.createDomain(new CreateDomainRequest(BIO4J_DOMAIN));

            System.out.println("done!");

            //-----------------reading nodes...-----------------
            File nodesFiles = new File(NODES_FILENAME);
            BufferedReader reader = new BufferedReader(new FileReader(nodesFiles));
            String line = null;
            //header line
            String[] header = reader.readLine().split("\t");
            String nodeName = header[0];
            String desc = header[1];
            String inRels = header[2];
            String outRels = header[3];
            String javadocUrl = header[4];

            System.out.println("reading nodes file....");

            while ((line = reader.readLine()) != null) {

                String[] columns = line.split("\t");

                System.out.println("line = " + line);

                PutAttributesRequest putAttributesRequest = new PutAttributesRequest();
                putAttributesRequest.setDomainName(BIO4J_DOMAIN);
                putAttributesRequest.setItemName(columns[0]);

                List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();

                ReplaceableAttribute nameAtt = new ReplaceableAttribute();
                nameAtt.setName(nodeName);
                nameAtt.setValue(columns[0]);
                attributes.add(nameAtt);

                ReplaceableAttribute descAtt = new ReplaceableAttribute();
                descAtt.setName(desc);
                descAtt.setValue(columns[1]);
                attributes.add(descAtt);

                String[] inRelsArray = columns[2].split(",");

                for (String currentInRel : inRelsArray) {
                    ReplaceableAttribute inRelsAtt = new ReplaceableAttribute();
                    inRelsAtt.setName(inRels);
                    inRelsAtt.setValue(currentInRel);
                    attributes.add(inRelsAtt);
                }

                String[] outRelsArray = columns[3].split(",");

                for (String currentOutRel : outRelsArray) {
                    ReplaceableAttribute outRelsAtt = new ReplaceableAttribute();
                    outRelsAtt.setName(outRels);
                    outRelsAtt.setValue(currentOutRel);
                    attributes.add(outRelsAtt);
                }
                
                ReplaceableAttribute javadocAtt = new ReplaceableAttribute();
                javadocAtt.setName(javadocUrl);
                javadocAtt.setValue(columns[4]);
                attributes.add(javadocAtt);
                
                ReplaceableAttribute itemTypeAtt = new ReplaceableAttribute();
                itemTypeAtt.setName(ITEM_TYPE_ATTRIBUTE);
                itemTypeAtt.setValue(ITEM_TYPE_NODE);
                attributes.add(itemTypeAtt);

                putAttributesRequest.setAttributes(attributes);

                simpleDBClient.putAttributes(putAttributesRequest);

            }
            reader.close();

            System.out.println("done!");


            //-----------reading rels.... ----------------
            File relsFiles = new File(RELATIONSHIPS_FILENAME);
            reader = new BufferedReader(new FileReader(relsFiles));
            //header line
            header = reader.readLine().split("\t");
            String relName = header[0];
            desc = header[1];
            String startNodes = header[2];
            String endNodes = header[3];
            javadocUrl = header[4];

            System.out.println("reading relationships file...");

            while ((line = reader.readLine()) != null) {

                String[] columns = line.split("\t");

                PutAttributesRequest putAttributesRequest = new PutAttributesRequest();
                putAttributesRequest.setDomainName(BIO4J_DOMAIN);
                putAttributesRequest.setItemName(columns[0]);

                List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();

                ReplaceableAttribute nameAtt = new ReplaceableAttribute();
                nameAtt.setName(relName);
                nameAtt.setValue(columns[0]);
                attributes.add(nameAtt);

                ReplaceableAttribute descAtt = new ReplaceableAttribute();
                descAtt.setName(desc);
                descAtt.setValue(columns[1]);
                attributes.add(descAtt);

                String[] startNodesArray = columns[2].split(",");

                for (String currentStartNode : startNodesArray) {
                    ReplaceableAttribute startNodesAtt = new ReplaceableAttribute();
                    startNodesAtt.setName(startNodes);
                    startNodesAtt.setValue(currentStartNode);
                    attributes.add(startNodesAtt);
                }

                String[] endNodesArray = columns[3].split(",");

                for (String currentEndNode : endNodesArray) {
                    ReplaceableAttribute endNodesAtt = new ReplaceableAttribute();
                    endNodesAtt.setName(endNodes);
                    endNodesAtt.setValue(currentEndNode);
                    attributes.add(endNodesAtt);
                }
                
                ReplaceableAttribute javadocAtt = new ReplaceableAttribute();
                javadocAtt.setName(javadocUrl);
                javadocAtt.setValue(columns[4]);
                attributes.add(javadocAtt);
                
                ReplaceableAttribute itemTypeAtt = new ReplaceableAttribute();
                itemTypeAtt.setName(ITEM_TYPE_ATTRIBUTE);
                itemTypeAtt.setValue(ITEM_TYPE_RELATIONSHIP);
                attributes.add(itemTypeAtt);

                putAttributesRequest.setAttributes(attributes);

                simpleDBClient.putAttributes(putAttributesRequest);

            }
            reader.close();
            
            //-----------reading node indexes.... ----------------
            File nodeIndexesFile = new File(NODE_INDEXES_FILENAME);
            reader = new BufferedReader(new FileReader(nodeIndexesFile));
            //header line
            header = reader.readLine().split("\t");
            nodeName = header[0];
            String propertyIndexed = header[1];
            String indexType = header[2];
            String indexName = header[3];

            System.out.println("reading node indexes file...");

            while ((line = reader.readLine()) != null) {

                String[] columns = line.split("\t");
                
                PutAttributesRequest putAttributesRequest = new PutAttributesRequest();
                putAttributesRequest.setDomainName(BIO4J_DOMAIN);
                putAttributesRequest.setItemName(columns[3]);
                
                List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();

                ReplaceableAttribute nodeNameAtt = new ReplaceableAttribute();
                nodeNameAtt.setName(nodeName);
                nodeNameAtt.setValue(columns[0]);
                attributes.add(nodeNameAtt);
                
                ReplaceableAttribute propertyIndexedAtt = new ReplaceableAttribute();
                propertyIndexedAtt.setName(propertyIndexed);
                propertyIndexedAtt.setValue(columns[1]);
                attributes.add(propertyIndexedAtt);
                
                ReplaceableAttribute indexTypeAtt = new ReplaceableAttribute();
                indexTypeAtt.setName(indexType);
                indexTypeAtt.setValue(columns[2]);
                attributes.add(indexTypeAtt);
                
                ReplaceableAttribute indexNameAtt = new ReplaceableAttribute();
                indexNameAtt.setName(indexName);
                indexNameAtt.setValue(columns[3]);
                attributes.add(indexNameAtt);

                ReplaceableAttribute itemTypeAtt = new ReplaceableAttribute();
                itemTypeAtt.setName(ITEM_TYPE_ATTRIBUTE);
                itemTypeAtt.setValue(ITEM_TYPE_NODE_INDEX);
                attributes.add(itemTypeAtt);
                
                putAttributesRequest.setAttributes(attributes);

                simpleDBClient.putAttributes(putAttributesRequest);
                
            }
            reader.close();

            //-----------reading relationship indexes.... ----------------
            File relIndexesFile = new File(RELATIONSHIP_INDEXES_FILENAME);
            reader = new BufferedReader(new FileReader(relIndexesFile));
            //header line
            header = reader.readLine().split("\t");
            relName = header[0];
            indexName = header[1];

            System.out.println("reading relationship indexes file...");

            while ((line = reader.readLine()) != null) {

                String[] columns = line.split("\t");
                
                PutAttributesRequest putAttributesRequest = new PutAttributesRequest();
                putAttributesRequest.setDomainName(BIO4J_DOMAIN);
                putAttributesRequest.setItemName(columns[1]);
                
                List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();

                ReplaceableAttribute relNameAtt = new ReplaceableAttribute();
                relNameAtt.setName(nodeName);
                relNameAtt.setValue(columns[0]);
                attributes.add(relNameAtt);
                
                ReplaceableAttribute indexNameAtt = new ReplaceableAttribute();
                indexNameAtt.setName(indexName);
                indexNameAtt.setValue(columns[1]);
                attributes.add(indexNameAtt);

                ReplaceableAttribute itemTypeAtt = new ReplaceableAttribute();
                itemTypeAtt.setName(ITEM_TYPE_ATTRIBUTE);
                itemTypeAtt.setValue(ITEM_TYPE_RELATIONSHIP_INDEX);
                attributes.add(itemTypeAtt);
                
                putAttributesRequest.setAttributes(attributes);

                simpleDBClient.putAttributes(putAttributesRequest);
                
            }
            reader.close();
            
            
            System.out.println("done!");



        } catch (Exception ex) {
            Logger.getLogger(CreateSimpleDBModel.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
}
