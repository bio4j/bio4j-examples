
```java
package com.bio4j.examples;

import com.bio4j.angulillos.UntypedGraph;
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

	protected abstract UniProtGraph<I, RV, RVT, RE, RET> graph();


	public String getName(Protein<I, RV, RVT, RE, RET> p) {

		return p.name();
	}

	// todo optionalStream to Stream
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

	// public Optional<Stream<GoTerm<I,RV,RVT,RE,RET>>> allTermsFromClusterOf(Protein<I,RV,RVT,RE,RET> protein) {
	//   return (
	//     goTermsFromTheClusterOf(protein).map(x -> any(x))
	//   ).map( p -> flatten(p) );
	// }

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
            + go
              + [ExportGOJSONToCSV.java][main\java\com\bio4j\examples\go\ExportGOJSONToCSV.java]
              + [GetCumulativeFrequenciesForGoSet.java][main\java\com\bio4j\examples\go\GetCumulativeFrequenciesForGoSet.java]
              + [GetGOAnnotation.java][main\java\com\bio4j\examples\go\GetGOAnnotation.java]
            + json
              + model
                + go
                  + [GoSet.java][main\java\com\bio4j\examples\json\model\go\GoSet.java]
                  + [GOTerm.java][main\java\com\bio4j\examples\json\model\go\GOTerm.java]
            + ncbi_taxonomy
              + [TaxonomyAlgo.java][main\java\com\bio4j\examples\ncbi_taxonomy\TaxonomyAlgo.java]
            + uniref
              + [FindLCAOfUniRefCluster.java][main\java\com\bio4j\examples\uniref\FindLCAOfUniRefCluster.java]

[main\java\com\bio4j\examples\BasicProteinManipulation.java]: BasicProteinManipulation.java.md
[main\java\com\bio4j\examples\go\ExportGOJSONToCSV.java]: go\ExportGOJSONToCSV.java.md
[main\java\com\bio4j\examples\go\GetCumulativeFrequenciesForGoSet.java]: go\GetCumulativeFrequenciesForGoSet.java.md
[main\java\com\bio4j\examples\go\GetGOAnnotation.java]: go\GetGOAnnotation.java.md
[main\java\com\bio4j\examples\json\model\go\GoSet.java]: json\model\go\GoSet.java.md
[main\java\com\bio4j\examples\json\model\go\GOTerm.java]: json\model\go\GOTerm.java.md
[main\java\com\bio4j\examples\ncbi_taxonomy\TaxonomyAlgo.java]: ncbi_taxonomy\TaxonomyAlgo.java.md
[main\java\com\bio4j\examples\uniref\FindLCAOfUniRefCluster.java]: uniref\FindLCAOfUniRefCluster.java.md