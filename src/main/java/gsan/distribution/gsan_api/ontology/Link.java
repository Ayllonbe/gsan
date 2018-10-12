package gsan.distribution.gsan_api.ontology;

import java.util.ArrayList;
import java.util.List;

public class Link {
	public List<String>  	ancestors ; 
	public List<String> 	parents;
	public List<String>  	descendants ;
	public List<String> 	childrens;
	public List<String> 	descLeaves;
	public Link() {
		this.ancestors	= new ArrayList<String>();
		this.descendants= new ArrayList<String>();
		this.parents 	= new ArrayList<String>();
		this.childrens 	= new ArrayList<String>();
		this.descLeaves = new ArrayList<String>();
	}
	public Link(Link link) {
		this.ancestors	= new ArrayList<String>(link.ancestors);
		this.descendants= new ArrayList<String>(link.descendants);
		this.parents 	= new ArrayList<String>(link.parents);
		this.childrens 	= new ArrayList<String>(link.childrens);
		this.descLeaves = new ArrayList<String>(link.descLeaves);
	}
}
