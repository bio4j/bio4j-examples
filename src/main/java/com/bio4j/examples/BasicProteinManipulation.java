package com.bio4j.examples;

import com.bio4j.model.uniprot.*;
import com.bio4j.model.uniprot.vertices.*;
import com.bio4j.model.uniprot.edges.*;

import com.bio4j.model.uniref.vertices.*;
import com.bio4j.model.go.vertices.*;

import com.bio4j.angulillos.*;
import static com.bio4j.angulillos.conversions.*;

import java.util.stream.*;
import java.util.Optional;

public abstract class BasicProteinManipulation <
  I extends UntypedGraph<RV, RVT, RE, RET>,
  // vertices
  RV, RVT,
  // edges
  RE, RET
>
{

  protected abstract UniprotGraph<I,RV,RVT,RE,RET> graph();



  public String getName(Protein<I,RV,RVT,RE,RET> p) {

    return p.name();
  }

  // todo optionalStream to Stream
  public Optional<Stream<Optional<Stream<GoTerm<I,RV,RVT,RE,RET>>>>> 
    goTermsFromTheClusterOf(Protein<I,RV,RVT,RE,RET> protein) {

    return 
      protein.uniref50Member_outV().map( 
        UniRef50Cluster::uniRef50Member_inV
      ).map(prts -> prts.map(
          Protein::goAnnotation_outV
        )
      );
  }

  // public Optional<Stream<GoTerm<I,RV,RVT,RE,RET>>> allTermsFromClusterOf(Protein<I,RV,RVT,RE,RET> protein) {
  //   return (
  //     goTermsFromTheClusterOf(protein).map(x -> any(x))
  //   ).map( p -> flatten(p) );
  // }

}
