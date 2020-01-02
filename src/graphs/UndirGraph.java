package graphs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import utils.RelativeMatrix;

public class UndirGraph {
	
	private RelativeMatrix nodesMem; //DEBUG
	
	private int nbNodes, nbEdges, nbHexagons, maxIndex;
	
	private ArrayList<ArrayList<Integer>> edgeMatrix;
	private int [][] adjacencyMatrix;
	
	private ArrayList<String> edgesString;
	private ArrayList<String> hexagonsString;
	
	private Node [] nodesRefs;
	
	private RelativeMatrix coords;
	
	private int [][] hexagons;
	
	/**
	 * Constructors
	 */
	
	public UndirGraph(int nbNodes, int nbEdges, int nbHexagons, ArrayList<ArrayList<Integer>> edgeMatrix,
			int[][] adjacencyMatrix, ArrayList<String> edgesString, ArrayList<String> hexagonsString,
			Node[] nodesRefs, RelativeMatrix coords) {

		this.nbNodes = nbNodes;
		this.nbEdges = nbEdges;
		this.nbHexagons = nbHexagons;
		this.edgeMatrix = edgeMatrix;
		this.adjacencyMatrix = adjacencyMatrix;
		this.edgesString = edgesString;
		this.hexagonsString = hexagonsString;
		this.nodesRefs = nodesRefs;
		this.coords = coords;
		
		hexagons = new int[nbHexagons][6];
		initHexagons();
	}
	
	public UndirGraph(int nbNodes, int nbEdges, int nbHexagons, ArrayList<ArrayList<Integer>> edgeMatrix,
			int[][] adjacencyMatrix, ArrayList<String> edgesString, ArrayList<String> hexagonsString,
			Node[] nodesRefs, RelativeMatrix coords, RelativeMatrix nodesMem, int maxIndex) {

		this.nbNodes = nbNodes;
		this.nbEdges = nbEdges;
		this.nbHexagons = nbHexagons;
		this.edgeMatrix = edgeMatrix;
		this.adjacencyMatrix = adjacencyMatrix;
		this.edgesString = edgesString;
		this.hexagonsString = hexagonsString;
		this.nodesRefs = nodesRefs;
		this.coords = coords;
		this.nodesMem = nodesMem;
		this.maxIndex = maxIndex;
		
		hexagons = new int[nbHexagons][6];
		initHexagons();
	}
	
	/**
	 * Getters and setters
	 */
	
	public int getNbNodes() {
		return nbNodes;
	}

	public int getNbEdges() {
		return nbEdges;
	}


	public int getNbHexagons() {
		return nbHexagons;
	}

	public int getMaxIndex() {
		return maxIndex;
	}

	public ArrayList<ArrayList<Integer>> getEdgeMatrix() {
		return edgeMatrix;
	}

	public int[][] getAdjacencyMatrix(){
		return adjacencyMatrix;
	}
	
	public ArrayList<String> getEdgesString() {
		return edgesString;
	}

	public ArrayList<String> getHexagonsString() {
		return hexagonsString;
	}
	
	public Node getNodeRef(int index) {
		return nodesRefs[index];
	}
	
	public RelativeMatrix getCoords() {
		return coords;
	}
	
	public Node[] getNodesRefs() {
		return nodesRefs;
	}
	
	/**
	 * Class's methods
	 */
	
	public void exportToGraphviz(String outputFileName) {
		//COMPIL: dot -Kfdp -n -Tpng -o test.png test
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputFileName)));
			
			w.write("graph{" + "\n");
			
			for (int i = 1 ; i <= nodesRefs.length ; i++) {
				w.write("\t" + i + " [pos=\"" + nodesRefs[(i-1)].getX() + "," + nodesRefs[(i-1)].getY() + "!\"]" + "\n" );
			}
			
			w.write("\n");
			
			for (int i = 0 ; i < adjacencyMatrix.length ; i++) {
				for (int j = i + 1 ; j < adjacencyMatrix[i].length ; j++) {
					
					if (adjacencyMatrix[i][j] != 0)
						w.write("\t" + (i) + " -- " + (j) + "\n");
					
				}
			}
			
			w.write("}");
			
			w.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public RelativeMatrix getNodesMem() {
		return nodesMem;
	}
	
	public void initHexagons() {
		for (int i = 0 ; i < nbHexagons ; i++) {
			String hexagon = hexagonsString.get(i);
			String [] sHexagon = hexagon.split(" ");
			
			for (int j = 1 ; j < sHexagon.length ; j++) {
				String [] sVertex = sHexagon[j].split(Pattern.quote("_"));
				int x = Integer.parseInt(sVertex[0]);
				int y = Integer.parseInt(sVertex[1]);
				hexagons[i][j-1] = coords.get(x, y);
			}
		}
	}
}
