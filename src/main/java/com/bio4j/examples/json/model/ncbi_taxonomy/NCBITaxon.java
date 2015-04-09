package com.bio4j.examples.json.model.ncbi_taxonomy;

import java.util.List;

/**
 * Created by ppareja on 4/9/2015.
 */
public class NCBITaxon {

    public List<NCBITaxon> children;
    public String name;
    public String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<NCBITaxon> getChildren() {

        return children;
    }

    public void setChildren(List<NCBITaxon> children) {
        this.children = children;
    }
}
