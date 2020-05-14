package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import graphs.Node;
import graphs.UndirGraph;
import graphs.UndirPonderateGraph;
import utils.RelativeMatrix;
import utils.Utils;

public class GraphParser {
	
	public static boolean isCommentary(String [] splittedLine) {
		return (splittedLine[0].equals("c"));
	}
	
	public static boolean isEdge(String [] splittedLine) {
		return (splittedLine[0].equals("e"));
	}
	
	public static boolean isHexagon(String [] splittedLine) {
		return (splittedLine[0].equals("h"));
	}
	
	public static UndirPonderateGraph exportSolutionToPonderateGraph(UndirGraph graph, int [] edgesValues) {
		
		int [][] adjacencyMatrix = new int [graph.getAdjacencyMatrix().length][graph.getAdjacencyMatrix()[0].length];
		ArrayList<String> edgesString = new ArrayList<String>();
		
		for (int i = 0 ; i < adjacencyMatrix.length ; i++) {
			for (int j = 0 ; j < adjacencyMatrix[i].length ; j++) {
				adjacencyMatrix[i][j] = -1;
			}
		}
		
		for (int i = 0 ; i < edgesValues.length ; i++) {
			String edgeStr = graph.getEdgesString().get(i);
			int edgeValue = edgesValues[i];
			
			String [] splittedString = edgeStr.split(" ");
			String [] uStr = splittedString[1].split(Pattern.quote("_"));
			String [] vStr = splittedString[2].split(Pattern.quote("_"));
			
			int x1 = Integer.parseInt(uStr[0]);
			int y1 = Integer.parseInt(uStr[1]);
			int x2 = Integer.parseInt(vStr[0]);
			int y2 = Integer.parseInt(vStr[1]);
			
			int uIndex = graph.getCoords().get(x1, y1);
			int vIndex = graph.getCoords().get(x2, y2);
			
			adjacencyMatrix[uIndex][vIndex] = edgeValue;
			adjacencyMatrix[vIndex][uIndex] = edgeValue;
			edgesString.add(edgeStr + " " + edgeValue);
		}
		
		return new UndirPonderateGraph(graph.getNbNodes(), graph.getNbEdges(), graph.getNbHexagons(), graph.getEdgeMatrix(),
				adjacencyMatrix, edgesString, graph.getHexagonsString(), graph.getNodesRefs(), graph.getNodesMem(), graph.getCoords(),
				graph.getMaxIndex());
		
	}
	
	public static void exportSolutionToPonderateGraphFile(String filename, UndirGraph graph, int [] edgesValues) {
		
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(filename)));
			
			w.write("p DIMACS " + graph.getNbNodes() + " " + graph.getNbEdges() + " " + graph.getNbHexagons() + "\n");
			
			
			ArrayList<String> edges = graph.getEdgesString();
			ArrayList<String> hexagons = graph.getHexagonsString();
			
			for (int i = 0 ; i < edges.size() ; i++) 
				w.write(edges.get(i) + " " + edgesValues[i] + "\n");
			
			
			for (String hexagon : hexagons)
				w.write(hexagon + "\n");
			
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static UndirPonderateGraph parseUndirPonderateGraph(String inputFileName, String fileWithNoCoords) {
		
		try {
			BufferedReader r = new BufferedReader(new FileReader(new File(inputFileName)));
			
			String line = null;
			boolean firstLine = true;
			
			int nbNodes = 0, nbEdges = 0, nbHexagons = 0;
			
			ArrayList<String> edgesStrings = new ArrayList<String>();
			ArrayList<String> hexagonsStrings = new ArrayList<String>();
			ArrayList<ArrayList<Integer>> edgesMatrix = new ArrayList<ArrayList<Integer>>();
			
			int [][] adjacencyMatrix = null;
			Node [] nodes = null;
			
			
			RelativeMatrix nodesCoord = null;
			
			int nodeIndex = 0;
			int edgeIndex = 0;
			
			while ((line = r.readLine()) != null) {
				String [] splittedLine = line.split(" ");
				
				if (!isCommentary(splittedLine)) {
					
					if (firstLine) {
						
						firstLine = false;
						nbNodes = Integer.parseInt(splittedLine[2]);
						nbEdges = Integer.parseInt(splittedLine[3]);
						nbHexagons = Integer.parseInt(splittedLine[4]);
						
						nodes = new Node[nbNodes];
						nodesCoord = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons, 8 * nbHexagons);
						
						for (int i = 0 ; i < nbNodes ; i++) {
							edgesMatrix.add(new ArrayList<Integer>());
						}
						
						adjacencyMatrix = new int [nbNodes][nbNodes];
						
						for (int i = 0 ; i < nbNodes ; i++) {
							for (int j = 0 ; j < nbNodes ; j++) {
								adjacencyMatrix[i][j] = -1;
							}
						}
						
					}
					
					else {
						
						if (isEdge(splittedLine)) {
							
							String uStr = splittedLine[1];
							String vStr = splittedLine[2];
							int value = Integer.parseInt(splittedLine[3]);
							
							String[] uSplit = uStr.split(Pattern.quote("_"));
							String[] vSplit = vStr.split(Pattern.quote("_"));
							
							int x1 = Integer.parseInt(uSplit[0]);
							int y1 = Integer.parseInt(uSplit[1]);
							int x2 = Integer.parseInt(vSplit[0]);
							int y2 = Integer.parseInt(vSplit[1]);
							
							int u, v;
							
							//Testing if nodes are already created
							if (nodesCoord.get(x1, y1) == -1) {
								if (fileWithNoCoords == null) {
									nodesCoord.set(x1, y1, nodeIndex);
									u = nodeIndex;
									nodes[nodeIndex] = new Node(x1, y1, nodeIndex);
									nodeIndex ++;
								}
								else {
									u = nodesCoord.get(x1, y1);
									nodes[u] = new Node(x1, y1, u);
								}
							} 
							else {	
								u = nodesCoord.get(x1, y1);
							}
							
							if (nodesCoord.get(x2, y2) == -1) {	
								if (fileWithNoCoords == null) {
									nodesCoord.set(x2, y2, nodeIndex);
									v = nodeIndex;
									nodes[nodeIndex] = new Node(x2, y2, nodeIndex);
									nodeIndex ++;
								}
								else {
									v = nodesCoord.get(x2, y2);
									nodes[v] = new Node(x2, y2, v);
								}
							} 
							else {	
								v = nodesCoord.get(x2, y2);
							}
							
							
							edgesMatrix.get(u).add(edgeIndex);
							edgesMatrix.get(v).add(edgeIndex);
							adjacencyMatrix[u][v] = value;
							adjacencyMatrix[v][u] = value;
							edgesStrings.add(line + " " + value);
						}
						
						if (isHexagon(splittedLine)) {
							hexagonsStrings.add(line);
						}
					}
					
				}
			}
			
			r.close();
			
			return new UndirPonderateGraph(nbNodes, nbEdges, nbHexagons, 
					edgesMatrix, adjacencyMatrix, edgesStrings, hexagonsStrings, nodes, null, null, -1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static UndirGraph parseUndirectedGraph(String inputFileName) {
		
		RelativeMatrix nodesMem = null;
		int maxIndex = -1;
		
		try {
			BufferedReader r = new BufferedReader(new FileReader(new File(inputFileName)));
			
			r = new BufferedReader(new FileReader(new File(inputFileName)));
			
			String line = null;
			boolean firstLine = true;
			
			int nbNodes = 0, nbEdges = 0, nbHexagons = 0;
			
			ArrayList<String> edgesStrings = new ArrayList<String>();
			ArrayList<String> hexagonsStrings = new ArrayList<String>();
			ArrayList<ArrayList<Integer>> edgesMatrix = new ArrayList<ArrayList<Integer>>();
			
			int [][] adjacencyMatrix = null;
			Node [] nodes = null;
			
			
			RelativeMatrix nodesCoord = null;
			
			int nodeIndex = 0;
			int edgeIndex = 0;
			
			firstLine = true;
			
			while ((line = r.readLine()) != null) {
				String [] splittedLine = line.split(" ");
				
				if (!isCommentary(splittedLine)) {
					
					if (firstLine) {
						
						firstLine = false;
						nbNodes = Integer.parseInt(splittedLine[2]);
						nbEdges = Integer.parseInt(splittedLine[3]);
						nbHexagons = Integer.parseInt(splittedLine[4]);
						
						nodes = new Node[nbNodes];
						nodesCoord = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons, 8 * nbHexagons);
						
						for (int i = 0 ; i < nbNodes ; i++) {
							edgesMatrix.add(new ArrayList<Integer>());
						}
						
						adjacencyMatrix = new int [nbNodes][nbNodes];
						
					}
					
					else {
						
						if (isEdge(splittedLine)) {
							
							String uStr = splittedLine[1];
							String vStr = splittedLine[2];
							
							String[] uSplit = uStr.split(Pattern.quote("_"));
							String[] vSplit = vStr.split(Pattern.quote("_"));
							
							int x1 = Integer.parseInt(uSplit[0]);
							int y1 = Integer.parseInt(uSplit[1]);
							int x2 = Integer.parseInt(vSplit[0]);
							int y2 = Integer.parseInt(vSplit[1]);
							
							int u, v;
							
							//Testing if nodes are already created
							if (nodesCoord.get(x1, y1) == -1) {
								nodesCoord.set(x1, y1, nodeIndex);
								u = nodeIndex;
								nodes[nodeIndex] = new Node(x1, y1, nodeIndex);
								System.out.println("nodes[" + nodeIndex + "] = (" + x1 + ", " + y1 + ")");
								nodeIndex ++;
							} 
							else {	
								u = nodesCoord.get(x1, y1);
							}
							
							if (nodesCoord.get(x2, y2) == -1) {				
								nodesCoord.set(x2, y2, nodeIndex);
								v = nodeIndex;
								nodes[nodeIndex] = new Node(x2, y2, nodeIndex);
								System.out.println("nodes[" + nodeIndex + "] = (" + x2 + ", " + y2 + ")");
								nodeIndex ++;
							} 
							else {	
								v = nodesCoord.get(x2, y2);
							}
							
							edgesMatrix.get(u).add(edgeIndex);
							edgesMatrix.get(v).add(edgeIndex);
							adjacencyMatrix[u][v] = 1;
							adjacencyMatrix[v][u] = 1;
							edgesStrings.add(line);
							edgeIndex ++;
						}
						
						if (isHexagon(splittedLine)) {
							
							hexagonsStrings.add(line);
						}
					}
					
				}
			}
			
			r.close();
			
			return new UndirGraph(nbNodes, nbEdges, nbHexagons, edgesMatrix, 
					adjacencyMatrix, edgesStrings, hexagonsStrings, nodes, nodesCoord, nodesMem, maxIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
