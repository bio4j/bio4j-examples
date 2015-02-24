package com.bio4j.examples.json.model.go;

import java.util.Set;

/**
 * Created by ppareja on 2/18/2015.
 */
public class GoSet {

	public Set<GOTerm> goTerms;

	public Set<GOTerm> getGoTerms() {
		return goTerms;
	}

	public void setGoTerms(Set<GOTerm> goTerms) {
		this.goTerms = goTerms;
	}

	public GoSet(Set<GOTerm> goTerms) {

		this.goTerms = goTerms;
	}
}
