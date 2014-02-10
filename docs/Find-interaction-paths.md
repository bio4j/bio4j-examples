This program expects the following parameters:

1. Source protein accession 
2. Target protein accession 
3. Max depth 
4. Bio4j DB folder 
5. Max number of results 
6. Results out file name (txt)

## How to execute the program

You can use the jar ExecuteBio4jTool providing an executions.xml file like this one:

``` xml
<scheduled_executions>	
	<execution>
		<class_full_name>com.era7.bioinfo.bio4j.tools.algo.FindInteractionPaths</class_full_name>
		<arguments>
			<argument>P29590</argument>
			<argument>P29590</argument>
			<argument>20</argument>
			<argument>bio4jdb</argument>
			<argument>50</argument>
			<argument>interactionPaths.txt</argument>
		</arguments>
	</execution>
</scheduled_executions>
```