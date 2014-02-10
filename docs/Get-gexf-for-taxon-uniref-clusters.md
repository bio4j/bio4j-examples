This program expects four parameters:

1. Bio4j DB folder
2. Organism scientific name (the one specified in Uniprot)
3. Uniref Cluster (one of these values: 50,90,100
4. Output GEXF file name

## Program structure

1. The starting point is the organism provided.
2. All proteins associated to this organism are retrieved.
3. A subset of Uniref clusters is retrieved: the clusters included are those which have at least one of the proteins previously retrieved either as member or representant.
4. Then organisms of the proteins included in these clusters are fetched.
5. The gexf file is created including a node per uniref cluster _(the label is the accession of the representant protein and the size is proportional to the number of proteins included in the cluster)_ plus nodes for all organisms involved _(size is proportional to the number of proteins linked to it)_.

## How to execute the program

You can use the jar ExecuteBio4jTool providing an executions.xml file like this one:

``` xml
<scheduled_executions>	
	<execution>
		<class_full_name>com.era7.bioinfo.bio4j.tools.gephi.GetGexfForTaxonUnirefClusters</class_full_name>
		<arguments>
			<argument>bio4jdb</argument>
			<argument>Enterococcus faecalis</argument>
			<argument>100</argument>
			<argument>tests.gexf</argument>
		</arguments>
	</execution>
</scheduled_executions>
```