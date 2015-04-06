
```java
package com.bio4j.examples.json.model.uniprot;

import com.bio4j.examples.json.model.go.GOTerm;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ppareja on 3/26/2015.
 */
public class Protein {

	public String accession;
	public String name;
	public String shortName;
	public String fullName;
	public List<String> geneNames;
	public List<GOTerm> annotatedByGOTerms;

	public List<GOTerm> getAnnotatedByGOTerms() {
		return annotatedByGOTerms;
	}

	public Protein(){

	}

	public Protein(com.bio4j.model.uniprot.vertices.Protein protein){
		this.accession = protein.accession();
		this.name = protein.name();
		this.shortName = protein.shortName();
		this.fullName = protein.fullName();
	}

	public void setAnnotatedByGOTerms(List<GOTerm> annotatedByGOTerms) {
		this.annotatedByGOTerms = annotatedByGOTerms;
	}

	public void addAnnotatedByGOTerm(GOTerm term){
		if(annotatedByGOTerms == null){
			annotatedByGOTerms = new LinkedList<>();
		}
		annotatedByGOTerms.add(term);
	}

	public List<String> getGeneNames() {
		return geneNames;
	}

	public void setGeneNames(List<String> geneNames) {
		this.geneNames = geneNames;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public List<String> getGeneLocations() {
		return geneLocations;
	}

	public void setGeneLocations(List<String> geneLocations) {
		this.geneLocations = geneLocations;
	}

	public List<String> geneLocations;
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