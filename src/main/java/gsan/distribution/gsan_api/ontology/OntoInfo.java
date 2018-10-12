package gsan.distribution.gsan_api.ontology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OntoInfo{
	private double maxdepth;
	public List<Double> maxIC;
	private int nEdgeIsA;
	private int nEdgePOf;
	private int nNodes;
	private Set<String> leavesISA;
	
	public OntoInfo() {
		this.maxdepth = 0;
		this.maxIC = new ArrayList<>();
		this.nEdgeIsA = 0;
		this.nEdgePOf = 0;
		this.leavesISA = new HashSet<>();
		
	}
	public OntoInfo(OntoInfo oi) {
		this.maxdepth = oi.maxdepth;
		this.maxIC = new ArrayList<>(oi.maxIC);
		this.nEdgeIsA = oi.nEdgeIsA;
		this.nEdgePOf = oi.nEdgePOf;
		this.leavesISA = new HashSet<>(oi.leavesISA);
		
	}
	
	public double maxDepth() {
		return this.maxdepth;
	}
	public void setMaxDepth(double md) {
		this.maxdepth = md;
	}
	public double maxIC(int pos) {
		return this.maxIC.get(pos);
	}
	public void addMaxIC(double ic) {
		this.maxIC.add(ic);
	}
	public void addAllMaxIC(Collection<Double> ics) {
		this.maxIC.addAll(ics);
	}
	public void ClearMaxIC() {
		this.maxIC.clear();
	}
	
	public int numberOfNodes() {
		return this.nNodes;
	}
	public void setnumbernodes(int nn) {
		this.nNodes = nn;
	}
	public void setnumberedge(int isa, int partof) {
		this.nEdgeIsA=isa;
		this.nEdgePOf=partof;
	}
	public int numberOfEdges() {
		return this.nEdgeIsA+this.nEdgePOf;
	}
	public void addLeaveIsA(String a) {
		this.leavesISA.add(a);
	}
	public void addAllLeavesISA(Collection<String> a) {
		this.leavesISA.addAll(a);
	}
	
	public void ClearLeavesISA() {
		this.leavesISA.clear();
	}
	public Set<String> getLeavesISA(){
		return this.leavesISA;
	}
	
}