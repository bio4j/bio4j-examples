package com.bio4j.examples.json.model.uniprot;

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
