package com.bio4j.examples.json.model.go;

/**
 * Created by ppareja on 2/18/2015.
 */
public class GOTerm {

	public String id;
	public String name;
	public String definition;
	public String synonym;
	public String comment;

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

	public String obsolete;
}
