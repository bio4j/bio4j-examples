/*
This program simply exports a JSON file including a GoSet annotation to a CSV file.
The program expects the following parameters:

1. Input JSON GO anmnotation file
2. Output CSV GO annotation file

 */
package com.bio4j.examples.go;

import com.bio4j.examples.json.model.go.GOTerm;
import com.bio4j.examples.json.model.go.GoSet;
import com.bio4j.examples.json.model.uniprot.Protein;
import com.era7.bioinfo.bioinfoutil.Executable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.util.*;

public class ExportGOJSONToCSV implements Executable{

	public static final String HEADER = "ID,NAME,TERM_COUNT,TERM_CUMULATIVE_COUNT";
	public static final String ANNOTATED_PROTEINS_HEADER = "ACCESSION,NAME,FULL_NAME,SHORT_NAME,GENE_NAMES,ANNOTATED_BY_GO_TERMS,ANNOTATED_BY_GO_TERMS_COUNT";

	@Override
	public void execute(ArrayList<String> array) {
		String[] args = new String[array.size()];
		for (int i = 0; i < array.size(); i++) {
			args[i] = array.get(i);
		}
		main(args);
	}

	public static void main(String[] args){

		if (args.length != 4) {
			System.out.println("This program expects the following parameters:\n"
					+ "1. Input JSON GO anmnotation file\n"
					+ "2. Output CSV GO annotation file\n"
					+ "3. Include annotated proteins (true/false)"
					+ "4. Generate annotated proteins CSV data file (true/false)");
		} else {

			String inputFileSt = args[0];
			String outputFileSt = args[1];
			boolean includeAnnotatedProteins = Boolean.parseBoolean(args[2]);
			boolean generateAnnotatedProteinsFile = Boolean.parseBoolean(args[3]);

			String annotatedProteinsFileSt = "annotatedProteins.csv";
			File annotatedProteinsFile = new File(annotatedProteinsFileSt);

			try {

				BufferedReader reader = new BufferedReader(new FileReader(new File(inputFileSt)));
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFileSt)));
				writer.write(HEADER + "\n");

				BufferedWriter annotatedProteinsWriter = new BufferedWriter(new FileWriter(new File(outputFileSt)));

				if(generateAnnotatedProteinsFile){
					annotatedProteinsWriter.write(ANNOTATED_PROTEINS_HEADER + "\n");
				}


				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				GoSet goSet = gson.fromJson(reader, GoSet.class);
				HashMap<String, Protein> proteinsMap = new HashMap<>();

				for (GOTerm goTerm : goSet.getGoTerms()){

					String tempSt = goTerm.getId() + ",\"" + goTerm.getName() + "\"," + goTerm.getTermCount() + "," + goTerm.getCumulativeCount();

					if(includeAnnotatedProteins){
						List<Protein> annotatedProteins = goTerm.getAnnotatedProteins();
						String proteinsSt = ",[";

						for (Protein protein : annotatedProteins){
							proteinsSt += protein.getAccession() + ":" + protein.getFullName() + ",";
						}
						proteinsSt = proteinsSt.substring(0, proteinsSt.length() - 1);
						proteinsSt += "]";
						tempSt += proteinsSt;
					}
					tempSt += "\n";
					writer.write(tempSt);

					if(generateAnnotatedProteinsFile){

					}
				}

				System.out.println("Closing writer...");
				writer.close();
				System.out.println("Output file created successfully! :)");

				if(generateAnnotatedProteinsFile){
					annotatedProteinsWriter.close();
					System.out.println("Annotated proteins file created successfully! :)");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
