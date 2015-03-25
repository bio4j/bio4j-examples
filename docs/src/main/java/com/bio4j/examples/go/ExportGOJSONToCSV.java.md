
This program simply exports a JSON file including a GoSet annotation to a CSV file.
The program expects the following parameters:

1. Input JSON GO anmnotation file
2. Output CSV GO annotation file



```java
package com.bio4j.examples.go;

import com.bio4j.examples.json.model.go.GOTerm;
import com.bio4j.examples.json.model.go.GoSet;
import com.era7.bioinfo.bioinfoutil.Executable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class ExportGOJSONToCSV implements Executable{

	public static final String HEADER = "ID,NAME,TERM_COUNT,TERM_CUMULATIVE_COUNT";

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
					+ "2. Output CSV GO annotation file");
		} else {

			String inputFileSt = args[0];
			String outputFileSt = args[1];

			try {

				BufferedReader reader = new BufferedReader(new FileReader(new File(inputFileSt)));
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFileSt)));
				writer.write(HEADER + "\n");

				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				GoSet goSet = gson.fromJson(reader, GoSet.class);

				for (GOTerm goTerm : goSet.getGoTerms()){
					writer.write(goTerm.getId() + ",\"" + goTerm.getName() + "\"," + goTerm.getTermCount() + "," + goTerm.getCumulativeCount() + "\n");
				}

				System.out.println("Closing writer...");
				writer.close();
				System.out.println("Output file created successfully! :)");


			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}

```


------

### Index

+ src
  + main
    + java
      + com
        + bio4j
          + examples
            + [BasicProteinManipulation.java][main\java\com\bio4j\examples\BasicProteinManipulation.java]
            + [ExecuteBio4jExample.java][main\java\com\bio4j\examples\ExecuteBio4jExample.java]
            + go
              + [ExportGOJSONToCSV.java][main\java\com\bio4j\examples\go\ExportGOJSONToCSV.java]
              + [GetCumulativeFrequenciesForGoSet.java][main\java\com\bio4j\examples\go\GetCumulativeFrequenciesForGoSet.java]
              + [GetGOAnnotation.java][main\java\com\bio4j\examples\go\GetGOAnnotation.java]
              + [TransformGOJSONtoHierarchicalJSON.java][main\java\com\bio4j\examples\go\TransformGOJSONtoHierarchicalJSON.java]
            + json
              + model
                + go
                  + [GoSet.java][main\java\com\bio4j\examples\json\model\go\GoSet.java]
                  + [GOTerm.java][main\java\com\bio4j\examples\json\model\go\GOTerm.java]
            + ncbi_taxonomy
              + [TaxonomyAlgo.java][main\java\com\bio4j\examples\ncbi_taxonomy\TaxonomyAlgo.java]
            + uniref
              + [FindLCAOfUniRefCluster.java][main\java\com\bio4j\examples\uniref\FindLCAOfUniRefCluster.java]

[main\java\com\bio4j\examples\BasicProteinManipulation.java]: ..\BasicProteinManipulation.java.md
[main\java\com\bio4j\examples\ExecuteBio4jExample.java]: ..\ExecuteBio4jExample.java.md
[main\java\com\bio4j\examples\go\ExportGOJSONToCSV.java]: ExportGOJSONToCSV.java.md
[main\java\com\bio4j\examples\go\GetCumulativeFrequenciesForGoSet.java]: GetCumulativeFrequenciesForGoSet.java.md
[main\java\com\bio4j\examples\go\GetGOAnnotation.java]: GetGOAnnotation.java.md
[main\java\com\bio4j\examples\go\TransformGOJSONtoHierarchicalJSON.java]: TransformGOJSONtoHierarchicalJSON.java.md
[main\java\com\bio4j\examples\json\model\go\GoSet.java]: ..\json\model\go\GoSet.java.md
[main\java\com\bio4j\examples\json\model\go\GOTerm.java]: ..\json\model\go\GOTerm.java.md
[main\java\com\bio4j\examples\ncbi_taxonomy\TaxonomyAlgo.java]: ..\ncbi_taxonomy\TaxonomyAlgo.java.md
[main\java\com\bio4j\examples\uniref\FindLCAOfUniRefCluster.java]: ..\uniref\FindLCAOfUniRefCluster.java.md