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

	protected abstract UniProtGraph<I, RV, RVT, RE, RET> graph();


	public String getName(Protein<I, RV, RVT, RE, RET> p) {

		return p.name();
	}

	// todo optionalStream to Stream
	public Optional<Stream<Optional<Stream<GoTerm<I, RV, RVT, RE, RET>>>>>
	goTermsFromTheClusterOf(Protein<I, RV, RVT, RE, RET> protein) {

		return
			protein.uniref50Member_outV().map(
				UniRef50Cluster::uniRef50Member_inV
			).map(prts -> prts.map(
					Protein::goAnnotation_outV
				)
			);
	}

	public Optional<Stream<GoTerm<I,RV,RVT,RE,RET>>> allTermsFromClusterOf(Protein<I,RV,RVT,RE,RET> protein) {

	  return flatten(
	  	goTermsFromTheClusterOf(protein).map( x -> any(x) )
	  )
	  .map( ss -> flatten(ss) );
	}

}
