/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools;

import com.era7.bioinfo.bio4jmodel.nodes.DatasetNode;
import com.era7.bioinfo.bio4jmodel.nodes.ProteinNode;
import com.era7.bioinfo.bio4jmodel.nodes.citation.ArticleNode;
import com.era7.bioinfo.bio4jmodel.relationships.protein.ProteinDatasetRel;
import com.era7.bioinfo.bio4jmodel.util.Bio4jManager;
import com.era7.bioinfo.bio4jmodel.util.NodeRetriever;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class FindArticlesAndJournalsFromSwissProtCitations {
    
    public static void main(String[] args) throws Exception{
        
        
        Bio4jManager manager = new Bio4jManager(args[0]);
        NodeRetriever nodeRetriever = new NodeRetriever(manager);
                
        
        System.out.println("Getting swiss-prot dataset...");
        //DatasetNode swissProtNode = nodeRetriever.getDatasetByName("Swiss-Prot");  
        
        //---temporal-----
        DatasetNode swissProtNode = nodeRetriever.getProteinNodeByAccession("P12345").getDataset();
        System.out.println("done!");
        
        HashMap<String, Set<String>> journalArticlesMap = new HashMap<String, Set<String>>();
        Set<String> articleSet = new HashSet<String>();
        Set<String> journalSet = new HashSet<String>();
        
        System.out.println("Getting proteins....");
        
        Iterator<Relationship> relIt = swissProtNode.getNode().getRelationships(Direction.INCOMING, new ProteinDatasetRel(null)).iterator();
        int proteinCounter = 0;
        
        while(relIt.hasNext()){
            proteinCounter++;
            
            ProteinNode protNode = new ProteinNode(relIt.next().getStartNode());
            
            System.out.println("Analyzing protein: " + protNode.getAccession());
            
            List<ArticleNode> articles = protNode.getArticleCitations();
            System.out.println("There are " + articles.size() + " article citations.");
            for (ArticleNode articleNode : articles) {
                
                String articleTitle = articleNode.getTitle();
                
                articleSet.add(articleTitle);
                String journalName = articleNode.getJournal().getName();
                journalSet.add(journalName);
                
                Set<String> journalArtsSet = journalArticlesMap.get(journalName);
                if(journalArtsSet == null){
                    journalArtsSet = new HashSet<String>();
                    journalArticlesMap.put(journalName, journalSet);
                }
                
                journalArtsSet.add(articleTitle);
            }
            
            if(proteinCounter % 10 == 0){
                System.out.println(proteinCounter + " proteins analyzed...");
                System.out.println(articleSet.size() + " articles found so far");
                System.out.println(journalSet.size() + " journals found so far");
            }
        }
        
        System.out.println("done with proteins!");
                
        manager.shutDown();
        
        
        BufferedWriter outBuff = new BufferedWriter(new FileWriter(new File(args[1])));
        
        for (String journalName : journalArticlesMap.keySet()) {
            outBuff.write(journalName + "\t" + journalArticlesMap.get(journalName).size()); 
        }
        
        outBuff.close();
        
    }
    
}
