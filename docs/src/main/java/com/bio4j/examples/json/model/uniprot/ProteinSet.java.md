
```java
package com.bio4j.examples.json.model.uniprot;

import java.util.Set;

/**
 * Created by ppareja on 2/18/2015.
 */
public class ProteinSet {

	public Set<Protein> proteins;

	public Set<Protein> getProteins() {
		return proteins;
	}

	public void setProteins(Set<Protein> proteins) {
		this.proteins = proteins;
	}

	public ProteinSet(Set<Protein> proteins) {

		this.proteins = proteins;
	}
	public ProteinSet() {
	}


	public void addProtein(Protein protein){
		proteins.add(protein);
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

[main\java\com\bio4j\examples\BasicProteinManipulation.java]: ..\..\..\BasicProteinManipulation.java.md
[main\java\com\bio4j\examples\enzyme\GetProteinEnzymaticActivity.java]: ..\..\..\enzyme\GetProteinEnzymaticActivity.java.md
[main\java\com\bio4j\examples\ExecuteBio4jExample.java]: ..\..\..\ExecuteBio4jExample.java.md
[main\java\com\bio4j\examples\geninfo\GetProteinsAssociatedToGIs.java]: ..\..\..\geninfo\GetProteinsAssociatedToGIs.java.md
[main\java\com\bio4j\examples\go\ExportGOJSONToCSV.java]: ..\..\..\go\ExportGOJSONToCSV.java.md
[main\java\com\bio4j\examples\go\GetCumulativeFrequenciesForGoSet.java]: ..\..\..\go\GetCumulativeFrequenciesForGoSet.java.md
[main\java\com\bio4j\examples\go\GetGOAnnotation.java]: ..\..\..\go\GetGOAnnotation.java.md
[main\java\com\bio4j\examples\go\TransformGOJSONtoHierarchicalJSON.java]: ..\..\..\go\TransformGOJSONtoHierarchicalJSON.java.md
[main\java\com\bio4j\examples\json\model\go\GoSet.java]: ..\go\GoSet.java.md
[main\java\com\bio4j\examples\json\model\go\GOTerm.java]: ..\go\GOTerm.java.md
[main\java\com\bio4j\examples\json\model\uniprot\Protein.java]: Protein.java.md
[main\java\com\bio4j\examples\json\model\uniprot\ProteinSet.java]: ProteinSet.java.md
[main\java\com\bio4j\examples\ncbi_taxonomy\TaxonomyAlgo.java]: ..\..\..\ncbi_taxonomy\TaxonomyAlgo.java.md
[main\java\com\bio4j\examples\uniref\FindLCAOfUniRefCluster.java]: ..\..\..\uniref\FindLCAOfUniRefCluster.java.md