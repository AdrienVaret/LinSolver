package utils;

import java.util.ArrayList;
import java.util.List;

import graphs.Node;

public class EdgeSet {

	private List<Node> firstVertices;
	private List<Node> secondVertices;
	
	public EdgeSet(List<Node> firstVertices, List<Node> secondVertices) {
		this.firstVertices = firstVertices;
		this.secondVertices = secondVertices;
	}

	public List<Node> getFirstVertices() {
		return firstVertices;
	}

	public List<Node> getSecondVertices() {
		return secondVertices;
	}
	
	public int size() {
		return firstVertices.size();
	}
}
