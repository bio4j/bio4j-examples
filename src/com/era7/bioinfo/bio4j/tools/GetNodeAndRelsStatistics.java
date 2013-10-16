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

import com.era7.bioinfo.bio4j.neo4j.model.nodes.AlternativeProductNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.CityNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.CommentTypeNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.ConsortiumNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.CountryNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.DatasetNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.EnzymeNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.FeatureTypeNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.GoTermNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.InstituteNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.InterproNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.IsoformNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.KeywordNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.OrganismNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.PersonNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.PfamNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.ProteinNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.SequenceCautionNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.SubcellularLocationNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.TaxonNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.ArticleNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.BookNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.DBNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.JournalNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.OnlineArticleNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.OnlineJournalNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.PatentNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.PublisherNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.SubmissionNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.ThesisNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.citation.UnpublishedObservationNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.ncbi.NCBITaxonNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.reactome.ReactomeTermNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.refseq.CDSNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.refseq.GeneNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.refseq.GenomeElementNode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.refseq.rna.MRNANode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.refseq.rna.MiscRNANode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.refseq.rna.NcRNANode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.refseq.rna.RRNANode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.refseq.rna.TRNANode;
import com.era7.bioinfo.bio4j.neo4j.model.nodes.refseq.rna.TmRNANode;
import com.era7.bioinfo.bio4j.neo4j.model.util.Bio4jManager;
import com.era7.lib.bioinfo.bioinfoutil.Executable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GetNodeAndRelsStatistics implements Executable {

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
            System.out.println("This program expects the following parameters: \n"
                    + "1. Bio4j DB folder\n"
                    + "2. Config Properties file (neo4j.properties)");
        } else {

            Bio4jManager manager = null;

            try {

                BufferedWriter nodesBuff = new BufferedWriter(new FileWriter(new File("Bio4jNodeStatistics.txt")));
                nodesBuff.write("NODE_NAME\tABSOLUTE_VALUE\n");

                BufferedWriter relsBuff = new BufferedWriter(new FileWriter(new File("Bio4jRelationshipStatistics.txt")));
                relsBuff.write("RELATIONSHIP_NAME\tABSOLUTE_VALUE\n");

                manager = new Bio4jManager(args[0], MapUtil.load(new File(args[1])), true);
                
                HashMap<String, Integer> relationshipsMap = new HashMap<>();

                int nCounter = 0;
                int rCounter = 0;

                int articleCounter = 0, bookCounter = 0, dbCounter = 0, journalCounter = 0, onlineArticleCounter = 0,
                        onlineJournalCounter = 0, patentCounter = 0, publisherCounter = 0, submissionCounter = 0,
                        thesisCounter = 0, unpublishedObsCounter = 0, ncbiTaxonCounter = 0, reactomeTermCounter = 0,
                        mrnaCounter = 0, miscRnaCounter = 0, ncRnaCounter = 0, rrnaCounter = 0, trnaCounter = 0, tmRnaCounter = 0,
                        cdsCounter = 0, geneCounter = 0, genomeElementCounter = 0, alternativeProductCounter = 0,
                        cityCounter = 0, commentTypeCounter = 0, consortiumCounter = 0, countryCounter = 0, datasetCounter = 0,
                        enzymeCounter = 0, featureTypeCounter = 0, goTermCounter = 0, instituteCounter = 0, interproCounter = 0,
                        isoformCounter = 0, keywordCounter = 0, organismCounter = 0, personCounter = 0, pfamCounter = 0,
                        proteinCounter = 0, sequenceCautionCounter = 0, subcellularLocationCounter = 0, taxonCounter = 0;


                long startTime = new Date().getTime();
                
                articleCounter = countNodes(ArticleNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += articleCounter;                
                nodesBuff.write(ArticleNode.NODE_TYPE + "\t" + articleCounter + "\n");

                bookCounter = countNodes(BookNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += bookCounter;                
                nodesBuff.write(BookNode.NODE_TYPE + "\t" + bookCounter + "\n");
                
                dbCounter = countNodes(DBNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += dbCounter;                
                nodesBuff.write(DBNode.NODE_TYPE + "\t" + dbCounter + "\n");
                
                journalCounter = countNodes(JournalNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += journalCounter;                
                nodesBuff.write(JournalNode.NODE_TYPE + "\t" + journalCounter + "\n");
                
                onlineArticleCounter = countNodes(OnlineArticleNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += onlineArticleCounter;                
                nodesBuff.write(OnlineArticleNode.NODE_TYPE + "\t" + onlineArticleCounter + "\n");
                
                onlineJournalCounter = countNodes(OnlineJournalNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += onlineJournalCounter;                
                nodesBuff.write(OnlineJournalNode.NODE_TYPE + "\t" + onlineJournalCounter + "\n");
                
                patentCounter = countNodes(PatentNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += patentCounter;                
                nodesBuff.write(PatentNode.NODE_TYPE + "\t" + patentCounter + "\n");
                
                publisherCounter = countNodes(PublisherNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += publisherCounter;                
                nodesBuff.write(PublisherNode.NODE_TYPE + "\t" + publisherCounter + "\n");
                
                submissionCounter = countNodes(SubmissionNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += submissionCounter;                
                nodesBuff.write(SubmissionNode.NODE_TYPE + "\t" + submissionCounter + "\n");

                thesisCounter = countNodes(ThesisNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += thesisCounter;                
                nodesBuff.write(ThesisNode.NODE_TYPE + "\t" + thesisCounter + "\n");
                
                unpublishedObsCounter = countNodes(UnpublishedObservationNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += unpublishedObsCounter;                
                nodesBuff.write(UnpublishedObservationNode.NODE_TYPE + "\t" + unpublishedObsCounter + "\n");
                
                ncbiTaxonCounter = countNodes(NCBITaxonNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += ncbiTaxonCounter;                
                nodesBuff.write(NCBITaxonNode.NODE_TYPE + "\t" + ncbiTaxonCounter + "\n");
                
                reactomeTermCounter = countNodes(ReactomeTermNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += reactomeTermCounter;                
                nodesBuff.write(ReactomeTermNode.NODE_TYPE + "\t" + reactomeTermCounter + "\n");
                
                mrnaCounter = countNodes(MRNANode.NODE_TYPE,manager,relationshipsMap);
                nCounter += mrnaCounter;                
                nodesBuff.write(MRNANode.NODE_TYPE + "\t" + mrnaCounter + "\n");
                
                miscRnaCounter = countNodes(MiscRNANode.NODE_TYPE,manager,relationshipsMap);
                nCounter += miscRnaCounter;                
                nodesBuff.write(MiscRNANode.NODE_TYPE + "\t" + miscRnaCounter + "\n");
                
                ncRnaCounter = countNodes(NcRNANode.NODE_TYPE,manager,relationshipsMap);
                nCounter += ncRnaCounter;                
                nodesBuff.write(NcRNANode.NODE_TYPE + "\t" + ncRnaCounter + "\n");
                
                rrnaCounter = countNodes(RRNANode.NODE_TYPE,manager,relationshipsMap);
                nCounter += rrnaCounter;                
                nodesBuff.write(RRNANode.NODE_TYPE + "\t" + rrnaCounter + "\n");
                
                trnaCounter = countNodes(TRNANode.NODE_TYPE,manager,relationshipsMap);
                nCounter += trnaCounter;                
                nodesBuff.write(TRNANode.NODE_TYPE + "\t" + trnaCounter + "\n");
                
                tmRnaCounter = countNodes(TmRNANode.NODE_TYPE,manager,relationshipsMap);
                nCounter += tmRnaCounter;                
                nodesBuff.write(TmRNANode.NODE_TYPE + "\t" + tmRnaCounter + "\n");
                
                cdsCounter = countNodes(CDSNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += cdsCounter;                
                nodesBuff.write(CDSNode.NODE_TYPE + "\t" + cdsCounter + "\n");
                
                geneCounter = countNodes(GeneNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += geneCounter;                
                nodesBuff.write(GeneNode.NODE_TYPE + "\t" + geneCounter + "\n");
                
                genomeElementCounter = countNodes(GenomeElementNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += genomeElementCounter;                
                nodesBuff.write(GenomeElementNode.NODE_TYPE + "\t" + genomeElementCounter + "\n");
                
                alternativeProductCounter = countNodes(AlternativeProductNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += alternativeProductCounter;                
                nodesBuff.write(AlternativeProductNode.NODE_TYPE + "\t" + alternativeProductCounter + "\n");
                
                cityCounter = countNodes(CityNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += cityCounter;                
                nodesBuff.write(CityNode.NODE_TYPE + "\t" + cityCounter + "\n");
                
                commentTypeCounter = countNodes(CommentTypeNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += commentTypeCounter;                
                nodesBuff.write(CommentTypeNode.NODE_TYPE + "\t" + commentTypeCounter + "\n");
                
                consortiumCounter = countNodes(ConsortiumNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += consortiumCounter;                
                nodesBuff.write(ConsortiumNode.NODE_TYPE + "\t" + consortiumCounter + "\n");
                
                countryCounter = countNodes(CountryNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += countryCounter;                
                nodesBuff.write(CountryNode.NODE_TYPE + "\t" + countryCounter + "\n");
                
                datasetCounter = countNodes(DatasetNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += datasetCounter;                
                nodesBuff.write(DatasetNode.NODE_TYPE + "\t" + datasetCounter + "\n");
                
                enzymeCounter = countNodes(EnzymeNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += enzymeCounter;                
                nodesBuff.write(EnzymeNode.NODE_TYPE + "\t" + enzymeCounter + "\n");
                
                featureTypeCounter = countNodes(FeatureTypeNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += featureTypeCounter;                
                nodesBuff.write(FeatureTypeNode.NODE_TYPE + "\t" + featureTypeCounter + "\n");
                
                goTermCounter = countNodes(GoTermNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += goTermCounter;                
                nodesBuff.write(GoTermNode.NODE_TYPE + "\t" + goTermCounter + "\n");
                
                instituteCounter = countNodes(InstituteNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += instituteCounter;                
                nodesBuff.write(InstituteNode.NODE_TYPE + "\t" + instituteCounter + "\n");
                
                interproCounter = countNodes(InterproNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += interproCounter;                
                nodesBuff.write(InterproNode.NODE_TYPE + "\t" + interproCounter + "\n");
                
                isoformCounter = countNodes(IsoformNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += isoformCounter;                
                nodesBuff.write(IsoformNode.NODE_TYPE + "\t" + isoformCounter + "\n");
                
                keywordCounter = countNodes(KeywordNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += keywordCounter;                
                nodesBuff.write(KeywordNode.NODE_TYPE + "\t" + keywordCounter + "\n");
                
                keywordCounter = countNodes(KeywordNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += keywordCounter;                
                nodesBuff.write(KeywordNode.NODE_TYPE + "\t" + keywordCounter + "\n");
                
                organismCounter = countNodes(OrganismNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += organismCounter;                
                nodesBuff.write(OrganismNode.NODE_TYPE + "\t" + organismCounter + "\n");
                
                personCounter = countNodes(PersonNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += personCounter;                
                nodesBuff.write(PersonNode.NODE_TYPE + "\t" + personCounter + "\n");
                
                pfamCounter = countNodes(PfamNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += pfamCounter;                
                nodesBuff.write(PfamNode.NODE_TYPE + "\t" + pfamCounter + "\n");
                
                proteinCounter = countNodes(ProteinNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += proteinCounter;                
                nodesBuff.write(ProteinNode.NODE_TYPE + "\t" + proteinCounter + "\n");
                
                sequenceCautionCounter = countNodes(SequenceCautionNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += sequenceCautionCounter;                
                nodesBuff.write(SequenceCautionNode.NODE_TYPE + "\t" + sequenceCautionCounter + "\n");
                
                subcellularLocationCounter = countNodes(SubcellularLocationNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += subcellularLocationCounter;                
                nodesBuff.write(SubcellularLocationNode.NODE_TYPE + "\t" + subcellularLocationCounter + "\n");
                
                taxonCounter = countNodes(TaxonNode.NODE_TYPE,manager,relationshipsMap);
                nCounter += taxonCounter;                
                nodesBuff.write(TaxonNode.NODE_TYPE + "\t" + taxonCounter + "\n");
                
                nodesBuff.close();
                relsBuff.close();


            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            } finally {

                manager.shutDown();
            }


        }
    }

    private static int countNodes(String nodeType, Bio4jManager manager, HashMap<String, Integer> relsMap) {
        int counter = 0;
        System.out.println("Looking for " + nodeType + " ...");
        IndexHits<Node> hits = manager.getNodeTypeIndex().get(Bio4jManager.NODE_TYPE_INDEX_NAME, nodeType);
        counter = hits.size();
        Iterator<Node> iterator = hits.iterator();
        int tempCounter = 0;
        while (iterator.hasNext()) {
            Node node = iterator.next();
            Iterator<Relationship> relIt = node.getRelationships(Direction.INCOMING).iterator();
            while(relIt.hasNext()){
                String relName = relIt.next().getType().name();
                Integer relCounter = relsMap.get(relName);
                if(relCounter == null){
                    relsMap.put(relName, 1);
                }else{
                    relsMap.put(relName, relCounter+1);
                }
                tempCounter++;
                if((tempCounter % 10000) == 0){
                    System.out.println(tempCounter + " rels counted...");
                }
            }
        }
        hits.close();
        System.out.println("there were " + counter + " found!");
        return counter;
    }
}
