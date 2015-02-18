This repository contains a set of example tools/utilities based on Bio4j.

----------------------------------

### Algo package

* [FindInteraction paths](docs/Find-interaction-paths.md)  
  This program finds interaction paths between two proteins with a set of customizable parameters.
* [Taxonomy algo](docs/Taxonomy-algo.md)  
  Utility class including algorithms for taxonomy analysis


### Gephi package

* [Get gexf for protein set Uniref clusters](docs/Get-gexf-for-protein-set-uniref-clusters.md)  
  Creates a visualization showing Uniref clusters, the taxonomy of their members and Genome Elements these proteins are linked to.
* [Get gexf for genome element Uniref clusters](docs/Get-gexf-for-genome-element-uniref-clusters.md)  
  Creates a visualization showing Uniref clusters, the taxonomy of their members and Genome Elements these proteins are linked to.
* [Get gexf for taxon Uniref clusters](docs/Get-gexf-for-taxon-uniref-clusters.md)  
  Creates a visualization showing Uniref clusters plus the taxonomy of their members.
* [Export Go slim to gexf](docs/Export-go-slim-to-gexf.md)  
  Exports the GO Slim result XML file provided to gexf format.
* [Apply layout algo and filters to Gexf](docs/Apply-layout-algo-and-filters-to-gexf.md)  
  Just a test program that applies a new layout and/or filters to a gexf file.
* [Generate gexf whole GO](docs/Generate-gexf-whole-go.md)  
  Generates a gexf file representing the whole Gene Ontology


### Taxonomy package

* [Lowest common ancestor test](docs/Lowest-common-ancestor-test.md)  
  Just a simple test looking for LCA of the NCBI taxon nodes included as parameters for the program.


### Uniref package

* [Find biggest uniref 100 clusters](docs/Find-biggest-uniref-100-clusters.md)  
  Finds the biggest Uniref 100 clusters, writing a file including those that have at least the minimum size specified as a parameter.
* [Find LCA of uniref cluster](docs/Find-lca-of-uniref-cluster.md)  
  This program finds the Lowest Common Ancestor of the Uniref cluster provided (clusters for the three types 50,90,100 are accepted).


### SimpleDB package

* [Create SimpleDB model](docs/Create-simpledb-model.md)   
  Creates the SimpleDB Model corresponding to the domain model of Bio4j, that's to say, the one used by the tool Bio4jExplorer.
* [Delete SimpleDB model](docs/Delete-simpledb-model.md)  
  Deletes the SimpleDB Model corresponding to the domain model of Bio4j, that's to say, the one used by the tool Bio4jExplorer.


### Uniprot package

* [Extract citations comments samples](docs/Extract-citations-comments-samples.md)   
  This program parses an Uniprot XML file looking for different citation and comment types.
* [Extract features samples](docs/Extract-features-samples.md)  
  This program parses an Uniprot XML file looking for different feature types.
* [Find articles and journals from Swiss-Prot citations](docs/Find-articles-and-journals-from-swiss-prot-citations.md)  
  This program creates a basic statistics information file regarding journals and articles count - it gets this information from a Bio4j DB.
* [Split Uniprot XML file](docs/Split-uniprot-xml-file.md)  
  You can use this program to split a big Uniprot XML file into as many smaller files as you may want.

### Other

* [Get node and rels statistics](docs/Get-node-and-rels-statistics.md)   
  General statistics about the number of nodes/relationships and their types in a Bio4j DB.
</del>
