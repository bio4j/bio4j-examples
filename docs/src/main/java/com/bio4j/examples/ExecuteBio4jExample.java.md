
This class is used as a bundle of all the programs included in this repository.
By means of the Executable interface, any program could be run using the generated jar file `ExecuteBio4jExample.jar` plus the respective
`executions.xml` file.


```java
package com.bio4j.examples;

import com.era7.bioinfo.bioinfoutil.ExecuteFromFile;

/**
 * Created by ppareja on 2/10/2015.
 */
public class ExecuteBio4jExample {

	public static void main(String[] args){

		ExecuteFromFile.main(args);

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
            + enzyme
              + [GetProteinEnzymaticActivity.java][main\java\com\bio4j\examples\enzyme\GetProteinEnzymaticActivity.java]
            + [ExecuteBio4jExample.java][main\java\com\bio4j\examples\ExecuteBio4jExample.java]
            + geninfo
              + [GetProteinsAssociatedToGIs.java][main\java\com\bio4j\examples\geninfo\GetProteinsAssociatedToGIs.java]
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
                + uniprot
                  + [Protein.java][main\java\com\bio4j\examples\json\model\uniprot\Protein.java]
                  + [ProteinSet.java][main\java\com\bio4j\examples\json\model\uniprot\ProteinSet.java]
            + ncbi_taxonomy
              + [TaxonomyAlgo.java][main\java\com\bio4j\examples\ncbi_taxonomy\TaxonomyAlgo.java]
            + uniref
              + [FindLCAOfUniRefCluster.java][main\java\com\bio4j\examples\uniref\FindLCAOfUniRefCluster.java]

[main\java\com\bio4j\examples\BasicProteinManipulation.java]: BasicProteinManipulation.java.md
[main\java\com\bio4j\examples\enzyme\GetProteinEnzymaticActivity.java]: enzyme\GetProteinEnzymaticActivity.java.md
[main\java\com\bio4j\examples\ExecuteBio4jExample.java]: ExecuteBio4jExample.java.md
[main\java\com\bio4j\examples\geninfo\GetProteinsAssociatedToGIs.java]: geninfo\GetProteinsAssociatedToGIs.java.md
[main\java\com\bio4j\examples\go\ExportGOJSONToCSV.java]: go\ExportGOJSONToCSV.java.md
[main\java\com\bio4j\examples\go\GetCumulativeFrequenciesForGoSet.java]: go\GetCumulativeFrequenciesForGoSet.java.md
[main\java\com\bio4j\examples\go\GetGOAnnotation.java]: go\GetGOAnnotation.java.md
[main\java\com\bio4j\examples\go\TransformGOJSONtoHierarchicalJSON.java]: go\TransformGOJSONtoHierarchicalJSON.java.md
[main\java\com\bio4j\examples\json\model\go\GoSet.java]: json\model\go\GoSet.java.md
[main\java\com\bio4j\examples\json\model\go\GOTerm.java]: json\model\go\GOTerm.java.md
[main\java\com\bio4j\examples\json\model\uniprot\Protein.java]: json\model\uniprot\Protein.java.md
[main\java\com\bio4j\examples\json\model\uniprot\ProteinSet.java]: json\model\uniprot\ProteinSet.java.md
[main\java\com\bio4j\examples\ncbi_taxonomy\TaxonomyAlgo.java]: ncbi_taxonomy\TaxonomyAlgo.java.md
[main\java\com\bio4j\examples\uniref\FindLCAOfUniRefCluster.java]: uniref\FindLCAOfUniRefCluster.java.md