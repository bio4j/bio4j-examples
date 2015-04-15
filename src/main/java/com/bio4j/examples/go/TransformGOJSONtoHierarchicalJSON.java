/*
This program simply exports a JSON file including a GoSet annotation to another JSON file but including the hierarchy
of the terms, that's to say, children terms would be inside their pa terms  when they .
The program expects the following parameters:

1. Input JSON GO anmnotation file
2. Output CSV GO annotation file

 */
package com.bio4j.examples.go;

import com.era7.bioinfo.bioinfoutil.Executable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.bio4j.json.go.GOTerm;
import com.bio4j.json.go.GoSet;

import java.io.*;
import java.util.*;

public class TransformGOJSONtoHierarchicalJSON implements Executable{

	@Override
	public void execute(ArrayList<String> array) {
		String[] args = new String[array.size()];
		for (int i = 0; i < array.size(); i++) {
			args[i] = array.get(i);
		}
		main(args);
	}

	public static void main(String[] args){

		if (args.length != 2) {
			System.out.println("This program expects the following parameters:\n"
					+ "1. Input JSON GO anmnotation file\n"
					+ "2. Output JSOM GO annotation file (hierarchical)");
		} else {

			String inputFileSt = args[0];
			String outputFileSt = args[1];

			try {

				BufferedReader reader = new BufferedReader(new FileReader(new File(inputFileSt)));
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFileSt)));

				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				GoSet goSet = gson.fromJson(reader, GoSet.class);

				HashMap<String, GOTerm> termsMap = new HashMap<>();

				for(GOTerm term : goSet.getGoTerms()){
					termsMap.put(term.getId(), term);
				}

				Set<GOTerm> termsSet = goSet.getGoTerms();
				GoSet newGoSet = new GoSet(new HashSet<GOTerm>());

				List<String> childrenTerms = new LinkedList<>();

				for (GOTerm currentTerm : termsSet){

					System.out.println("currentTerm.id = " + currentTerm.id);

					List<String> parentIdList = currentTerm.getParentIds();
					for (String parentId : parentIdList){
						GOTerm parentTerm = termsMap.get(parentId);
						if(parentTerm == null){
							newGoSet.addGOTerm(currentTerm);
						}else{
							parentTerm.addTermToChildren(currentTerm);
							childrenTerms.add(currentTerm.id);
						}
					}
				}

				for (GOTerm currentTerm : termsSet){
					if(!childrenTerms.contains(currentTerm.id)){
						newGoSet.addGOTerm(currentTerm);
					}
				}

				writer.write(gson.toJson(newGoSet));
				System.out.println("Closing writer...");
				writer.close();
				System.out.println("Output file created successfully! :)");


			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
