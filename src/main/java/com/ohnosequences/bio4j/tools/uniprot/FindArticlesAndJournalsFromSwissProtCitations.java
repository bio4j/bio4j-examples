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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;

import com.ohnosequences.bio4j.neo4j.model.nodes.DatasetNode;
import com.ohnosequences.bio4j.neo4j.model.nodes.ProteinNode;
import com.ohnosequences.bio4j.neo4j.model.nodes.citation.ArticleNode;
import com.ohnosequences.bio4j.neo4j.model.relationships.protein.ProteinDatasetRel;
import com.ohnosequences.bio4j.neo4j.model.util.Bio4jManager;
import com.ohnosequences.bio4j.neo4j.model.util.NodeRetriever;
import com.ohnosequences.util.Executable;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class FindArticlesAndJournalsFromSwissProtCitations implements Executable {

    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("This program expects the following parameters:\n"
                    + "1. Bio4j DB folder\n"
                    + "2. Output TXT file");
        } else {
            BufferedWriter outBuff = null;

            try {

                Bio4jManager manager = new Bio4jManager(args[0]);
                NodeRetriever nodeRetriever = new NodeRetriever(manager);

                System.out.println("Getting swiss-prot dataset...");
                DatasetNode swissProtNode = nodeRetriever.getDatasetByName("Swiss-Prot");
                System.out.println("done!");

                HashMap<String, Set<String>> journalArticlesMap = new HashMap<String, Set<String>>();
                Set<String> articleSet = new HashSet<String>();
                Set<String> journalSet = new HashSet<String>();

                System.out.println("Getting proteins....");

                Iterator<Relationship> relIt = swissProtNode.getNode().getRelationships(Direction.INCOMING, new ProteinDatasetRel(null)).iterator();
                int proteinCounter = 0;

                while (relIt.hasNext()) {
                    proteinCounter++;

                    ProteinNode protNode = new ProteinNode(relIt.next().getStartNode());

                    //System.out.println("Analyzing protein: " + protNode.getAccession());

                    List<ArticleNode> articles = protNode.getArticleCitations();
                    //System.out.println("There are " + articles.size() + " article citations.");
                    for (ArticleNode articleNode : articles) {

                        String articleTitle = articleNode.getTitle();

                        articleSet.add(articleTitle);
                        String journalName = articleNode.getJournal().getName();
                        journalSet.add(journalName);

                        Set<String> journalArtsSet = journalArticlesMap.get(journalName);
                        if (journalArtsSet == null) {
                            journalArtsSet = new HashSet<String>();
                            journalArticlesMap.put(journalName, journalArtsSet);
                        }

                        journalArtsSet.add(articleTitle);
                    }

                    if (proteinCounter % 100 == 0) {
                        System.out.println(proteinCounter + " proteins analyzed...");
                        System.out.println(articleSet.size() + " articles found so far");
                        System.out.println(journalSet.size() + " journals found so far");

                        System.out.println("First article: " + articleSet.iterator().next());
                        System.out.println("First journal: " + journalSet.iterator().next());
                        //System.out.println("First journal: " + journalArticlesMap.get(journalArticlesMap.keySet().iterator().next()));

                    }
                }
                System.out.println("done with proteins!");
                manager.shutDown();
                System.out.println("Writing output file...");

                outBuff = new BufferedWriter(new FileWriter(new File(args[1])));
                for (String journalName : journalArticlesMap.keySet()) {
                    outBuff.write(journalName + "\t" + journalArticlesMap.get(journalName).size() + "\n");
                }
                outBuff.close();

                System.out.println("Done! ;)");

            } catch (IOException ex) {
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
