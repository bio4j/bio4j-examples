This program expects four parameters:

1. Bio4j DB folder
2. Genome Element version
3. Uniref Cluster (one of these values: 50,90,100
4. Output GEXF file name

## Program structure

1. The starting point is the specific Genome element provided.
2. All proteins associated to this genome element are retrieved.
3. A subset of Uniref clusters is retrieved: the clusters included are those which have at least one of the proteins previously retrieved either as member or representant.
4. Then the organisms of every protein included in these clusters are fetched, as well as genome elements that have any of these proteins from the clusters initially selected associated to them.
5. The gexf file is created including a node per uniref cluster _(the label is the accession of the representant protein and the size is proportional to the number of proteins included in the cluster)_, nodes for all organisms involved _(size is proportional to the number of proteins linked to it)_, plus a node for each genome element involved _(size is proportional to the number of proteins linked to it)_

> Different colors are used for different types of nodes

## How to execute the program

You can use the jar ExecuteBio4jTool providing an executions.xml file like this one:

``` xml
<scheduled_executions>	
	<execution>
		<class_full_name>com.era7.bioinfo.bio4j.tools.gephi.GetGexfForGenomeElementUnirefClusters</class_full_name>
		<arguments>
			<argument>bio4jdb</argument>
			<argument>NC_008821.1</argument>
			<argument>50</argument>
			<argument>NC_008821_1_uniref50.gexf</argument>
		</arguments>
	</execution>
</scheduled_executions>
```