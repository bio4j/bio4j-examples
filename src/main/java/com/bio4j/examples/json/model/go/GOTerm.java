package com.bio4j.examples.json.model.go;

import java.util.List;

/**
 * Created by ppareja on 2/18/2015.
 */
public class GOTerm {

	public String id;
	public String name;
	public String definition;
	public String synonym;
	public String comment;
	public int termCount;
	public int cumulativeCount;
	public List<String> parentIds;

	public int getCumulativeCount() {
		return cumulativeCount;
	}

	public void setCumulativeCount(int cumulativeCount) {
		this.cumulativeCount = cumulativeCount;
	}

	public int getTermCount() {
		return termCount;
	}

	public void setTermCount(int termCount) {
		this.termCount = termCount;
	}

	public GOTerm(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getObsolete() {

		return obsolete;
	}

	public void setObsolete(String obsolete) {
		this.obsolete = obsolete;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<String> getParentIds() {
		return parentIds;
	}

	public void setParentIds(List<String> parentIds) {
		this.parentIds = parentIds;
	}

	public String obsolete;
}
