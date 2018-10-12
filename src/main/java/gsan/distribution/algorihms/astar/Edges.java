package gsan.distribution.algorihms.astar;

import java.io.Serializable;

import gsan.distribution.gsan_api.ontology.InfoTerm;


public class Edges implements Serializable {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public final double cost;
public final InfoTerm target;
public final InfoTerm initial;

public Edges(InfoTerm initialNode,InfoTerm targetNode, double costVal){
        target = targetNode;
        cost = costVal;
        initial = initialNode;
}
public Edges() {
	this.cost = 0.;
	this.initial = new InfoTerm();
	this.target = new InfoTerm();
}


}


