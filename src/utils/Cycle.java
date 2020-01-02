package utils;

import java.util.ArrayList;

import graphs.Arc;

public class Cycle implements Comparable<Cycle>{

	private int [] edges;
	private int [] nodes;
	private int size;
	private int nbNodes;
	private int nbEdges;
	
	private ArrayList<Integer> hexagonsCovered;
	
	//DEBUG
	ArrayList<Arc> arcs;
	
	public Cycle(int [] edges, int [] nodes, int size, ArrayList<Arc> arcs, int nbNodes, int nbEdges) {
		this.edges = edges;
		this.nodes = nodes;
		this.size  = size;
		this.nbNodes = nbNodes;
		this.nbEdges = nbEdges;
		
		this.arcs = arcs;
		hexagonsCovered = new ArrayList<Integer>();
	}
	
	public int [] getEdges() {
		return edges;
	}
	
	public int [] getNodes() {
		return nodes;
	}
	
	public int getEdge(int index) {
		return edges[index];
	}
	
	public int getNode(int index) {
		return nodes[index];
	}
	
	public int getSize() {
		return size;
	}

	
	public ArrayList<Arc> getArcs(){
		return arcs;
	} 
	
	@Override
	public int compareTo(Cycle o) {
		if (hexagonsCovered.size() < o.getHexagonsCovered().size())
			return -1;
		else if (hexagonsCovered.size() == o.getHexagonsCovered().size())
			return 0;
		else 
			return 1;
			
	}


	public int getNbNodes() {
		return nbNodes;
	}
	
	public int getNbEdges() {
		return nbEdges;
	}
	
	public ArrayList<Integer> getHexagonsCovered(){
		return hexagonsCovered;
	}
	
	public void setHexagonsCovered(ArrayList<Integer> hexagonsCovered) {
		this.hexagonsCovered = hexagonsCovered;
	}
	
	public int getR() {
		 return (int) (((nbEdges*2) - 2) / 4) - 1;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		for (int i = 0 ; i < nodes.length ; i++) {
			if (nodes[i] != -1)
				b.append(nodes[i] + " ");
		}
		
		b.append(" => " + hexagonsCovered.toString());
		
		return b.toString();
	}
	
	public String toString2() {
		StringBuilder b = new StringBuilder();
		
		for (int i = 0 ; i < nodes.length ; i++) {
			if (nodes[i] != -1)
				b.append(nodes[i] + " ");
		}
		
		return b.toString();
	}

}
