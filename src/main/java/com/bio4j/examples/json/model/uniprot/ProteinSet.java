package com.bio4j.examples.json.model.uniprot;

import java.util.Set;

/**
 * Created by ppareja on 2/18/2015.
 */
public class ProteinSet {

	public Set<Protein> proteins;

	public Set<Protein> getProteins() {
		return proteins;
	}

	public void setProteins(Set<Protein> proteins) {
		this.proteins = proteins;
	}

	public ProteinSet(Set<Protein> proteins) {

		this.proteins = proteins;
	}
	public ProteinSet() {
	}


	public void addProtein(Protein protein){
		proteins.add(protein);
	}
}
