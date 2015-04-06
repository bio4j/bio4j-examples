
```java
package com.bio4j.examples;

import com.bio4j.angulillos.*;
import static com.bio4j.angulillos.conversions.*;
import com.bio4j.model.go.vertices.GoTerm;
import com.bio4j.model.uniprot.UniProtGraph;
import com.bio4j.model.uniprot.vertices.Protein;
import com.bio4j.model.uniref.vertices.UniRef50Cluster;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class BasicProteinManipulation<
		I extends UntypedGraph<RV, RVT, RE, RET>,
		// vertices
		RV, RVT,
		// edges
		RE, RET
		> {

//	protected abstract UniProtGraph<I, RV, RVT, RE, RET> graph();
//
//
//	public String getName(Protein<I, RV, RVT, RE, RET> p) {
//
//		return p.name();
//	}
//
//	// todo optionalStream to Stream
//	public Optional<Stream<Optional<Stream<GoTerm<I, RV, RVT, RE, RET>>>>>
//	goTermsFromTheClusterOf(Protein<I, RV, RVT, RE, RET> protein) {
//
//		return
//				protein.uniref50Member_outV().map(
//						UniRef50Cluster::uniRef50Member_inV
//				).map(prts -> prts.map(
//								Protein::goAnnotation_outV
//						)
//				);
//	}
//
//	public Optional<Stream<GoTerm<I,RV,RVT,RE,RET>>> allTermsFromClusterOf(Protein<I,RV,RVT,RE,RET> protein) {
//
//	  return flatten(
//
//	  	goTermsFromTheClusterOf(protein).map( x -> any(x) ) // OOSS(GT)
//	  )
//	  .map( ss -> flatten(ss) );
//
//	}

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