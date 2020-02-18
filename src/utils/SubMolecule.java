package utils;

public class SubMolecule {

	private int nbNodes;
	private int [] vertices;
	private int [][] adjacencyMatrix;
	
	public SubMolecule(int nbNodes, int [] vertices, int [][] adjacencyMatrix) {
		this.nbNodes = nbNodes;
		this.vertices = vertices;
		this.adjacencyMatrix = adjacencyMatrix;
	}

	public int getNbNodes() {
		return nbNodes;
	}

	public int[] getVertices() {
		return vertices;
	}

	public int[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}
	
	
}
