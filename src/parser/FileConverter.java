package parser;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import graphs.Node;
import utils.RelativeMatrix;

public class FileConverter {

	//Constants
	private static final int H = 0;
	private static final int HD = 1;
	private static final int BD = 2;
	private static final int B = 3;
	private static final int BG = 4;
	private static final int HG = 5;
	
	private static int nbNodes = -1;
	private static int nbEdges = -1;
	private static int nbHexagons = -1;
	private static  int maxHexagonIndex = -1;
	private static int [][] adjacencyMatrix = null;
	private static Node [] nodes = null;
	private static ArrayList<String> hexagonsString = new ArrayList<String>();
	
	private static RelativeMatrix nodesCoord = null;
	private static int [][] hexagons = null;
	private static  int[] hexagonsCovered = null;
	
	

	public static boolean isCommentary(String [] splittedLine) {
		return splittedLine[0].equals("c");
	}
	
	public static boolean isEdge(String [] splittedLine) {
		return splittedLine[0].equals("e");
	}
	
	public static boolean isHexagon(String [] splittedLine) {
		return splittedLine[0].equals("h");
	}
	
	public static boolean isHeader(String [] splittedLine) {
		return splittedLine[0].equals("p");
	}
	
	public static int invert(int position) {
		return (position + 3) % 6;
	}
	
	public static Point transition(int x, int y, int position){
		
		if (position == H) return new Point(x+1, y+1);
		if (position == HD) return new Point(x, y+1);
		if (position == BD) return new Point(x-1, y+1);
		if (position == B) return new Point(x-1, y-1);
		if (position == BG) return new Point(x, y-1);
		if (position == HG) return new Point(x+1, y-1);
		
		return null;
	}
	
	public static void readInput (String inputFileName) {
		try {
			
			
			BufferedReader r = new BufferedReader(new FileReader(new File(inputFileName)));
			String line = null;
			
			int hexagonIndex = 0;
			
			while ((line = r.readLine()) != null) {
				
				String [] splittedLine = line.split(" ");
				
				if (!isCommentary(splittedLine)) {
					
					if (isHeader(splittedLine)) {
						
						nbNodes = Integer.parseInt(splittedLine[2]);
						nbEdges = Integer.parseInt(splittedLine[3]);
						nbHexagons = Integer.parseInt(splittedLine[4]);
						
						if (splittedLine.length >= 6)
							maxHexagonIndex = Integer.parseInt(splittedLine[5]);
						else 
							maxHexagonIndex = nbNodes;
						
						nodes = new Node[maxHexagonIndex+1];
						nodesCoord = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons, 8 * nbHexagons);
						adjacencyMatrix = new int [maxHexagonIndex+1][maxHexagonIndex+1];
						hexagonsCovered = new int[nbHexagons];
						
						hexagons = new int[nbHexagons][6];
					}
					
					else if (isHexagon(splittedLine)) {
						
						for (int i = 0 ; i < 6 ; i++) 
							hexagons[hexagonIndex][i] = Integer.parseInt(splittedLine[i+1]);
						
						hexagonIndex ++;
					}
					
				}
			}
				
			r.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setFirstHexagon() {
		//Adding firstHexagon
				nodesCoord.set(0, 0, hexagons[0][0]);
				nodesCoord.set(1, 1, hexagons[0][1]);
				nodesCoord.set(1, 2, hexagons[0][2]);
				nodesCoord.set(0, 3, hexagons[0][3]);
				nodesCoord.set(-1, 2, hexagons[0][4]);
				nodesCoord.set(-1, 1, hexagons[0][5]);
						
				nodes[hexagons[0][0]] = new Node(0, 0, hexagons[0][0]);
				nodes[hexagons[0][1]] = new Node(1, 1, hexagons[0][1]);
				nodes[hexagons[0][2]] = new Node(1, 2, hexagons[0][2]);
				nodes[hexagons[0][3]] = new Node(0, 3, hexagons[0][3]);
				nodes[hexagons[0][4]] = new Node(-1, 2, hexagons[0][4]);
				nodes[hexagons[0][5]] = new Node(-1, 1, hexagons[0][5]);
						
				adjacencyMatrix[hexagons[0][0]][hexagons[0][1]] = 1;
				adjacencyMatrix[hexagons[0][1]][hexagons[0][0]] = 1;
				adjacencyMatrix[hexagons[0][1]][hexagons[0][2]] = 1;
				adjacencyMatrix[hexagons[0][2]][hexagons[0][1]] = 1;
				adjacencyMatrix[hexagons[0][2]][hexagons[0][3]] = 1;
				adjacencyMatrix[hexagons[0][3]][hexagons[0][2]] = 1;
				adjacencyMatrix[hexagons[0][3]][hexagons[0][4]] = 1;
				adjacencyMatrix[hexagons[0][4]][hexagons[0][3]] = 1;
				adjacencyMatrix[hexagons[0][4]][hexagons[0][5]] = 1;
				adjacencyMatrix[hexagons[0][5]][hexagons[0][4]] = 1;
				adjacencyMatrix[hexagons[0][5]][hexagons[0][0]] = 1;
				adjacencyMatrix[hexagons[0][0]][hexagons[0][5]] = 1;

				hexagonsString.add(new String("h 0_0 1_1 1_2 0_3 -1_2 -1_1"));
						
				hexagonsCovered[0] = 1;
	}
	
	public static void saveResult(String outputFileName) {
		
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputFileName)));
			
			System.out.println("p DIMACS " + nbNodes + " " + nbEdges + " " + nbHexagons);
			w.write("p DIMACS " + nbNodes + " " + nbEdges + " " + nbHexagons + "\n");
			
			for (int i = 0 ; i < adjacencyMatrix.length ; i++) {
				for (int j = i+1 ; j < adjacencyMatrix.length ; j++) {
					if (adjacencyMatrix[i][j] == 1) {
						System.out.println("e " + nodes[i].getDimacsStr() +  " " + nodes[j].getDimacsStr());
						w.write("e " + nodes[i].getDimacsStr() +  " " + nodes[j].getDimacsStr() + "\n");
					}
				}
			}
/*			
			for (String str : hexagonsString) {
				System.out.println(str);
				w.write(str + "\n");
			}
*/
			
			for (int i = 0 ; i < nbHexagons ; i++) {
				int [] hexagon = hexagons[i];
				
				StringBuilder hexagonStr = new StringBuilder();
				hexagonStr.append("h ");
				for (int j = 0 ; j < 6 ; j++) {
					Node u = nodes[hexagon[j]];
					hexagonStr.append(u.getX() + "_" + u.getY() + " ");
				}
				w.write(hexagonStr.toString() + "\n");
			}
			
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void convertInstance(String inputFileName, String outputFileName) {
				
		System.out.println(inputFileName);
		
		readInput(inputFileName);
		
		setFirstHexagon();
				
		ArrayList<Integer> candidats = new ArrayList<Integer>();
		candidats.add(0);
				
		while (candidats.size() > 0) {
			int candidat = candidats.get(0);
						
			for (int hexagon = 0 ; hexagon < nbHexagons ; hexagon++) {
				if (hexagon != candidat && hexagonsCovered[hexagon] == 0) {
							
					for (int i = 0 ; i < 6 ; i++) {
						int j = (i+1) % 6;
								
						//CHERCHER SI hexagon est adjacent à candidat
								
						for (int i2 = 0 ; i2 < 6 ; i2++) {
							int j2 = (i2 + 1) % 6;
									
							if ( (hexagons[candidat][i] == hexagons[hexagon][i2] && 
								  hexagons[candidat][j] == hexagons[hexagon][j2]) ||
											
								 (hexagons[candidat][i] == hexagons[hexagon][j2] && 
								  hexagons[candidat][j] == hexagons[hexagon][i2])) {
										
								Node [] nodesHexagon = new Node[6];
										
								if (hexagons[candidat][i] == hexagons[hexagon][i2] && 
									hexagons[candidat][j] == hexagons[hexagon][j2]) {
											 
									nodesHexagon[i2] = nodes[hexagons[candidat][i]];
									nodesHexagon[j2] = nodes[hexagons[candidat][j]];
										
								}
										
								if (hexagons[candidat][i] == hexagons[hexagon][j2] && 
									hexagons[candidat][j] == hexagons[hexagon][i2]) {
												 
									nodesHexagon[i2] = nodes[hexagons[candidat][j]];
									nodesHexagon[j2] = nodes[hexagons[candidat][i]];
											
								}
										
								//On cherche si il existe d'autres hexagons déja traités à part candidat qui sont adjacents à $hexagone
								for (int hexagon2 = 0 ; hexagon2 < nbHexagons ; hexagon2 ++) {
									if (hexagon2 != candidat && hexagon2 != hexagon && hexagonsCovered[hexagon2] == 1) {
												
										//test d'adjacence
										for (int i3 = 0 ; i3 < 6 ; i3++) {					
											int j3 = (i3 + 1) % 6;
											
											for (int i4 = 0 ; i4 < 6 ; i4 ++) {
												int j4 = (i4 + 1) % 6;
														
												if (hexagons[hexagon][i3] == hexagons[hexagon2][i4] &&
													hexagons[hexagon][j3] == hexagons[hexagon2][j4]) {
															
													nodesHexagon[i3] = nodes[hexagons[hexagon2][i4]];
													nodesHexagon[j3] = nodes[hexagons[hexagon2][j4]];
												}
														
												if (hexagons[hexagon][i3] == hexagons[hexagon2][j4] &&
													hexagons[hexagon][j3] == hexagons[hexagon2][i4]) {
																
													nodesHexagon[i3] = nodes[hexagons[hexagon2][j4]];
													nodesHexagon[j3] = nodes[hexagons[hexagon2][i4]];
												}
															
											}
										}
									}
								}
										
								//puis on ajoute les noeuds non renseignés par rapport à ceux déja connus
										
								int firstIndex = 0;
								for (int index = 0 ; index < 6 ; index++) {
									if (nodesHexagon[index] != null) {
										firstIndex = index;
										break;
									}
								}
										
								int cpt = 0;
								while (cpt < 6) {
									int nextIndex = (firstIndex + 1) % 6;
									if (nodesHexagon[nextIndex] == null) {
										Point newCoord = transition(nodesHexagon[firstIndex].getX(), nodesHexagon[firstIndex].getY(), firstIndex);
										int nodeId = hexagons[hexagon][nextIndex];
										nodesHexagon[nextIndex] = new Node((int)newCoord.getX(), (int)newCoord.getY(), nodeId);
									}
									//firstIndex ++;
									firstIndex = (firstIndex + 1) % 6;
									cpt ++;
								}
										
								StringBuilder builder = new StringBuilder();
								builder.append("h ");
								for (int index = 0 ; index < nodesHexagon.length ; index ++) {
									Node node = nodesHexagon[index];
									Node node2 = nodesHexagon[(index + 1) % 6];
											
									adjacencyMatrix[node.getIndex()][node2.getIndex()] = 1;
									adjacencyMatrix[node2.getIndex()][node.getIndex()] = 1;
											
									builder.append(node.getX() + "_" + node.getY());
									if (index < nodesHexagon.length - 1)
										builder.append(" ");
												
										nodesCoord.set(node.getX(), node.getY(), node.getIndex());
										nodesCoord.set(node2.getX(), node2.getY(), node2.getIndex());
											
										nodes[node.getIndex()] = node;
								}
										
								hexagonsString.add(builder.toString());
								//on ajoute hexagone aux candidats 
								candidats.add(hexagon);
								hexagonsCovered[hexagon] = 1;
							}		
						}			
					}
				}
			}
			candidats.remove(0);
		}
		
		saveResult(outputFileName);
	}
	
	public static void displayUsage() {
		System.err.println("USAGE : java -jar ${EXEC_NAME} $inputFileName $outputFileName");
	}
	
	public static void main(String [] args) {
	
		if (args.length < 2) {
			displayUsage();
			System.exit(1);
		}
		
		convertInstance(args[0], args[1]);
		System.exit(0);

	}
}
